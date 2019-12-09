/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.tools.mavenutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author lheinema
 */
public class M2RepoCleaner {

	public static final String DEFAULT_REPO_FOLDER = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository" + File.separator;
	
	private static final String ARG_PATHS = "FUSE_REPO_CLEANER_PATHS";
	private static final String ARG_DELETE_LASTUPDATED = "FUSE_REPO_CLEANER_DELETELASTUPDATED";
	
	private static long checkedFiles = 0;
	private static long checkedFolders = 0;
	private static List<String> corruptedFiles = new ArrayList<>();
	private static List<String> lastUpdatedFileDeleted = new ArrayList<>();
	
	private static boolean deleteLastUpdatedFiles = Boolean.parseBoolean(System.getProperty(ARG_DELETE_LASTUPDATED, "false"));
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String reposPath; 
		if (args.length>0) {
			reposPath = args[0];
		} else {
		 	reposPath = DEFAULT_REPO_FOLDER;
		}

		String repoPathsVar = System.getProperty(ARG_PATHS, "");
		if (repoPathsVar.trim().length()>0) {
			reposPath += ";" + repoPathsVar;
		}

		System.out.println("Using repo folders: " + reposPath);
		
		String[] paths = reposPath.split(";");
		for (String path : paths) {
			if (path.trim().isEmpty()) {
				continue;
			}
			
			File repoFolder = new File(path);
			if (!repoFolder.exists() || !repoFolder.isDirectory()) {
				System.err.println("The given folder " + path + " does not exist or is not a directory. Also make sure you have the rights to access it.");
			} else {
				findCorruptedJarFiles(repoFolder);
			}
		}
		System.out.println("FINISHED - We checked " + checkedFiles + " files in " + checkedFolders + " folders with " + corruptedFiles.size() + " corrupted files detected!");
		corruptedFiles.forEach(System.err::println);
		
		if (deleteLastUpdatedFiles) {
			if(lastUpdatedFileDeleted.isEmpty()){
				System.out.println("No .lastUpdated file found and deleted.");
			} else {
				System.out.println("Deleted "+ lastUpdatedFileDeleted.size() + " .lastupdated file.");
				lastUpdatedFileDeleted.forEach(System.err::println);
			}
		}
	}
	
	private static void findCorruptedJarFiles(File folder) {
		checkedFolders++;
		File[] files = folder.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				findCorruptedJarFiles(f);
			} 
			String fname = f.getName().toLowerCase();
			if (f.isFile() && (fname.endsWith(".jar") || fname.endsWith(".zip"))) {
				checkForCorruption(f);
			} else if (f.isFile() && fname.endsWith(".lastupdated") && f.exists() && deleteLastUpdatedFiles) {
				handleLastUpdatedFile(f);
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
				try(InputStream inputStream = zf.getInputStream(ze)){
					inputStream.read();
				}
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
					if ("..".equals(f.getName())) {
						continue;
					}
					Files.delete(f.toPath());
				}
				Files.delete(parentFolder.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static void handleLastUpdatedFile(File file) {
		lastUpdatedFileDeleted.add(file.getPath());
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}
}
