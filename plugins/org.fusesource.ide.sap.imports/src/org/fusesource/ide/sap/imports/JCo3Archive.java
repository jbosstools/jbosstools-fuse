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
import static org.osgi.framework.Constants.BUNDLE_NATIVECODE;
import static org.osgi.framework.Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT;
import static org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME;
import static org.osgi.framework.Constants.BUNDLE_VENDOR;
import static org.osgi.framework.Constants.BUNDLE_VERSION;
import static org.osgi.framework.Constants.EXPORT_PACKAGE;
import static org.osgi.framework.Constants.FRAGMENT_HOST;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.eclipse.core.runtime.Platform;

public class JCo3Archive extends SAPArchive {

	private static final String SAPJCO_RELEASE_DELIMITER = "."; //$NON-NLS-1$

	private static final String MANIFEST_LINE_DELIMITER = ":"; //$NON-NLS-1$

	/**
	 * The plug-in name for platform-independent parts of the SAP Java
	 * Connector.
	 */
	public static final String PLUGIN_JCO = "com.sap.conn.jco"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Windows 32-bit platform native libraries.
	 */
	public static final String FRAGMENT_WINDOWS_32 = "com.sap.conn.jco.win32.x86"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Windows 64-bit Itanium platform native
	 * libraries.
	 */
	public static final String FRAGMENT_WINDOWS_64IA = "com.sap.conn.jco.win32.ia64"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Windows 64-bit x86 platform native libraries.
	 */
	public static final String FRAGMENT_WINDOWS_64X86 = "com.sap.conn.jco.win32.x86_64"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Linux 32-bit platform native libraries.
	 */
	public static final String FRAGMENT_LINUX_32 = "com.sap.conn.jco.linux.x86"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Linux 64-bit Itanium platform native libraries.
	 */
	public static final String FRAGMENT_LINUX_64IA = "com.sap.conn.jco.linux.ia64"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Linux 64-bit x86 platform native libraries.
	 */
	public static final String FRAGMENT_LINUX_64X86 = "com.sap.conn.jco.linux.x86_64"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Darwin 32-bit x86 platform native libraries.
	 */
	public static final String FRAGMENT_DARWIN_32 = "com.sap.conn.jco.osx.x86"; //$NON-NLS-1$

	/**
	 * The plug-in name for the Darwin 64-bit Itanium platform native libraries.
	 */
	public static final String FRAGMENT_DARWIN_64 = "com.sap.conn.jco.osx.x86_64"; //$NON-NLS-1$

	public enum JCoArchiveType {
		JCO_INVALID_ARCHIVE("Invalid JCo 3 Archive", "", "", "", "", "", ""),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		JCO_WIN32_X86_ARCHIVE("Microsoft Windows (x86 32 bit)", "NTintel", "sapjco3.dll", FRAGMENT_WINDOWS_32, "(& (osgi.os=win32) (osgi.arch=x86))", Platform.OS_WIN32, Platform.ARCH_X86),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_WIN32_IA64_ARCHIVE("Microsoft Windows (Itanium 64 bit)", "NTia64", "sapjco3.dll", FRAGMENT_WINDOWS_64IA, "(& (osgi.os=win32) (osgi.arch=ia64n))", Platform.OS_WIN32, Platform.ARCH_IA64),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_WIN32_X86_64_ARCHIVE("Microsoft Windows (x86 64 bit)", "NTAMD64", "sapjco3.dll", FRAGMENT_WINDOWS_64X86, "(& (osgi.os=win32) (osgi.arch=x86_64))", Platform.OS_WIN32, Platform.ARCH_X86_64),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_LINUX_X86_ARCHIVE("Linux (x86 32 bit)", "linuxintel", "libsapjco3.so", FRAGMENT_LINUX_32, "(& (osgi.os=linux) (osgi.arch=x86))", Platform.OS_LINUX, Platform.ARCH_X86),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_LINUX_IA64_ARCHIVE("Linux (Itanium 64 bit)", "linuxia64", "libsapjco3.so", FRAGMENT_LINUX_64IA, "(& (osgi.os=linux) (osgi.arch=ia64n))", Platform.OS_LINUX, Platform.ARCH_IA64),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_LINUX_X86_64_ARCHIVE("Linux (x86 64 bit)", "linuxx86_64", "libsapjco3.so", FRAGMENT_LINUX_64X86, "(& (osgi.os=linux) (osgi.arch=x86_64))", Platform.OS_LINUX, Platform.ARCH_X86_64),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_OSX_X86_ARCHIVE("Apple MacOS X (x86 32 bit)", "darwinintel", "libsapjco3.jnilib", FRAGMENT_DARWIN_32, "(& (osgi.os=macosx) (osgi.arch=x86))", Platform.OS_MACOSX, Platform.ARCH_X86),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JCO_OSX_X86_64_ARCHIVE("Apple MacOS X (x86 64 bit)", "darwinintel64", "libsapjco3.jnilib", FRAGMENT_DARWIN_64, "(& (osgi.os=macosx) (osgi.arch=x86_64))", Platform.OS_MACOSX, Platform.ARCH_X86_64); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
		private String description;
		private String sapjcoOs;
		private String nativeArchiveName;
		private String fragmentName;
		private String platformFilter;
		private String eclipseOS;
		private String eclipseOSArch;
	
