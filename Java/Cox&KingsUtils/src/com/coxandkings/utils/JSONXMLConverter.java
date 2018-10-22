package com.coxandkings.utils;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.*;


public class JSONXMLConverter {

	public static String convertJSON2XML(String jsonStr) {
		JSONObject jsonObj = new JSONObject(jsonStr);
		return XML.toString(jsonObj);
	}
	
	public static String convertJSON2XMLAndReplaceNumericElements(String jsonStr) {
		return replaceNumericElements(convertJSON2XML(jsonStr));
	}

	public static String convertXML2JSON(String xmlStr) {
		JSONObject jsonObj = XML.toJSONObject(xmlStr);
		return jsonObj.toString();
	}
	
	private static String readFileContents(String inFile) throws Exception {
		StringBuilder strBldr = new StringBuilder();
		BufferedReader buffRdr = null;
		String line = null;
		
		try {
			buffRdr = new BufferedReader(new FileReader(inFile));
			while ((line = buffRdr.readLine()) != null) {
				strBldr.append(line);
			}
			
			return strBldr.toString();
		}
		finally {
			if (buffRdr != null ) {
				try { buffRdr.close();} 
				catch (Exception x) {}
			}
		}
	}

	private static String replaceNumericElements(String xmlStr) {
		boolean patternNotFound = false;
		int numericElem = 1;
		while (patternNotFound == false) {
			String searchStr = String.format("<%d>", numericElem);
			String replaceStr = String.format("<occurrence index=\"%d\">", numericElem);
			String searchEndStr = String.format("</%d>", numericElem);
			String replaceEndStr = "</occurrence>";
			String searchEmptyElemStr = String.format("<%d/>", numericElem);
			String replaceEmptyElemStr = String.format("<occurrence index=\"%d\"/>", numericElem);
			
			if (xmlStr.indexOf(searchStr) > -1) {
				xmlStr = xmlStr.replace(searchStr, replaceStr);
				xmlStr = xmlStr.replace(searchEndStr, replaceEndStr);
				numericElem++;
			}
			else if (xmlStr.indexOf(searchEmptyElemStr) > -1) {
				xmlStr.replace(searchEmptyElemStr, replaceEmptyElemStr);
				numericElem++;
			}
			else {
				patternNotFound = true;
			}
			
		}
		
		return xmlStr;

	}
	
	public static void main(String[] args) {
		try {
			String fileContents = readFileContents(args[1]); 
			//System.out.println(("J2X".equals(args[0])) ? convertJSON2XML(fileContents) : convertXML2JSON(fileContents));
			System.out.println(convertJSON2XMLAndReplaceNumericElements(fileContents));
		}
		catch (Exception x) {
			x.printStackTrace();
		}

		System.exit(0);
	}
}
