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

package org.fusesource.ide.server.tests.locator;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeLocator;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.MockRuntimeCreationUtil;
import org.fusesource.ide.server.tests.util.ParametizedTestUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(value = Parameterized.class)
public class RuntimeLocatorTest extends TestCase {
	@Parameters
	public static Collection<Object[]> data() {
		return ParametizedTestUtil.asCollection(MockRuntimeCreationUtil.SUPPORTED_RUNTIMES);
	}
	
	private String fRuntimeType;
	public RuntimeLocatorTest(String runtimeType) {
		fRuntimeType = runtimeType;
	}
	
	
	@Test
	public void testKaraf() throws Exception {
		IPath dest = FuseServerTestActivator.getDefault()
				.getStateLocation().append(fRuntimeType);
		MockRuntimeCreationUtil.createRuntimeMock(
				fRuntimeType, dest);
		
		KarafRuntimeLocator locator = new KarafRuntimeLocator();
		MockListener listener = new MockListener();
		locator.searchForRuntimes(dest, 
				listener, new NullProgressMonitor());
		assertTrue(listener.found != null);
	}
	
	@After
	public void cleanup() {
		FuseServerTestActivator.cleanup();
	}
	
	private class MockListener implements RuntimeLocatorDelegate.IRuntimeSearchListener {
		private IRuntimeWorkingCopy found;
		public void runtimeFound(IRuntimeWorkingCopy arg0) {
			found = arg0;
		}
	}
	
}
