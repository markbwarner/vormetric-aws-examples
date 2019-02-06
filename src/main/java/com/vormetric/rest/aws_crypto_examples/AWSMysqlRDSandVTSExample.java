package com.vormetric.rest.aws_crypto_examples;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;

import com.vormetric.rest.helperclasses.VormetricCryptoServerHelper;
import com.vormetric.rest.helperclasses.VormetricCryptoServerSettings;

public class AWSMysqlRDSandVTSExample {
	
	 public static void main(String[] args) throws Exception {
		 
			String action = "tokenize";
			VormetricCryptoServerSettings vcs = new VormetricCryptoServerSettings();
			
			Calendar calendar = Calendar.getInstance();

			// Get start time (this needs to be a global variable).
			Date startDate = calendar.getTime();
			
			
			Connection connection = ConnectionObject.getConnection();
			String SQL = "insert into person values (?,?,?,?)";
			int batchSize = 100;
			//		int batchSize = 50;
			int count = 0;
			int[] result;
			
			connection.setAutoCommit(false);
			PreparedStatement pstmt = connection.prepareStatement(SQL);
			String results = null;
			String sensitive = null;
			for(int i=1;i<= 1000;i++){
			   sensitive = "Java"+i*Math.random();
				results = VormetricCryptoServerHelper.doTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(), vcs.getvcspassword(), vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), sensitive, action);

			  pstmt.setString(1,results);
			  pstmt.setString(2,"CodeGeeks"+i);
			  pstmt.setInt(3,i);
			  pstmt.setInt(4, i+i);
			  pstmt.addBatch();
			  
			  count++;
			  
			  if(count % batchSize == 0){
				  System.out.println("Commit the batch");
				  result = pstmt.executeBatch();
				  System.out.println("Number of rows inserted: "+ result.length);
				  connection.commit();
			  }			  
			}
						
			if(pstmt!=null)
				pstmt.close();
			if(connection!=null)
				connection.close();
				
			 Calendar calendar2 = Calendar.getInstance();

				// Get start time (this needs to be a global variable).
				Date endDate = calendar2.getTime();
				long sumDate = endDate.getTime() - startDate.getTime();				
				System.out.println("Total time " + sumDate);					
		  }
}
