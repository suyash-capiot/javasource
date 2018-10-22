package com.coxandkings.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.sun.jersey.oauth.signature.HMAC_SHA1;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthRequest;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;


public class RedBusOAuthHeaderGenerator implements OAuthRequest {
	
	private String httpMethod, queryString;
	private HashMap<String, String> queryParams, headerVals;
	private URL sURL;
	
	private RedBusOAuthHeaderGenerator(String httpMethod, String svcURL, String qryString) throws Exception {
		this.httpMethod = httpMethod;
		this.sURL = new URL(svcURL);
		this.queryString = qryString;
		this.queryParams = new HashMap<String, String>();
		if (queryString != null && queryString.isEmpty() == false) {
			String[] qryParms = queryString.split("&");
			for (String qryParm : qryParms) {
				String[] parmDef = qryParm.split("=");
				queryParams.put(parmDef[0], parmDef[1]);
			}
		}
		this.headerVals = new HashMap<String, String>();
	}
	
	public static String generateHeader(String httpMethod, String svcURL, String qryString, String oAuthConsumerKey, String oAuthConsumerSecret) throws Exception{
		String oAuthHdr = "";
		
		OAuthParameters params = new OAuthParameters();
		params.consumerKey(oAuthConsumerKey);
		params.signatureMethod(HMAC_SHA1.NAME);
		params.version("1.0");
		params.timestamp();
		params.nonce();
		OAuthSecrets secrets = new OAuthSecrets();
		secrets.consumerSecret(oAuthConsumerSecret);
		
		RedBusOAuthHeaderGenerator rbhGen = new RedBusOAuthHeaderGenerator(httpMethod, svcURL, qryString);
		OAuthSignature.sign(rbhGen, params, secrets);
		
		List<String> authHeader = rbhGen.getHeaderValues("Authorization");
		if (authHeader.size() > 0) {
			oAuthHdr = authHeader.get(0);
		}
		return oAuthHdr;
	}

	@Override
	public String getRequestMethod() {
		return httpMethod;
	}

	@Override
	public URL getRequestURL() {
		return sURL;
	}

	@Override
	public Set<String> getParameterNames() {
		return queryParams.keySet();
	}

	@Override
	public List<String> getParameterValues(String paramString) {
		ArrayList<String> parmVals = new ArrayList<String>();
		parmVals.add(queryParams.get(paramString));
		return parmVals;
	}

	@Override
	public List<String> getHeaderValues(String paramString) {
		ArrayList<String> hdrVals = new ArrayList<String>();
		hdrVals.add(headerVals.get(paramString));
		return hdrVals;
	}

	@Override
	public void addHeaderValue(String paramString1, String paramString2)
			throws IllegalStateException {
		headerVals.put(paramString1, paramString2);
	}

}
