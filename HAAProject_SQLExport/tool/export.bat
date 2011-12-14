
set classpath=.;.\haaproject_sqlexport.jar
set classpath=%classpath%;.\ojdbc14.jar
set classpath=%classpath%;.\sqljdbc.jar

java org.haaproject.sqlexport.ExportInsert com.microsoft.sqlserver.jdbc.SQLServerDriver "jdbc:sqlserver://192.168.5.111:1433;DatabaseName=twse;SelectMethod=cursor" twse password tb110

pause