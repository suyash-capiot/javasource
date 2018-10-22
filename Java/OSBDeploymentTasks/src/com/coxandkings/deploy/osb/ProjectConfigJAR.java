package com.coxandkings.deploy.osb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringEscapeUtils;

import com.bea.alsb.tools.configjar.ConfigJarException;
import com.bea.alsb.tools.configjar.ConfigJarLogger;
import com.bea.alsb.tools.configjar.ConfigJarText;
import com.bea.alsb.tools.configjar.container.OSBInstance;
import com.bea.alsb.tools.configjar.settings.ContentSet;
import com.bea.alsb.tools.configjar.settings.Source;
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


public class ProjectConfigJAR {
	
	private static String DVM_FUNCTION = "dvm:lookup";
	private static String DVM_XSL_FUNCTION = "DVMFunctions:lookup";
	private static String DVM_EXTENSION = ".dvm";
	private static String XREF_LOOKUP_FUNCTION = ":lookupXRef";
	//private static String XREF_POPULATE_FUNCTION = ":populate";
	private static String XREF_EXTENSION = ".xref";
	private static String SINGLE_QUOTE ="'";
	private static String DOUBLE_QUOTE ="\"";
	private static ResourceTypeCatalog catalog = new ResourceTypeCatalog();
	private static boolean traceEnabled;

