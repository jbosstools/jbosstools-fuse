/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.tests.bean;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.fusesource.ide.server.karaf.core.bean.KarafBeanProvider;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.MockRuntimeCreationUtil;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.junit.Test;


public class ServerBeanTest extends TestCase {

	@Test
	public void testKaraf20() throws Exception {
		IPath dest = FuseServerTestActivator.getDefault()
				.getStateLocation().append(MockRuntimeCreationUtil.KARAF_20);
		MockRuntimeCreationUtil.createRuntimeMock(
				MockRuntimeCreationUtil.KARAF_20, dest);
		ServerBeanLoader l = new ServerBeanLoader(dest.toFile());
		ServerBean b = l.getServerBean();
		assertTrue(b.getBeanType() == KarafBeanProvider.KARAF_2x);
		assertEquals(b.getVersion(), "2.0");
		assertEquals(b.getFullVersion(), "2.0.0");
	}
}
