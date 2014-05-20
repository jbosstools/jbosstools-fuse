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

import static org.osgi.framework.Constants.BUNDLE_ACTIVATIONPOLICY;
import static org.osgi.framework.Constants.BUNDLE_CLASSPATH;
import static org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION;
import static org.osgi.framework.Constants.BUNDLE_NAME;
import static org.osgi.framework.Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VENDOR;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.util.IClassFileReader;
import org.eclipse.jdt.core.util.IConstantPoolConstant;
import org.eclipse.jdt.core.util.IConstantPoolEntry;
import org.eclipse.jdt.core.util.IConstantValueAttribute;
import org.eclipse.jdt.core.util.IFieldInfo;

/**
 * 
 */
public class IDoc3Archive extends SAPArchive {

	private static final String JCOIDOC_VERSION_STRING_DELIMITER = " "; //$NON-NLS-1$
	private static final String JCOIDOC_CLASSFILE_ENTRY = "com/sap/conn/idoc/jco/JCoIDoc.class"; //$NON-NLS-1$

	/**
	 * The plug-in name for SAP IDoc library.
	 */
	public static final String PLUGIN_IDOC = "com.sap.conn.jco.idoc"; //$NON-NLS-1$

	// /////////////////////////////////
	// Bundle Manifest Values
	//

	public static final String BUNDLE_CLASS_PATH_VALUE = "bin/,\n sapidoc3.jar"; //$NON-NLS-1$
	public static final String EXPORT_PACKAGE_VALUE = "com.sap.conn.idoc,com.sap.conn.idoc.jco,com.sap.conn.idoc.rt.cp,com.sap.conn.idoc.rt.record,com.sap.conn.idoc.rt.record.impl,com.sap.conn.idoc.rt.trace,com.sap.conn.idoc.rt.util,com.sap.conn.idoc.rt.xml"; //$NON-NLS-1$
	

	//
	// /////////////////////////////////

	// /////////////////////////////////
	// SAP IDoc3 Archive Filename Paths
	//

	/**
	 * Path to SAP IDoc3 Jar File in archive
	 */
	public static final String SAPIDOC3_JAR = "sapidoc3.jar"; //$NON-NLS-1$

	//
	// /////////////////////////////////

	private static char[] VERSION_NAME = new char[] { 'V', 'E', 'R', 'S', 'I', 'O', 'N' };

	protected byte[] buf = new byte[32 * 1024];

	protected String name;
	protected Map<String, String> manifest = new HashMap<String, String>();
	protected long lastModified;

	private String version = ""; //$NON-NLS-1$

	private byte[] sapidoc3jar;

