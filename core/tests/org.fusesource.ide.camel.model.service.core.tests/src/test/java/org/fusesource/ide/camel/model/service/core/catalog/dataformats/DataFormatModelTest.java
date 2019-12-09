/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.dataformats;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.junit.Before;
import org.junit.Test;

public class DataFormatModelTest {

	private CamelModel model;
	private DataFormat dataFormat;

	@Before
	public void setup() {
		model = new CamelModel();
		Map<String, DataFormat> supportedDataFormats = new HashMap<>();
		dataFormat = new DataFormat();
		final String sameModelName = "model-name";
		dataFormat.setModelName(sameModelName);
		dataFormat.setName("name1");
		supportedDataFormats.put(dataFormat.getName(), dataFormat);

		DataFormat dataFormat2 = new DataFormat();
		dataFormat2.setModelName(sameModelName);
		dataFormat2.setName("name2");
		supportedDataFormats.put(dataFormat2.getName(), dataFormat2);
		model.setDataFormats(supportedDataFormats);
	}

	@Test
	public void testGetDataFormatByName() throws Exception {
		assertThat(model.getDataFormat(dataFormat.getName())).isEqualTo(dataFormat);
	}

	@Test
	public void testGetDataFormatByName_returnNullIfNotExists() throws Exception {
		assertThat(model.getDataFormat("dumbName")).isNull();
	}

	@Test
	public void testGetDataFormatByModelName() throws Exception {
		assertThat(model.getDataFormatsByModelName(dataFormat.getModelName())).hasSize(2);
	}

	@Test
	public void testGetDataFormatByModelName_returnsEmptyIfNotExists() throws Exception {
		assertThat(model.getDataFormatsByModelName("dumbModelName")).isEmpty();
	}

}
