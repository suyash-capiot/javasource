package com.coxandkings.deploy.soa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.bea.alsb.tools.configjar.ConfigJarException;
import com.bea.alsb.tools.configjar.ConfigJarLogger;
import com.bea.alsb.tools.configjar.ConfigJarText;
import com.bea.alsb.tools.configjar.container.OSBInstance;
import com.bea.alsb.tools.configjar.settings.ContentSet;
import com.bea.alsb.tools.configjar.settings.Source;
//import com.bea.alsb.tools.configjar.synchronize.Synchronizer;
import com.bea.wli.config.ConfigContext;
import com.bea.wli.config.ConfigService;
import com.bea.wli.config.Ref;
import com.bea.wli.config.mbeans.ConfigMBean;
import com.bea.wli.sb.ALSBConfigService;
import com.bea.wli.sb.resources.source.ResourceType;
import com.bea.wli.sb.resources.source.ResourceTypeCatalog;
import com.bea.wli.sb.util.Refs;
import com.coxandkings.deploy.common.CommonUtils;
import com.coxandkings.deploy.common.ResourceDef;

public class SOAProjectMDSZip {

	private static int READ_BUFF_SIZE = 4096;
	private static char FWD_SLASH_CHAR = '/';
	private static String FWD_SLASH_STR = Character.toString(FWD_SLASH_CHAR);
	private static ResourceTypeCatalog catalog = new ResourceTypeCatalog();
	private static boolean traceEnabled;

	public static void create(String projectDir, String mdsDir, String outDir, boolean includeSystem, boolean enableTrace) throws ConfigJarException {
		traceEnabled = enableTrace;
	    File projectdir = CommonUtils.validateDirectory(projectDir, false);
	    projectDir = formatDirName(projectDir);
	    mdsDir = formatDirName(mdsDir);
	    outDir = formatDirName(outDir);
	    
		String configJAR = outDir + projectdir.getName() + "_MDS.jar";
		String configZIP = outDir + projectdir.getName() + "_MDS.zip";
	    validateConfigJAR(configJAR);

		String session = "configjar" + System.currentTimeMillis();
		OSBInstance.start();

		try {
			try {
				OSBInstance.discardSession(session);
				OSBInstance.createSession(session);
			} 
			catch (Exception e) {
				ConfigJarLogger.error(e);
				throw new ConfigJarException(e, ConfigJarText.ERROR_EXEC_CREATE_SESSION,
						new String[] { e.getMessage() });
			}

			ConfigService configService = ALSBConfigService.get().getConfigService();			
			ConfigContext cfgCtx = configService.getConfigContext(session);
			ConfigMBean cfgMBean = cfgCtx.getConfigMBean();

		    List<ResourceDef> resources = new ArrayList<ResourceDef>();
		    
		    ContentSet contentset = new ContentSet(null);
		    contentset.addExclude("*/pom.xml");
		    contentset.addExclude("*/.data");
			
			traverse(contentset, projectDir, mdsDir, resources, includeSystem);
			
			HashMap<Ref, File> source = new HashMap<Ref, File>();
			for (ResourceDef def : resources) {
			    source.put(def._ref, def._file);
			}
			Source src = new Source(source);
			CommonUtils.synchronizeSources(catalog,configService, session, src.getFiles());
			
			Vector<Ref> projRefs = new Vector<Ref>();
			Vector<Ref> syncedRefs = new Vector<Ref>();
			projRefs.addAll(source.keySet());
			
			Ref projRef = null;
			syncedRefs.addAll(source.keySet());
			
			while (projRefs.size() > 0 && (projRef = projRefs.firstElement()) != null) {
				traceMessage("----- Dependencies for " + projRef.getFullName() + " -----\n");
				source.clear();
				Set<Ref> dependencies = cfgMBean.getDependencies(projRef);
				Iterator<Ref> depIter = dependencies.iterator();
				while (depIter.hasNext()) {
					Ref currRef = depIter.next();
					ResourceType currResType = catalog.getResourceType(currRef.getTypeId());
					
					// If current dependency reference type is system and system references are not to be 
					// included then skip further processing
					if (currResType.isSystem() != includeSystem) {
						continue;
					}
					
					//String dependencyFile = CommonUtils.getRefPath(catalog, projDirFile.getParent(), currRef);
					String dependencyFile = CommonUtils.getRefPath(catalog, mdsDir, currRef);
					String currRefDftExt = currResType.getDefaultExtension();
					String currRefFullName = currRef.getFullName();
					if (currRefFullName.endsWith(currRefDftExt) == false) {
						currRefFullName += "." + currRefDftExt;
					}
					if ( syncedRefs.contains(currRef) == false ) {
						traceMessage("\t" + dependencyFile + "\n");
						//ResourceDef rscDef = new ResourceDef(currRef, new File(dependencyFile), currRef.getFullName());
						ResourceDef rscDef = new ResourceDef(currRef, new File(dependencyFile), currRefFullName);
						source.put(rscDef._ref, rscDef._file);
						resources.add(rscDef);
						if ( projRefs.contains(currRef) == false ) {
							projRefs.add(currRef);
						}
					}
				}
				CommonUtils.synchronizeSources(catalog, configService, session, source);
				syncedRefs.addAll(source.keySet());
				projRefs.remove(0);
			}
			
			Set<Ref> projSet = cfgMBean.getProjects();
			projSet.remove(Refs.SYSTEM_PROJECT_REF);
			
			writeJARFile(configJAR, resources);
			writeZIPFile(configZIP, configJAR);
			
			Path configJARPath = FileSystems.getDefault().getPath(configJAR);
			Files.delete(configJARPath);
		} 
		catch (Exception x) {
			x.printStackTrace();
		}
		finally {
			try {
				OSBInstance.discardSession(session);
			} 
			catch (Exception e) {
				ConfigJarLogger.error(e);
				throw new ConfigJarException(e, ConfigJarText.ERROR_EXEC_DISCARD_SESSION,
						new String[] { e.getMessage() });
			}
		}
	}
	
