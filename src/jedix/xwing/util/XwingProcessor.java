package jedix.xwing.util;

import java.io.Reader;
import java.sql.Clob;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;


/**
 * Jedi의 Param,ListParam객체와 MiXmlData,DataSet간의 매핑을 지원하는 클래스ㅎ
 */
public class XwingProcessor {
	public static Param parseRequest(JediRequest req,Param param) {
		try{
			if(req == null) return null;
			if(param == null) param = new Param();

			/* Xwing Request 전체 내용 */
  		HttpServletRequest hReq = req.getHttpServletRequest();
		ErrorLogger.debug("111111>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>XWing Request headers <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		for(Enumeration headEnum = hReq.getHeaderNames(); headEnum.hasMoreElements();){
			String headName = headEnum.nextElement().toString();
			ErrorLogger.debug( headName);
			for(Enumeration headEnum2 = hReq.getHeaders(headName); headEnum2.hasMoreElements();){
				ErrorLogger.debug("\t"+ headEnum2.nextElement().toString());
			}
		}
		ErrorLogger.debug(" Method : "+ hReq.getMethod() );

		ErrorLogger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>XWing Request Atts <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		for(Enumeration attEnum = hReq.getAttributeNames(); attEnum.hasMoreElements();){
			String attName = attEnum.nextElement().toString();
			ErrorLogger.debug("attName : " + attName  + ", attValue : "+ hReq.getAttribute(attName).toString());
		}

		ErrorLogger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>XWing Request Params <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		for(Enumeration paramEnum = hReq.getParameterNames(); paramEnum.hasMoreElements();){
			String paramName = paramEnum.nextElement().toString();
			ErrorLogger.debug("paramName : " + paramName  + ", paramValue  : "+ 	hReq.getParameter(paramName).toString()) ;
			param.addValue(paramName, hReq.getParameter(paramName).toString());
		}

		ErrorLogger.debug("----------------------------------------- end of req ----------------------------------------");

		//이곳에서 httpReuest에서 Xwing Request를 읽어서 jediReq.param으로 변환 한다.
		/*
		 * TODO  _dataset parameter value parsing ( )
		 * 1.comma 구분자를 이용하여 데이터셋 이름을 얻기.  -ok
		 * 2.key에 따른 dataset 객체 get.  -ok
		 * 3.get 한 객체를 listParam으로 변환. -ok
		 * 4.sql 여러 개 실행 . -ok 
		 * 5. dataset of response value(Object) : key(result name) {[columns], [rowss]} - ok 
		 */
		String _dataset = param.getString("_dataset",null);
		String[] _datasetList = null;

ErrorLogger.debug("★★★★★★★★★★★★★★★★★★★★★★  START ★★★★★★★★★★★★★★★★★★★★★★★★★★");
		
		if( _dataset != null) {
			
ErrorLogger.debug("1: 데이타셋 : " + _dataset);
			_datasetList = _dataset.split(",");
			
ErrorLogger.debug("2: 데이타셋 갯수 :" + _datasetList.length);
			



			for( int k=0; k < _datasetList.length; k++){
				ErrorLogger.debug( k + " --> DataSet Check : " + param.getString(_datasetList[0], "{}"));
				
				JSONObject dataSets = new JSONObject(param.getString(_datasetList[k], "{}"));
			//	for(Iterator iter = dataSets.keys() ; iter.hasNext(); ){
					//String listParamName = iter.next().toString();
					//JSONObject aListParam = dataSets.getJSONObject(listParamName);
				
					JSONArray colInfo = dataSets.getJSONArray("column");
					String[] columns= new String[colInfo.length()];
					for(int i=0; i < colInfo.length(); i++){
						//JSONObject aCol = colInfo.getJSONObject(i);
						columns[i] = colInfo.get(i).toString();
					}
					
					ListParam listParam = new ListParam(columns);
					JSONArray record = dataSets.getJSONArray("record");
					
					for(int i=0; i < record.length(); i++){
						
						int row = listParam.createRow();
						JSONArray aRec = record.getJSONArray(i);
						
						for(int j=0; j < columns.length; j++){
							
//							ErrorLogger.debug( j +  " row : " + row );
//							ErrorLogger.debug( j + " columns : " + columns[j]);
//							ErrorLogger.debug( j + " aRec.get : "+ aRec.get(j));
							listParam.setValue(row,columns[j],aRec.get(j));
							
						}
					}
					
					ErrorLogger.debug( "listParam : " + listParam);
					
					param.addValue(_datasetList[k], listParam);
					
			//	}//end of for
			}
		}

		param.remove("_dataSet");
		ErrorLogger.debug("Xwing Param : " + param);	
		return param;
		}catch(Exception e){
			e.printStackTrace();
			ErrorLogger.debug(e);
			return param;
		}
	}

