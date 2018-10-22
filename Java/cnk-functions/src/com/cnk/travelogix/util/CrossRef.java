package com.cnk.travelogix.util;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class CrossRef {
	private static String SOURCE_SYSTEM_PROP = "{SOURCESYSTEM}";
	private static String SOURCE_ENTITY_PROP = "{SOURCEENTITY}";
	private static String SOURCE_VALUE_PROP  = "{SOURCEVALUE}";
	private static String TARGET_SYSTEM_PROP = "{TARGETSYSTEM}";
	
	private static Logger logger = Logger.getLogger(CrossRef.class.getName());
	
	public static String lookup(String srcSys, String srcEntity, String srcVal, String tgtSys) {
		String tgtVal = srcVal;
		String lookupCtxStr = getLookupCtxString(srcSys, srcEntity, srcVal, tgtSys);
		String svrURL = CrossRefURL.SingletonHolder.getServerURL();
		if (svrURL.isEmpty()) {
			logger.log(Level.WARNING, "Cross-reference server URL not found! Source system's entity value will be returned as target system entity value!!", lookupCtxStr);
			return tgtVal;
		}
		
		try {
			svrURL = svrURL.replace(SOURCE_SYSTEM_PROP, srcSys);
			svrURL = svrURL.replace(SOURCE_ENTITY_PROP, srcEntity);
			svrURL = svrURL.replace(SOURCE_VALUE_PROP, srcVal);
			svrURL = svrURL.replace(TARGET_SYSTEM_PROP, tgtSys);
			
			Client clnt = Client.create();
			WebResource rsc = clnt.resource(svrURL);
			String response = rsc.get(String.class);
			logger.log(Level.FINE, "Received response", response);
			
			JsonReader jsonRdr = Json.createReader(new StringReader(response));
			JsonArray matches = jsonRdr.readArray();
			if (matches.size() <= 0) {
				logger.log(Level.WARNING, "No target system mappings found! Source system's entity value will be returned as target system entity value!!", lookupCtxStr);
			}
			else {
				JsonObject firstMatch = matches.getJsonObject(0);
				String tgtValCode = firstMatch.getString("Target_Value_Code");
				logger.log(Level.FINE, "Retrieved Target System Code", tgtValCode);
				if (tgtValCode != null && tgtValCode.length() > 0) {
					tgtVal = tgtValCode;
				}
				else {
					logger.log(Level.WARNING, "Target system code is null or empty! Source system's entity value will be returned as target system entity value!!", lookupCtxStr);
				}
			}
		}
		catch (Exception x) {
			logger.log(Level.SEVERE, "An error occurred when retrieving entity value of target system! Source system's entity value will be returned as target system entity value!!", x);
		}
		
		return tgtVal;
	}

	private static String getLookupCtxString(String srcSys, String srcEntity, String srcVal, String tgtSys) {
		StringBuilder strBldr = new StringBuilder();
		return strBldr.append(srcSys).append('|').append(srcEntity).append('|').append(srcVal).append('|').append(tgtSys).toString();
	}
	
}
