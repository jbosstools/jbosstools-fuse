/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.tools.mavenutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author lheinema
 */
public class M2RepoChecker {

	public static final String DEFAULT_REPO_FOLDER = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository" + File.separator;
	
	private static long checkedFiles = 0;
	private static long checkedFolders = 0;
	private static List<String> corruptedFiles = new ArrayList<>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String repoPath = DEFAULT_REPO_FOLDER;
		if (args.length>0) {
			repoPath = args[0];
			System.out.println("Using custom repo folder: " + repoPath);
		} else {
			System.out.println("Using default repo folder: " + repoPath);
		}
		
		File repoFolder = new File(repoPath);
		if (!repoFolder.exists() || !repoFolder.isDirectory()) {
			System.err.println("The given folder does not exist or is not a directory. Also make sure you have the rights to access it.");
			System.exit(0);
		}
		findCorruptedJarFiles(repoFolder);
		System.out.println("FINISHED - We checked " + checkedFiles + " files in " + checkedFolders + " folders with " + corruptedFiles.size() + " corrupted files detected!");
		for (String f : corruptedFiles) {
			System.err.println(f);
		}
	}
	
	private static void findCorruptedJarFiles(File folder) {
		checkedFolders++;
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				findCorruptedJarFiles(f);
			} 
			if (f.isFile() && (f.getName().toLowerCase().endsWith(".jar") || f.getName().toLowerCase().endsWith(".zip"))) {
				checkForCorruption(f);
			}
		}
	}
	
	private static void checkForCorruption(File file) {
		checkedFiles++;
		try (ZipFile zf = new ZipFile(file)) {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				ze.getName();
			}
		} catch (IOException ex) {
			handleCorruptedZip(file);
		}
	}
	
	private static void handleCorruptedZip(File file) {
		corruptedFiles.add(file.getPath());
		File parentFolder = file.getParentFile();
		if (parentFolder != null && parentFolder.exists() && parentFolder.isDirectory()) {
			try {
				for (File f : parentFolder.listFiles()) {
					if (f.getName().equals("..")) continue;
					Files.delete(f.toPath());
				}
				Files.delete(parentFolder.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
