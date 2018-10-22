package com.coxandkings.deploy.osb.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.coxandkings.deploy.osb.ProjectConfigJAR;

public class ProjectConfigJARTask extends Task {

	private boolean mFailOnError;
	private String mProjectDir;
	private String mConfigJARFile;
	private boolean mIncludeSystem;
	private boolean mEnableTrace;
	
	public void setFailOnError(boolean failOnError) {
		this.mFailOnError = failOnError;
	}
	
	public void setProjectDirectory(String projDir) {
		this.mProjectDir = projDir;
	}
	
	public void setConfigJARFile(String cfgJARFile) {
		this.mConfigJARFile = cfgJARFile;
	}
	
	public void setIncludeSystem(boolean includeSystem) {
		this.mIncludeSystem = includeSystem;
	}
	
	public void setEnableTrace(boolean enableTrace) {
		this.mEnableTrace = enableTrace;
	}

	public void execute() throws BuildException {
		try {
			ProjectConfigJAR.create(mProjectDir, mConfigJARFile, mIncludeSystem, mEnableTrace);
		}
		catch (Exception x) {
			if (mFailOnError) {
				throw new BuildException(x);
			}
		}
	}
}