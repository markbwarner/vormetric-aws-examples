

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionObject {
   private static Connection conn = null;
   
   public static Connection getConnection(){
	    String url = "jdbc:mysql://vormetric-crypto-server.cosqxcdfdl0s.us-east-1.rds.amazonaws.com:3306/vtsdemo?useSSL=false";

	   try{
		   Class.forName("com.mysql.jdbc.Driver").newInstance();
		   conn = DriverManager.getConnection(url,"vormetric","Vormetric123!");
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return conn;
   }
}
