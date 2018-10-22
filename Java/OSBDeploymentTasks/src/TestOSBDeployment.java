import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import com.bea.wli.config.Ref;
import com.bea.wli.config.importexport.ImportResult;
import com.bea.wli.config.resource.Diagnostics;
import com.bea.wli.sb.management.configuration.ALSBConfigurationMBean;
import com.bea.wli.sb.management.configuration.SessionManagementMBean;

import weblogic.management.jmx.MBeanServerInvocationHandler;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;

public class TestOSBDeployment {
	
	private static int BUFFER_SIZE = 4096;
	
	public static void exportProjectConfigJAR(String configJAR) throws Exception {
		
		String session = "deployProj" + System.currentTimeMillis();

		// get the jmx connector
		JMXConnector conn = initConnection("localhost", 7001, "weblogic", "toipac5102");

		// get mbean connection
		MBeanServerConnection mbconn = conn.getMBeanServerConnection();

		// get domain service mbean. This is the topmost mbean
		DomainRuntimeServiceMBean domainService = (DomainRuntimeServiceMBean) MBeanServerInvocationHandler
				.newProxyInstance(mbconn, new ObjectName(DomainRuntimeServiceMBean.OBJECT_NAME));

		// obtain session management mbean to create a session.
		// This mbean instance can be used more than once to
		// create/discard/commit many sessions
		SessionManagementMBean sm = (SessionManagementMBean) domainService.findService(SessionManagementMBean.NAME,
				SessionManagementMBean.TYPE, null);

		try {
			// create a session
			sm.createSession(session);

			// obtain the ALSBConfigurationMBean instance that operates on the session that has
			// just been created. Notice that the name of the mbean contains the session name.
			ALSBConfigurationMBean alsbSession = (ALSBConfigurationMBean) domainService
					.findService(ALSBConfigurationMBean.NAME + "." + session, ALSBConfigurationMBean.TYPE, null);

			byte[] jarBytes = readConfigJARFile(configJAR);
			alsbSession.uploadJarFile(jarBytes);

			// Pass null to importUploaded method to mean the default import plan.
			ImportResult result = alsbSession.importUploaded(null);

			// print out status
			if (result.getImported().size() > 0) {
				System.out.println("The following resources have been successfully imported.");
				for (Ref ref : result.getImported()) {
					System.out.println("\t" + ref);
				}
			}
			if (result.getFailed().size() > 0) {
				System.out.println("The following resources have failed to be imported.");
				for (Map.Entry<Ref, Diagnostics> e : result.getFailed().entrySet()) {
					Ref ref = e.getKey();
					// Diagnostics object contains validation errors
					// that caused the failure to import this resource
					Diagnostics d = e.getValue();
					System.out.println("\t" + ref + ". reason: " + d);
				}
			}

			// Activate session
			sm.activateSession(session, "Deployment of " + configJAR);

		} catch (Exception x) {
			x.printStackTrace();
			sm.discardSession(session);
		}

	}

	public static byte[] readConfigJARFile(String configJAR) throws Exception {
		ByteArrayOutputStream fileBytes = new ByteArrayOutputStream();
		FileInputStream fileIn = null;

		try {
			fileIn = new FileInputStream(configJAR);

			byte[] byteBuff = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			while ((bytesRead = fileIn.read(byteBuff)) > -1) {
				fileBytes.write(byteBuff, 0, bytesRead);
			}

			return fileBytes.toByteArray();
		} 
		finally {
			if (fileIn != null) {
				try { fileIn.close(); }
				catch (Exception ex) { }
			}

			try { fileBytes.close(); }
			catch (Exception ex) { }
		}

	}

	public static JMXConnector initConnection(String hostname, int port, String username, String password)
			throws IOException, MalformedURLException {
		JMXServiceURL serviceURL = new JMXServiceURL("t3", hostname, port,
				"/jndi/" + DomainRuntimeServiceMBean.MBEANSERVER_JNDI_NAME);

		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, username);
		h.put(Context.SECURITY_CREDENTIALS, password);
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
		return JMXConnectorFactory.connect(serviceURL, h);
	}

	public static void main(String[] args) {
		try {
			exportProjectConfigJAR(args[0]);
		} 
		catch (Exception x) {
			x.printStackTrace();
		}

		System.exit(0);
	}

}
