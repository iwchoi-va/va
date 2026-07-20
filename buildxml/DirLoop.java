import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;
import java.io.*;

public class DirLoop extends Task {
  private String calltarget;
  private String keyname;
  private DirSet dirset;

  public void setCalltarget(String calltarget) {
    this.calltarget = calltarget;
  }
  
  public void setKeyname(String keyname){
	this.keyname = keyname;
  }
  public void addDirset(DirSet dirset) {
    this.dirset = dirset;
  }

  public void execute() {
	
	DirectoryScanner scanner = dirset.getDirectoryScanner(project);
	File dir = dirset.getDir(project);
	String[] list = scanner.getIncludedDirectories() ;
	System.out.println("number of directory : " +list.length);
	
    for (int i=0; i < list.length;i++) {
		project.setProperty(keyname,list[i]);
		Ant callee = (Ant)getProject().createTask("ant");
        callee.setOwningTarget(getOwningTarget());
        callee.setTaskName(getTaskName());
        callee.setLocation(getLocation());
        callee.init();
		callee.setAntfile(project.getProperty("ant.file"));
        callee.setTarget(calltarget);
        callee.setInheritAll(true);
        callee.setInheritRefs(true);
        callee.execute();
    }

	
  }
}
