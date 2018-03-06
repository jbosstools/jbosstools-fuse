/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 */
public class SyndesisExtensionJSONIOTest {

	private static final String EXT_ID = "myId";
	private static final String EXT_NAME = "myName";
	private static final String EXT_DESC = "myDescription";
	private static final String EXT_VERSION = "1.1.1";
	
	private ObjectMapper mapper = new ObjectMapper();
	private SyndesisExtension extension;
	
	@Before
	public void initialize() {
		extension = new SyndesisExtension();
		extension.setExtensionId(EXT_ID);
		extension.setName(EXT_NAME);
		extension.setDescription(EXT_DESC);
		extension.setVersion(EXT_VERSION);
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
	}
	
	@Test
	public void testWriteAndReload() throws IOException {
		File f = File.createTempFile("unitTest", "json");
		SyndesisExtension.writeToFile(new FileOutputStream(f), extension);
		SyndesisExtension extNew = SyndesisExtension.getJSONFactoryInstance(new FileInputStream(f));
		assertThat(extNew.getExtensionId()).isEqualTo(extension.getExtensionId());
		assertThat(extNew.getName()).isEqualTo(extension.getName());
		assertThat(extNew.getDescription()).isEqualTo(extension.getDescription());
		assertThat(extNew.getVersion()).isEqualTo(extension.getVersion());
		f.deleteOnExit();

		String jsonOriginalString = mapper.writeValueAsString(extension);
		String jsonRenewedString = mapper.writeValueAsString(extNew);
		assertThat(jsonOriginalString).isEqualTo(jsonRenewedString);
	}
}