		JCoArchiveType(String description, String sapjcoOs, String nativeArchiveName, String fragmentName, String platformFilter, String eclipseOS, String eclipseOSArch) {
			this.description = description;
			this.nativeArchiveName = nativeArchiveName;
			this.sapjcoOs = sapjcoOs;
			this.fragmentName = fragmentName;
			this.platformFilter = platformFilter;
			this.eclipseOS = eclipseOS;
			this.eclipseOSArch = eclipseOSArch;
		}
	
		public String getDescription() {
			return description;
		}
	
		public String getSapjcoOs() {
			return sapjcoOs;
		}
	
		public String getNativeArchiveName() {
			return nativeArchiveName;
		}
		
		public String getPluginName() {
			return PLUGIN_JCO;
		}
		
		public String getFragmentName() {
			return fragmentName;
		}
		
		public String getPlatformFilter() {
			return platformFilter;
		}
	
		public String getEclipseOS() {
			return eclipseOS;
		}

		public String getEclipseOSArch() {
			return eclipseOSArch;
		}

		public static JCoArchiveType getType(String sapjcoOsCode) {
			sapjcoOsCode = sapjcoOsCode.trim();
			if (sapjcoOsCode.equals(JCO_WIN32_X86_ARCHIVE.getSapjcoOs())) {
				return JCO_WIN32_X86_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_WIN32_IA64_ARCHIVE.getSapjcoOs())) {
				return JCO_WIN32_IA64_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_WIN32_X86_64_ARCHIVE.getSapjcoOs())) {
				return JCO_WIN32_X86_64_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_LINUX_X86_ARCHIVE.getSapjcoOs())) {
				return JCO_LINUX_X86_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_LINUX_IA64_ARCHIVE.getSapjcoOs())) {
				return JCO_LINUX_IA64_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_LINUX_X86_64_ARCHIVE.getSapjcoOs())) {
				return JCO_LINUX_X86_64_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_OSX_X86_ARCHIVE.getSapjcoOs())) {
				return JCO_OSX_X86_ARCHIVE;
			} else if (sapjcoOsCode.equals(JCO_OSX_X86_64_ARCHIVE.getSapjcoOs())) {
				return JCO_OSX_X86_64_ARCHIVE;
			} else {
				return JCO_INVALID_ARCHIVE;
			}
		}
		
		public static JCoArchiveType getTypeForCurrentPlatform() {
			String os = Platform.getOS();
			String arch = Platform.getOSArch();
			if (os.equals(Platform.OS_WIN32)) {
				if (arch.equals(Platform.ARCH_X86)) {
					return JCO_WIN32_X86_ARCHIVE;
				} else if (arch.equals(Platform.ARCH_IA64)) {
					return JCO_WIN32_IA64_ARCHIVE;
				} else if (arch.equals(Platform.ARCH_X86_64)) {
					return JCO_WIN32_X86_64_ARCHIVE;
				}
			} else if (os.equals(Platform.OS_LINUX)) {
				if (arch.equals(Platform.ARCH_X86)) {
					return JCO_LINUX_X86_ARCHIVE;
				} else if (arch.equals(Platform.ARCH_IA64)) {
					return JCO_LINUX_IA64_ARCHIVE;
				} else if (arch.equals(Platform.ARCH_X86_64)) {
					return JCO_LINUX_X86_64_ARCHIVE;
				}
			} else if (os.equals(Platform.OS_MACOSX)) {
				if (arch.equals(Platform.ARCH_X86)) {
					return JCO_OSX_X86_ARCHIVE;
				} else if (arch.equals(Platform.ARCH_X86_64)) {
					return JCO_OSX_X86_64_ARCHIVE;
				}
			}
			return JCO_INVALID_ARCHIVE;
		}
	
	}

	public static final String ECLIPSE_PLATFORM_FILTER = "Eclipse-PlatformFilter"; //$NON-NLS-1$

	

	// /////////////////////////////////
	// Bundle Manifest Values
	//

	public static final String BUNDLE_CLASS_PATH_VALUE = "bin/,\n sapjco3.jar,\n jni/"; //$NON-NLS-1$
	public static final String EXPORT_PACKAGE_VALUE = "com.sap.conn.jco, com.sap.conn.jco.ext, com.sap.conn.jco.monitor, com.sap.conn.jco.rt, com.sap.conn.jco.rt.json,  com.sap.conn.jco.server, com.sap.conn.jco.session, com.sap.conn.jco.support, com.sap.conn.jco.util";
	

	//
	// /////////////////////////////////

	// /////////////////////////////////
	// SAP JCo3 Archive Manifest Headers
	//

	public static final String SAPJCO_RELEASE = "sapjco release"; //$NON-NLS-1$
	public static final String SAPJCO_PATCH_LEVEL = "sapjco patch level"; //$NON-NLS-1$
	public static final String SAPJCO_OS = "sapjco os"; //$NON-NLS-1$

	//
	// /////////////////////////////////

	// /////////////////////////////////
	// SAP JCo3 Archive Filename Paths
	//

	/**
	 * Path to SAP JCo3 Jar File in archive
	 */
	public static final String SAPJCO3_JAR = "sapjco3.jar"; //$NON-NLS-1$
	/**
	 * Path to SAP JCo3 Manifest File in archive
	 */
	public static final String SAPJCOMANIFEST_MF = "sapjcomanifest.mf"; //$NON-NLS-1$

	//
	// /////////////////////////////////

	protected byte[] buf = new byte[32 * 1024];

	protected String name;
	protected JCoArchiveType type;
	protected Map<String, String> manifest = new HashMap<String, String>();
	protected long lastModified;

	private String version;

	private byte[] sapjco3jar;

	private byte[] nativelib;

	public JCo3Archive(String filename) throws IOException {
		name = filename;
		InputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			File file = new File(filename);
			lastModified = file.lastModified();
			is = new FileInputStream(file);
			os = new ByteArrayOutputStream();
			while (true) {
				int numRead = is.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				os.write(buf, 0, numRead);
			}
			readArchiveFile(filename, os.toByteArray());
			readArchiveType();
			readJCo3JarFile();
			readNativeLibraryFile();
		} catch (IOException e) {
			type = JCoArchiveType.JCO_INVALID_ARCHIVE;
			throw e;
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();

		}
	}

	public String getName() {
		return name;
	}

	public JCoArchiveType getType() {
		return type;
	}

	public Map<String, byte[]> getContents() {
		return contents;
	}

	public Map<String, String> getManifest() {
		return manifest;
	}

	public byte[] getJCo3JarFile() {
		return sapjco3jar;
	}

	public byte[] getNativeLibraryFile() {
		return nativelib;
	}

	public String getVersion() {
		if (version == null) {
			StringBuffer buf = new StringBuffer();

			String sapjcoRelease = manifest.get(SAPJCO_RELEASE).trim();
			String sapjcoPatchLevel = manifest.get(SAPJCO_PATCH_LEVEL).trim();
			if (sapjcoRelease != null && sapjcoRelease.length() > 0) {
				buf.append(sapjcoRelease);
				buf.append(SAPJCO_RELEASE_DELIMITER); //$NON-NLS-1$
				buf.append(sapjcoPatchLevel);
				version = buf.toString();
			}
		}
		return version;
	}
	
	public String getBundleName() {
		return type.getPluginName();
	}

	public byte[] getSapjco3jar() {
		return sapjco3jar;
	}

	public byte[] getNativelib() {
		return nativelib;
	}
	
	public boolean supportsCurrentPlatform() {
		if (Platform.getOS().equals(getType().getEclipseOS()) && Platform.getOSArch().equals(getType().getEclipseOSArch())) {
			return true;
		}
		return false;
	}

	public void buildJCoPlugin(JCo3ImportSettings settings) throws IOException {
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
			manifestEntry.setTime(lastModified);
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

			// create native library folder in jar
			JarEntry nativeDirEntry = new JarEntry(settings.getBundleNativeDirEntry());
			nativeDirEntry.setTime(lastModified);
			target.putNextEntry(nativeDirEntry);

			// Populate JCo3 jar into root of jar
			JarEntry jco3JarEntry = new JarEntry(settings.getBundleJCoJarEntry());
			jco3JarEntry.setTime(lastModified);
			target.putNextEntry(jco3JarEntry);
			is = new ByteArrayInputStream(sapjco3jar);
			while (true) {
				int numRead = is.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				target.write(buf, 0, numRead);
			}
			target.closeEntry();
		} catch (Exception e) {
			throw new IOException(Messages.JCo3Archive_FailedToBuildJCo3Plugin, e);
		} finally {
			if (is != null) {
				is.close();
			}
			if (target != null) {
				target.close();
			}
		}

	}

	public void buildJCoNativePlugin(JCo3ImportSettings settings) throws IOException {
		InputStream is = null;
		JarOutputStream target = null;
		byte[] buf = new byte[32 * 1024];

		try {
			// Create Jar output stream using manifest file
			String bundleFilename = settings.getFragmentFilename();
			target = new JarOutputStream(new FileOutputStream(bundleFilename));

			// Create and populate manifest file.
			byte[] manifest = createFragmentManifestFile(settings);
			JarEntry manifestEntry = new JarEntry(settings.getBundleManifestEntry());
			manifestEntry.setTime(lastModified);
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

			// create native library folder in jar
			JarEntry nativeDirEntry = new JarEntry(settings.getBundleNativeDirEntry());
			nativeDirEntry.setTime(lastModified);
			target.putNextEntry(nativeDirEntry);

			// Populate native library into jni folder.
			JarEntry jco3NativeLibEntry = new JarEntry(settings.getBundleNativeLibraryEntry());
			jco3NativeLibEntry.setTime(lastModified);
			target.putNextEntry(jco3NativeLibEntry);
			is = new ByteArrayInputStream(nativelib);
			while (true) {
				int numRead = is.read(buf, 0, buf.length);
				if (numRead == -1) {
					break;
				}
				target.write(buf, 0, numRead);
			}
			target.closeEntry();
		} catch (Exception e) {
			throw new IOException(Messages.JCo3Archive_FailedToBuildJCo3PluginFragment, e);
		} finally {
			if (is != null) {
				is.close();
			}
			if (target != null) {
				target.close();
			}
		}

	}

	private void readArchiveType() throws IOException {
		byte[] file = contents.get(SAPJCOMANIFEST_MF);
		if (file == null) {
			type = JCoArchiveType.JCO_INVALID_ARCHIVE;
			throw new IOException(MessageFormat.format(Messages.JCo3Archive_FileMissingFromArchive, SAPJCOMANIFEST_MF));
		}
		try {
			manifest = parseManifest(file);
		} catch (IOException e) {
			type = JCoArchiveType.JCO_INVALID_ARCHIVE;
			throw new IOException(Messages.JCo3Archive_UnableToParseArchiveManifestFile);
		}
		String sapjcoos = manifest.get(SAPJCO_OS);
		if (sapjcoos == null) {
			type = JCoArchiveType.JCO_INVALID_ARCHIVE;
			throw new IOException(MessageFormat.format(Messages.JCo3Archive_HeaderisMissingFromManifestFile,
					SAPJCO_OS));
		}
		type = JCoArchiveType.getType(sapjcoos);
		if (type == JCoArchiveType.JCO_INVALID_ARCHIVE) {
			throw new IOException(MessageFormat.format(Messages.JCo3Archive_OSPlatformIsNotSupported, sapjcoos));
		}
	}

	private void readJCo3JarFile() throws IOException {
		byte[] sapjco3jar = contents.get(SAPJCO3_JAR);
		if (sapjco3jar == null) {
			throw new IOException(MessageFormat.format(Messages.JCo3Archive_FileMissingFromArchive, SAPJCO3_JAR)); //$NON-NLS-1$
		}
		this.sapjco3jar = sapjco3jar;
	}

	private void readNativeLibraryFile() throws IOException {
		byte[] nativelib = contents.get(type.getNativeArchiveName());
		if (nativelib == null) {
			throw new IOException(MessageFormat.format(Messages.JCo3Archive_FileMissingFromArchive, type.getNativeArchiveName()));
		}
		this.nativelib = nativelib;
	}

	private Map<String, String> parseManifest(byte[] file) throws IOException {
		manifest.clear();
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] elements = line.split(MANIFEST_LINE_DELIMITER);
			if (elements.length >= 2) {
				manifest.put(elements[0], elements[1]);
			}
		}
		return manifest;
	}

	@SuppressWarnings("deprecation")
	private byte[] createBundleManifestFile(JCo3ImportSettings settings) throws IOException {
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

	@SuppressWarnings("deprecation")
	private byte[] createFragmentManifestFile(JCo3ImportSettings settings) throws IOException {
		StringBuilder manifest = new StringBuilder();
		writeAttribute(manifest, Attributes.Name.MANIFEST_VERSION.toString(), MANIFEST_VERSION_VALUE);
		writeAttribute(manifest, BUNDLE_MANIFESTVERSION, BUNDLE_MANIFEST_VERSION_VALUE);
		writeAttribute(manifest, BUNDLE_NAME, settings.getFragmentBundleName());
		writeAttribute(manifest, BUNDLE_SYMBOLICNAME, settings.getFragmentSymbolicName());
		writeAttribute(manifest, BUNDLE_VERSION, settings.getBundleVersion());
		writeAttribute(manifest, BUNDLE_VENDOR, settings.getBundleVendor());
		writeAttribute(manifest, FRAGMENT_HOST, settings.getFragmentHost());
		writeAttribute(manifest, BUNDLE_REQUIREDEXECUTIONENVIRONMENT,
				ImportUtils.getExecutionEnvironment(settings.getRequiredExecutionEnvironmentIndex()));
		writeAttribute(manifest, BUNDLE_NATIVECODE, settings.getBundleNativeLibraryEntry());
		writeAttribute(manifest, ECLIPSE_PLATFORM_FILTER, settings.getPlatformFilter());
		return manifest.toString().getBytes(MANIFEST_ENCODING);
	}
}
