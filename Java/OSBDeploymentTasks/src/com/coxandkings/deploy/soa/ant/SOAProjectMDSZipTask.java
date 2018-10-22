package com.coxandkings.deploy.soa.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import com.coxandkings.deploy.soa.SOAProjectMDSZip;

public class SOAProjectMDSZipTask extends Task {
	private String mSOAProjectDir;
	private String mLocalMDSDir;
	private String mOutputZipDir;
	private boolean mFailOnError;
	private boolean mEnableTrace;
	
	public void setEnableTrace(boolean enableTrace) {
		this.mEnableTrace = enableTrace;
	}
	
	public void setFailOnError(boolean failOnError) {
		this.mFailOnError = failOnError;
	}
	
	public void setSOAProjectDir(String projDir) {
		this.mSOAProjectDir = projDir;
	}
	
	public void setLocalMDSDir(String mdsDir) {
		this.mLocalMDSDir = mdsDir;
	}
	
	public void setOutputZipDir(String outZipDir) {
		this.mOutputZipDir = outZipDir;
	}
	
	public void execute() throws BuildException {
		try {
			SOAProjectMDSZip.create(mSOAProjectDir, mLocalMDSDir, mOutputZipDir, false, mEnableTrace);
		}
		catch (Exception x) {
			if (mFailOnError) {
				throw new BuildException(x);
			}
		}
	}
	

}