	public static JSONObject sendResponse(JediResponse jediRes,Param param){
			JSONObject res = new JSONObject();
		try{
			if(jediRes==null) return null;
			if(param == null){
				param = jediRes.param;
			}
			//이곳에서  Param을 Xwing response 로 만들어 준다.
			ErrorLogger.debug(param);
			JSONObject jsonDs = new JSONObject();
			
			String code=jediRes.getResultCode();
			if(code==null || "".equals(code.trim()))
				code="UnknownError";
			
			String message = jediRes.getResultMessage();
			if(message == null || "".equals(message.trim()))
				message = "no message";
			
			String logTrace = jediRes.getLogTrace();
			if(logTrace == null || "".equals(logTrace.trim())){
				logTrace = "no trace";
			}

			res.put("Error",jediRes.isError() ? "1":"0");
			res.put("ResultCode",code);
			res.put("ResultMessage",message);			
			
			String[] paramKeys = param.keys();
			for(int i=0; i <paramKeys.length; i++){
				Object obj = param.getValue(paramKeys[i]);
				if(obj instanceof SQLParam){
					SQLParam sqlParam = (SQLParam)obj;
					String resultName = sqlParam.getResultName();
					if(resultName != null ){
						ListParam sqlList = sqlParam.getListParam(resultName);
						if( sqlList == null) continue;
						JSONObject aDs = listParam2Json(sqlList);
						aDs.put("paging", sqlParam.isPaging());
						aDs.put("count", sqlParam.getCount());
						aDs.put("page", sqlParam.getPage());
						aDs.put("totalpage", sqlParam.getTotalPage());
						aDs.put("totalcount", sqlParam.getTotalCount());
						//jsonDs.put(resultName.toUpperCase(), aDs);
						res.put(resultName.toUpperCase(), aDs);
					}else{
						String[] sqlParamKeys =sqlParam.keys();
						JSONObject sqlJson = new JSONObject();
						for(int j=0; j <sqlParamKeys.length; j++){
							sqlJson.put(sqlParamKeys[j], sqlParam.getString(sqlParamKeys[j]))	;
							res.put(sqlParamKeys[j], sqlParam.getString(sqlParamKeys[j]))	;							
						}
						res.put(sqlParam.getSqlName(), sqlJson);
					}
					
				}else	if(obj instanceof ListParam){
					JSONObject aDs = listParam2Json(param.getListParam(paramKeys[i]));
					//jsonDs.put(paramKeys[i], aDs);
					
					res.put(paramKeys[i], aDs);
				}else{
					res.put(paramKeys[i], param.getString(paramKeys[i]));
				}
			}
			//res.put("params", jsonParams);
			//res.put("_dataset", jsonDs);
			return res;
		}catch(Exception e){
			e.printStackTrace();
			return res;
		}
	}
	
	private static JSONObject listParam2Json(ListParam listParam) throws JSONException{
		JSONObject resJson = new JSONObject();
		JSONArray colArray = new JSONArray();
		JSONArray rowArray = new JSONArray();
		String[] columns = listParam.getColumns();
		String[] colObj = new String[3];
		short columnType;
		Object obj;
		
		for (int j = 0; j < columns.length; j++) {
			colArray.put(columns[j]);
		}

		int n_arr_length = 1024;
		int row;
		Clob clob;
		StringBuffer buffer;
        char charBuffer[];
        int read;
        Reader reader;
        
		for (int j = 0; j < listParam.rowSize(); j++) {
			Param aParam = listParam.getParam(j);
			JSONArray aRow = new JSONArray();
			
			for (int k = 0; k < columns.length; k++) {
				obj = aParam.getValue(columns[k]);
                
                if (obj != null && (obj instanceof Clob)) {
                    clob = (Clob)obj;
                    buffer = new StringBuffer();
                    charBuffer = new char[n_arr_length];
                    read = 0;
                    reader = null;
                    
                    try  {
                        reader = clob.getCharacterStream();
                        
                        while((read = reader.read(charBuffer, 0, 1024)) != -1) 
                            buffer.append(charBuffer, 0, read);
                        
                        reader.close();
                        reader = null;
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    } finally {
                    	try {
                    		if(reader != null) reader.close();
                    	} catch(Exception ex) {}
                    }
                    
                    aRow.put(buffer.toString());
                    
                    /*
                } else if (listParam.getColType(j) == 2) {
                	if (aParam.getString(columns[k]) != null && 
                		!aParam.getString(columns[k]).equals("")) {
                		
						aRow.put(aParam.getString(columns[k]));
					}
				*/
                } else {
                    
                	aRow.put(aParam.getString(columns[k], ""));
                }
			}
			rowArray.put( aRow);
		}
		resJson.put("column", colArray);
		resJson.put("record", rowArray);
		return resJson;
	}
	

};