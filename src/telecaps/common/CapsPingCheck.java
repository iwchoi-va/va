package telecaps.common;

import telecaps.common.CapsPingProcess;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;

public class CapsPingCheck extends TimerTask {

	String[] ips = null;
	String[] ids = null;
	CapsPingProcess[] pps = null;
	int threadPoolPingCnt;

	ExecutorService ExecPingChkThread;
	Future<String>[] fPing = null;

	public CapsPingCheck(int threadPoolPingCnt) {
		this.threadPoolPingCnt = threadPoolPingCnt;
	}

	public void run() {

		JediTransaction tran = JediTransactionManager.getJediTransaction();
		SQLParam sqlParam = null;

		try {
			ErrorLogger.error(Thread.currentThread().getName()
					+ " ping check ����");

			sqlParam = new SQLParam();

			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.ping.ip.list");
			SQLParam sqlResult = SQLServiceManager.getInstance().execute(
					sqlParam);

			//ErrorLogger.debug("sqlResult.getCount " + sqlResult.getCount());

			if (sqlResult.getCount() > 0) {
				ips = new String[sqlResult.getCount()];
				ids = new String[sqlResult.getCount()];
				pps = new CapsPingProcess[ips.length];
				fPing = new Future[ips.length];

				for (int i = 0; i < sqlResult.getCount(); i++) {
					ips[i] = sqlResult
							.getListParam("telecaps.res.ping.ip.list")
							.getParam(i).getString("DEVICE_IP");
					ids[i] = sqlResult
							.getListParam("telecaps.res.ping.ip.list")
							.getParam(i).getString("DEVICE_ID");
					//ErrorLogger.debug("Ping IP:" + ids[i] + " " + ips[i]);
				}
			}
			// Thread.sleep(50000);
			ExecPingChkThread = Executors.newFixedThreadPool(threadPoolPingCnt);

			for (int i = 0; i < pps.length; i++) {
				pps[i] = new CapsPingProcess(ips[i]);
				// pps[i].start();
				// ExecPingChkThread.execute(pps[i]); // start task3

				fPing[i] = ExecPingChkThread.submit(pps[i]);
			}

			for (int i = 0; i < pps.length; i++) {
				// System.out.println("test "+ips[i] + " : " + pps[i].getMsg());
				// ErrorLogger.debug("Ping Check "+ids[i]+ " "+ ips[i] + ":" +
				// pps[i].getMsg());
				ErrorLogger.error("Ping Check " + ids[i] + " " + ips[i] + ":"
						+ fPing[i].get());

				// if (ips[i]!=null && "DEAD".equals(pps[i].getMsg())) {
				if (ips[i] != null && "DEAD".equals(fPing[i].get())) {
					sqlParam.clear();
					sqlParam.setSqlName("telecaps.sql.ping.ip.insert");
					sqlParam.addValue("device_ip", ips[i]);
					SQLServiceManager.getInstance().execute(sqlParam);
					tran.commit();
				}
			}

			ExecPingChkThread.shutdown();

			try {
				if (!ExecPingChkThread.awaitTermination(60, TimeUnit.SECONDS)) {
					ExecPingChkThread.shutdownNow();

					if (!ExecPingChkThread.awaitTermination(60,
							TimeUnit.SECONDS)) {
						ErrorLogger.debug("�� threadExecutor did not terminate");
					}
				}
			} catch (InterruptedException ie) {
				ExecPingChkThread.shutdownNow();
				Thread.currentThread().interrupt();
			}

			ErrorLogger.error(Thread.currentThread().getName()
					+ " ping check ����");
		} catch (Exception ex2) {
			tran.rollback();
			ErrorLogger.error(ex2);
		}

	}
}
