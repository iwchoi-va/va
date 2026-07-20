package kr.co.kico.wfm.encmapper.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import kr.co.kico.wfm.encmapper.config.WfmEncMapperConfig;

public class WfmEncMapperListener implements ServletContextListener{
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			WfmEncMapperConfig mapperConfig = WfmEncMapperConfig.getInstance();

			System.out.println("===========================================");
			System.out.println("WfmEncMapperConfig initialized success!!!");
			System.out.println("===========================================");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("===========================================");
			System.out.println("WfmEncMapperConfig initialized fail!!!");
			System.out.println("===========================================");
		}
	}
}
