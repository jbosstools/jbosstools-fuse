/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/
package org.fusesource.ide.imports.sap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class SAPArchive {

	private static final String TAR_EXTENTION = ".tar"; //$NON-NLS-1$
	private static final String TAR_GZ_EXTENTION = ".tar.gz"; //$NON-NLS-1$
	private static final String TGZ_EXTENTION = ".tgz"; //$NON-NLS-1$
	private static final String ZIP_EXTENTION = ".zip"; //$NON-NLS-1$
	private static final String END_OF_LINE = "\n"; //$NON-NLS-1$
	protected static final Charset MANIFEST_ENCODING = StandardCharsets.UTF_8; //$NON-NLS-1$
	public static final String MANIFEST_VERSION_VALUE = "1.0"; //$NON-NLS-1$
	public static final String BUNDLE_MANIFEST_VERSION_VALUE = "2"; //$NON-NLS-1$
	public static final String BUNDLE_ACTIVATION_POLICY_VALUE = "lazy"; //$NON-NLS-1$
	protected Map<String, byte[]> contents = new HashMap<>();

	protected void readArchiveFile(String filename, byte[] fileBytes) throws IOException {
		String fileNameLowerCased = filename.toLowerCase();
		if (fileNameLowerCased.endsWith(ZIP_EXTENTION)) { //$NON-NLS-1$
			readZIPFile(fileBytes);
		} else if (fileNameLowerCased.endsWith(TGZ_EXTENTION) || fileNameLowerCased.endsWith(TAR_GZ_EXTENTION)) { //$NON-NLS-1$ //$NON-NLS-2$
			readTGZFile(fileBytes);
		} else if (fileNameLowerCased.endsWith(TAR_EXTENTION)) { //$NON-NLS-1$
			readTarFile(fileBytes);
		} else {
			throw new IOException(Messages.SAPArchive_InvalidFile);
		}

		if (contents.size() == 1) {
			filename = contents.keySet().iterator().next();
			fileBytes = contents.values().iterator().next();
			if (fileNameLowerCased.endsWith(ZIP_EXTENTION) || fileNameLowerCased.endsWith(TGZ_EXTENTION)
					|| fileNameLowerCased.endsWith(TAR_GZ_EXTENTION) || fileNameLowerCased.endsWith(TAR_EXTENTION)) {
				readArchiveFile(filename, fileBytes);
			}
		}

	}

	protected void readZIPFile(byte[] fileBytes) throws IOException {
		contents.clear();
		byte[] buf = new byte[32 * 1024];
		InputStream fs = new ByteArrayInputStream(fileBytes);
		try(ZipInputStream zis = new ZipInputStream(fs)) {
			read(buf, zis);
		}
	}

	private void read(byte[] buf, ZipInputStream zis) throws IOException {
		for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
			try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				while (true) {
					int numRead = zis.read(buf, 0, buf.length);
					if (numRead == -1) {
						break;
					}
					os.write(buf, 0, numRead);
				}
				contents.put(entry.getName(), os.toByteArray());
			}
		}
	}

	protected void readTGZFile(byte[] fileBytes) throws IOException {
		contents.clear();
		try (TarInputStream tin = new TarInputStream(new GZIPInputStream(new ByteArrayInputStream(fileBytes)))) {
			read(tin);
		}
	}

	protected void readTarFile(byte[] fileBytes) throws IOException {
		contents.clear();
		try (TarInputStream tin = new TarInputStream(new ByteArrayInputStream(fileBytes))) {
			read(tin);
		}
	}

	private void read(TarInputStream tin) throws IOException {
		TarEntry tarEntry = tin.getNextEntry();
		while (tarEntry != null) {
			if (!tarEntry.isDirectory()) {
				// tar.getName()
				try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					tin.copyEntryContents(os);
					contents.put(tarEntry.getName(), os.toByteArray());
				}
			}
			tarEntry = tin.getNextEntry();
		}
	}

	protected void writeAttribute(StringBuilder manifest, String attributeName, String attributeValue) throws IOException {
		String line = attributeName + ": " + attributeValue; //$NON-NLS-1$
		while (line.getBytes(MANIFEST_ENCODING).length > 70) {
			// Find a line break
			int index = 70;
			String section = line.substring(0, index >= line.length() ? (line.length() - 1) : index);
			while (section.getBytes(MANIFEST_ENCODING).length > 70 && index > 0) {
				index--;
				section = line.substring(0, index);
			}
			if (index == 0) {
				throw new IOException();
			}
			manifest.append(section + END_OF_LINE);
			line = " " + line.substring(index); //$NON-NLS-1$
		}
		manifest.append(line + END_OF_LINE);
	}

	public void readJARFile(byte[] fileBytes, Map<String, byte[]> contents) throws IOException {
		contents.clear();
		byte[] buf = new byte[32 * 1024];
		InputStream fs = new ByteArrayInputStream(fileBytes);
		try(JarInputStream jis = new JarInputStream(fs)) {
			for (ZipEntry entry = jis.getNextEntry(); entry != null; entry = jis.getNextEntry()) {
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					while (true) {
						int numRead = jis.read(buf, 0, buf.length);
						if (numRead == -1) {
							break;
						}
						os.write(buf, 0, numRead);
					}
					contents.put(entry.getName(), os.toByteArray());
				}
			}
		}
	}

	protected void addJarEntry(JarOutputStream target, String jarEntryName, byte[] jarEntryContents, long lastModified) throws IOException {
		byte[] buf = new byte[32 * 1024];

		JarEntry jarEntry = new JarEntry(jarEntryName);
		jarEntry.setTime(lastModified);
		target.putNextEntry(jarEntry);
		InputStream is = new ByteArrayInputStream(jarEntryContents);
		while (true) {
			int numRead = is.read(buf, 0, buf.length);
			if (numRead == -1) {
				break;
			}
			target.write(buf, 0, numRead);
		}
		target.closeEntry();
	}
}
