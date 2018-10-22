package com.coxandkings.utils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
 
public class UrlEncode {

	public static String encode(String P) {

		String encoded = null;
		try {
			encoded = URLEncoder.encode(P, "UTF-8");
		}catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return encoded;

	}

}
