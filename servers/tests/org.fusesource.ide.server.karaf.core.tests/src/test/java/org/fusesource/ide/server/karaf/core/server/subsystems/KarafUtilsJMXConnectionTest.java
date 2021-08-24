/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KarafUtilsJMXConnectionTest {
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private IServer server;
	
	@Test
	public void testRetrieveForDefault7_8() throws Exception {
		testRetrieveConnection("fake7_8");
	}

	@Test
	public void testRetrieveForDefault7_9() throws Exception {
		testRetrieveConnection("fake7_9");
	}
	
	private void testRetrieveConnection(String fakeConfigFolder) throws URISyntaxException {
		IPath pathToFakeConfigFolder = new Path(this.getClass().getResource("/"+fakeConfigFolder+"/etc/system.properties").toURI().getPath()).removeLastSegments(2);
		when(server.getRuntime().getLocation()).thenReturn(pathToFakeConfigFolder);
		String jmxConnectionURL = KarafUtils.getJMXConnectionURL(server);
		assertThat(jmxConnectionURL).isEqualTo("service:jmx:rmi://127.0.0.1:44444/jndi/rmi://127.0.0.1:1099/karaf-root");
	}
	
}
