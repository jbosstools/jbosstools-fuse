/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.tests.bean;

import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.fusesource.ide.server.karaf.core.bean.KarafBeanProvider;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.MockRuntimeCreationUtil;
import org.fusesource.ide.server.tests.util.ParametizedTestUtil;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author lhein
 */
@RunWith(value = Parameterized.class)
public class ServerBean3xTest extends TestCase {

	public static final HashMap<String, String> TYPE_TO_VERSION;
	static {
		TYPE_TO_VERSION = new HashMap<String, String>();
		TYPE_TO_VERSION.put(MockRuntimeCreationUtil.KARAF_30, "3.0.1");		
	}

	private String fRuntimeType;

	/**
	 * creates a server bean loader test for the given runtime type id
	 * 
	 * @param runtimeType
	 */
	public ServerBean3xTest(String runtimeType) {
		this.fRuntimeType = runtimeType;
	}

	/**
	 * returns the runtime types to test
	 * 
	 * @return
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return ParametizedTestUtil
				.asCollection(MockRuntimeCreationUtil.SUPPORTED_3X_RUNTIMES);
	}

	/**
	 * creates a mock server directory structure and tests if the server bean
	 * loader can handle that folder structure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testKaraf() throws Exception {
		IPath dest = FuseServerTestActivator.getDefault().getStateLocation()
				.append(this.fRuntimeType);
		MockRuntimeCreationUtil.create3xRuntimeMock(this.fRuntimeType, dest);
		ServerBeanLoader l = new ServerBeanLoader(dest.toFile());
		ServerBean b = l.getServerBean();
		assertTrue(b.getBeanType() == KarafBeanProvider.KARAF_3x);
		assertEquals(b.getFullVersion(), TYPE_TO_VERSION.get(this.fRuntimeType));
		assertEquals(b.getVersion(),
				ServerBeanLoader.getMajorMinorVersion(TYPE_TO_VERSION
						.get(this.fRuntimeType)));
	}
}
