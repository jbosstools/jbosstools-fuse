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
package org.fusesource.ide.launcher.debug.util;

import org.assertj.core.api.Assertions;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.fusesource.ide.camel.model.service.core.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CamelDebugUtilsTest {

	@Mock
	private ILaunchConfiguration launchConfig;

	@Test
	public void testRawCamelContextFilePathFromLaunchConfig_withSpace() throws Exception {
		Mockito.doReturn("file:C:\\my%20path%20with%20space").when(launchConfig).getAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, (String) null);

		Assertions.assertThat(CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(launchConfig)).isEqualTo("C:\\my path with space");
	}

}
