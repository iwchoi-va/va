package sens.src.estimate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.log.IVRLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.Code;
import com.locus.jedi.util.CodeUtil;
 
/*TM Core Interface하는 WebAction*/
public class TmInterWebaction extends Thread { 

    private String tm_url = "";
    
	public TmInterWebaction(String p_server_gb) {		

		ErrorLogger.debug("############################## TmInterfaceWebaction ############################");
		
		Code[] code = CodeUtil.getCodes("SYS020");
		
		for (int j = 0; code != null && j < code.length; j++) { // tmurl 정보 조회
			if (!"Y".equalsIgnoreCase(code[j].getUseYn())) {
				continue;
			}
		
			if(p_server_gb.equals(code[j].getEtc4())) tm_url = code[j].getEtc3();
		}
		
		tm_url += "tmMciService.do?_serviceName=?serviceName&_reqJson={?reqJson}";
	
	}
	
	//청약상태변경 -- core 인터페이스 연동
	public JSONObject set_LTTMINFR0096(String plan_number, String status_cd){
			ErrorLogger.debug("##########청약상태 변경 인터페이스 호출####");
			String v_url = tm_url;
			String v_reqJson = "";
			
			// MCI 호출 사용자 정보 셋팅
			v_url = v_url.replace("?serviceName", "LTTMINFR0096");
			v_reqJson += "\"planNumber\":\""+plan_number+"\"";
			v_reqJson += ",\"statusCode\":\""+status_cd+"\"";
			v_url = v_url.replace("?reqJson", v_reqJson);
			
			ErrorLogger.debug("#########인터페이스 호출 url 테스트!");
			ErrorLogger.debug("#####url :: " + v_url);
			
			IVRLogger.info("###url :: " + v_url);
		
			JSONObject jobj = openUrlConnection(v_url);
			//※ _ResultCode가 0이아니면 에러라는걸 보여주는 걸 추가할것
			
			return jobj;
	}
	
	// 사망동의서 우편발송 I/F 호출 -- core 인터페이스 연동
	public JSONObject set_LTTMINFR0124(JSONArray DS_LTTMINFR0124){

			String v_url = tm_url;
			String v_reqJson = "";
			
			String[] arr = {"issuBrkdClcd", "issReqDt", "rltNo", "issuClcd", "pydrRltNoClcd","rqsqCrno", "reqOgid", "prodCd", "prodSctrCd", "istpCd",
				    "actlCrno", "actlOgid", "spctEn", "newYn", "chtIssuClcd", "issuPlacClcd", "gdcMtcd", "ctraId", "insaId", "drvrId", "vcleNo",
				    "ctrStdt","ctrEndt","chtUsePupsCd", "printYear", "printTargetDateCls", "printTargetDate", "posmRvplCd", "loginClerk", 
				    "clerkDept", "printValidCls", "staffName", "staffPoint", "staffTelNo", "staffEmail", "printDt", "contractorEngName", "engAddress",
				    "pibojaEngName", "startYm", "endYm", "regYn", "clauOutpYn", "pkgYn", "mgntNo"};

			JSONArray jarray = new JSONArray();
			JSONObject jobj = new JSONObject();
			
			for(int i=0; i< DS_LTTMINFR0124.size(); i++){
				JSONObject temp = (JSONObject) DS_LTTMINFR0124.get(i);
				jobj = new JSONObject();
				
				for(int j=0; j< arr.length; j++){
					Object val = temp.get(arr[j]);

					if(val != null) jobj.put(arr[j], val);
					else jobj.put(arr[j], "");
				}
				
				jarray.add(jobj);
			}
			
			v_url = v_url.replace("?serviceName", "LTTMINFR0124");
			v_reqJson += "\"printDetailListNormal\":"+jarray.toString();
			v_url = v_url.replace("?reqJson", v_reqJson);
			
			ErrorLogger.debug("#####사망동의서 인터페이스 호출 url");
			ErrorLogger.debug("###url :: " + v_url);
			
			IVRLogger.info("###url :: " + v_url);
		
			JSONObject jsonobject = openUrlConnection(v_url);
			//※ _ResultCode가 0이아니면 에러라는걸 보여주는 걸 추가할것
			
			return jsonobject;
	}
	
	// 개인고객 정보활용동의등록
	public JSONObject set_LTTMINFR0127(ListParam DS_INSU_PLAN_MAST, String ent_dgn_no){
		
		String v_url = tm_url;
		String v_reqJson = "";
		
		v_url = v_url.replace("?serviceName", "LTTMINFR0127");
		v_reqJson += "\"ssno\":\""+ DS_INSU_PLAN_MAST.getParam(0).getValue("CON_P_PSN_NO")+"\"";
		v_reqJson += ",\"picuKncd\":\"04\"";
		v_reqJson += ",\"picuProcMediClcd\":\"02\"";
		v_reqJson += ",\"picuProcKeyVal\":\""+ ent_dgn_no+"\"";
		v_reqJson += ",\"picuDt\":\""+ DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE")+"\"";
		v_reqJson += ",\"ippsCrno\":\""+ DS_INSU_PLAN_MAST.getParam(0).getValue("TREATYCD")+"\"";
		
		v_url = v_url.replace("?reqJson", v_reqJson);
		
		ErrorLogger.debug("#####개인정보 활용등록 url ::");
		ErrorLogger.debug("#### url : " + v_url);
		
		IVRLogger.info("###url :: " + v_url);
		
		// ※ SSL 통신 완료 된 후에 개발하여 적용할 것!
		JSONObject jobj = openUrlConnection(v_url);
		
		return jobj;
		
	}
	
