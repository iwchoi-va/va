package xwing.export;

import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonInfo {
	private String format = "";
	private String filename = "";
	
	public CommonInfo(String format,String filename){
		this.format = format;
		if(filename.equals("")) setFilename();
		else this.filename = filename;
	}
	
	public void setContentType(HttpServletResponse rep){
		if(this.format.equals("excel")){
			rep.setContentType("application/vnd.ms-excel");
		}else if(this.format.equals("csv")){
			//TODO csv/json/xml
		}
	}
	
	public void setFilename(){
		if(this.filename.equals("")){
			Date date = new Date();
			String name = Integer.toString(date.getYear())+Integer.toString(date.getMonth())+Integer.toString(date.getDay())
						+ Integer.toString(date.getHours())+Integer.toString(date.getMinutes())+Integer.toString(date.getSeconds());
			this.filename = name;
		}
	}
	public void setAttachment(HttpServletResponse rep){
		if(this.format.equals("excel")){
			rep.setHeader("Content-Disposition", "attachment; filename="+this.filename+".xls");
		}
		//TODO
	}
}
