package cs.com.webaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jedix.xwing.action.XwingWebAction;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.locus.jedi.log.ErrorLogger;
import com.locus.jedi.transfer.ListParam;
import com.locus.jedi.transfer.Param;
import com.locus.jedi.waf.action.WebActionException;
import com.locus.jedi.waf.controller.JediRequest;
import com.locus.jedi.waf.controller.JediResponse;

import cs.com.util.XmlUtil;

public class XCronControlWebAction extends XwingWebAction {
	public void perform(JediRequest req, JediResponse res) throws WebActionException{
		try{
			processRequest(req, res);
		}catch(Exception e){
			throw new WebActionException("XCronControlWebAction : "+e.getMessage(),e);
		}
	}
	
	public void processRequest(JediRequest req, JediResponse res) {
		Param param = req.param;
		
		// XCron 서버 통신 정보
		String ip = valCheck(param.getString("SERVER_IP", "127.0.0.1"));
		int port = param.getInt("SERVER_PORT", 5292);
		int timeout = param.getInt("SERVER_TIMEOUT", 60000);
		
		// XCron Operation 정보
		String cmd = valCheck(param.getString("cmd", ""));
		String type = valCheck(param.getString("type", ""));
		String service = valCheck(param.getString("service", ""));
		String operation = valCheck(param.getString("operation", ""));
		
		ListParam listParam = req.param.getListParam("XCRON_REQUEST");
		
		if (listParam != null && listParam.rowSize() > 0) {
			param = listParam.getParam(0);
		} else {
			param = null;			
		}
		
		
		
		String prolog = "<?xml version='1.0' encoding='euc-kr'?>";
		StringBuffer buffer = new StringBuffer(prolog);
		
		buffer.append("<ControlConfig>");
		buffer.append("<Cmd Name=\"").append(cmd).append("\" ");
		
		if ("getinfo".equals(cmd) || "setinfo".equals(cmd)) {
			buffer.append("Type=\"").append(type).append("\" ");
		}
		
		if ("setinfo".equals(cmd)) {
			buffer.append("Operation=\"").append(operation).append("\" ");
		}
		
		buffer.append("Service=\"").append(service).append("\"/>");
		
		buffer.append(getXmlFromParam(param));

		buffer.append("</ControlConfig>");
		ErrorLogger.debug(buffer.toString());
		
		PrintWriter bos = null;
		BufferedReader bis = null;
		Socket socket = null;
		
		try {			
			socket = new Socket(ip, port);
			socket.setSoTimeout(timeout);
			
			bos = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			bis = new BufferedReader(new InputStreamReader(socket.getInputStream(), "EUC-KR"));
			/*2018.08.23  변경*/
		//String buf=ESAPI.encoder().encodeForHTML(buffer.toString());
			
			bos.write(buffer.toString());
			bos.flush();
			
			StringBuffer sb = new StringBuffer();
			int ch = 0;
			
			while((ch = bis.read()) != -1) {
				sb.append((char)ch);
			}
			
			String response = sb.toString();
			
			ErrorLogger.debug("Received XML Length<" + response.length() + "> ");
			
			Element root = XmlUtil.loadXmlDocument(new InputSource(new StringReader(response)));
			processResponse(root, res);
		} catch(Exception ex) {
			res.setResultCode("9999");
			res.setResultMessage(ex.getMessage());
			ErrorLogger.error(req.getCommonDTO(), "XCronControlWebAction", ex);
		} finally {
			try {
				if(bis != null) bis.close();
			} catch(Exception ex) {
			}

			try {
				if(bos != null) bos.close();
			} catch(Exception ex) {
			}
			
			try {
				if(socket != null) socket.close();
			} catch(Exception ex) {
			}
		}
	}	
	
	public void processResponse(Element root, JediResponse res) {
		if(root == null || ! root.hasChildNodes()) return;		
		NodeList children = root.getChildNodes();
		
		for(int i = 0 ; i < children.getLength() ; i++) {
			Node node = children.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			
			if("ResultCode".equals(node.getNodeName())) {
				res.setResultCode(XmlUtil.getText(node));
			} else if("ResultMessage".equals(node.getNodeName())) {
				res.setResultMessage(XmlUtil.getText(node));
			} else if("ListParam".equals(node.getNodeName())) {
				ListParam listParam = getListParamFromXml(node);
				if(listParam != null) {
					res.param.addValue("XCRON_RESULT", listParam);
				}
			}
		}
	}
	
	public ListParam getListParamFromXml(Node node) {
		ListParam result = null;
		NodeList children = node.getChildNodes();
		
		for(int i = 0 ; i < children.getLength() ; i++) {
			Node param = children.item(i);
			NodeList fields = param.getChildNodes();
			
			if(i == 0) {	
				String[] columns = new String[fields.getLength()];
				for(int j = 0 ; j < fields.getLength() ; j++)
					columns[j] = XmlUtil.getAttribute(fields.item(j), "Name");
				result = new ListParam(columns);
			}
			
			String[] values = new String[fields.getLength()];
			for(int j = 0 ; j < fields.getLength() ; j++) {
				values[j] = XmlUtil.getCdata(fields.item(j));
			}
			result.addRow(values);
		}

		return result;
	}
	
	public String getXmlFromParam(Param param) {
		if(param == null) return "";
		
		StringBuffer buffer = new StringBuffer("<Param>");
		String[] keys = param.keys();
		
		for(int i = 0 ; i < keys.length ; i++) {
			buffer.append("<Field Name=\"" + keys[i] + "\">");
			buffer.append("<![CDATA[").append(param.getValue(keys[i], "")).append("]]>");
			buffer.append("</Field>");
		}
		
		buffer.append("</Param>");
		return buffer.toString();
		
	}
	
	public String valCheck(String val){
		String pattern = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
		Pattern p = Pattern.compile(pattern);
		Matcher m_val = p.matcher(val);
		
		if(m_val.find()){ //VAL에서 걸릴 때
			val=val.replaceAll(pattern, "").replaceAll("script", "");
		}
		return val;
	}
	
}
