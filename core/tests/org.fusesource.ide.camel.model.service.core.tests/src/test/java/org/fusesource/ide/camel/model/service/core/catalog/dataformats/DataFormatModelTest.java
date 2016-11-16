/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.dataformats;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DataFormatModelTest {

	private DataFormatModel dataFormatModel;
	private DataFormat dataFormat;

	@Before
	public void setup() {
		dataFormatModel = new DataFormatModel();
		ArrayList<DataFormat> supportedDataFormats = new ArrayList<>();
		dataFormat = new DataFormat();
		final String sameModelName = "model-name";
		dataFormat.setModelName(sameModelName);
		dataFormat.setName("name1");
		supportedDataFormats.add(dataFormat);

		DataFormat dataFormat2 = new DataFormat();
		dataFormat2.setModelName(sameModelName);
		dataFormat2.setName("name2");
		supportedDataFormats.add(dataFormat2);
		dataFormatModel.setSupportedDataFormats(supportedDataFormats);
	}

	@Test
	public void testGetDataFormatByName() throws Exception {
		assertThat(dataFormatModel.getDataFormatByName(dataFormat.getName())).isEqualTo(dataFormat);
	}

	@Test
	public void testGetDataFormatByName_returnNullIfNotExists() throws Exception {
		assertThat(dataFormatModel.getDataFormatByName("dumbName")).isNull();
	}

	@Test
	public void testGetDataFormatByModelName() throws Exception {
		assertThat(dataFormatModel.getDataFormatsByModelName(dataFormat.getModelName())).hasSize(2);
	}

	@Test
	public void testGetDataFormatByModelName_returnsEmptyIfNotExists() throws Exception {
		assertThat(dataFormatModel.getDataFormatsByModelName("dumbModelName")).isEmpty();
	}

}