	// 청약일자변경
	public JSONObject set_LTTMINFR0073(ListParam DS_INSU_PLAN_MAST, String ent_dgn_no, String dgn_user_id){
		String v_url = tm_url;
		String v_reqJson = "";
		v_url = v_url.replace("?serviceName", "LTTMINFR0073");
		
		JSONArray jarray = new JSONArray();
		JSONObject jobj = new JSONObject();
		
		jobj.put("param0", ent_dgn_no);
		jobj.put("param1", "1");
		jobj.put("param2", dgn_user_id);
		jobj.put("param3", DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(0,4) + "-" + DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(4,6) + "-" + DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(6,8));
		jobj.put("param4", DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(0,4) + "-" + DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(4,6) + "-" + DS_INSU_PLAN_MAST.getParam(0).getValue("CON_APP_DATE").toString().substring(6,8));
		
		jarray.add(jobj);
		
		v_reqJson = "\"multiParamsList\":" + jarray.toString();
		v_url = v_url.replace("?reqJson", v_reqJson);
		
		ErrorLogger.debug("####청약일자 변경 url :: ");
		ErrorLogger.debug("###url : " + v_url);
		
		IVRLogger.info("###url :: " + v_url);
		
		// ※ SSL 통신 완료 된 후에 개발하여 적용할 것!
		JSONObject jsonobject = openUrlConnection(v_url);
		//※ _ResultCode가 0이아니면 에러라는걸 보여주는 걸 추가할것
		
		return jsonobject;
		
	}
	
	// 심사의견반영
	public JSONObject set_LTTMINFR0097(JSONArray DS_EST_HIST, String ent_dgn_no){
		String v_url = tm_url;
		String v_reqJson = "";
		
		JSONObject obj = (JSONObject) DS_EST_HIST.get(0);
		
		v_url = v_url.replace("?serviceName", "LTTMINFR0097");
		
		//v_reqJson += "\"planNumber\":\""+ ent_dgn_no+"\"";
		//v_reqJson += ",\"qcOpni\":\""+obj.get("QAOPINION")+"\"";
		//v_reqJson += ",\"qcOpni\":\""+obj.+"\"";
		
		
		JSONObject jobj = new JSONObject();
		
		jobj.put("planNumber", ent_dgn_no);
		jobj.put("qcOpni", obj.get("QAOPINION"));
		
		try {
			v_url = v_url.replace("{?reqJson}", URLEncoder.encode(jobj.toString(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IVRLogger.info("###url :: " + v_url);
		
		
		// ※ SSL 통신 완료 된 후에 개발하여 적용할 것!
		JSONObject jsonobject = openUrlConnection(v_url);
		
		
		return jsonobject;
		
	}
	
	// 사망담보 정보 조회
	public JSONObject get_LTTMINFR0128(String service_name, String prod_cd, String dmbo_cd, String attr_type){
		JSONArray DS_POA_VALUE = new JSONArray();
		
		String v_url = tm_url;
		String v_reqJson = "";
		
		v_url = v_url.replace("?serviceName", "LTTMINFR0128");
		
		v_reqJson += "\"prodCd\":\""+prod_cd+"\"";
		v_reqJson += ",\"attributeTypeCode\":\""+attr_type+"\"";
		
		JSONArray dmbo_cd_json = new JSONArray();
		
		String[] tmp_dmbo = dmbo_cd.split(",");
		
		ErrorLogger.debug("######dmbo !! " + dmbo_cd); 
		
		for(int i =0; i< tmp_dmbo.length;i++){
			JSONObject jobj = new JSONObject();
			
			jobj.put("covrCd", tmp_dmbo[i]);
			jobj.put("attributeTypeCode", "");
			jobj.put("attributeTypeValue", "");
			
			dmbo_cd_json.add(jobj);
		}
		
		v_reqJson += ",\"reqPdCvrAttTypeVOListNormal\":" + dmbo_cd_json.toString();
		
		v_url = v_url.replace("?reqJson", v_reqJson);
		
		IVRLogger.info("###v_url :: " + v_url);
		
		ErrorLogger.debug("#########인터페이스 호출 url 테스트!");
		ErrorLogger.debug("#####url :: " + v_url);
		
		JSONObject jobj = openUrlConnection(v_url);
		//DS_POA_VALUE = (JSONArray) jobj.get("pdCvrAttTypeVOList");
		
		/***********여기부터는 테스틑위해 개발하는 영역임 ******************************/
		/* JSONParser parser = new JSONParser();
		
		try{
			Object obj = parser.parse(new FileReader("C:\\va_workspace\\MSENS_OB_FINAL\\webapps\\temp\\poaTest.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			DS_POA_VALUE = (JSONArray) jsonObject.get("pdCvrAttTypeVOList");
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		return jobj;
	}
	
	// aigen url connection 부분
	private JSONObject openUrlConnection(String p_url){
		JSONObject result = new JSONObject();
		URL url = null;
		BufferedReader reader = null;
		HttpsURLConnection conn = null;
		
		try {
			url = new URL(p_url);
			conn = (HttpsURLConnection) url.openConnection();
			
			System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
			
			//conn.setRequestMethod("POST");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			StringBuffer buffer = new StringBuffer();
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			
			Object obj = JSONValue.parse(buffer.toString());
			
			result =  (JSONObject) obj;
			
			
		} catch (Exception e) {
			//e.printStackTrace();
			ErrorLogger.error("###tminterwebation connect error : " + e.getMessage());
		} finally {
			try {
				if (reader != null) reader.close();
				if (conn != null) conn.disconnect();
			} catch (Exception e1) {
			}
		}
		
		
		return result;
		
	}
	
	
};
