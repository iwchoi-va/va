package sens.service.webaction;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import sens.src.alarm.monitoring.AlarmMonitoringCache;
import sens.src.dashboard.DashBoardCache;
import sens.src.systeminfo.SystemInfoCache;

public class SensServiceListener implements ServletContextListener{
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		
		try {
			SystemInfoCache systeminfo = SystemInfoCache.getInstance();
			//systeminfo.SystemInfoCacheCall();
			
			System.out.println("===========================================");
			System.out.println("SystemInfo initialized success!!!");
			System.out.println("===========================================");
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println("===========================================");
			System.out.println("SystemInfo initialized fail!!!");
			System.out.println("===========================================");
		}
			
		try {
			AlarmMonitoringCache alarmMonitoring = AlarmMonitoringCache.getInstance();
			//alarmMonitoring.AlarmMonitoringCacheCall();
			
			System.out.println("===========================================");
			System.out.println("AlarmMonitoring initialized success!!!");
			System.out.println("===========================================");
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println("===========================================");
			System.out.println("AlarmMonitoring initialized fail!!!");
			System.out.println("===========================================");
		}
		
		/*try { 임시 주석 -> 대시보드 개발 후 풀것
			DashBoardCache dashboard = DashBoardCache.getInstance();
			dashboard.DashBoardCacheCall();
			
			System.out.println("===========================================");
			System.out.println("DashBoard initialized success!!!");
			System.out.println("===========================================");
		} catch (Exception e) {
			e.printStackTrace();
			
			System.out.println("===========================================");
			System.out.println("DashBoard initialized fail!!!");
			System.out.println("===========================================");
		}*/
	}
}
