package sens.service.webaction;

 
import java.sql.ResultSet;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;

import javax.servlet.http.HttpServletRequest;

import jedix.xwing.action.XwingWebAction;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceException;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.sql.JediConnection;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GetUcidDuration extends XwingWebAction {
	ListParam DS_UCID = null;
	static SQLParam tmp_sqlparam = null;
	ListParam DS_DURA = new ListParam(new String[] { "UCID", "DURATION", "SUM_DURA"	});
	
	public void perform(JediRequest req, JediResponse res) throws WebActionException {
		HttpServletRequest request = req.getHttpServletRequest();
		String ced_no = req.param.getString("ced_no");
		String rjct_list = req.param.getString("rjct_list","N");
		String sqlNames = req.param.getString("_sqlName","");
		String comp_stat = req.param.getString("COMP_STAT","");
		ListParam DS_SPO = null;
		ListParam DS_META = null;
		
		String [] sql = sqlNames.trim().split(",");
		
		String sql1 = "oba.oba010p1.getUcid.sel";
		String sql2 = "oba.oba010p1.getMetaUcid.sel";
		
		if(sql.length == 2) {
			sql1 = sql[0];
			sql2 = sql[1];
		}
		
		String ucid_comma = "";
	
		SQLParam sqlresult2 = new SQLParam();;
		SQLParam sqlresult3 = new SQLParam();
		
		try {
			//비교 기준이 meta인 경우
			if("".equals(comp_stat)){
				SQLParam sqlparam3 = new SQLParam();
				sqlparam3.setSqlName(sql1);			
				sqlparam3.addValue("ced_no",  ced_no);

				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName(sql2);			
				sqlparam2.addValue("ced_no",  ced_no);

				sqlresult3 = SQLServiceManager.getInstance().execute(sqlparam3);
				DS_UCID =  sqlresult3.getListParam("DS_SPO");
				
				sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
				DS_META = sqlresult2.getListParam("DS_META");
				
				ErrorLogger.debug("##########DS_UCD"+DS_UCID.toString());
				
				for(int i =0; i< DS_UCID.rowSize(); i++){
					String ucid = (String) DS_UCID.getValue(i, "ucid");
					ucid_comma += DS_UCID.getValue(i,"UCID")+",";
					DS_DURA.addRow(new Object[] {ucid,"00:00:00","00:00:00"});
					
					if("N".equals(rjct_list)){
						int idx = DS_META.findRow("UCID", ucid);
						if(idx != -1){
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," [분석대상]");
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " [분석대상]");
						}else{
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," [비대상]");
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " [비대상]");
						}
					}else{
						String reject_yn = DS_UCID.getValue(DS_UCID.findRow("UCID",ucid),"REJECT_YN").toString();
						if("Y".equals(reject_yn)){
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," [거절콜]");
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " [거절콜]");
						}else{
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT","");
							DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE"));
						}
					}
					
				}
				res.param.addValue("DS_META",DS_META);

				getDuration(ucid_comma,res,sqlresult2);
			}else if("MRLS".equals(comp_stat)){ //비교 기준이 mrls인 경우
				
				SQLParam sqlparam3 = new SQLParam();
				sqlparam3.setSqlName(sql1);			
				sqlparam3.addValue("ced_no",  ced_no);

				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName(sql2);			
				sqlparam2.addValue("ced_no",  ced_no);

				sqlresult3 = SQLServiceManager.getInstance().execute(sqlparam3);
				DS_UCID =  sqlresult3.getListParam("DS_SPO");
				
				sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
				DS_META = sqlresult2.getListParam("DS_MRLS");
				
				for(int i =0; i< DS_UCID.rowSize(); i++){
					String ucid = (String) DS_UCID.getValue(i, "ucid");
					ucid_comma += DS_UCID.getValue(i,"UCID")+",";
					DS_DURA.addRow(new Object[] {ucid,"00:00:00","00:00:00"});
					
					int idx = DS_META.findRow("UCID", ucid);
					if(idx != -1){
						String stat = "";
						
						if(DS_META.getValue(idx, "MR_CODE").toString().length() > 0)  stat += "실손";
						if(DS_META.getValue(idx, "LS_CODE").toString().length() > 0){
							if(stat.length() > 0) stat += "/정액";
							else stat += "정액";
						}
						
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," ["+stat+"]");
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " ["+stat+"]");
					}else{
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," [비대상]");
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " [비대상]");
					}
				}
				
				res.param.addValue("DS_META",DS_META);
				
				getDuration(ucid_comma,res,sqlresult2);
			}else if("REREG".equals(comp_stat)){
				SQLParam sqlparam2 = new SQLParam();
				sqlparam2.setSqlName(sql2);			
				sqlparam2.addValue("ced_no",  ced_no);
				
				sqlresult2 = SQLServiceManager.getInstance().execute(sqlparam2);
				DS_META = sqlresult2.getListParam("DS_MRLS");
				
				String ucids = "";
				for(int i =0; i< DS_META.rowSize(); i++){
					if(DS_META.getValue(i, "UCID") != null) ucids += "'"+DS_META.getValue(i, "UCID").toString() + "',";
				}
				
				if(ucids.length() > 0) ucids = ucids.substring(0, ucids.length()-1);
				
				SQLParam sqlparam3 = new SQLParam();
				sqlparam3.setSqlName(sql1);			
				sqlparam3.addValue("ucid",  ucids);
				
				sqlresult3 = SQLServiceManager.getInstance().execute(sqlparam3);
				DS_UCID =  sqlresult3.getListParam("DS_SPO");
				
				
				for(int i =0; i< DS_UCID.rowSize(); i++){
					String ucid = (String) DS_UCID.getValue(i, "ucid");
					ucid_comma += DS_UCID.getValue(i,"UCID")+",";
					DS_DURA.addRow(new Object[] {ucid,"00:00:00","00:00:00"});
					
					int idx = DS_META.findRow("UCID", ucid);
					if(idx != -1){
						String stat = "";
						
						if(DS_META.getValue(idx, "MR_CODE").toString().length() > 0)  stat += "실손";
						if(DS_META.getValue(idx, "LS_CODE").toString().length() > 0){
							if(stat.length() > 0) stat += "/정액";
							else stat += "정액";
						}
						
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," ["+stat+"]");
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " ["+stat+"]");
					}else{
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"ANL_STAT"," [비대상]");
						DS_UCID.setValue(DS_UCID.findRow("UCID",ucid),"DATETIME",DS_UCID.getValue(i,"UCID_DATE") +""+ " [비대상]");
					}
				}
				
				res.param.addValue("DS_META",DS_META);
				
				getDuration(ucid_comma,res,sqlresult2);
				
			}
		} catch (SQLServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection(SQLParam sqlresult){
		Connection conn = null;
		
		  JediTransaction tran = JediTransactionManager.getJediTransaction();
		
		try {
			
			 String dsName = sqlresult.getDatasource();
			 String dbmsType = sqlresult.getDbmsType();
			 int fetchMax = sqlresult.getFetchMax();
			 String encoding = sqlresult.getEncoding();
			 String isolationLevel = sqlresult.getIsolationLevel();
			JediConnection jediCon =JediConnection.getInstance(dsName);
			jediCon.setDbmsType(dbmsType);
			jediCon.setFetchMax(fetchMax);
			jediCon.setEncoding(encoding);
			jediCon.setIsolationLevel(isolationLevel);
			conn = jediCon.getConnection();
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
		
	}

	private void getDuration(String ucid_comma,JediResponse res,SQLParam sqlresult) {
		// TODO Auto-generated method stub
		Connection m_conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		
		try{
			
			m_conn = GetUcidDuration.getConnection(sqlresult);
		
			String query = "SELECT DISTINCT B.UCID ,B.DURATION ,'' SUM_DURA FROM ms_stt_rslt_merge B WHERE 1=1 AND B.UCID = ?";
			StringBuilder newQuery = new StringBuilder();
			
			String[] ucid = ucid_comma.split(","); 
			
			for(int i =0 ; i< ucid.length; i++){
				newQuery.append(query);
				if(i < ucid.length-1) newQuery.append(" UNION ALL ");
			}
			
			//PreparedStatement 객체 생성
			pstmt = m_conn.prepareStatement(newQuery.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			//파라미터 할당하고 쿼리를 수행
			for(int i =0; i<  ucid.length; i++){
				pstmt.setString(i+1, ucid[i]);
			}
			rs = pstmt.executeQuery();
			double s_sum_dura = 0;
			
			ErrorLogger.debug("########"+DS_DURA.toString());
			
				while(rs.next()){
					int i = 1;
					String s_ucid = rs.getString("UCID");
					double totalSeconds = Double.parseDouble(rs.getString("DURATION"));
					s_sum_dura += totalSeconds;
					
					
					totalSeconds = Math.floor(totalSeconds / 100);
					String h = trimTrailingZeros(Math.floor(totalSeconds / 3600)+"");
					totalSeconds %= 3600;
					String m = trimTrailingZeros((Math.floor(totalSeconds / 60))+"");
					String s = trimTrailingZeros((Math.floor(totalSeconds % 60))+"");
					String opt = (h.length()==1?"0"+h:h) + ":" + (m.length()==1?"0"+m:m) + ":" + (s.length()==1?"0"+s:s);
					
					DS_DURA.setValue(DS_DURA.findRow("UCID",s_ucid),"DURATION",totalSeconds);
					opt = DS_UCID.getValue(DS_UCID.findRow("UCID",s_ucid),"UCID_DATE") + " (" + opt + ")"+ DS_UCID.getValue(DS_UCID.findRow("UCID",s_ucid),"ANL_STAT");
					DS_UCID.setValue(DS_UCID.findRow("UCID",s_ucid),"DATETIME",opt);
				}
				
				s_sum_dura = Math.floor(s_sum_dura / 100);
				String h2 = trimTrailingZeros(Math.floor(s_sum_dura / 3600)+"");
				s_sum_dura %= 3600;
				String m2 = trimTrailingZeros((Math.floor(s_sum_dura / 60))+"");
				String s2 = trimTrailingZeros((Math.floor(s_sum_dura % 60))+"");
				String opt2 = (h2.length()==1?"0"+h2:h2) + ":" + (m2.length()==1?"0"+m2:m2) + ":" + (s2.length()==1?"0"+s2:s2);
				
				
				DS_DURA.setValue(0,"SUM_DURA",opt2);
			
			res.param.addValue("DS_DURA",DS_DURA);
			res.param.addValue("DS_UCID",DS_UCID);
			
			
		} catch(Exception e){
			
				e.printStackTrace();
			
		} finally{
			try{
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(m_conn != null){
					m_conn.close();
				}
				
			}catch (Exception e){
				
			}
		}
		
		
		
	}
	private static String trimTrailingZeros(String number){
		if(!number.contains(".")){
			return number;
		}
		return number.replaceAll("\\.?0*$","");
	}
	
	private static boolean resultSetIsEmpty(ResultSet rs){
		try {
			int rsRows =0;
			if(rs.last()){
				rsRows=rs.getRow();
				
				if(rsRows == 0){
					return true;
				}
			}
			rs.beforeFirst();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
			// TODO Auto-generated catch block
		}
		
	
	}
	
	
};
