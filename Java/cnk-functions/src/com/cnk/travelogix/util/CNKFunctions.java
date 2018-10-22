package com.cnk.travelogix.util;


import java.util.List;

import oracle.fabric.common.xml.xpath.IXPathContext;
import oracle.fabric.common.xml.xpath.IXPathFunction;
import oracle.fabric.common.xml.xpath.XPathFunctionException;

public class CNKFunctions {

	public static String decrypt(String encryptedString) {
		return CryptoUtil.decrypt(encryptedString);
	}
	
	public static String encrypt(String cleartextString) {
		return CryptoUtil.encrypt(cleartextString);
	}
	
	public static String epochToDate(String epochValue){
		return EpochConverter.epochToDate(epochValue);
	}
	
	public static String calculateJourneyDuration(String departureTime, String departureOffset , String arrivalTime , String arrivalOffset){
		return Journey_Duration.calculateJourneyDuration(departureTime, departureOffset, arrivalTime, arrivalOffset);
	}
	
	public static String crossRefLookup(String srcSys, String srcEntity, String srcEntityVal, String tgtSys) {
		return CrossRef.lookup(srcSys, srcEntity, srcEntityVal, tgtSys);
	}
	
	@SuppressWarnings("rawtypes")
	private static String validateAndExtractStringParam(List args, String parmDesc, int parmIdx) throws XPathFunctionException {
		Object parmObj = args.get(parmIdx);
		if ((parmObj instanceof String) == false) {
			throw new XPathFunctionException("This function accepts only java.lang.String value for parameter " + parmDesc + ".");
		}
		
		return (String) parmObj;
	}
	
	public static class Decrypt implements IXPathFunction {

		@SuppressWarnings("rawtypes")
		@Override
		public Object call(IXPathContext xpathCtx, List args) throws XPathFunctionException {
			if (args.size() < 1 || args.size() > 1) {
				throw new XPathFunctionException("This function accepts encrypted string as the only argument.");
			}
			
			Object encryptedStringObj = args.get(0);
			if ((encryptedStringObj instanceof String) == false) {
				throw new XPathFunctionException("This function accepts only java.lang.String as argument type.");
			}
			
			return CNKFunctions.decrypt((String) encryptedStringObj);
		}
		
	}

	public static class Encrypt implements IXPathFunction {

		@SuppressWarnings("rawtypes")
		@Override
		public Object call(IXPathContext xpathCtx, List args) throws XPathFunctionException {
			if (args.size() < 1 || args.size() > 1) {
				throw new XPathFunctionException("This function accepts cleartext string as the only argument.");
			}
			
			Object cleartextStringObj = args.get(0);
			if ((cleartextStringObj instanceof String) == false) {
				throw new XPathFunctionException("This function accepts only java.lang.String as argument type.");
			}
			
			return CNKFunctions.encrypt((String) cleartextStringObj);
		}
		
	}
	
	public static class EpochToDate implements IXPathFunction {

		@SuppressWarnings("rawtypes")
		@Override
		public Object call(IXPathContext xpathCtx, List args) throws XPathFunctionException {
			if (args.size() < 1 || args.size() > 1) {
				throw new XPathFunctionException("This function accepts cleartext string as the only argument.");
			}
			
			Object cleartextStringObj = args.get(0);
			if ((cleartextStringObj instanceof String) == false) {
				throw new XPathFunctionException("This function accepts only java.lang.String as argument type.");
			}
			
			return CNKFunctions.epochToDate((String)cleartextStringObj);
		}
		
	}

	public static class JourneyDuration implements IXPathFunction {

		@SuppressWarnings("rawtypes")
		@Override
		public Object call(IXPathContext xpathCtx, List args) throws XPathFunctionException {
			if (args.size() < 1 || args.size() > 1) {
				throw new XPathFunctionException("This function accepts cleartext string as the only argument.");
			}
			
			Object cleartextStringObj1 = args.get(0);
			Object cleartextStringObj2 = args.get(1);
			Object cleartextStringObj3 = args.get(2);
			Object cleartextStringObj4 = args.get(3);
			if ((cleartextStringObj1 instanceof String) == false) {
				throw new XPathFunctionException("This function accepts only java.lang.String as argument type.");
			}
			
			return CNKFunctions.calculateJourneyDuration((String)cleartextStringObj1,(String)cleartextStringObj2,(String)cleartextStringObj3,(String)cleartextStringObj4);
		}
		
	}
	
	public static class CrossRefLookup implements IXPathFunction {

		@SuppressWarnings("rawtypes")
		@Override
		public Object call(IXPathContext xpathCtx, List args) throws XPathFunctionException {
			if (args.size() != 4) {
				throw new XPathFunctionException("This function accepts four String arguments: SourceSystem, SourceEntity, SourceEntityValue, TargetSystem.");
			}
			
			String srcSys 		= validateAndExtractStringParam(args, "SourceSystem", 0);
			String srcEntity 	= validateAndExtractStringParam(args, "SourceEntity", 1);
			String srcEntityVal	= validateAndExtractStringParam(args, "SourceEntityValue", 2);
			String tgtSys 		= validateAndExtractStringParam(args, "TargetSystem", 3);
			
			return CNKFunctions.crossRefLookup(srcSys, srcEntity, srcEntityVal, tgtSys);
		}

	}
}
