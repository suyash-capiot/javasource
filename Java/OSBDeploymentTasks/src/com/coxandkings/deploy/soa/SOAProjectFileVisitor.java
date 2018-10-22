package com.coxandkings.deploy.soa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class SOAProjectFileVisitor implements FileVisitor<Path> {

	private static String MDS_PREFIX = "oramds:/apps/";
	private static char DOUBLE_QUOTE_CHAR = '\"';
	private static char SINGLE_QUOTE_CHAR = '\'';

	private static String DOUBLE_QUOTE = Character.toString(DOUBLE_QUOTE_CHAR);
	private static String SINGLE_QUOTE = Character.toString(SINGLE_QUOTE_CHAR);

	private static String TEST_SUITES_DIR = "testSuites";
	private ArrayList<String> mdsRefs;
	
	public SOAProjectFileVisitor() {
		mdsRefs = new ArrayList<String>();
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		String currDirName = dir.toFile().getName();
		return (TEST_SUITES_DIR.equals(currDirName)) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		File inFile = file.toFile();
		if (inFile.getName().toLowerCase().endsWith(".jar") == false) {
			searchMDSRefs(file.toFile());
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public void searchMDSRefs(File inFile) {
		BufferedReader buffRdr = null;
		String line = null;
		int idx = -1;
		
		try {
			buffRdr = new BufferedReader(new FileReader(inFile));
			while ((line = buffRdr.readLine()) != null) {
				if ((idx = line.indexOf(MDS_PREFIX)) == -1) {
					continue;
				}

				int refStartIdx = idx + MDS_PREFIX.length();
				int refEndIdx = 0;
				if ( DOUBLE_QUOTE_CHAR == line.charAt(idx - 1)) {
					refEndIdx = line.indexOf(DOUBLE_QUOTE, refStartIdx);
				}
				else if (SINGLE_QUOTE_CHAR  == line.charAt(idx - 1)) {
					refEndIdx = line.indexOf(SINGLE_QUOTE, refStartIdx);
				}
				else {
					continue;
				}
				String refName = line.substring(refStartIdx, refEndIdx);
				
				if (mdsRefs.contains(refName) == false) {
					mdsRefs.add(refName);
				}
			}
		}
		catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	public ArrayList<String> getSOAProjectMDSRefs() {
		return mdsRefs;
	}

}
