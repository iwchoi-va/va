package kr.co.kico.wfm.encmapper.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.kico.wfm.encmapper.util.XmlUtil;
import kr.co.kico.wfm.encmapper.vo.EncMappingVO;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WfmEncMapperConfig {
	private static WfmEncMapperConfig instance;
	private XmlUtil xmlUtil;
	private ArrayList encVOList;
	private ArrayList decrypVOList;
	private HashMap encVOMap;
	private HashMap decrypVOMap;
	
	private WfmEncMapperConfig() throws Exception {
		this.xmlUtil = new XmlUtil();
		loadEncConfig();
	}
	
	private void loadEncConfig() throws Exception {
		makeVOList("ENC");
		makeVOList("DECRYP");
	}
	
	private void makeVOList(String div) throws Exception {
		if ("ENC".equals(div)) {
			this.encVOMap = new HashMap();

			InputStream is = new  FileInputStream(System.getProperty("jedi.home") + "/config/db-enc-config.xml");
			this.xmlUtil.parse(is);

			makeVODetailList(this.xmlUtil, this.encVOMap);
		}

		if ("DECRYP".equals(div)) {
			this.decrypVOMap = new HashMap();

			InputStream is = new  FileInputStream(System.getProperty("jedi.home") + "/config/db-decryp-config.xml");
			this.xmlUtil.parse(is);

			makeVODetailList(this.xmlUtil, this.decrypVOMap);
		}
	}
	
	private void makeVODetailList(XmlUtil xmlUtil, HashMap map) throws Exception {
		NodeList dataNodeList = xmlUtil.getElementsByTagName("data");

		if ((dataNodeList != null) && (dataNodeList.getLength() > 0)) {
			for (int i = 0; i < dataNodeList.getLength(); i++) {
				Node dataNode = dataNodeList.item(i);

				String queryId = xmlUtil.getAttributeValue(dataNode, "query-id");
				String columns = xmlUtil.getAttributeValue(dataNode, "columns");
				String initechPolyColumns = xmlUtil.getAttributeValue(dataNode, "initech-poly-columns");

				EncMappingVO mappingVO = new EncMappingVO();

				mappingVO.setQueryId(queryId);
				mappingVO.setColumns(columns);
				mappingVO.setInitechPolyColumns(initechPolyColumns);

				map.put(queryId, mappingVO);
			}
		}
	}
	
	public ArrayList getEncVOList() {
		return this.encVOList;
	}

	public ArrayList getDecrypVOList() {
		return this.decrypVOList;
	}

	public static synchronized WfmEncMapperConfig getInstance() throws Exception
	{
		if (instance == null) {
			synchronized (WfmEncMapperConfig.class) {
				if (instance == null) {
					instance = new WfmEncMapperConfig();
				}
			}
		}

		return instance;
	}

	public HashMap getEncVOMap() {
		return this.encVOMap;
	}

	public void setEncVOMap(HashMap encVOMap) {
		this.encVOMap = encVOMap;
	}

	public HashMap getDecrypVOMap() {
		return this.decrypVOMap;
	}

	public void setDecrypVOMap(HashMap decrypVOMap) {
		this.decrypVOMap = decrypVOMap;
	}

	public EncMappingVO getEncInfoByQueryId(String queryId) {
		return (EncMappingVO)this.encVOMap.get(queryId);
	}

	public EncMappingVO getDecrypInfoByQueryId(String queryId) {
		return (EncMappingVO)this.decrypVOMap.get(queryId);
	}
}
