
package com.coxandkings.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExpediaMDHHashGen {

public static String getSignature(String apiKey,String sharedSecret) throws Exception{
			MessageDigest md = MessageDigest.getInstance("MD5");
			long timeInSeconds = (System.currentTimeMillis() / 1000);
			String input = apiKey + sharedSecret + timeInSeconds;
			md.update(input.getBytes());
			return String.format("%032x", new BigInteger(1, md.digest()));
		
	}

}