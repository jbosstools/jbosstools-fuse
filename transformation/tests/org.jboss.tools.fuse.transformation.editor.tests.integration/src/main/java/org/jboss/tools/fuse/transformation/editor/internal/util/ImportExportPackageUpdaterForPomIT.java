/******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.StringCharacterIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ImportExportPackageUpdaterForPomIT {

	private static final String VALID_INSTRUCTIONS =
				"          <instructions>\n" +
				"		     <Import-Package>*</Import-Package>\n" +
				"		   </instructions>\n";

	private static final String POM_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"" +
			"		  xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
			"    	  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
			"  <modelVersion>4.0.0</modelVersion>\n" +
			"  <groupId>com.mycompany</groupId>\n" +
			"  <artifactId>testproject</artifactId>\n" +
			"  <version>1.0.0-SNAPSHOT</version>\n" +
			"  <packaging>bundle</packaging>\n" +
			"  <name>Some Dummy Project</name>\n"+
			"  <dependencies>\n" + 
			"	  <dependency>\n" + 
			"	    <groupId>org.apache.camel</groupId>\n" + 
			"	    <artifactId>camel-core</artifactId>\n" + 
			"	    <version>2.21.0</version>\n" + 
			"	  </dependency>\n" + 
			"  </dependencies>" +
			"  <build>\n" +
			"    <plugins>\n";

	private static final String POM_END = 
			"    </plugins>\n" +
			"  </build>\n" +
			"</project>";

	private static final String EXPECTED_POM_WITHOUT_EXPORT_PACKAGE = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
						VALID_INSTRUCTIONS +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;
	
	private static final String POM_WITH_ASTERISK_IMPORT_PACKAGE = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"          <instructions>\n" +
			"		     <Import-Package>*</Import-Package>\n" +
			"		   </instructions>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;

	private static final String POM_WITHOUT_PLUGIN = POM_START + POM_END;

	private static final String POM_WITHOUT_CONFIGURATION = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"      </plugin>\n" +
			POM_END;

	private static final String POM_WITHOUT_INSTRUCTIONS = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;

	private static final String POM_WITHOUT_IMPORT_PACKAGE = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"          <instructions>\n" +
			"		   </instructions>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;

	private static final String EXPECTED_POM_WITH_EXPORT_PACKAGE_INCLUDING_COMSUNEL = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"          <instructions>\n" +
			"            <Export-Package>dummy.pack,source.pack,target.pack</Export-Package>\n" +
			"		     <Import-Package>*,com.sun.el;version=\"[2,3)\"</Import-Package>\n" +
			"		   </instructions>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;
	
	private static final String EXPECTED_POM_WITH_EXPORT_PACKAGE = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"          <instructions>\n" +
			"            <Export-Package>dummy.pack,source.pack,target.pack</Export-Package>\n" +
			"		     <Import-Package>*</Import-Package>\n" +
			"		   </instructions>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;

	private static final String POM_WITH_SOME_EXPORT_PACKAGE = POM_START +
			"      <plugin>\n" +
			"		 <groupId>org.apache.felix</groupId>\n" +
			"        <artifactId>maven-bundle-plugin</artifactId>\n" +
			"        <version>3.2.0</version>\n" +
			"		 <extensions>true</extensions>\n" +
			"        <configuration>\n" +
			"          <instructions>\n" +
			"            <Export-Package>dummy.pack</Export-Package>" +
			"		   </instructions>\n" +
			"        </configuration>\n" +
			"      </plugin>\n" +
			POM_END;

	@Rule
	public FuseProject fuseProject = new FuseProject(ImportExportPackageUpdaterForPomIT.class.getName());

	private IProject project;
	private IFile pomIFile;

	@Before
	public void setup() throws Exception {
		project = fuseProject.getProject();
		pomIFile = project.getFile(IMavenConstants.POM_FILE_NAME);
		pomIFile.delete(true, new NullProgressMonitor());
	}

	@Test
	public void shouldUpdatePomWithoutPlugin() throws Exception {
		updatePom(POM_WITHOUT_PLUGIN);
	}

	@Test
	public void shouldUpdatePomWithoutConfiguration() throws Exception {
		updatePom(POM_WITHOUT_CONFIGURATION);
	}

	@Test
	public void shouldUpdatePomWithoutInstructions() throws Exception {
		updatePom(POM_WITHOUT_INSTRUCTIONS);
	}

	@Test
	public void shouldUpdatePomWithoutImportPackage() throws Exception {
		updatePom(POM_WITHOUT_IMPORT_PACKAGE);
	}

	@Test
	public void shouldNotModifyExpectedPom() throws Exception {
		updatePom(EXPECTED_POM_WITHOUT_EXPORT_PACKAGE);
	}
	
	@Test
	public void shouldSupportAsteriskAlreadyProvided() throws Exception {
		updatePom(POM_WITH_ASTERISK_IMPORT_PACKAGE);
	}

	@Test
	public void shouldNotModifyWarPom() throws Exception {
		String pom = POM_WITHOUT_PLUGIN.replace("bundle", "war");
		updatePom(pom, pom);
	}
	
	@Test
	public void shouldNotAddExportedPackageIfNoExportPackageConfigured() throws Exception {
		updatePom(EXPECTED_POM_WITHOUT_EXPORT_PACKAGE, EXPECTED_POM_WITHOUT_EXPORT_PACKAGE, "source.pack.MyClass", "target.pack.MyOtherClass");
	}
	
	@Test
	public void shouldAddExportedPackageIfExportPackageConfiguredAndImportPackageWithComSunElwhenVersionFuse63() throws Exception {
		updatePom(POM_WITH_SOME_EXPORT_PACKAGE.replace("2.21.0", "2.17.3"), EXPECTED_POM_WITH_EXPORT_PACKAGE_INCLUDING_COMSUNEL.replace("2.21.0", "2.17.3"), "source.pack.MyClass", "target.pack.MyOtherClass");
	}
	
	@Test
	public void shouldAddExportedPackageIfExportPackageConfiguredAndNotImportPackageWithComSunElwhenVersionFuse70() throws Exception {
		updatePom(POM_WITH_SOME_EXPORT_PACKAGE, EXPECTED_POM_WITH_EXPORT_PACKAGE, "source.pack.MyClass", "target.pack.MyOtherClass");
	}

	private void updatePom(String pom) throws Exception {
		updatePom(pom, EXPECTED_POM_WITHOUT_EXPORT_PACKAGE);
	}

	private void updatePom(String pom, String expectedPom) throws Exception {
		updatePom(pom, expectedPom, null, null);
	}
	
	private void updatePom(String pom, String expectedPom, String sourceClassName, String targetClassName) throws Exception {
		pomIFile.create(new ByteArrayInputStream(pom.getBytes(StandardCharsets.UTF_8)), true, new NullProgressMonitor());
		new ImportExportPackageUpdater(project, sourceClassName, targetClassName).updatePackageImports(new NullProgressMonitor());
		InputStream pomContentsToCheck = pomIFile.getContents();
		char[] buf = new char[pomContentsToCheck.available()];
		try (InputStreamReader reader = new InputStreamReader(pomContentsToCheck, StandardCharsets.UTF_8)) {
			reader.read(buf);
		}
		assertThat(normalize(String.valueOf(buf))).isEqualTo(normalize(expectedPom));
	}

	private String normalize(String text) {
		StringBuilder builder = new StringBuilder();
		StringCharacterIterator iter = new StringCharacterIterator(text);
		for (char chr = iter.first(); chr != StringCharacterIterator.DONE; chr = iter.next()) {
			if (!Character.isWhitespace(chr)){
				builder.append(chr);
			}
		}
		return builder.toString();
	}
}
