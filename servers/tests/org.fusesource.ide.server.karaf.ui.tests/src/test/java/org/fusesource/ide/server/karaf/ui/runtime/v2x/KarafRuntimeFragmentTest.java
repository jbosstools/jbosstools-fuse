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
package org.fusesource.ide.server.karaf.ui.runtime.v2x;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KarafRuntimeFragmentTest {
	
	private static final String HOME_VERSION_WARNING = "HomVersionWarning";
	private static final String AN_ERROR_FROM_PARENT = "anErrorFromParent";

	@Mock
	private KarafRuntimeFragment karafRuntimeFragment;

	@Before
	public void setup() {
		doCallRealMethod().when(karafRuntimeFragment).getErrorString();
	}

	@Test
	public void testErrorIncludesHomeVersionWarning_whenThereIsAnError() throws Exception {
		doReturn(AN_ERROR_FROM_PARENT).when(karafRuntimeFragment).getErrorFromParent();
		doReturn(HOME_VERSION_WARNING).when(karafRuntimeFragment).getHomeVersionWarning();

		assertThat(karafRuntimeFragment.getErrorString())
			.contains(AN_ERROR_FROM_PARENT)
			.contains(HOME_VERSION_WARNING);
	}

	@Test
	public void testErrorIncludesHomeVersionWarning_whenThereIsNoError() throws Exception {
		doReturn(null).when(karafRuntimeFragment).getErrorFromParent();
		doReturn(HOME_VERSION_WARNING).when(karafRuntimeFragment).getHomeVersionWarning();

		assertThat(karafRuntimeFragment.getErrorString())
			.doesNotContain(AN_ERROR_FROM_PARENT)
			.contains(HOME_VERSION_WARNING);
	}
	
	@Test
	public void testError_whenNoHomeVersioNWarning() throws Exception {
		doReturn(AN_ERROR_FROM_PARENT).when(karafRuntimeFragment).getErrorFromParent();
		doReturn(null).when(karafRuntimeFragment).getHomeVersionWarning();

		assertThat(karafRuntimeFragment.getErrorString())
			.contains(AN_ERROR_FROM_PARENT)
			.doesNotContain(HOME_VERSION_WARNING);
	}

	@Test
	public void testNoErrorNoWarning() throws Exception {
		doReturn(null).when(karafRuntimeFragment).getErrorFromParent();
		doReturn(null).when(karafRuntimeFragment).getHomeVersionWarning();

		assertThat(karafRuntimeFragment.getErrorString()).isNull();
	}
	
}
