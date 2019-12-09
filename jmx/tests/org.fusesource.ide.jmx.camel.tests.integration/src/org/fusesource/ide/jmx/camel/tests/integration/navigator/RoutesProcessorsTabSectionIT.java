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
package org.fusesource.ide.jmx.camel.tests.integration.navigator;

import java.util.Collections;

import org.fusesource.ide.foundation.ui.propsrc.BeanPropertySource;
import org.fusesource.ide.jmx.camel.navigator.RoutesProcessorsTabSection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoutesProcessorsTabSectionIT {

	@Mock
	BeanPropertySource bps;

	@Test
	public void testRefreshCallCleanCacheOnBeanPropertySource() throws Exception {
		RoutesProcessorsTabSection section = new RoutesProcessorsTabSection();
		section.setPropertySources(Collections.singletonList(bps));

		section.refresh();

		verify(bps).cleanCache();
	}

}
