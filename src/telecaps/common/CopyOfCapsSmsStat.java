package telecaps.common;

import generated.Data;
import generated.Entity;
import hli_common_lib.EsbHeader;
import hli_common_lib.ObjectFactory;
import hliesbclient.hliesbclient_if.HLIEsbClient_WSExport_HLIEsbClient_IFHttpServiceStub;
import hliesbclient.hliesbclient_if.Operation;
import hliesbclient.hliesbclient_if.OperationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.httpclient.Header;

import com.locus.jedi.biz.JediTransaction;
import com.locus.jedi.biz.JediTransactionManager;
import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.service.sql.SQLParam;
import com.locus.jedi.service.sql.SQLServiceManager;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.util.EnvironmentXmlDAO;

//15.08.07 알람등급추가<INFO등급>

public class CopyOfCapsSmsStat extends TimerTask {
	String[] v_device_id = null;
	int[] v_device_seq = null;
	String[] v_sms_msg = null;
	String[] v_hp = null;
	String[] alarm_gd = null;

	public CopyOfCapsSmsStat() {
	}

	public void run() {
		//Logger.write("CapsSmsStat", Thread.currentThread().getName(), "CapsSmsStat 시작", "", "", "", "");

		JediTransaction tran = JediTransactionManager.getJediTransaction();
		SQLParam sqlParam = null;
		SQLParam sqlResult = null;
		ListParam SmsListValue = null;

		try {
			sqlParam = new SQLParam();
			sqlParam.clear();
			sqlParam.setSqlName("telecaps.sql.smslist.select");
			sqlResult = SQLServiceManager.getInstance().execute(sqlParam);

			//ErrorLogger.debug("sqlResult.getCount " + sqlResult.getCount());

			if (sqlResult.getCount() > 0) {
				v_device_id = new String[sqlResult.getCount()];
				v_device_seq = new int[sqlResult.getCount()];
				v_sms_msg = new String[sqlResult.getCount()];
				v_hp = new String[sqlResult.getCount()];
				alarm_gd = new String[sqlResult.getCount()];

				SmsListValue = new ListParam(new String[] { "device_id", "device_seq", "sms_msg", "hp", "alarm_grade" });

				for (int i = 0; i < sqlResult.getCount(); i++) {
					v_device_id[i] = sqlResult.getListParam("telecaps.sql.smslist.select").getParam(i)
							.getString("DEVICE_ID");
					v_device_seq[i] = sqlResult.getListParam("telecaps.sql.smslist.select").getParam(i)
							.getInt("DEVICE_SEQ");
					v_sms_msg[i] = sqlResult.getListParam("telecaps.sql.smslist.select").getParam(i)
							.getString("SMS_MSG");
					v_hp[i] = sqlResult.getListParam("telecaps.sql.smslist.select").getParam(i).getString("HP");
					alarm_gd[i] = sqlResult.getListParam("telecaps.sql.smslist.select").getParam(i)
							.getString("ALARM_GRADE");

					//ErrorLogger.debug("sms 전송DEVICE_ID:" + v_device_id[i] + "|DEVICE_SEQ:" + v_device_seq[i]
					//		+ "|SMS_MSG:" + v_sms_msg[i] + "|HP:" + v_hp[i]);
					//Logger.write("pingchekdb", Thread.currentThread().getName(), "ping check 시작", "", "", "", "");

					SmsListValue.addRow(new Object[] { v_device_id[i], v_device_seq[i], v_sms_msg[i], v_hp[i],
							alarm_gd[i] });

				}

				/*
				 * DB INSERT SMS 처리 sqlParam.clear();
				 * sqlParam.setSqlName("telecaps.sql.smslist.smssystem.insert");
				 * sqlParam
				 * .addValue("telecaps.sql.smslist.smssystem.insert",SmsListValue
				 * ); SQLServiceManager.getInstance().execute(sqlParam);
				 * 
				 * tran.commit();
				 */

				// 한화생명 ESB SMS 처리
				for (int i = 0; i < SmsListValue.rowSize(); i++) {
					String v_messages = (String) SmsListValue.getValue(i, "sms_msg");
					byte[] v_arr = v_messages.getBytes();

					if (v_arr.length > 78) {
						EmsEsbService(v_messages, (String) SmsListValue.getValue(i, "hp"));
						Thread.sleep(5000);

						/*
						 * int all_sms_cnt = (v_arr.length / 70) + 1; int
						 * sms_cnt = 0; int byte_cnt = 0; int string_num = 0;
						 * 
						 * 
						 * for (int j = 0; j < v_messages.length(); j++) {
						 * String v_char = v_messages.substring(j, j + 1);
						 * 
						 * if (v_char.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) { byte_cnt +=
						 * 2; } else { byte_cnt += 1; }
						 * 
						 * if (byte_cnt > 70 || j == v_messages.length() - 1) {
						 * String v_temp = "[" + (sms_cnt + 1) + "/" +
						 * all_sms_cnt + "] "; v_temp +=
						 * v_messages.substring(string_num, j + 1);
						 * 
						 * SmsEsbService(v_temp,(String)
						 * SmsListValue.getValue(i, "hp")); Thread.sleep(5000);
						 * 
						 * byte_cnt = 0; string_num = j + 1; sms_cnt++; } }
						 */

						/*
						 * for (int j = 0; j < all_sms_cnt; j++) { String v_temp
						 * = "[" + (j+1) + "/" + all_sms_cnt +"] ";
						 * 
						 * if (j == (v_arr.length / 72)) { v_temp +=
						 * getCalcStr(v_messages,72 * j, v_arr.length); } else {
						 * v_temp += getCalcStr(v_messages,72 * j, 72 * (j +
						 * 1)); } SmsEsbService(v_temp, (String)
						 * SmsListValue.getValue(i, "hp")); Thread.sleep(3000);
						 * }
						 */

						/*
						 * int all_sms_cnt = (v_messages.length()/55)+1;
						 * 
						 * for(int j=0; j<all_sms_cnt; j++){ String v_temp = "["
						 * + (j+1) + "/" + all_sms_cnt +"] ";
						 * 
						 * if((j+1) == all_sms_cnt){ v_temp +=
						 * v_messages.substring(55*j); }else{ v_temp +=
						 * v_messages.substring(55*j, 55*(j+1)); }
						 * SmsEsbService(v_temp, (String)
						 * SmsListValue.getValue(i, "hp")); }
						 */
					} else {
						SmsEsbService(v_messages, (String) SmsListValue.getValue(i, "hp"));
						Thread.sleep(5000);
					}
				}
		

				sqlParam.clear();
				sqlParam.setSqlName("telecaps.sql.smslist.send.update");
				sqlParam.addValue("telecaps.sql.smslist.send.update", SmsListValue);
				SQLServiceManager.getInstance().execute(sqlParam);

				tran.commit();

				sqlParam.clear();
				sqlParam.setSqlName("telecaps.sql.smslist.send.update.clear");
				sqlParam.addValue("telecaps.sql.smslist.send.update.clear", SmsListValue);
				SQLServiceManager.getInstance().execute(sqlParam);

				tran.commit();

				for (int i = 0; i < SmsListValue.rowSize(); i++) {
					// ErrorLogger.error("alarm_grade" + (String)
					// SmsListValue.getValue(i, "alarm_grade"));

					if ("INFO".equalsIgnoreCase((String) SmsListValue.getValue(i, "alarm_grade"))) {
						// ErrorLogger.error("device_seq" +
						// SmsListValue.getValue(i, "device_seq"));
						sqlParam.clear();
						sqlParam.setSqlName("telecaps.sql.smslist.clear.info"); // sms보내고
																				// 장애해제처리.
						sqlParam.addValue("device_seq", SmsListValue.getValue(i, "device_seq"));
						SQLServiceManager.getInstance().execute(sqlParam);

						tran.commit();
					}
				}

				//Logger.write("CapsSmsStat", Thread.currentThread().getName(), "CapsSmsStat 종료", "", "", "", "");
			}

		} catch (Exception ex2) {
			tran.rollback();
			ErrorLogger.error(ex2);
		}
	}

