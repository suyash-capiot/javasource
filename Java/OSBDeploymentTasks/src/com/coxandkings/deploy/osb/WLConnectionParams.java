package com.coxandkings.deploy.osb;

import java.io.FileReader;
import java.util.Properties;

public class WLConnectionParams {
	
	private static String ADMIN_HOST_PROP ="adminhost";
	private static String ADMIN_PORT_PROP = "adminport";
	private static String USERNAME_PROP = "username";
	private static String PASSWORD_PROP = "password";
	
	private String mWLHost;
	private int mWLPort;
	private String mWLUser;
	private transient String mWLPassword;
	
	WLConnectionParams(String connParamsFile) throws Exception {
		Properties connProps = new Properties();
		connProps.load(new FileReader(connParamsFile));
		
		if (connProps.containsKey(ADMIN_HOST_PROP)) {
			mWLHost = connProps.getProperty(ADMIN_HOST_PROP);
		}
		else {
			throw new Exception("Property " + ADMIN_HOST_PROP + " is missing from connection parameters file " + connParamsFile);
		}
		
		if (connProps.containsKey(ADMIN_PORT_PROP)) {
			mWLPort = Integer.valueOf(connProps.getProperty(ADMIN_PORT_PROP));
		}
		else {
			throw new Exception("Property " + ADMIN_PORT_PROP +" is missing from connection parameters file " + connParamsFile);
		}
		
		if (connProps.containsKey(USERNAME_PROP)) {
			mWLUser = connProps.getProperty(USERNAME_PROP);
		}
		else {
			throw new Exception("Property " + USERNAME_PROP + " is missing from connection parameters file " + connParamsFile);
		}

		if (connProps.containsKey(PASSWORD_PROP)) {
			mWLPassword = connProps.getProperty(PASSWORD_PROP);
		}
		else {
			throw new Exception("Property " + PASSWORD_PROP + " is missing from connection parameters file " + connParamsFile);
		}

	}
	
	String getAdminHost() {
		return mWLHost;
	}
	
	int getAdminPort() {
		return mWLPort;
	}
	
	String getUserName() {
		return mWLUser;
	}
	
	String getPassword() {
		return mWLPassword;
	}

}
