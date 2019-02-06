package com.vormetric.rest.aws_crypto_examples;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.services.kms.*;
import com.amazonaws.services.kms.model.*;

public class AWSBYOKEncryptExample {
	public static void main(final String[] args) {
		final AWSKMS kms = new AWSKMSClient();
		
	
		System.out.println("Demo showing encryption key created from Thales nShield BYOK or CCKM");

		final String plaintext = "My very secret message";
		final byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
		System.out.println("Plaintext: " + plaintext);

		// Encrypt the data
		final EncryptRequest encReq = new EncryptRequest();

		encReq.setKeyId("alias/youralias");

		encReq.setPlaintext(ByteBuffer.wrap(plaintextBytes));
		final ByteBuffer ciphertext = kms.encrypt(encReq).getCiphertextBlob();

		byte[] arr = ciphertext.array();
		String base64ciphertext = Base64.encodeBase64String(arr);

		// String base64String = Convert.ToBase64String(arr);
		System.out.println("ciphertext in bas64= " + base64ciphertext);

		// Decrypt the data
		final DecryptRequest decReq1 = new DecryptRequest();
		decReq1.setCiphertextBlob(ciphertext);
		final ByteBuffer decrypted = kms.decrypt(decReq1).getPlaintext();
		final String decryptedStr = new String(decrypted.array(), StandardCharsets.UTF_8);
		System.out.println("Decrypted: " + decryptedStr);

	}
}

