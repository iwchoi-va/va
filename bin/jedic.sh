export JEDI_HOME=/sw/msens/MSENS_OB
export ANT_HOME=/sw/msens/MSENS_OB/bin/apache-ant-1.8.1
export JAVA_HOME=/sw/jdk1.7.0_80
export WAS_LIB=/sw/msens/MSENS_OB/webapps/WEB-INF/lib
export WORK_DIR=$JEDI_HOME/src
export WAS_TYPE=jeus
export WAS_HOME=/home/jycheun/tomcat
export ANT_FILE=build.xml

$ANT_HOME/bin/ant -Dantfile=$ANT_FILE -buildfile $JEDI_HOME/buildxml/tool.xml
