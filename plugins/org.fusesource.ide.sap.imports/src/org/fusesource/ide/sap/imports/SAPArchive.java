/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
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
	protected static final String MANIFEST_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final String MANIFEST_VERSION_VALUE = "1.0"; //$NON-NLS-1$
	public static final String BUNDLE_MANIFEST_VERSION_VALUE = "2"; //$NON-NLS-1$
	public static final String BUNDLE_ACTIVATION_POLICY_VALUE = "lazy"; //$NON-NLS-1$
	protected Map<String, byte[]> contents = new HashMap<String, byte[]>();

	protected void readArchiveFile(String filename, byte[] fileBytes) throws IOException {
		if (filename.toLowerCase().endsWith(ZIP_EXTENTION)) { //$NON-NLS-1$
			readZIPFile(fileBytes);
		} else if (filename.toLowerCase().endsWith(TGZ_EXTENTION)) { //$NON-NLS-1$
			readTGZFile(fileBytes);
		} else if (filename.toLowerCase().endsWith(TAR_GZ_EXTENTION)) { //$NON-NLS-1$
			readTGZFile(fileBytes);
		} else if (filename.toLowerCase().endsWith(TAR_EXTENTION)) { //$NON-NLS-1$
			readTarFile(fileBytes);
		} else {
			throw new UnsupportedOperationException();
		}
	
		if (contents.size() == 1) {
			filename = contents.keySet().iterator().next();
			fileBytes = contents.values().iterator().next();
			if (filename.toLowerCase().endsWith(ZIP_EXTENTION) || filename.toLowerCase().endsWith(TGZ_EXTENTION)
					|| filename.toLowerCase().endsWith(TAR_GZ_EXTENTION) || filename.toLowerCase().endsWith(TAR_EXTENTION)) {
				readArchiveFile(filename, fileBytes);
			}
		}
	
	}

	protected void readZIPFile(byte[] fileBytes) throws IOException {
		contents.clear();
		byte[] buf = new byte[32 * 1024];
		InputStream fs = new ByteArrayInputStream(fileBytes);
		ZipInputStream zis = new ZipInputStream(fs);
		for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while (true) {
				int numRead = zis.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				os.write(buf, 0, numRead);
			}
			os.close();
			contents.put(entry.getName(), os.toByteArray());
		}
		zis.close();
	}

	protected void readTGZFile(byte[] fileBytes) throws IOException {
		contents.clear();
		TarInputStream tin = new TarInputStream(new GZIPInputStream(new ByteArrayInputStream(fileBytes)));
		TarEntry tarEntry = tin.getNextEntry();
		while (tarEntry != null) {
			if (!tarEntry.isDirectory()) {
				// tar.getName()
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				tin.copyEntryContents(os);
				os.close();
				contents.put(tarEntry.getName(), os.toByteArray());
			}
			tarEntry = tin.getNextEntry();
		}
		tin.close();
	}

	protected void readTarFile(byte[] fileBytes) throws IOException {
		contents.clear();
		TarInputStream tin = new TarInputStream(new ByteArrayInputStream(fileBytes));
		TarEntry tarEntry = tin.getNextEntry();
		while (tarEntry != null) {
			if (!tarEntry.isDirectory()) {
				// tar.getName()
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				tin.copyEntryContents(os);
				os.close();
				contents.put(tarEntry.getName(), os.toByteArray());
			}
			tarEntry = tin.getNextEntry();
		}
		tin.close();
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
		JarInputStream jis = new JarInputStream(fs);
		for (ZipEntry entry = jis.getNextEntry(); entry != null; entry = jis.getNextEntry()) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while (true) {
				int numRead = jis.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				os.write(buf, 0, numRead);
			}
			os.close();
			contents.put(entry.getName(), os.toByteArray());
		}
		jis.close();
	}

}
