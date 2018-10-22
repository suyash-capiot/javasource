package com.coxandkings.deploy.common;

import java.io.File;
import java.util.Map;

import com.bea.alsb.tools.configjar.ConfigJarException;
import com.bea.alsb.tools.configjar.ConfigJarLogger;
import com.bea.alsb.tools.configjar.ConfigJarText;
import com.bea.alsb.tools.configjar.synchronize.Synchronizer;
import com.bea.wli.config.ConfigService;
import com.bea.wli.config.Ref;
import com.bea.wli.sb.resources.source.ResourceType;
import com.bea.wli.sb.resources.source.ResourceTypeCatalog;

public class CommonUtils {

	private static char FWD_SLASH_CHAR = '/';
	
	public static String getRefPath(ResourceTypeCatalog catalog, String parentDir, Ref currRef) {
		StringBuilder strBldr = new StringBuilder();
		strBldr.append(CommonUtils.slashify(parentDir));
		strBldr.append(FWD_SLASH_CHAR);
		strBldr.append(currRef.getFullName());
		strBldr.append('.');
		strBldr.append(catalog.getResourceType(currRef.getTypeId()).getDefaultExtension());
		return strBldr.toString();
	}
	
	public static Ref makeRef(ResourceTypeCatalog catalog, String fullname, boolean system) {
		int dotIdx = fullname.lastIndexOf('.');
		if (dotIdx == -1) {
			return null;
		}
		String ext = fullname.substring(dotIdx + 1);
		
		ResourceType type = catalog.getResourceTypeByExt(ext);
		if (type == null) {
			return null;
		}
		if (type.isSystem() != system) {
			return null;
		}
		try {
			if (system) {
				int slashIdx = fullname.lastIndexOf(FWD_SLASH_CHAR);
				fullname = fullname.substring(slashIdx + 1, dotIdx);
				return Ref.makeRef(type.getId(), type.getSystemFolder(), fullname);
			}
			fullname = fullname.substring(0, dotIdx);
			return Ref.makeRef(type.getId(), Ref.getNames(fullname));
		} 
		catch (Exception e) {
		}
		return null;
	}

	public static String slashify(String str) {
		if (File.separatorChar != FWD_SLASH_CHAR) {
			str = str.replace(File.separatorChar, FWD_SLASH_CHAR);
		}
		return str;
	}	

	public static void synchronizeSources(ResourceTypeCatalog catalog, ConfigService configService, String sessionId, Map<Ref, File> fileMap) throws ConfigJarException {
		try {
			Synchronizer sync = new Synchronizer(configService, catalog);
			sync.synchronize(sessionId, fileMap);
		}
		catch (Exception e) {
		    ConfigJarLogger.error(e);
		    throw new ConfigJarException(e, ConfigJarText.ERROR_EXEC_SYNC, new String[] { e.getMessage() });
		}
	}
	
	public static File validateDirectory(String str, boolean system) throws ConfigJarException {
		if ((str == null) || (str.trim().isEmpty())) {
			throw new ConfigJarException(ConfigJarText.ERROR_SETTINGS_SRC_DIR_EMPTY);
		}
		File dir = new File(str.trim());
		dir = dir.getAbsoluteFile();
		try {
			dir = dir.getCanonicalFile();
		} 
		catch (Exception e) {
			throw new ConfigJarException(ConfigJarText.ERROR_SETTINGS_SRC_DIR_CANONICAL,
					new String[] { str, e.getMessage() });
		}
		if (dir.isFile()) {
			throw new ConfigJarException(ConfigJarText.ERROR_SETTINGS_SRC_DIR_ISFILE, new String[] { str });
		}
		if ((!system) && (!Ref.validateProjectOrFolderPart(null, dir.getName(), null))) {
			throw new ConfigJarException(ConfigJarText.ERROR_SETTINGS_SRC_DIR_INVALID_PROJECT, new String[] { str });
		}
		return dir;
	}


}
