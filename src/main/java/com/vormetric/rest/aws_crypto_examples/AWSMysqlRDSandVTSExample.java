package com.vormetric.rest.aws_crypto_examples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
/*
 * 
CREATE TABLE Persons (
    PersonID int,
    LastName varchar(255),
    FirstName varchar(255),
    Address varchar(255),
    City varchar(255)
); 
 */
import com.vormetric.rest.helperclasses.VormetricCryptoServerHelper;
import com.vormetric.rest.helperclasses.VormetricCryptoServerSettings;

public class AWSMysqlRDSandVTSExample {

	public static void main(String[] args) throws Exception {
//Valid options are encrypt or tokenize
		String action = "tokenize";

		VormetricCryptoServerSettings vcs = new VormetricCryptoServerSettings();

		Calendar calendar = Calendar.getInstance();

		// Get start time (this needs to be a global variable).
		Date startDate = calendar.getTime();

		Connection connection = ConnectionObject.getConnection();

		tokenizeorencryptdata(vcs, connection, action, 10, 5);
		detokenizeordecryptdata(vcs, connection, action);

		if (connection != null)
			connection.close();

		Calendar calendar2 = Calendar.getInstance();

		// Get start time (this needs to be a global variable).
		Date endDate = calendar2.getTime();
		long sumDate = endDate.getTime() - startDate.getTime();
		System.out.println("Total time " + sumDate);
	}

	static void detokenizeordecryptdata(VormetricCryptoServerSettings vcs, Connection connection, String action)
			throws Exception {

		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String results;

			String sql = "SELECT PersonID, LastName, FirstName, Address FROM Persons";
			ResultSet rs = stmt.executeQuery(sql);
			// STEP 5: Extract data from result set
			while (rs.next()) {
				// Retrieve by column name

				int id = rs.getInt("PersonID");
				String last = rs.getString("LastName");
				String first = rs.getString("FirstName");
				String addr = rs.getString("Address");

				// Display values
				if (action.equalsIgnoreCase("tokenize")) {
					results = VormetricCryptoServerHelper.doDeTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(),
							vcs.getvcspassword(), vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), last,
							"detokenize");
				} else {
					results = VormetricCryptoServerHelper.doDeCryptData(vcs.getvcstokenserver(), vcs.getvcsuserid(),
							vcs.getvcspassword(), last, vcs.getvcsalg(), vcs.getvcsivtext(), "decrypt",
							vcs.getvcsencryptdecryptkey());
				}
				System.out.print("ID: " + id);

				System.out.print(", last: " + last);
				System.out.print(", last detokenized: " + results);
				System.out.print(", First: " + first);
				System.out.println(", addr: " + addr);
			}
			rs.close();

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					connection.close();
			} catch (SQLException se) {
			} // do nothing
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");

	}

	static void tokenizeorencryptdata(VormetricCryptoServerSettings vcs, Connection connection, String action,
			int nbrofrecords, int batchqty) throws Exception {

		String SQL = "insert into Persons values (?,?,?,?,?)";
		// String SQL = "insert into person values (?,?,?,?)";
		int batchSize = batchqty;
		// int batchSize = 50;
		int count = 0;
		int[] result;
		int size = nbrofrecords;
		connection.setAutoCommit(false);
		PreparedStatement pstmt = connection.prepareStatement(SQL);
		String results = null;
		String sensitive = null;
		// String dataarray[] = new String[size];

		for (int i = 1; i <= size; i++) {

			sensitive = randomAlphaNumeric(15);
			if (action.equalsIgnoreCase("tokenize")) {
				results = VormetricCryptoServerHelper.doTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(),
						vcs.getvcspassword(), vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), sensitive, action);
			} else {
				results = VormetricCryptoServerHelper.doEncryptData(vcs.getvcstokenserver(), vcs.getvcsuserid(),
						vcs.getvcspassword(), sensitive, vcs.getvcsalg(), vcs.getvcsivtext(), "encrypt",
						vcs.getvcsencryptdecryptkey());
			}
			pstmt.setInt(1, i);
			pstmt.setString(2, results);
			pstmt.setString(3, "FirstName");
			pstmt.setString(4, sensitive + " Addr");
			pstmt.setString(5, action);
			pstmt.addBatch();

			count++;

			if (count % batchSize == 0) {
				System.out.println("Commit the batch");
				result = pstmt.executeBatch();
				System.out.println("Number of rows inserted: " + result.length);
				connection.commit();
			}
		}

		if (pstmt != null)
			pstmt.close();
		// if(connection!=null)
		// connection.close();

	}

	//private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

}
