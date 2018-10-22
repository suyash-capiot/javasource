This project wraps CryptoUtil class provided by Hybris team to expose it as custom XPath functions in XSL mapper and XQuery.
The required classes and config/properties files are exported by running cnk-functions-exportJAR.jardesc file in Eclipse.

Design Time Installation in JDeveloper
======================================
OSB projects: 
	1. Close JDeveloper, 
	2. Follow the run time installation instructions for OSB.
	3. Start JDeveloper.

SOA Projects: 
	1. Start JDeveloper.
	2. In JDeveloper, navigate to -
			Tools -> Preferences -> SOA
	3. Add cnk-functions.jar file as a 'User Defined FunctionJAR files'.
	4. Restart JDeveloper.

	
Run Time Installation for OSB
=============================
	1. Stop any running instances of WebLogic server (Admin, Integrated or Managed)
	2. Copy following three files to <Oracle Home>/osb/config/xpath-functions directory
			cnk-functions.jar
			cnk-functions.properties
			cnk-functions.xml
	3. Start instances of WebLogic server.


Run Time Installation for SOA
=============================
	1. Stop any running instances of WebLogic server (Admin, Integrated or Managed)
	2. Copy cnk-functions.jar file to <Oracle Home>/soa/soa/modules/oracl.soa.ext_11.1.1/classes directory
	3. In META-INF/MANIFEST.MF file that is located inside <Oracle Home>/soa/soa/modules/oracl.soa.ext_11.1.1/oracle.soa.ext.jar, 
	   confirm that Class-Path parameter has entry for cnk-functions.jar. 
	   If not, run 'ant' in <Oracle Home>/soa/soa/modules/oracl.soa.ext_11.1.1 directory. 
	3. Start instances of WebLogic server.
