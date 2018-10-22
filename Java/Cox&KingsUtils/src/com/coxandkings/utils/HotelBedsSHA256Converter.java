package com.coxandkings.utils;

public class HotelBedsSHA256Converter {
	
	public static String getSignature(String apiKey,String sharedSecret) throws Exception{
		String token = apiKey+sharedSecret+(System.currentTimeMillis()/1000);
		return Sha256Converter.encryptSHA256String(token);
	}
	
}
