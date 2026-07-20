package telecaps.common.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import telecaps.common.CapsCmsStat;
import telecaps.common.CapsDBValue;
import telecaps.common.CapsDBWriter;
import telecaps.common.CapsProcessCheckDb;
import telecaps.common.CapsSnmpCheckDb;
import telecaps.common.SnmpGet;
import telecaps.common.USMFactory;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.startup.StartupAction;
import com.locus.jedi.util.EnvironmentXmlDAO;

public class TeleCapsStartupAction extends StartupAction {
	static final int QUEUE_SIZE = 2048;

	boolean startupTrap = true; // trap start
	boolean startupChart = true; // chart start

	Timer minTimer = new Timer(); // timer for minute, hour value
	Timer batchTimer = new Timer(); // timer for call value

	// Blocking_queue queue = null;
	BlockingQueue<CapsDBValue> queue = null;
	CapsDBWriter dbw = null;

	Timer snmptimer = new Timer();
	TimerTask snmpchkdb = null;
	
	Timer processalarmtimer = new Timer();
	TimerTask processalarmchkdb = null;
	
	Timer cmsTimer = new Timer();
	TimerTask cmsStat = null;
	
	// ExecutorService threadExecutor = null;
	ExecutorService threadExecutorDB = null;

	public void startup(long delay) {
		USMFactory.getInstance();
		String startupTypeProp = EnvironmentXmlDAO.getInstance().getProperty("startup-type"); // startup ����(trap,chart,all)
		String startupDelayProp = EnvironmentXmlDAO.getInstance().getProperty("startup-delay");
		String cmsIntervalProp = EnvironmentXmlDAO.getInstance().getProperty("cms-interval");
		String intervalProp = EnvironmentXmlDAO.getInstance().getProperty("interval");
		String intervalRealProp = EnvironmentXmlDAO.getInstance().getProperty("intervalReal");
		String cmsTimerStart = EnvironmentXmlDAO.getInstance().getProperty("cmsTimerStart");
		String minTimerStart = EnvironmentXmlDAO.getInstance().getProperty("minTimerStart");
		String snmptimerStart = EnvironmentXmlDAO.getInstance().getProperty("snmpTimerStart");
		String processAlarmTimerStart = EnvironmentXmlDAO.getInstance().getProperty("processAlarmTimerStart");
		String threadPoolCntProp = EnvironmentXmlDAO.getInstance().getProperty("threadPoolCnt");
		String threadPoolDbCntProp = EnvironmentXmlDAO.getInstance().getProperty("threadPoolDbCnt");
		String pingIntervalProp = EnvironmentXmlDAO.getInstance().getProperty("ping-interval");
		String pingIntervalDelayProp = EnvironmentXmlDAO.getInstance().getProperty("ping-interval-delay");

		long startupDelay, interval, intervalReal, pingInterval, pingIntervalDelay, cmsInterval;
		int  threadPoolCnt, threadPoolDbCnt;

		try {
			startupDelay = Long.parseLong(startupDelayProp);
			interval = Long.parseLong(intervalProp);
			intervalReal = Long.parseLong(intervalRealProp);
			threadPoolCnt = Integer.parseInt(threadPoolCntProp);
			threadPoolDbCnt = Integer.parseInt(threadPoolDbCntProp);
			cmsInterval = Long.parseLong(cmsIntervalProp);
			pingInterval = Long.parseLong(pingIntervalProp);
			pingIntervalDelay = Long.parseLong(pingIntervalDelayProp);
		} catch (Exception e) {
			ErrorLogger.error("Exception environment: " + e);
			// e.printStackTrace();

			startupDelay = 20; // 5 seconds
			cmsInterval = 300; // 5분
			interval = 120; // 1 minute
			intervalReal = 120;
			threadPoolCnt = 20;
			threadPoolDbCnt = 10;
			pingInterval = 60; // 2 minutes
			pingIntervalDelay = 180; // 3��
		}

		if (startupTypeProp.equalsIgnoreCase("trap")) {
			startupChart = false; // chart start
		} else if (startupTypeProp.equalsIgnoreCase("chart")) {
			startupTrap = false; // trap start
		}

		if (startupTrap) {
			// ������ �и�
			// create ExecutorService to manage threads
			// threadExecutor = Executors.newFixedThreadPool(threadPoolCnt);
			threadExecutorDB = Executors.newFixedThreadPool(threadPoolDbCnt);

			// queue = new Blocking_queue();
			queue = new ArrayBlockingQueue<CapsDBValue>(QUEUE_SIZE);
			// dbw = new CapsDBWriter(queue);
			dbw = new CapsDBWriter(queue, threadExecutorDB);

			cmsStat = new CapsCmsStat();
			//snmpchkdb = new CapsSnmpCheckDb();
			//processalarmchkdb = new CapsProcessCheckDb();
			
			try {
				
				//ErrorLogger.error("dbw start");
				dbw.start();
				
				ErrorLogger.debug("���������������");
				ErrorLogger.debug("TeleCaps Trap Timer startup!");
				ErrorLogger.debug("���������������");
				
				TimerTask capsTimerTask = new CapsTimerTask(queue, threadPoolCnt);

				if ("Y".equals(minTimerStart)) {
					minTimer.scheduleAtFixedRate(capsTimerTask, startupDelay * 1000, intervalReal * 1000);
				}
				
				if ("Y".equals(cmsTimerStart)) {
					cmsTimer.scheduleAtFixedRate(cmsStat, startupDelay * 1000, cmsInterval * 1000);
				}
			
				//if ("Y".equals(processAlarmTimerStart)) {
				//	processalarmtimer.scheduleAtFixedRate(processalarmchkdb, startupDelay * 1000, intervalReal * 1000);
				//}
				
				//if ("Y".equals(snmptimerStart)) {
				//	snmptimer.scheduleAtFixedRate(snmpchkdb, pingIntervalDelay * 1000, pingInterval * 1000);
				//}
				
			} catch (Exception e) {
				ErrorLogger.error(e);
			}
		}

		if (startupChart) {
			try {
				ErrorLogger.debug("���������������");
				ErrorLogger.debug("TeleCaps Create Chart startup!");
				ErrorLogger.debug("���������������");
			} catch (Exception e) {
				ErrorLogger.error(e);
			}
		}
	}

	public void shutdown() {
		ErrorLogger.debug("���������������");
		ErrorLogger.debug("TeleCaps Timer shutdown!");
		ErrorLogger.debug("���������������");

		if (minTimer != null) {
			minTimer.cancel();
		}

		if (dbw != null) {
			dbw.complete();
		}
	}

	private class CapsTimerTask extends TimerTask {
		private final BlockingQueue<CapsDBValue> queue;
		private ExecutorService threadExecutor;
		private final int poolSize;

		CapsTimerTask(BlockingQueue<CapsDBValue> q, int poolSize) {
			queue = q;
			this.poolSize = poolSize;
		}

		public void run() {

			//ErrorLogger.debug("[snmpget] CapsTimerTask ����:" + Thread.currentThread().getName());

			threadExecutor = Executors.newFixedThreadPool(poolSize);

			SnmpGet snmpGet = new SnmpGet(queue, threadExecutor);
			snmpGet.getRealScheduled();

			threadExecutor.shutdown();

			try {
				if (!threadExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
					threadExecutor.shutdownNow();

					if (!threadExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
						ErrorLogger.debug("�� threadExecutor did not terminate");
					}
				}
			} catch (InterruptedException ie) {
				threadExecutor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}
