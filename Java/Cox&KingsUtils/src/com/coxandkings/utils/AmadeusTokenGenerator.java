package com.coxandkings.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest; 
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


public class AmadeusTokenGenerator {
	
	private static final String CHARACTER_SET = "Windows-1252";
	
    public static byte[] encryptSHA1Byte(String x) throws Exception {
        MessageDigest d = null;
        d = java.security.MessageDigest.getInstance("SHA-1");
        d.reset();
        d.update(x.getBytes(Charset.forName(CHARACTER_SET)));
        return d.digest();
    }

    public static byte[] encryptSHA1Byte(byte[] inBytes) throws Exception {
        MessageDigest d = null;
        d = java.security.MessageDigest.getInstance("SHA-1");
        d.reset();
        d.update(inBytes);
        return d.digest();
    }
	    
    public static String tokenGenerator(String pwd) throws Exception{
    	//Nonce Generator
    	String characters  = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	int max = 10;
    	int min = 0;
    	Random randm = new Random();
    	int ran = randm.nextInt((characters.length()-min))+min;
    	String uuid = "";    	
    	
    	for (int i = 0; i < max; i++) {
    		ran = randm.nextInt((characters.length()-min))+min;
    		uuid = uuid + characters.charAt(ran);
    	}
    	
    	//Current Time in UTC
    	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String ctime = sdf.format(new Date());
        ctime = ctime.split("\\+")[0]+"Z";
        
        if (pwd.equals(""))
        	pwd = "AMADEUS";

    	System.out.println(ctime);
    		        
    	String encodeNonce = Base64.getEncoder().encodeToString(uuid.getBytes());
    	
    	System.out.println(encodeNonce);
    	
    	byte[] pwdSHA1Bytes = encryptSHA1Byte(pwd);
    	
    	ByteArrayOutputStream inBytesStrm = new ByteArrayOutputStream();
    	inBytesStrm.write(uuid.getBytes(Charset.forName(CHARACTER_SET)));
    	inBytesStrm.write(ctime.getBytes(Charset.forName(CHARACTER_SET)));
    	inBytesStrm.write(pwdSHA1Bytes);
    	byte[] inBytes = inBytesStrm.toByteArray();
		inBytesStrm.close();
    	
    	//Password Digest Generation logic
		String hashpwd = Base64.getEncoder().encodeToString(encryptSHA1Byte(inBytes));
    	System.out.println(hashpwd);
    	
    	return hashpwd + "~#~" + encodeNonce + "~#~" + ctime;
    }
  
}
