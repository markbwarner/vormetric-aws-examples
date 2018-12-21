
import java.util.*;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.vormetric.rest.sample.VormetricCryptoServerHelper;
import com.vormetric.rest.sample.VormetricCryptoServerSettings;

public class AWSDynoDBAndVTS {

	//
	private static final String ADDRESS = "Address";
	private static final String EMAIL = "EmailAddress";
	private static final String TABLE = "VormetricDynoDBCrypto";
	final static AmazonDynamoDB ddb = new AmazonDynamoDBClient();

	public static void main(final String[] args) throws Exception {

		String action = "encrypt";
		
		VormetricCryptoServerSettings vcs = new VormetricCryptoServerSettings();
				
	String address = "Alice Lovelace, 123 Anystreet Rd., Anytown, USA";
//		String address = "34567883";
		String results = null;
		String email = "alice@example.com";
		action = "tokenize";
		results = VormetricCryptoServerHelper.doTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(), vcs.getvcspassword(),  vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), address, action);
		// Alice stores her address
		
		final Map<String, AttributeValue> item = new HashMap<>();
		item.put(EMAIL, new AttributeValue().withS(email));
		item.put(ADDRESS, new AttributeValue().withS(results));
		ddb.putItem(TABLE, item);

		address = "Mallory Evesdotir, 321 Evilstreed Ave., Despair, USA";
		results = null;
		email = "mallory@example.com";
		
		results = VormetricCryptoServerHelper.doTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(), vcs.getvcspassword(), vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), address, action);
		// Alice stores her address
		final Map<String, AttributeValue> item2 = new HashMap<>();
		item2.put(EMAIL, new AttributeValue().withS(email));
		item2.put(ADDRESS, new AttributeValue().withS(results));
		ddb.putItem(TABLE, item2);

		action = "detokenize";
		email = "alice@example.com";
		final Map<String, AttributeValue> item3 = ddb
				.getItem(TABLE, Collections.singletonMap(EMAIL, new AttributeValue().withS(email))).getItem();

		address = item3.get(ADDRESS).getS();
		System.out.println("address in dyndb " +  address);
		results = VormetricCryptoServerHelper.doDeTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(), vcs.getvcspassword(),  vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), address, action);

		email = "mallory@example.com";
		final Map<String, AttributeValue> item4 = ddb
				.getItem(TABLE, Collections.singletonMap(EMAIL, new AttributeValue().withS(email))).getItem();

		address = item4.get(ADDRESS).getS();
		results = VormetricCryptoServerHelper.doDeTokenizeData(vcs.getvcstokenserver(), vcs.getvcsuserid(), vcs.getvcspassword(),  vcs.getvcsTokengroup(), vcs.getvcsTokentemplate(), address, action);

	}

}