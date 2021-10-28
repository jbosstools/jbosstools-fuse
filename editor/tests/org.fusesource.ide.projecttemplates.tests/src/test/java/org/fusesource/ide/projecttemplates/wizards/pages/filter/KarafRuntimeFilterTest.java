/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.wizards.pages.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.server.core.IRuntime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KarafRuntimeFilterTest {
	
	@Mock (answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime karafRuntimeType;
	@Mock (answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime fuseRuntimeType;
	@Mock (answer = Answers.RETURNS_DEEP_STUBS)
	private IRuntime otherRuntimeType;
	private Map<String, IRuntime> serverRuntimes;
	
	@Before
	public void setup() {
		when(karafRuntimeType.getRuntimeType().getId()).thenReturn(KarafRuntimeFilter.KARAF_RUNTIME_ID_PREFIX);
		when(fuseRuntimeType.getRuntimeType().getId()).thenReturn(KarafRuntimeFilter.FUSE_RUNTIME_ID_PREFIX);
		when(otherRuntimeType.getRuntimeType().getId()).thenReturn("other");
		serverRuntimes = new HashMap<>();
		serverRuntimes.put("aKaraf", karafRuntimeType);
		serverRuntimes.put("aFuse", fuseRuntimeType);
		serverRuntimes.put("other", otherRuntimeType);
	}

	@Test
	public void testSelectKaraf() throws Exception {
		assertThat(new KarafRuntimeFilter(serverRuntimes).select(null, null, "aKaraf")).isTrue();
	}
	
	@Test
	public void testSelectFuse() throws Exception {
		assertThat(new KarafRuntimeFilter(serverRuntimes).select(null, null, "aFuse")).isTrue();
	}
	
	@Test
	public void testFilterOutOther() throws Exception {
		assertThat(new KarafRuntimeFilter(serverRuntimes).select(null, null, "other")).isFalse();
	}

	@Test
	public void testSelectUnknown() throws Exception {
		assertThat(new KarafRuntimeFilter(serverRuntimes).select(null, null, "unknown")).isTrue();
	}
}
