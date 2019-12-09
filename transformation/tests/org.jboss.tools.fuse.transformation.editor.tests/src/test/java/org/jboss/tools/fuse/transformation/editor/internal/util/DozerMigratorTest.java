/******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class DozerMigratorTest {

	@Test
	public void testMigrateIfNecessary() throws Exception {
		Path initialFile = createTmpFileFromBundle("/dozerPre6.1.xml", "testDozerMigration-fileToConvert");
		Path expectedMigratedFile = createTmpFileFromBundle("/dozerPre6.1-postMigration.xml", "testDozerMigration-expectedResult");
		File migratedFile = File.createTempFile("testDozerMigration", ".xml");

		assertThat(new DozerMigrator().migrate(migratedFile , initialFile)).isTrue();

		assertThat(migratedFile).hasSameContentAs(expectedMigratedFile.toFile());
	}

	@Test
	public void testMigrateIfNecessaryForXsdWrittenOnSeveralLines() throws Exception {
		Path initialFile = createTmpFileFromBundle("/dozerPre6.1-writtenOnSeveralLines.xml", "testDozerMigration-fileToConvert");
		Path expectedMigratedFile = createTmpFileFromBundle("/dozerPre6.1-writtenOnSeveralLines-postMigration.xml", "testDozerMigration-expectedResult");
		File migratedFile = File.createTempFile("testDozerMigration", ".xml");

		assertThat(new DozerMigrator().migrate(migratedFile , initialFile)).isTrue();

		assertThat(migratedFile).hasSameContentAs(expectedMigratedFile.toFile());
	}

	@Test
	public void testMigrateIfNecessary_doesNotMigrateXSD6() throws Exception {
		Path initialFile = createTmpFileFromBundle("/dozer6.1.xml", "testDozerMigration-fileToConvert");
		File migratedFile = File.createTempFile("testDozerMigration", ".xml");

		assertThat(new DozerMigrator().migrate(migratedFile , initialFile)).isFalse();
	}

	private Path createTmpFileFromBundle(String fileNameInBundle, String tmpFileName) throws IOException {
		InputStream in = DozerMigratorTest.class.getResourceAsStream(fileNameInBundle);
		Path res = File.createTempFile(tmpFileName, ".xml").toPath();
		Files.copy(in, res, StandardCopyOption.REPLACE_EXISTING);
		return res;
	}

}