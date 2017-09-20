/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.tests.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.IPath;
import org.fusesource.ide.server.karaf.core.bean.KarafBeanProvider;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.KarafMockRuntimeCreationUtil;
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
public class KarafServerBean4xIT {

	protected static final HashMap<String, String> TYPE_TO_VERSION;
	static {
		TYPE_TO_VERSION = new HashMap<>();
		TYPE_TO_VERSION.put(KarafMockRuntimeCreationUtil.KARAF_40, "4.0.9");		
		TYPE_TO_VERSION.put(KarafMockRuntimeCreationUtil.KARAF_41, "4.1.2");
	}

	private String fRuntimeType;

	/**
	 * creates a server bean loader test for the given runtime type id
	 * 
	 * @param runtimeType
	 */
	public KarafServerBean4xIT(String runtimeType) {
		this.fRuntimeType = runtimeType;
	}

	/**
	 * returns the runtime types to test
	 * 
	 * @return
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return KarafMockRuntimeCreationUtil.SUPPORTED_4X_RUNTIMES;
	}

	/**
	 * creates a mock server directory structure and tests if the server bean
	 * loader can handle that folder structure
	 */
	@Test
	public void testKaraf() {
		IPath dest = FuseServerTestActivator.getDefault().getStateLocation().append(this.fRuntimeType);
		KarafMockRuntimeCreationUtil.create4xRuntimeMock(this.fRuntimeType, dest);
		ServerBeanLoader l = new ServerBeanLoader(dest.toFile());
		ServerBean b = l.getServerBean();
		assertTrue(b.getBeanType() == KarafBeanProvider.KARAF_4x);
		assertEquals(b.getFullVersion(), TYPE_TO_VERSION.get(this.fRuntimeType));
		assertEquals(b.getVersion(), ServerBeanLoader.getMajorMinorVersion(TYPE_TO_VERSION.get(this.fRuntimeType)));
	}
}
