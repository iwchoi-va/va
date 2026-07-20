package kr.co.kico.wfm.encmapper.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.AttributeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributeListImpl;

public class XmlUtil {
	private Document document;
	private Node rootNode;
	private DocumentBuilder builder;
	
	public XmlUtil() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		//보안 취약성 검출로 추가된 부분(2018.07.25)
		String FEATURE = null;
	 	   
	 	FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
	 	factory.setFeature(FEATURE, true);
	 	    
	 	FEATURE = "http://xml.org/sax/features/external-general-entities";
	 	factory.setFeature(FEATURE, false);
	 	 
	 	FEATURE = "http://xml.org/sax/features/external-parameter-entities";
	 	factory.setFeature(FEATURE, false);
	 	 
	    FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
	 	factory.setFeature(FEATURE, false);
	 	  
	 	factory.setXIncludeAware(false);
	 	
		//----------------------------------------------------------------------------------
	 	
		factory.setExpandEntityReferences(true);

		this.builder = factory.newDocumentBuilder();
	}
	
	public void parse(Object obj) throws Exception {
		if (obj == null) {
			throw new Exception("DOM Parsing is not Object");
		}

		if ((obj instanceof String)) {
			byte[] byteAry = ((String)obj).getBytes();
			ByteArrayInputStream byteis = new ByteArrayInputStream(byteAry);
			createDOMDocument(byteis);
		} else {
			createDOMDocument(obj);
		}
	}
	
	public void createDOMDocument(Object obj) throws Exception {
		if ((obj instanceof InputSource)) {
			this.document = this.builder.parse((InputSource)obj);
			this.rootNode = this.document.getDocumentElement();
		} else if ((obj instanceof File)) {
			this.document = this.builder.parse((File)obj);
			this.rootNode = this.document.getDocumentElement();
		} else if ((obj instanceof InputStream)) {
			this.document = this.builder.parse((InputStream)obj);
			this.rootNode = this.document.getDocumentElement();
		} else {
			throw new Exception("DOM Document is not created");
		}
	}
	
	public Node getRootNode() {
		return rootNode;
	}

	public String getNodeValue(Node node) {
		if ((node == null) || (!node.hasChildNodes())) {
			return "";
		}

		return node.getFirstChild().getNodeValue();
	}

	public String getNodeValue(String targetName) {
		return getNodeValue(rootNode, targetName);
	}
	
	public String getNodeValue(Node parentNode, String targetName) {
		String value = null;
		Node thisNode = null;
		NodeList nodeList = parentNode.getChildNodes();

		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				thisNode = nodeList.item(i);

				if (targetName.equals(thisNode.getNodeName())) {
					value = getNodeValue(thisNode);
				}
				else {
					value = getNodeValue(thisNode, targetName);

					if (value != null) {
						break;
					}
				}
			}
		}
		return value;
	}
	
	public NodeList getElementsByTagName(Node parentNode, String targetName) {
		NodeList nodeList = null;

		if (parentNode.getNodeType() != 1) {
			return null;
		}

		nodeList = ((Element)parentNode).getElementsByTagName(targetName);
		return nodeList;
	}

	public NodeList getElementsByTagName(String targetName) {
		return getElementsByTagName(rootNode, targetName);
	}
	
	public Node getNode(String nodeName) {
		Node returnNode = null;
		NodeList nodeList = getElementsByTagName(rootNode, nodeName);

		if ((nodeList != null) && (nodeList.getLength() > 0)) {
			returnNode = nodeList.item(0);
		}

		return returnNode;
	}

	public Node getNode(String nodeName, int nodeIndex) {
		Node returnNode = null;
		NodeList nodeList = getElementsByTagName(this.rootNode, nodeName);

		if ((nodeList != null) && (nodeList.getLength() < nodeIndex)) {
			returnNode = null;
		}

		if ((nodeList != null) && (nodeList.getLength() >= nodeIndex)) {
			returnNode = nodeList.item(nodeIndex);
		}

		return returnNode;
	}
	
	public String getAttributeValue(Node node, String attrName) {
		return ((Element)node).getAttribute(attrName);
	}

	public String getAttributeValue(AttributeList attrList, String attrName) {
		return attrList.getValue(attrName);
	}
	
	public AttributeList getAttributeList(Node node) {
		if (node.getNodeType() != 1) {
			return null;
		}

		AttributeListImpl attrListImpl = null;
		NamedNodeMap nodeMap = node.getAttributes();

		if (nodeMap != null) {
			attrListImpl = new AttributeListImpl();
			Node tempNode = null;

			for (int i = 0; i < nodeMap.getLength(); i++) {
				tempNode = nodeMap.item(i);
				attrListImpl.addAttribute(tempNode.getNodeName(), "NMTOKEN", tempNode.getNodeValue());
			}
		}

		return attrListImpl;
	}

	public String[] getAttributeNames(Node node) {
		AttributeList attrList = getAttributeList(node);
		String[] strAry = null;

		if (attrList != null) {
			strAry = new String[attrList.getLength()];

			for (int i = 0; i < attrList.getLength(); i++) {
				strAry[i] = attrList.getName(i);
			}
		}

		return strAry;
	}

	public HashMap getAttributeHashMap(Node node) {
		HashMap resultMap = null;
		AttributeList attrList = getAttributeList(node);
		String[] strAry = getAttributeNames(node);

		if ((strAry != null) && (strAry.length > 0)) {
			resultMap = new HashMap();

			for (int i = 0; i < strAry.length; i++) {
				resultMap.put(strAry[i], getAttributeValue(attrList, strAry[i]));
			}
		}

		return resultMap;
	}
}
