package com.coxandkings.deploy.osb.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.coxandkings.deploy.osb.DeployConfigJAR;

public class DeployConfigJARTask extends Task {
	
	private boolean mFailOnError;
	private String mConnParmsFile;
	private String mConfigJARFile;
	
	public void setFailOnError(boolean failOnError) {
		this.mFailOnError = failOnError;
	}
	
	public void setConnParmsFile(String connParmsFile) {
		this.mConnParmsFile = connParmsFile;
	}
	
	public void setConfigJARFile(String cfgJARFile) {
		this.mConfigJARFile = cfgJARFile;
	}
	
	public void execute() throws BuildException {
		try {
			DeployConfigJAR.deployConfigJAR(mConnParmsFile, mConfigJARFile);
		}
		catch (Exception x) {
			if (mFailOnError) {
				throw new BuildException(x);
			}
		}
	}

}
