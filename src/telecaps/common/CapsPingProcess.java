package telecaps.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.locus.jedi.log.ErrorLogger;

import java.util.concurrent.Callable;

//public class CapsPingProcess extends Thread
//public class CapsPingProcess implements Runnable
public class CapsPingProcess implements Callable<String> {

	private String msg = "";
	private String ip;

	public CapsPingProcess(String ip) {
		this.ip = ip;
	}

	// public void run(){
	public String call() {
		InputStream is = null;
		BufferedReader br = null;
		// String pingCheck = "DEAD";
		Process p = null;
		String line = null;
		boolean[] bPingChk = new boolean[2];

		try {
			Runtime run = Runtime.getRuntime();
			// Process p = run.exec("c:\\winnt\\system32\\ping.exe " + ip);

			for (int i = 0; i < bPingChk.length; i++) {

				//ErrorLogger.debug(Thread.currentThread().getName() + " " + ip
				//		+ " " + i + " ���� ");

				p = run.exec("ping.exe " + ip);
				is = p.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				line = null;

				while ((line = br.readLine()) != null) {
					// System.out.println(line); // line�� �ӽ� ����غ���.
					if ((line.indexOf("Reply") >= 0)
							|| (line.indexOf("����") >= 0)) {
						// pingCheck = "ALIVE";

						bPingChk[i] = true;
						break;
					}

					// if( (line.indexOf("timed out") >= 0)
					// || (line.indexOf("��û") >= 0) ){
					// break;
					// }

					if ((line.indexOf("100% loss") >= 0)
							|| (line.indexOf("100% �ս�") >= 0)) {

						bPingChk[i] = false;
						break;
					}
				}

				//ErrorLogger.debug(Thread.currentThread().getName() + " " + ip
				//		+ " " + i + " ���� " + bPingChk[i]);
			}

			// if(line != null){
			// //msg = line.substring(line.indexOf("time=")+5,
			// line.indexOf("TTL=")-1);
			// msg = pingCheck;
			// }// if

			//ErrorLogger.debug(Thread.currentThread().getName() + " "
			//		+ bPingChk[0] + " " + bPingChk[1]);

			if (!bPingChk[0] && !bPingChk[1]) {
				msg = "DEAD";
			} else {
				msg = "ALIVE";
			}

		} catch (Exception e) {
			ErrorLogger.error(e);

		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex2) {
			}
			try {
				if (is != null)
					is.close();
			} catch (Exception ex2) {
			}
		}

		return msg;
	}

	public String getMsg() {

		try {
			Thread.currentThread().join(); // �ش� thread�� ����ɶ����� ���.
		} catch (InterruptedException ie) {
			return null;
		}

		return msg;
	}
}
