package com.cnk.travelogix.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bea.wli.sb.functions.dvm.OSBDvmMetaDataManager;

import oracle.integration.platform.blocks.xpath.XPathContext;
import oracle.tip.dvm.DVMManager;
import oracle.tip.dvm.DVMManagerFactory;
import oracle.tip.dvm.exception.DVMException;

public class CrossRefURL {
	
	private static final String BPEL_DVM_LOC =  "oramds:/apps/common/Configurations.dvm";	
	private static final String OSB_DVM_LOC =  "SharedArtifacts/common/Configurations";
	private static final String SRC_COLUMN_NAME = "Name";
	private static final String SRC_COLUMN_VALUE = "CrossRefURL";
	private static final String TGT_COLUMN_NAME = "Value";
	private static final String CNK_FUNCTIONS_CLASS_NAME = CNKFunctions.class.getName();
	
	private static DVMManager dvmMgr = DVMManagerFactory.getDVMManager();
	private static OSBDvmMetaDataManager osbDvmMgr = new OSBDvmMetaDataManager();
	private String serverURL = "";
	
	private static Logger logger = Logger.getLogger(CrossRef.class.getName());
	
	private CrossRefURL() {
		Thread currThd = Thread.currentThread();
		StackTraceElement[] stkTrc = currThd.getStackTrace();
		
		try {
			for (int i=0; i < stkTrc.length; i++) {
				if (CNK_FUNCTIONS_CLASS_NAME.equals(stkTrc[i].getClassName())) {
					serverURL = dvmMgr.lookupValue(BPEL_DVM_LOC, SRC_COLUMN_NAME, SRC_COLUMN_VALUE, TGT_COLUMN_NAME, "");
					break;
				}
			}
			
			if (serverURL.isEmpty()) {
				XPathContext.setXPathContext(osbDvmMgr);
				serverURL = dvmMgr.lookupValue(OSB_DVM_LOC, SRC_COLUMN_NAME, SRC_COLUMN_VALUE, TGT_COLUMN_NAME, "");
			}
		}
		catch (DVMException dvmx) {
			logger.log(Level.SEVERE, "An exception occurred while retrieving cross-ref server URL!", dvmx);
		}
	}
	
	static class SingletonHolder {
		private static CrossRefURL xrefURL = new CrossRefURL();
		
		public static String getServerURL() {
			return xrefURL.serverURL;
		}
	}

}
