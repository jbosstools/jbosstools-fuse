/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.server.karaf.core.server.subsystems;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.junit.Test;

/**
 * @author lhein
 */
public class KarafUtilsTest {
	
	private static final String MANIFEST_BLURP = "Manifest-Version: 1.0\n" + 
			"Bundle-ManifestVersion: 2\n" + 
			"Bundle-Name: Test Bundle\n" + 
			"Bundle-SymbolicName: com.sample.mytest.BundleName\n";
	
	@Test
	public void testGetBundleVersionFromManifest() throws CoreException, IOException {
		File mfFile = createTempManifest("1.0.0");
		String version = KarafUtils.getBundleVersionFromManifest(mfFile);
		assertEquals("1.0.0", version);
		
		mfFile = createTempManifest("1.0.0.SNAPSHOT");
		version = KarafUtils.getBundleVersionFromManifest(mfFile);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetBundleVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0.jar");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0.SNAPSHOT", version);

		bundleFile = new File("./target/test-1.0.0.jar");
		version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0.SNAPSHOT", version);
		
		bundleFile = new File("./target/test-1.0.0.war");
		version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test-1.0.0-SNAPSHOT.war");
		version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetBundleVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test-1.0.0.jar");
		String uri = String.format("%sfile:%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_BUNDLE), bundleFile.toURI().toString());
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		uri = String.format("%sfile:%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_BUNDLE), bundleFile.toURI().toString());
		version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0.SNAPSHOT", version);
		
		bundleFile = new File("./target/test.jar");
		uri = String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_JAR), bundleFile.toURI().toString(), "test", "1.0.0");
		version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test.jar");
		uri = String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_JAR), bundleFile.toURI().toString(), "test", "1.0.0.SNAPSHOT");
		version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0.SNAPSHOT", version);
		
		bundleFile = new File("./target/test.war");
		uri = String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_WAR), bundleFile.toURI().toString(), "test", "1.0.0");
		version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0", version);
		
		bundleFile = new File("./target/test.war");
		uri = String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_WAR), bundleFile.toURI().toString(), "test", "1.0.0.SNAPSHOT");
		version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	
	private File createTempManifest(String version) throws IOException {
		File manifestFile = new File("./target/MANIFEST.MF");
		if (manifestFile.exists()) manifestFile.delete();
		IOUtils.writeText(manifestFile, MANIFEST_BLURP + "Bundle-Version: " + version + "\n\n");
		manifestFile.deleteOnExit();
		
		return manifestFile;
	}
	
	private static String getProtocolPrefixForModule(String packaging) throws CoreException {	
		if (Strings.isBlank(packaging) || packaging.equalsIgnoreCase(KarafUtils.PACKAGING_JAR)) {
			return KarafUtils.PROTOCOL_PREFIX_JAR;
		} else if (packaging.equalsIgnoreCase(KarafUtils.PACKAGING_BUNDLE)) {
			return KarafUtils.PROTOCOL_PREFIX_OSGI;
		} else if (packaging.equalsIgnoreCase(KarafUtils.PACKAGING_WAR)) {
			return KarafUtils.PROTOCOL_PREFIX_WEB;
		} else {
			return KarafUtils.PROTOCOL_PREFIX_JAR;
		}
	}
}
