import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.taskdefs.*;
import java.io.*;

public class MakeBean extends Task {
  private String dir;
  private String beanmaker;

  public void setDir(String dir) {
    this.dir = dir;
  }
  public void setBeanmaker(String beanmaker){
	  this.beanmaker = beanmaker;
  }

  public void execute() {
	FileSet fileset = new FileSet();
	fileset.setDir(new File(dir));
	fileset.setIncludes("src/**/ejb/**/*Bean.java");
	
	DirectoryScanner scan= fileset.getDirectoryScanner(project);
	String[] beanList = scan.getIncludedFiles();
	StringBuffer buffer = new StringBuffer();
	for(int j=0;j < beanList.length;j++){
		String bean = beanList[j];
		String home = bean.substring(0,bean.indexOf("Bean"))+"Home.java";
		String remote = bean.substring(0,bean.indexOf("Bean"))+".java";
		
		if(!new File(dir+"\\"+home).exists() || 
		   !new File(dir+"\\"+remote).exists()){
			continue;
		}
		String remoteClass = remote.substring(4,remote.indexOf(".java")).replace('\\','.');
		String beanName = remoteClass.substring(remoteClass.lastIndexOf(".")+1);
	
		buffer.append("    <bean name='"+beanName+"' remote='"+remoteClass+"'/>\n");
	}
	if(buffer.length() > 0){
		String xml = "<bean-list>\n"+buffer.toString()+"</bean-list>";
		Echo echo = new Echo();
		echo.setFile(new File(dir+"\\bean-list.xml"));
		echo.setMessage(xml);
		echo.execute();
		
		
		//project.setProperty("category",dir.substring(dir.lastIndexOf("/"),dir.lastIndexOf("_")));
		//project.setProperty("component",dir.substring(dir.lastIndexOf("_")+1));
		
		project.executeTarget(beanmaker);
	}
	
  }
}
