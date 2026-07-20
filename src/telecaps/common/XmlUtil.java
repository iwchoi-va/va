/*************************************************************
 * This software was developed and owned by Inticube
 * Illegal use of this software will violate the Copy Right Law
 * ************************************************************
 * Program Name : @(#)XmlUtil.java
 * Function description :
 * Programmer Name : Youn TaeHee (oinee2k@inticube.com)
 * Creation Date : 2006. 3. 07.
 * ************************************************************
 *                P R O G R A M H I S T O R Y
 * ************************************************************
 * DATE          :     PROGRAMMER    :          REASON
 *
 *
 *
 */
package telecaps.common;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlUtil {
	public static final int NOT_FOUND = -1;
	public static final int COMPLETE = 1;

	private XmlUtil() {
	}

	public static void appendSubTag(Element root, String tagName,
			String subTagName) {
		Document doc = root.getOwnerDocument();
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if ((node != null) && (node.getNodeName() != null)
					&& node.getNodeName().equals(tagName)) {
				Element tmp = doc.createElement(subTagName);
				node.appendChild(tmp);
			}
		}
	}

	public static void appendTag(Element root, String tagName) {
		Document doc = root.getOwnerDocument();
		Element tmp = doc.createElement(tagName);
		root.appendChild(tmp);
	}

	public static String getSubTagAttribute(Element root, String tagName,
			String subTagName, String attribute) {
		String returnString = "";
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if (node != null) {
				NodeList children = node.getChildNodes();
				for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
					Node child = children.item(innerLoop);
					if ((child != null) && (child.getNodeName() != null)
							&& child.getNodeName().equals(subTagName)) {
						if (child instanceof Element) {
							return ((Element) child).getAttribute(attribute);
						}
					}
				} // end inner loop
			}
		}
		return returnString;
	}

	public static String getSubTagValue(Element root, String tagName,
			String subTagName) {
		String returnString = "";
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if (node != null) {
				NodeList children = node.getChildNodes();
				for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
					Node child = children.item(innerLoop);
					if ((child != null) && (child.getNodeName() != null)
							&& child.getNodeName().equals(subTagName)) {
						Node grandChild = child.getFirstChild();
						if ((grandChild != null)
								&& grandChild.getNodeValue() != null) {
							return grandChild.getNodeValue();
						}
					}
				} // end inner loop
			}
		}
		return returnString;
	}

	public static String getSubTagValue(Node node, String subTagName) {
		NodeList nList = node.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			Node child = nList.item(i);

			if ((child != null) && (child.getNodeName() != null)
					&& child.getNodeName().equals(subTagName)) {
				Node grandChild = child.getFirstChild();
				if (grandChild != null && grandChild.getNodeValue() != null)
					return grandChild.getNodeValue();
			}
		}
		return "";
	}

	public static ArrayList getSubTagValues(Element root, String tagName,
			String subTagName) {
		ArrayList results = new ArrayList();
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if (node != null) {
				NodeList children = node.getChildNodes();
				for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
					Node child = children.item(innerLoop);
					if ((child != null) && (child.getNodeName() != null)
							&& child.getNodeName().equals(subTagName)) {
						Node grandChild = child.getFirstChild();
						if ((grandChild != null)
								&& grandChild.getNodeValue() != null) {
							results.add(grandChild.getNodeValue());
						}
					}
				} // end inner loop
			}
		}
		return results;
	}

	public static String getTagValue(Element root, String tagName) {
		NodeList nList = root.getElementsByTagName(tagName);
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node != null) {
				Node child = node.getFirstChild();
				if ((child != null) && (child.getNodeValue() != null)) {
					return child.getNodeValue();
				}
			}
		}
		return "";
	}

	public static Element loadXmlDocument(InputSource xmlSrc) throws Exception {
		Element root = null;
		try {
			Properties prop = System.getProperties();
			prop.put("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			System.setProperties(prop);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlSrc);
			root = doc.getDocumentElement();
			root.normalize();
		} catch (SAXParseException err) {
			throw new Exception("XmlUtil Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
		} catch (SAXException e) {
			throw new Exception("SAX Exception " + e.getMessage());
		} catch (java.net.MalformedURLException mfx) {
			throw new Exception(" MalformedURLException " + mfx.getMessage());
		} catch (java.io.IOException e) {
			throw new Exception("IOException " + e.getMessage());
		} catch (Exception pce) {
			throw new Exception(" Exception :" + pce.getMessage());
		}
		return root;
	}

	public static Element loadXmlDocument(String filePath) throws Exception {
		try {
			Properties prop = System.getProperties();
			prop.put("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			System.setProperties(prop);
			java.io.File file = new java.io.File(filePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource xmlSrc = new InputSource(new java.io.FileReader(file));
			Document doc = builder.parse(xmlSrc);
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;
		} catch (SAXParseException err) {
			err.printStackTrace();
			throw new RuntimeException("XmlUtil Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
		} catch (SAXException e) {
			throw new Exception("SAX Exception " + e.getMessage());
		} catch (java.net.MalformedURLException mfx) {
			throw new Exception("MalformedURLException " + mfx.getMessage());
		} catch (java.io.IOException e) {
			throw new Exception("IOException " + e.getMessage());
		} catch (Exception pce) {
			throw new Exception("Exception :" + pce.getMessage());
		}
	}

	public static Element loadXmlDocument(URL url) throws Exception {
		try {
			Properties prop = System.getProperties();
			prop.put("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			System.setProperties(prop);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource xmlSrc = new InputSource(url.openStream());
			Document doc = builder.parse(xmlSrc);
			Element root = doc.getDocumentElement();
			root.normalize();
			return root;
		} catch (SAXParseException err) {
			throw new Exception("XmlUtil Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
		} catch (SAXException e) {
			throw new Exception("SAX Exception " + e.getMessage());
		} catch (java.net.MalformedURLException mfx) {
			throw new Exception("MalformedURLException " + mfx.getMessage());
		} catch (java.io.IOException e) {
			throw new Exception("IOException " + e.getMessage());
		} catch (Exception pce) {
			throw new Exception("Exception :" + pce.getMessage());
		}
	}

	public static int removeSubTag(Element root, String tagName,
			String subTagName) {
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if (node != null) {
				NodeList children = node.getChildNodes();
				for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
					Node child = children.item(innerLoop);
					if ((child != null) && (child.getNodeName() != null)
							&& child.getNodeName().equals(subTagName)) {
						node.removeChild(child);
						return COMPLETE;
					}
				}
			}
		}
		return NOT_FOUND;
	}

	public static int removeTag(Element root, String tagName) {
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if ((node != null) && (node.getNodeName() != null)
					&& node.getNodeName().equals(tagName)) {
				root.removeChild(node);
				return COMPLETE;
			}
		}
		return NOT_FOUND;
	}

	public static int updateSubTagValue(Element root, String tagName,
			String subTagName, String value) {
		NodeList list = root.getElementsByTagName(tagName);
		for (int loop = 0; loop < list.getLength(); loop++) {
			Node node = list.item(loop);
			if (node != null) {
				NodeList children = node.getChildNodes();
				for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
					Node child = children.item(innerLoop);
					if ((child != null) && (child.getNodeName() != null)
							&& child.getNodeName().equals(subTagName)) {
						Node grandChild = child.getFirstChild();
						if (grandChild.getNodeValue() != null) {
							grandChild.setNodeValue(value);
							return COMPLETE;
						}
					}
				} // end inner loop
			}
		}
		return NOT_FOUND;
	}

	public static boolean isElementExist(Element root, String tagName) {
		NodeList list = root.getElementsByTagName(tagName);
		return list.getLength() != 0 ? true : false;
	}

	public static String getText(Node node) {
		if (node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		else
			return "";
	}

	public static String getCdata(Node parent) {
		if (parent == null)
			return "";

		NodeList nodeList = parent.getChildNodes();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node != null && node.getNodeType() == Node.CDATA_SECTION_NODE) {
				buffer.append(node.getNodeValue().trim());
				break;
			}
		}

		return buffer.toString();
	}

	public static String getAttribute(Node node, String attrName) {
		if ((node != null) && (node instanceof Element)) {
			return ((Element) node).getAttribute(attrName);
		}

		return "";
	}

	public static boolean getAttribute(Node node, String name,
			boolean defaultValue) {
		return ((Element) node).hasAttribute(name) ? Boolean.valueOf(
				((Element) node).getAttribute(name)).booleanValue()
				: defaultValue;
	}

	/**
	 * Element의 속성(attribute)의 값을 가져오는 메소드로서 속성이 없거나 값이 설정되어있지 않은 경우 default 값을
	 * 반환한다.
	 * 
	 * @param node
	 * @param attrName
	 * @param defaultValue
	 * @return
	 */
	public static String getAttribute(Node node, String attrName,
			String defaultValue) {
		String returnValue = ((Element) node).getAttribute(attrName);
		if (returnValue == null || returnValue.trim().length() == 0)
			return defaultValue;
		return returnValue.trim();
	}

	public static Node findChildWithAtt(Node parent, String elemName,
			String attName, String attVal) {
		NodeList nodeList = parent.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (!child.getNodeName().equals(elemName)
					|| child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if (attVal.equals(getAttribute(child, attName))) {
				return nodeList.item(i);
			}
		}

		return null;
	}

	public static Node getChild(Node parent, String elemName) {
		NodeList nodeList = parent.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (child.getNodeName().equalsIgnoreCase(elemName))
				return child;
		}

		return null;
	}

	public static Node getChild(Node parent, int type) {
		Node n = parent.getFirstChild();

		while (n != null && type != n.getNodeType()) {
			n = n.getNextSibling();
		}

		if (n == null)
			return null;
		return n;
	}

	/**
	 * Get the next sibling with the same name and type
	 */
	public static Node getNext(Node current) {
		String name = current.getNodeName();
		int type = current.getNodeType();
		return getNext(current, name, type);
	}

	/**
	 * Return the next sibling with a given name and type
	 */
	public static Node getNext(Node current, String name, int type) {
		Node first = current.getNextSibling();
		if (first == null)
			return null;

		for (Node node = first; node != null; node = node.getNextSibling()) {
			if (type >= 0 && node.getNodeType() != type)
				continue;
			if (name == null)
				return node;
			if (name.equals(node.getNodeName())) {
				return node;
			}
		}
		return null;
	}
}
