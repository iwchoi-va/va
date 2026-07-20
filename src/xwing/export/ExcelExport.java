package xwing.export;

import java.io.*;

import javax.servlet.http.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class ExcelExport  {
	private static Element datagrid ;
	
	private static WritableWorkbook workbook;
	private static WritableSheet sheet;
	private static WritableCellFormat cellFormat;
	
	private static int rowIndex = 0;
	private static Dataset dataset;
	
	public void Export(HttpServletRequest req, File file) throws Exception{
		//System.out.println("ExcelExport.java : " + req.getParameter("datagrid"));
		//System.out.println("ExcelExport.java : " + new String(req.getParameter("datagrid").getBytes(), "utf-8"));
		//System.out.println("ExcelExport.java : " + new String(req.getParameter("datagrid").getBytes("8859_1"), "utf-8"));
				
		//InputSource is = new InputSource(new StringReader(new String(req.getParameter("datagrid").getBytes(), "utf-8")));
		//InputSource is = new InputSource(new StringReader(new String(req.getParameter("datagrid").getBytes("8859_1"), "utf-8")));
		//InputSource is = new InputSource(new StringReader(req.getParameter("datagrid"))); //local
		InputSource is = new InputSource(new StringReader(new String(req.getParameter("datagrid").getBytes())));
		//Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		
		/* 유효성 검증 (2018.07.27) */
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    String FEATURE = null;
	    
	    FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
	    dbf.setFeature(FEATURE, true);
	    
	    FEATURE = "http://xml.org/sax/features/external-general-entities";
	    dbf.setFeature(FEATURE, false);
	 
	    FEATURE = "http://xml.org/sax/features/external-parameter-entities";
	    dbf.setFeature(FEATURE, false);
	 
	    FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
	    dbf.setFeature(FEATURE, false);
	 
	    // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
	    dbf.setXIncludeAware(false);
	    dbf.setExpandEntityReferences(false);
	    
	    Document document = dbf.newDocumentBuilder().parse(is);
	    
		//----------------------------------------------------------------------------------
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList children = (NodeList)xpath.evaluate("/datagrid", document, XPathConstants.NODESET);
		this.datagrid = ((Element) children.item(0));
		//this.dataset = new Dataset(req.getParameter("column"),req.getParameter("row")); //local
		
		this.dataset = new Dataset(new String(req.getParameter("column").getBytes(),"utf-8"),new String(req.getParameter("row").getBytes(),"utf-8"));
		//this.dataset = new Dataset(new String(req.getParameter("column").getBytes("8859_1"),"utf-8"),new String(req.getParameter("row").getBytes("8859_1"),"utf-8"));
		
		initExcel(file);
		createExcel();
	}
	
	private void initExcel(File file){
		try{
			workbook = Workbook.createWorkbook(file); 
			sheet = workbook.createSheet("sheet0", 0); 
			cellFormat = new WritableCellFormat(); 
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void createExcel(){
			NodeList part = datagrid.getChildNodes();
			for(int i=0; i < part.getLength(); i++){
				Node node = part.item(i);
				String nodeName = node.getNodeName();
				if(nodeName.equals("datagrid-colgroup")){
					setColumnWidth(node);
				}else if(nodeName.equals("datagrid-head")){
					addHead(node);
				}else if(nodeName.equals("datagrid-body")){
					cellFormat = new WritableCellFormat();   //���� ��Ÿ�� ����
					addBody(node);
				}else if(nodeName.equals("datagrid-summary")){
					cellFormat = new WritableCellFormat();   //���� ��Ÿ�� ����
					addSummay(node);
				}
			}
			
			try {
				workbook.write();
				if(workbook != null) workbook.close();
			}  catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void setColumnWidth(Node node){
		NodeList colList = ((Element) node).getElementsByTagName("datagrid-column");
		for( int i=0; i < colList.getLength(); i++){
			int width = parseInt(((Element) colList.item(i)).getAttribute("width"),0);
			if ( width != 0 ) sheet.setColumnView(i, Math.round((width - 5)/5));
		}
	}
	public void addHead(Node node){
		System.out.println("Add Head");
		NodeList cellList = ((Element) node).getElementsByTagName("datagrid-cell");
		try {
			cellFormat = new WritableCellFormat();   //���� ��Ÿ�� ����
			cellFormat.setBackground(Colour.GRAY_25);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			this.rowIndex = 0;
			for(int i=0; i < cellList.getLength(); i++){
				String value = ((Element) cellList.item(i)).getAttribute("text"); 
				createCell((Element) cellList.item(i),value);
			}
			this.rowIndex = ((Element) node).getElementsByTagName("datagrid-row").getLength();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addBody(Node node){
		System.out.println("Add Body");
		NodeList cellList = ((Element) node).getElementsByTagName("datagrid-cell");
		int rowSize = ((Element) node).getElementsByTagName("datagrid-row").getLength();
		try {
			for(int i=0; i < dataset.size(); i++){
				for(int j=0; j < cellList.getLength(); j++){
					String bindcolumn = ((Element) cellList.item(j)).getAttribute("bindcolumn");
					String value = dataset.getValue(i, bindcolumn); 
					createCell((Element) cellList.item(j),value);
				}
				this.rowIndex += rowSize;
			}
			
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addSummay(Node node){
		System.out.println("Add Summary");
		NodeList cellList = ((Element) node).getElementsByTagName("datagrid-cell");
		try {
			for(int i=0; i < cellList.getLength(); i++){
				String value = ((Element) cellList.item(i)).getAttribute("text"); 
				createCell((Element) cellList.item(i),value);
			}
			this.rowIndex = ((Element) node).getElementsByTagName("datagrid-row").getLength();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createCell(Element ele, String value) throws RowsExceededException, WriteException{
		Node row = ele.getParentNode();
		Element area = (Element) row.getParentNode();
		NodeList nodeList = area.getElementsByTagName("datagrid-row");
		NodeList cellList;
		Node cell;
		boolean create = false;
		
		int colCount = ele.getOwnerDocument().getElementsByTagName("datagrid-column").getLength();
		int rowCount = nodeList.getLength();

		Integer[][] matrix = new Integer[rowCount][colCount];
		
		for(int i=0; i < nodeList.getLength() && !create; i++){
			row = nodeList.item(i);
			cellList = ((Element) row).getElementsByTagName("datagrid-cell");
			for(int j=0; j < cellList.getLength()  && !create; j++){
				cell = cellList.item(j);
				int colSpan = parseInt(((Element) cell).getAttribute("colspan"),1);
				int rowSpan = parseInt(((Element) cell).getAttribute("rowspan"),1);
				int colIndex = -1;
				
				String exporttype = ((Element) cell).getAttribute("exporttype");
                for (int l = 0; l <= matrix[i].length && colIndex == -1; l++) {
                    if (matrix[i][l] == null) {
                        colIndex = l;
                    }
                }

                if (ele == cell) {
                    int rowIndex = this.rowIndex + i;
                    create = true;
                    if( "number".equals(exporttype)){
                        WritableCellFormat numberFormat;
                        Double db;
                        boolean format = true;
                        if( value.indexOf(",") != -1 ){
                            System.out.println(value.replaceAll(",", ""));
                            db = new Double(value.replaceAll(",", ""));
                            numberFormat  = new WritableCellFormat(NumberFormats.THOUSANDS_INTEGER);
                        }else if( value.indexOf("%") != -1 ){
                            numberFormat  = new WritableCellFormat(NumberFormats.PERCENT_INTEGER);
                            if (  value.indexOf(".") != -1){
                                numberFormat  = new WritableCellFormat(NumberFormats.PERCENT_FLOAT);
                            }
                            db = new Double(value.replaceAll("%", "")) / 100;
                        }else if( value.indexOf(".") != -1 ){
                            numberFormat  = new WritableCellFormat(NumberFormats.FLOAT);
                            db = new Double(value);
                        }else{
                            format = false;
                            numberFormat  = new WritableCellFormat();
                            db = new Double(value);
                        }
                    
                        if( !format ){
                            Number number = new Number(colIndex,rowIndex,db);
                            sheet.addCell(number);
                        }else{
                            Number number = new Number(colIndex,rowIndex,db,numberFormat);
                            sheet.addCell(number);
                        }
                        
                        
                    }else{
                        Label label = new Label(colIndex,rowIndex,value,cellFormat);
                        sheet.addCell(label);
                    }
                    if( colSpan != 1 || rowSpan != 1){
                        int lastC = (colSpan != 1) ? (colIndex+colSpan - 1) : colIndex;
                        int lastR = (rowSpan != 1) ? (rowIndex+rowSpan - 1) : rowIndex;
                        sheet.mergeCells(colIndex, rowIndex, lastC, lastR );
                    }
                    break;
                }

				for (int k = i; k < i + rowSpan; k++) {
					for (int l = colIndex; l < colIndex + colSpan; l++) {
						matrix[k][l] = 1;
					}
				}
			}
		}
		
	}
	
	private void setCellStyle(Element elem) throws WriteException{
		 NamedNodeMap attr = elem.getAttributes();
		 WritableFont font = new WritableFont(WritableFont.createFont("Dotum"));
//		 Color color = new Color(0xFF0096);
//		 System.out.println("CO:"+color.getBlue());
//		 jxl.format.Colour colour = new  jxl.format.Colour(1,"FD",2,3,3);
//		 font.setColour(Colour.getInternalColour(color.getRGB()));
//		Col
//		 System.out.println(font.getColour().getValue());
//		 font.setColour(Colour.getInternalColour("acacac"));
//		 for( int i=0; i < attr.getLength(); i++){
//			 Attr attribute = (Attr)attr.item(i); 
//			 String name = attribute.getName();
//			 String value = attribute.getValue();
//			 // font
//			 if( name.indexOf("font") > -1) setFontStyle(font, name, value);
//			 else if( name.equals("bgcolor")) cellFormat.setBackground(Colour.getInternalColour(parseInt(value,0)));
//			 else if( name.equals("valign")){ 
//				 cellFormat.setAlignment(Alignment.CENTRE);
//			 }else if( name.equals("halign")){
//				 cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
//			 }
//				
//		       System.out.println(" " + attribute.getName()+" = "+attribute.getValue());
//		 }
		 if( font == null) cellFormat = new WritableCellFormat();   //���� ��Ÿ�� ����
		 else cellFormat = new WritableCellFormat(font);   //���� ��Ÿ�� ����
	}
	
	private void setFontStyle(WritableFont font, String fontname, String fontvalue) throws WriteException{
		if(fontname.equals("fontfamily")){
		//	font = new WritableFont(WritableFont.createFont(fontvalue));
		}else if(fontname.equals("fontcolor") && fontvalue.indexOf("#") > -1){
			font.setColour(Colour.getInternalColour(parseInt(fontvalue,0)));
		}else if(fontname.equals("fontweight") && !fontvalue.equals("none")){
			font.setBoldStyle(font.BOLD);
		}else if(fontname.equals("fontsize")){
			font.setPointSize(parseInt(fontvalue,11));
		}
	}
	
	public static int parseInt(String value, int defaultValue) {
		String v = value.replaceAll("[^-0-9]", "");

		if (v.length() != 0) {
			try {
				return Integer.parseInt(v);
			} catch (Exception ex) {
			}
		}

		return defaultValue;
	}
}
