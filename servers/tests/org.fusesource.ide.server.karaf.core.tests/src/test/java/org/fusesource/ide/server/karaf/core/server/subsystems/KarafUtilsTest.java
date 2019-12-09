/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.server.karaf.core.server.subsystems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.server.subsystems.publish.ModuleBundleVersionUtility.BundleDetails;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author lhein
 */
public class KarafUtilsTest {
	
	private static final String MANIFEST_BLURP = "Manifest-Version: 1.0\n" + 
			"Bundle-ManifestVersion: 2\n" + 
			"Bundle-Name: Test Bundle\n" + 
			"Bundle-SymbolicName: com.sample.mytest.BundleName\n";
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	@Test
	public void testStripParametersFromSymbolicName() throws CoreException {
		assertThat(KarafUtils.stripParametersFromSymbolicName("mySampleApp;blueprint.aries.xml-validation:=false")).isEqualTo("mySampleApp");
	}
	
	@Test
	public void testBundleDetailsSymbolicNameStrippedParams() throws CoreException {
		BundleDetails bd = new BundleDetails("mySampleApp;blueprint.aries.xml-validation:=false", "1.0.0");
		assertThat(bd.getSymbolicName()).isEqualTo("mySampleApp");
	}
	
	@Test
	public void testStripParametersFromSymbolicNameWithoutParams() throws CoreException {
		assertThat(KarafUtils.stripParametersFromSymbolicName("mySampleApp")).isEqualTo("mySampleApp");
	}
	
	@Test
	public void testBundleDetailsSymbolicNameStrippedParamsWithoutParams() throws CoreException {
		BundleDetails bd = new BundleDetails("mySampleApp", "1.0.0");
		assertThat(bd.getSymbolicName()).isEqualTo("mySampleApp");
	}

	@Test
	public void testGetBundleVersionFromManifest() throws CoreException, IOException {
		File mfFile = createTempManifest("1.0.0");
		String version = KarafUtils.getBundleVersionFromManifest(mfFile);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetBundleSnapshotVersionFromManifest() throws CoreException, IOException {
		File mfFile = createTempManifest("1.0.0.SNAPSHOT");
		String version = KarafUtils.getBundleVersionFromManifest(mfFile);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetJarVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0.jar");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetJarSnapshotVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}

	@Test
	public void testGetBundleVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0.jar");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetBundleSnapshotVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0.SNAPSHOT", version);
	}

	@Test
	public void testGetWarVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0.war");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetWarSnapshotVersionFromFileName() throws CoreException, IOException {
		File bundleFile = new File("./target/test-1.0.0-SNAPSHOT.war");
		String version = KarafUtils.getBundleVersionFromFileName(bundleFile, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetBundleVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test-1.0.0.jar");
		String uri = String.format("%sfile:%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_BUNDLE), bundleFile.toURI().toString());
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testBundleSnapshotVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test-1.0.0-SNAPSHOT.jar");
		String uri = String.format("%sfile:%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_BUNDLE), bundleFile.toURI().toString());
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_BUNDLE);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetJarVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test.jar");
		String uri = String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_JAR), bundleFile.toURI().toString(), "test", "1.0.0");
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetJarSnapshotVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test.jar");
		String uri = String.format("%sfile:%s$Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_JAR), bundleFile.toURI().toString(), "test", "1.0.0.SNAPSHOT");
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_JAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
	
	@Test
	public void testGetWarVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test.war");
		String uri = String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_WAR), bundleFile.toURI().toString(), "test", "1.0.0");
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0", version);
	}
	
	@Test
	public void testGetWarSnapshotVersionFromURI() throws CoreException {
		File bundleFile = new File("./target/test.war");
		String uri = String.format("%sfile:%s?Bundle-SymbolicName=%s&Bundle-Version=%s", getProtocolPrefixForModule(KarafUtils.PACKAGING_WAR), bundleFile.toURI().toString(), "test", "1.0.0.SNAPSHOT");
		String version = KarafUtils.getBundleVersionFromURI(uri, KarafUtils.PACKAGING_WAR);
		assertEquals("1.0.0.SNAPSHOT", version);
	}
		
	private File createTempManifest(String version) throws IOException {
		File manifestFile = testFolder.newFile("MANIFEST.MF");
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