	public static void create(String projectDir, String configJAR, boolean includeSystem, boolean enableTrace) throws ConfigJarException {
		traceEnabled = enableTrace;
		File projectdir = CommonUtils.validateDirectory(projectDir, false);
	    validateConfigJAR(configJAR);

		projectDir = CommonUtils.slashify(projectDir);
		File projDirFile = new File(projectDir);
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
			
			File parentdir = projectdir.getParentFile();
			traverse(contentset, parentdir, projectdir, resources, includeSystem);
			
			HashMap<Ref, File> source = new HashMap<Ref, File>();
			for (ResourceDef def : resources) {
			    source.put(def._ref, def._file);
			}
			Source src = new Source(source);
			CommonUtils.synchronizeSources(catalog, configService, session, src.getFiles());
			
			Vector<Ref> projRefs = new Vector<Ref>();
			Vector<Ref> syncedRefs = new Vector<Ref>();
			projRefs.addAll(source.keySet());
			
			Ref projRef = null;
			syncedRefs.addAll(source.keySet());
			
			while (projRefs.size() > 0 && (projRef = projRefs.firstElement()) != null) {
				traceMessage("----- Dependencies for " + projRef.getFullName() + " -----\n");
				source.clear();
				Set<Ref> dependencies = cfgMBean.getDependencies(projRef);
				if (Refs.PIPELINE_TYPE.endsWith(projRef.getTypeId()) || Refs.XSLT_TYPE.endsWith(projRef.getTypeId())) {
					String pipelineFile = CommonUtils.getRefPath(catalog, projDirFile.getParent(), projRef);
					ArrayList<Ref> dvmXRefDependencies = search(pipelineFile);
					for (Ref dvmXRefDependency : dvmXRefDependencies){
						if (syncedRefs.contains(dvmXRefDependency) == false) {
							String dvmXRefFile = CommonUtils.getRefPath(catalog, projDirFile.getParent(), dvmXRefDependency);
							traceMessage("\t" + dvmXRefFile +"\n");
							source.put(dvmXRefDependency, new File(dvmXRefFile));
						}
					}
				}
				
				Iterator<Ref> depIter = dependencies.iterator();
				while (depIter.hasNext()) {
					Ref currRef = depIter.next();
					ResourceType currResType = catalog.getResourceType(currRef.getTypeId());
					
					// If current dependency reference type is system and system references are not to be 
					// included then skip further processing
					if (currResType.isSystem() != includeSystem) {
						continue;
					}
					
					String dependencyFile = CommonUtils.getRefPath(catalog, projDirFile.getParent(), currRef);
					if ( syncedRefs.contains(currRef) == false ) {
						traceMessage("\t" + dependencyFile + "\n");
						source.put(currRef, new File(dependencyFile));
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
			
			//byte[] jarContent = cfgMBean.exportProjects(projSet, null);
			byte[] jarContent = cfgMBean.export(syncedRefs, false, null, null);
			saveJAR(jarContent, new File(configJAR));
			
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
	
	private static void validateConfigJAR(String configJAR) throws ConfigJarException {
		File cfgJarFile = new File(configJAR);
		if (cfgJarFile.getParentFile().exists() == false) {
			throw new ConfigJarException("Directory for creating project configuration JAR file does not exist.");
		}
	}

	private static void saveJAR(byte[] bytes, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();
	}
	
	private static void traverse(ContentSet contentset, File root, File dir, List<ResourceDef> resources, boolean system) {
		File[] children = dir.listFiles();
		if (children == null) {
			ConfigJarLogger.debug("Source: skipping directory " + dir.getPath() + " due to error reading contained files.");
			return;
		}
		for (File child : children) {
			if (child.isDirectory()) {
				if ((!system) && (!Ref.validateProjectOrFolderPart(null, child.getName(), null))) {
					ConfigJarLogger.debug("Source: skipping folder " + child.getPath() + " due to invalid folder name.");
				} 
				else {
					String fullname = child.getPath().substring(root.getPath().length() + 1);
					fullname = CommonUtils.slashify(fullname);
					if (!contentset.accept(fullname)){
						ConfigJarLogger.debug("Source: skipping resource " + child.getPath() + " due to content set exclusion.");
					}
					else {	
						traverse(contentset, root, child, resources, system);
					}
				}
			} 
			else if (child.isFile()) {
				if (!Ref.validateResourceLocalNamePart(null, child.getName(), null)) {
					ConfigJarLogger.debug("Source: skipping resource " + child.getPath() + " due to invalid name.");
				} 
				else {
					String fullname = child.getPath().substring(root.getPath().length() + 1);
					fullname = CommonUtils.slashify(fullname);
					if (!contentset.accept(fullname)) {
						ConfigJarLogger.debug("Source: skipping resource " + child.getPath() + " due to content set exclusion.");
					} 
					else {
						Ref ref = CommonUtils.makeRef(catalog, fullname, system);
						if (ref == null) {
							ConfigJarLogger.debug("Source: skipping resource " + child.getPath() + " due to unsupported ref.");
						} 
						else {
							ResourceDef resource = new ResourceDef(ref, child, fullname);
							resources.add(resource);
							ConfigJarLogger.debug("Source: adding resource " + child.getPath());
						}
					}
				}
			}
		}
	}
	
	public static ArrayList<Ref> search(String pipelineFile) {
		ArrayList<Ref> dvmXRefs = new ArrayList<Ref>();
		BufferedReader buffRdr = null;
		String line = null;
		int idx = -1;
		
		try {
			buffRdr = new BufferedReader(new FileReader(pipelineFile));
			while ((line = buffRdr.readLine()) != null) {
				line = StringEscapeUtils.unescapeHtml(line);
				
				if ((idx = line.indexOf(DVM_FUNCTION)) > -1  || (idx = line.indexOf(DVM_XSL_FUNCTION)) > -1) {
					
					//int startQuoteIdx = line.indexOf(SINGLE_QUOTE, idx) + 1;
					int startQuoteIdx = searchQuote(line, idx) + 1;
					//int endQuoteIdx = line.indexOf(SINGLE_QUOTE, startQuoteIdx);
					int endQuoteIdx = searchQuote(line, startQuoteIdx);
					String dvmName = line.substring(startQuoteIdx, endQuoteIdx);
					
					Ref parentRef = null;
					String dvmNameWithExt = (dvmName.toLowerCase().endsWith(DVM_EXTENSION)) ? dvmName : (dvmName + DVM_EXTENSION); 
					parentRef = CommonUtils.makeRef(catalog, dvmNameWithExt, false);
					if (dvmXRefs.contains(parentRef) == false) {
						dvmXRefs.add(parentRef);
					}
					
					continue;
				}
				
				if ((idx = line.indexOf(XREF_LOOKUP_FUNCTION)) > -1) {
					
					//int startQuoteIdx = line.indexOf(SINGLE_QUOTE, idx) + 1;
					int startQuoteIdx = searchQuote(line, idx) + 1;
					//int endQuoteIdx = line.indexOf(SINGLE_QUOTE, startQuoteIdx);
					int endQuoteIdx = searchQuote(line, startQuoteIdx);
					String xrefName = line.substring(startQuoteIdx, endQuoteIdx);
					
					Ref parentRef = null;
					String xrefNameWithExt = (xrefName.toLowerCase().endsWith(XREF_EXTENSION)) ? xrefName : (xrefName + XREF_EXTENSION); 
					parentRef = CommonUtils.makeRef(catalog, xrefNameWithExt, false);
					if (dvmXRefs.contains(parentRef) == false) {
						dvmXRefs.add(parentRef);
					}
					
					continue;
				}
					
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		return dvmXRefs;
	}
	
	private static int searchQuote(String str, int offset) {
		int sqIdx = str.indexOf(SINGLE_QUOTE, offset);
		int dqIdx = str.indexOf(DOUBLE_QUOTE, offset);

		if (sqIdx > -1 && dqIdx == -1) {
			return sqIdx;
		}
		if (sqIdx == -1 && dqIdx > -1) {
			return dqIdx;
		}
		
		return (sqIdx < dqIdx) ? sqIdx : dqIdx;
	}

	public static void main(String[] args) {
		try {
			create(args[0], args[1], false, true);
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		System.exit(0);
	}

}
