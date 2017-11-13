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

import java.util.HashMap;

import org.eclipse.core.runtime.IPath;
import org.fusesource.ide.server.fuse.core.bean.FuseBeanProvider;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.FuseESBMockRuntimeCreationUtil;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBean;
import org.jboss.ide.eclipse.as.core.server.bean.ServerBeanLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

@RunWith(value = Parameterized.class)
public class FuseESBServerBean6xIT extends TestCase {

	private static final HashMap<String, String> TYPE_TO_VERSION = new HashMap<>();
	static {
		TYPE_TO_VERSION.put(FuseESBMockRuntimeCreationUtil.FUSEESB_60, "6.0.0.redhat-065");
		TYPE_TO_VERSION.put(FuseESBMockRuntimeCreationUtil.FUSEESB_61, "6.1.0.redhat-376");
		TYPE_TO_VERSION.put(FuseESBMockRuntimeCreationUtil.FUSEESB_62, "6.2.1.redhat-084");
		TYPE_TO_VERSION.put(FuseESBMockRuntimeCreationUtil.FUSEESB_63, "6.3.0.redhat-187");
	}
	
	private String fRuntimeType;

	/**
	 * creates a server bean loader test for the given runtime type id
	 * @param runtimeType
	 */
	public FuseESBServerBean6xIT(String runtimeType) {
		this.fRuntimeType = runtimeType;
	}
	
	/**
	 * returns the runtime types to test
	 * @return
	 */
	@Parameters
	public static Object[] data() {
		return FuseESBMockRuntimeCreationUtil.SUPPORTED_6X_RUNTIMES.toArray();
	}

	/**
	 * creates a mock server directory structure and tests if the 
	 * server bean loader can handle that folder structure
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFuseESB() throws Exception {
		IPath dest = FuseServerTestActivator.getDefault().getStateLocation().append(this.fRuntimeType);
		FuseESBMockRuntimeCreationUtil.create6xRuntimeMock(this.fRuntimeType, dest);
		ServerBeanLoader l = new ServerBeanLoader(dest.toFile());
		ServerBean b = l.getServerBean();
		assertTrue(b.getBeanType() == FuseBeanProvider.FUSE_6x);
		assertEquals(b.getFullVersion(), TYPE_TO_VERSION.get(this.fRuntimeType));
		assertEquals(b.getVersion(), ServerBeanLoader.getMajorMinorVersion(TYPE_TO_VERSION.get(this.fRuntimeType)));
	}
}
