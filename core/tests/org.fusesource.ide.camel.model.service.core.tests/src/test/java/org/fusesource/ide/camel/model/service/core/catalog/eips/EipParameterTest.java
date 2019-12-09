/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.eips;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Before;
import org.junit.Test;

public class EipParameterTest {

	private Eip eip;
	
	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/to.json");
		this.eip = Eip.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}
	
	
	@Test
	public void testGetParameterWithEnum() throws Exception {
		Parameter patternParam = eip.getParameter("pattern");
		assertThat(patternParam.getChoice()).contains("InOnly", "InOptionalOut", "InOut", "OutIn", "OutOnly", "OutOptionalIn", "RobustInOnly", "RobustOutOnly");
	}
	
}