	private static void traceMessage(String message) {
		if (traceEnabled) {
			System.out.println(message);
		}
	}
	
	private static String formatDirName(String dirName) {
		dirName = CommonUtils.slashify(dirName);
		if (dirName.endsWith(FWD_SLASH_STR) == false) {
			dirName = dirName + FWD_SLASH_STR;
		}

		return dirName;
	}
	
	private static void traverse(ContentSet contentset, String projDir, String mdsDir, List<ResourceDef> resources, boolean system) {
		Path soaProjPath = FileSystems.getDefault().getPath(projDir);
		SOAProjectFileVisitor soaProjVisitor = new SOAProjectFileVisitor();
		
		try {
			Files.walkFileTree(soaProjPath, soaProjVisitor);
			ArrayList<String> mdsRefs = soaProjVisitor.getSOAProjectMDSRefs();
			for (String mdsRef : mdsRefs) {
				//Ref refMDS = makeRef(mdsRef, false);
				Ref refMDS = CommonUtils.makeRef(catalog, mdsRef, false);
				ResourceDef rscRef = new ResourceDef(refMDS, new File(mdsDir + mdsRef), mdsRef);
				resources.add(rscRef);
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	private static void validateConfigJAR(String configJAR) throws ConfigJarException {
		File cfgJarFile = new File(configJAR);
		if (cfgJarFile.getParentFile().exists() == false) {
			throw new ConfigJarException("Directory for creating project configuration JAR file does not exist.");
		}
	}

	public static void writeFileToOutputStream(OutputStream outStream, File wrtFile) throws Exception {
		int bytesRead = -1;
		byte[] readBuff = new byte[READ_BUFF_SIZE];
		FileInputStream fileIn = new FileInputStream(wrtFile);
		while ( (bytesRead = fileIn.read(readBuff)) > -1) {
			outStream.write(readBuff, 0, bytesRead);
		}
		fileIn.close();
	}
	
	public static void writeJARFile(String jarName, List<ResourceDef> syncedRefs) throws Exception {
		JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarName));
		for (ResourceDef mdsRef : syncedRefs) {
			JarEntry jarEntry = new JarEntry(mdsRef._fullname);
			jarOut.putNextEntry(jarEntry);
			writeFileToOutputStream(jarOut, mdsRef._file);
			jarOut.closeEntry();
		}
		jarOut.close();
	}
	
	public static void writeZIPFile(String zipName, String jarName) throws Exception {
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipName));
		File jarFile = new File(jarName);
		ZipEntry zipEntry = new ZipEntry(jarFile.getName());
		zipOut.putNextEntry(zipEntry);
		writeFileToOutputStream(zipOut, jarFile);
		zipOut.closeEntry();
		zipOut.close();
	}

	public static void main(String[] args) {
		try {
			create(args[0], args[1], args[2], false, true);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		System.exit(0);
	}
	

}