	private boolean isValid;

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public IDoc3Archive(String filename) throws IOException {
		this.name = filename;
		InputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			File file = new File(filename);
			this.lastModified = file.lastModified();
			is = new FileInputStream(file);
			os = new ByteArrayOutputStream();
			while (true) {
				int numRead = is.read(this.buf, 0, this.buf.length);
				if (numRead == -1) {
					break;
				}
				os.write(this.buf, 0, numRead);
			}
			readArchiveFile(filename, os.toByteArray());
			readIDoc3JarFile();
			readVersion();
			this.isValid = true;
		} catch (IOException e) {
			this.isValid = false;
			throw e;
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();

		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return this.isValid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, byte[]> getContents() {
		return this.contents;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getManifest() {
		return this.manifest;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getIDoc3JarFile() {
		return this.sapidoc3jar;
	}

	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleName() {
		return PLUGIN_IDOC;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] getSapidoc3jar() {
		return this.sapidoc3jar;
	}
	
	/**
	 * 
	 * @param settings
	 * @throws IOException
	 */
	public void buildIDoc3Plugin(IDoc3ImportSettings settings) throws IOException {
		InputStream is = null;
		JarOutputStream target = null;
		byte[] buf = new byte[32 * 1024];

		try {
			// Create Jar output stream using manifest file
			String bundleFilename = settings.getBundleFilename();
			target = new JarOutputStream(new FileOutputStream(bundleFilename));
			
			// Create and populate manifest file.
			byte[] manifest = createBundleManifestFile(settings);
			JarEntry manifestEntry = new JarEntry(settings.getBundleManifestEntry());
			manifestEntry.setTime(this.lastModified);
			target.putNextEntry(manifestEntry);
			is = new ByteArrayInputStream(manifest);
			while (true) {
				int numRead = is.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				target.write(buf, 0, numRead);
			}
			target.closeEntry();
			
			// Populate IDoc3 jar into root of jar
			JarEntry jco3JarEntry = new JarEntry(settings.getBundleIDoc3JarEntry());
			jco3JarEntry.setTime(this.lastModified);
			target.putNextEntry(jco3JarEntry);
			is = new ByteArrayInputStream(this.sapidoc3jar);
			while (true) {
				int numRead = is.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				target.write(buf, 0, numRead);
			}
			target.closeEntry();
		} catch (Exception e) {
			throw new IOException(Messages.IDoc3Archive_FailedToBuildJCo3Plugin, e);
		} finally {
			if (is != null) {
				is.close();
			}
			if (target != null) {
				target.close();
			}
		}
		
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readIDoc3JarFile() throws IOException {
		byte[] sapidoc3jar = this.contents.get(SAPIDOC3_JAR);
		if (sapidoc3jar == null) {
			throw new IOException(MessageFormat.format(Messages.IDoc3Archive_FileIsMissingFromArchive, SAPIDOC3_JAR));
		}
		this.sapidoc3jar = sapidoc3jar;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readVersion() throws IOException {
		Map<String, byte[]> idoc3Contents = new HashMap<String, byte[]>();
		readJARFile(this.sapidoc3jar, idoc3Contents);
		byte[] jco3IdocContents = idoc3Contents.get(JCOIDOC_CLASSFILE_ENTRY);
		ByteArrayInputStream bais = new ByteArrayInputStream(jco3IdocContents);
		IClassFileReader classfileReader = ToolFactory.createDefaultClassFileReader(bais, IClassFileReader.ALL);
		IFieldInfo[] fieldInfos = classfileReader.getFieldInfos();
		for (int i = 0; i < fieldInfos.length; i++) {
			if (Arrays.equals(fieldInfos[i].getName(), VERSION_NAME)) {
				IConstantValueAttribute constantValueAttribute = fieldInfos[i].getConstantValueAttribute();
				if (constantValueAttribute != null) {
					IConstantPoolEntry constantPoolEntry = constantValueAttribute.getConstantValue();
					if (constantPoolEntry.getKind() == IConstantPoolConstant.CONSTANT_String) {
						this.version = constantPoolEntry.getStringValue();
						if (this.version != null) {
							this.version = this.version.split(JCOIDOC_VERSION_STRING_DELIMITER)[0];
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param settings
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private byte[] createBundleManifestFile(IDoc3ImportSettings settings) throws IOException {
		StringBuilder manifest = new StringBuilder();
		writeAttribute(manifest, Attributes.Name.MANIFEST_VERSION.toString(), MANIFEST_VERSION_VALUE);
		writeAttribute(manifest, BUNDLE_MANIFESTVERSION, BUNDLE_MANIFEST_VERSION_VALUE);
		writeAttribute(manifest, BUNDLE_NAME, settings.getBundleName());
		writeAttribute(manifest, BUNDLE_SYMBOLICNAME, settings.getBundleSymbolicName());
		writeAttribute(manifest, BUNDLE_VERSION, settings.getBundleVersion());
		writeAttribute(manifest, BUNDLE_CLASSPATH, BUNDLE_CLASS_PATH_VALUE);
		writeAttribute(manifest, BUNDLE_VENDOR, settings.getBundleVendor());
		writeAttribute(manifest, BUNDLE_REQUIREDEXECUTIONENVIRONMENT,
				ImportUtils.getExecutionEnvironment(settings.getRequiredExecutionEnvironmentIndex()));
		writeAttribute(manifest, EXPORT_PACKAGE, EXPORT_PACKAGE_VALUE);
		writeAttribute(manifest, BUNDLE_ACTIVATIONPOLICY, BUNDLE_ACTIVATION_POLICY_VALUE);
		return manifest.toString().getBytes(MANIFEST_ENCODING);
	}
}