	public String getCalcStr(String str, int sLoc, int eLoc) {
		byte[] bystStr;
		String rltStr = str;
		try {
			bystStr = str.getBytes();
			int bytelen = bystStr.length;
			if (bytelen >= eLoc) {
				rltStr = new String(bystStr, sLoc, eLoc - sLoc);
			}
		} catch (Exception e) {
			return rltStr;
		}
		return rltStr;
	}

	public void EmsEsbService(String v_msg, String v_hp) {
		try {
			String endpoint = EnvironmentXmlDAO.getInstance().getProperty("EsbUrl");

			HLIEsbClient_WSExport_HLIEsbClient_IFHttpServiceStub stub = new HLIEsbClient_WSExport_HLIEsbClient_IFHttpServiceStub(
					endpoint);

			// 공통 Header 정보 세팅
			ObjectFactory OFHeader = new ObjectFactory();
			EsbHeader esbHeader = OFHeader.createEsbHeader();
			esbHeader.setHeaderVersion("1.0"); // EsbHeader Version
			esbHeader.setServiceId("SO00556"); // Service ID
			esbHeader.setProgramId("cs_mon_mms"); // Program ID

			// HTTP HEADER 설정 - CEM(사용자 체감 모니터링) 연계
			List listHttpHeader = new ArrayList();
			Header httpHeader = new Header();
			httpHeader.setName("ServiceId");
			httpHeader.setValue("SO00556");
			listHttpHeader.add(httpHeader);
			ServiceClient client = stub._getServiceClient();
			client.getOptions().setProperty(HTTPConstants.HTTP_HEADERS, listHttpHeader);

			// 호출 TIMEOUT 설정 (ms)
			client.getOptions().setTimeOutInMilliSeconds(60000L);

			// ESB Operation 세팅
			hliesbclient.hliesbclient_if.ObjectFactory OFOperation = new hliesbclient.hliesbclient_if.ObjectFactory();
			Operation op = OFOperation.createOperation();

			// ESB Data 세팅
			generated.ObjectFactory obf = new generated.ObjectFactory();
			Data data = obf.createData();

			Entity entity = obf.createEntity();
			entity.setName("AA_LON_001");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("1"));
			entity.getVal().add("1");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_LON_002");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("1"));
			entity.getVal().add("0");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_001");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("1180026");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_002");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("정경희");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_003");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("금융운영팀");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_004");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("00213");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_005");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("한화생명");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_006");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("15886363");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_007");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_008");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("CC1004");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_009");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add(v_hp.replaceAll("-", ""));
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_010");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("시스템관리자");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_011");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_012");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("H-V.SYS알람메시지");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_013");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add(v_msg);
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_014");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_015");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_016");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_017");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_018");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_019");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_020");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_021");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_022");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_023");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_024");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("");
			data.getEntity().add(entity);

			entity = obf.createEntity();
			entity.setName("AA_STR_025");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3"));
			entity.getVal().add("N");
			data.getEntity().add(entity);

			op.setData(data);

			ArrayList<String> entityList = new ArrayList<String>();

			System.out.println("===============  Request DATA ===============");
			for (int i = 0; i < op.getData().getEntity().size(); i++) {
				System.out.println(op.getData().getEntity().get(i).getName() + " : "
						+ op.getData().getEntity().get(i).getVal());
			}

			// ESB 호출 및 응답값 Return
			OperationResponse response = stub.operation(op, esbHeader);

			// 응답 EsbHeader 설정
			HashMap resEsbHeader = new HashMap<String, String>();

			MessageContext _returnMessageContext = client.getServiceContext().getLastOperationContext()
					.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
			SOAPHeader resSoapHeader = _returnEnv.getHeader();

			AXIOMXPath xpathResEsbHeader = new AXIOMXPath("hl:EsbHeader");
			xpathResEsbHeader.addNamespace("hl", "http://HLI_Common_LIB");
			OMElement omResEsbHeader = (OMElement) xpathResEsbHeader.selectSingleNode(resSoapHeader);
			Iterator<OMElement> itrResEsbHeader = omResEsbHeader.getChildren();

			while (itrResEsbHeader.hasNext()) {
				OMElement omChild = itrResEsbHeader.next();
				resEsbHeader.put(omChild.getLocalName(), omChild.getText());
			}

			System.out.println("\n===============  Response DATA ===============");

			int entitySize = response.getData().getEntity().size();

			Map responseMap = new HashMap<String, ArrayList<String>>();

			System.out.println("\n===============  ESB Error Info ===============");
			if (response != null) {
				String errorCode = (String) resEsbHeader.get("ErrorCode");
				String errorMessage = (String) resEsbHeader.get("ErrorMessage");

				if (errorCode.equals("0")) { // ESB 호출 성공
					System.out.println("==== ESB SUCCESS   ............");
					System.out.println("ReturnCode   : " + errorCode);// '0' 정상
					System.out.println("ErrorCode    : " + errorCode);// '0' 정상
					System.out.println("ErrorMessage : " + errorMessage);

					String[] entityNameList = new String[entitySize];
					for (int i = 0; i < entitySize; i++) {
						String entityName = response.getData().getEntity().get(i).getName();
						entityNameList[i] = entityName;
						ArrayList valList = (ArrayList) response.getData().getEntity().get(i).getVal();
						responseMap.put(entityName, valList);
					}

					System.out.println("\n===============  Response Data ===============");

					for (int j = 0; j < entitySize; j++) {
						System.out.print(entityNameList[j] + " : ");
						System.out.println(responseMap.get(entityNameList[j]));
					}

				} else { // ESB 호출 실패
					System.out.println("==== ESB FAIL   ............");
					System.out.println("ReturnCode   : " + errorCode);
					System.out.println("ErrorCode    : " + errorCode);
					System.out.println("ErrorMessage : " + errorMessage);
				}
			}
		} catch (Exception e) {
			ErrorLogger.error(e);
		}
	}

	public void SmsEsbService(String v_msg, String v_hp) {
		try {
			String endpoint = EnvironmentXmlDAO.getInstance().getProperty("EsbUrl");

			HLIEsbClient_WSExport_HLIEsbClient_IFHttpServiceStub stub = new HLIEsbClient_WSExport_HLIEsbClient_IFHttpServiceStub(
					endpoint);

			// 공통 Header 정보 세팅
			ObjectFactory OFHeader = new ObjectFactory();
			EsbHeader esbHeader = OFHeader.createEsbHeader();
			esbHeader.setHeaderVersion("1.0"); // EsbHeader Version
			esbHeader.setServiceId("SO00719"); // Service ID
			esbHeader.setProgramId("cs_mon_sms"); // Program ID

			// HTTP HEADER 설정 - CEM(사용자 체감 모니터링) 연계
			List listHttpHeader = new ArrayList();
			Header httpHeader = new Header();
			httpHeader.setName("ServiceId");
			httpHeader.setValue("SO00719");
			listHttpHeader.add(httpHeader);
			ServiceClient client = stub._getServiceClient();
			client.getOptions().setProperty(HTTPConstants.HTTP_HEADERS, listHttpHeader);

			// 호출 TIMEOUT 설정 (ms)
			client.getOptions().setTimeOutInMilliSeconds(60000L);

			// ESB Operation 세팅
			hliesbclient.hliesbclient_if.ObjectFactory OFOperation = new hliesbclient.hliesbclient_if.ObjectFactory();
			Operation op = OFOperation.createOperation();

			// ESB Data 세팅
			generated.ObjectFactory obf = new generated.ObjectFactory();
			Data data = obf.createData();

			// 전송요청건수
			Entity entity = obf.createEntity();
			entity.setName("AA_LON_001");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("1");
			data.getEntity().add(entity);

			// 보낼메시지
			entity = obf.createEntity();
			entity.setName("AA_STR_001");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add(v_msg);
			data.getEntity().add(entity);

			// 송신자사번
			entity = obf.createEntity();
			entity.setName("AA_STR_002");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("1180026");
			data.getEntity().add(entity);

			// 송신자이름
			entity = obf.createEntity();
			entity.setName("AA_STR_003");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("정경희");
			data.getEntity().add(entity);

			// 송신자부서이름
			entity = obf.createEntity();
			entity.setName("AA_STR_004");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("금융운영팀");
			data.getEntity().add(entity);

			// 송신자부서코드
			entity = obf.createEntity();
			entity.setName("AA_STR_005");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("00213");
			data.getEntity().add(entity);

			// 송신자부서소속국
			entity = obf.createEntity();
			entity.setName("AA_STR_006");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("한화생명");
			data.getEntity().add(entity);

			// 송신자전화번호
			entity = obf.createEntity();
			entity.setName("AA_STR_007");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("15886363");
			data.getEntity().add(entity);

			// 송신자이메일주소("")
			entity = obf.createEntity();
			entity.setName("AA_STR_008");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("");
			data.getEntity().add(entity);

			// 폼ID : 메일일 경우("")
			entity = obf.createEntity();
			entity.setName("AA_STR_009");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("");
			data.getEntity().add(entity);
			op.setData(data);

			// umsJobCode : ("KM1001")
			entity = obf.createEntity();
			entity.setName("AA_STR_010");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("CC1003");
			data.getEntity().add(entity);

			// PGm ID("SVuvcom101in")
			entity = obf.createEntity();
			entity.setName("AA_STR_011");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("SVuvcom101in");
			data.getEntity().add(entity);

			// 주석("")
			entity = obf.createEntity();
			entity.setName("AA_STR_012");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("");
			data.getEntity().add(entity);

			// 예약시간
			entity = obf.createEntity();
			entity.setName("AA_STR_013");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("");
			data.getEntity().add(entity);

			// 수신자정보
			entity = obf.createEntity();
			entity.setName("AA_STR_014");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add(v_hp.replaceAll("-", ""));
			data.getEntity().add(entity);

			// Job Title("")
			entity = obf.createEntity();
			entity.setName("AA_STR_015");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("H-V.SYS알람메시지");
			data.getEntity().add(entity);

			// 문자메세지 수신확인
			entity = obf.createEntity();
			entity.setName("AA_STR_016");
			entity.setType(org.apache.axis2.databinding.utils.ConverterUtil.convertToInteger("3")); // Integer
																									// Type
																									// 값을
																									// 넘기는
																									// 경우
																									// "1"지정
			entity.getVal().add("");
			data.getEntity().add(entity);

			op.setData(data);

			ArrayList<String> entityList = new ArrayList<String>();

			System.out.println("===============  Request DATA ===============");
			for (int i = 0; i < op.getData().getEntity().size(); i++) {
				System.out.println(op.getData().getEntity().get(i).getName() + " : "
						+ op.getData().getEntity().get(i).getVal());
			}

			// ESB 호출 및 응답값 Return
			OperationResponse response = stub.operation(op, esbHeader);

			// 응답 EsbHeader 설정
			HashMap resEsbHeader = new HashMap<String, String>();

			MessageContext _returnMessageContext = client.getServiceContext().getLastOperationContext()
					.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
			SOAPHeader resSoapHeader = _returnEnv.getHeader();

			AXIOMXPath xpathResEsbHeader = new AXIOMXPath("hl:EsbHeader");
			xpathResEsbHeader.addNamespace("hl", "http://HLI_Common_LIB");
			OMElement omResEsbHeader = (OMElement) xpathResEsbHeader.selectSingleNode(resSoapHeader);
			Iterator<OMElement> itrResEsbHeader = omResEsbHeader.getChildren();

			while (itrResEsbHeader.hasNext()) {
				OMElement omChild = itrResEsbHeader.next();
				resEsbHeader.put(omChild.getLocalName(), omChild.getText());
			}

			System.out.println("\n===============  Response DATA ===============");

			int entitySize = response.getData().getEntity().size();

			Map responseMap = new HashMap<String, ArrayList<String>>();

			System.out.println("\n===============  ESB Error Info ===============");
			if (response != null) {
				String errorCode = (String) resEsbHeader.get("ErrorCode");
				String errorMessage = (String) resEsbHeader.get("ErrorMessage");

				if (errorCode.equals("0")) { // ESB 호출 성공
					System.out.println("==== ESB SUCCESS   ............");
					System.out.println("ReturnCode   : " + errorCode);// '0' 정상
					System.out.println("ErrorCode    : " + errorCode);// '0' 정상
					System.out.println("ErrorMessage : " + errorMessage);

					String[] entityNameList = new String[entitySize];
					for (int i = 0; i < entitySize; i++) {
						String entityName = response.getData().getEntity().get(i).getName();
						entityNameList[i] = entityName;
						ArrayList valList = (ArrayList) response.getData().getEntity().get(i).getVal();
						responseMap.put(entityName, valList);
					}

					System.out.println("\n===============  Response Data ===============");

					for (int j = 0; j < entitySize; j++) {
						System.out.print(entityNameList[j] + " : ");
						System.out.println(responseMap.get(entityNameList[j]));
					}

				} else { // ESB 호출 실패
					System.out.println("==== ESB FAIL   ............");
					System.out.println("ReturnCode   : " + errorCode);
					System.out.println("ErrorCode    : " + errorCode);
					System.out.println("ErrorMessage : " + errorMessage);
				}
			}
		} catch (Exception e) {
			ErrorLogger.error(e);
		}
	}
}
