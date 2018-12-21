

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionObject {
   private static Connection conn = null;
   
   public static Connection getConnection(){
	    String url = "jdbc:mysql://Your RDS URL";

	   try{
		   Class.forName("com.mysql.jdbc.Driver").newInstance();
		   conn = DriverManager.getConnection(url,"vormetric","Your PWD");
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return conn;
   }
}
