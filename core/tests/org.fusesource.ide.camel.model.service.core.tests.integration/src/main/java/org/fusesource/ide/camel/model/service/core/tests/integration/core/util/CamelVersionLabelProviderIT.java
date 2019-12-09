/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.tests.integration.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.camel.model.service.core.util.CamelVersionLabelProvider;
import org.junit.Test;

public class CamelVersionLabelProviderIT {
	
	@Test
	public void testProvideDisplayName() throws Exception {
		assertThat(new CamelVersionLabelProvider().getText("2.18.1.redhat-000021")).isEqualTo("2.18.1.redhat-000021 (FIS 2.0 R3)");
	}
	
	@Test
	public void testForVersionWithoutMappingProvided() throws Exception {
		assertThat(new CamelVersionLabelProvider().getText("2.14.0")).isEqualTo("2.14.0");
	}
}
