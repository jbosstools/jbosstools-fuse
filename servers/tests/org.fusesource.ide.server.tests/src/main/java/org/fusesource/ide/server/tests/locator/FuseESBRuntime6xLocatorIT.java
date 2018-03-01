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
package org.fusesource.ide.server.tests.locator;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.fusesource.ide.server.fuse.core.runtime.FuseESBRuntimeLocator;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.FuseESBMockRuntimeCreationUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class FuseESBRuntime6xLocatorIT {
	
	private String fRuntimeType;
	
	/**
	 * create a runtime locator test for the given runtime type id
	 * 
	 * @param runtimeType	the runtime type id to test
	 */
	public FuseESBRuntime6xLocatorIT(String runtimeType) {
		this.fRuntimeType = runtimeType;
	}
	
	/**
	 * returns all runtime types to test in this test case
	 * @return
	 */
	@Parameters
	public static Object[] data() {
		return FuseESBMockRuntimeCreationUtil.SUPPORTED_6X_RUNTIMES.toArray();
	}
			
	/**
	 * tests the runtime locator for the given runtime type id
	 * @throws Exception
	 */
	@Test
	public void testFuseESB() throws Exception {
		IPath dest = FuseServerTestActivator.getDefault().getStateLocation().append(fRuntimeType);
		FuseESBMockRuntimeCreationUtil.create6xRuntimeMock(fRuntimeType, dest);
		
		FuseESBRuntimeLocator locator = new FuseESBRuntimeLocator();
		MockListener listener = new MockListener();
		locator.searchForRuntimes(dest, listener, new NullProgressMonitor());
		
		assertThat(listener.getFoundRuntime().getName()).contains("Red Hat Fuse 6");
	}
	
	/**
	 * clean up created files after test finished
	 */
	@After
	public void cleanup() {
		FuseServerTestActivator.cleanup();
	}
	
	/**
	 * listener which looks for found runtimes
	 */
	private class MockListener implements RuntimeLocatorDelegate.IRuntimeSearchListener {
		
		private IRuntimeWorkingCopy foundRuntime;
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.wst.server.core.model.RuntimeLocatorDelegate.IRuntimeSearchListener#runtimeFound(org.eclipse.wst.server.core.IRuntimeWorkingCopy)
		 */
		@Override
		public void runtimeFound(IRuntimeWorkingCopy arg0) {
			this.foundRuntime = arg0;
		}
		
		/**
		 * returns the found runtime or null if nothing found
		 * @return
		 */
		public IRuntimeWorkingCopy getFoundRuntime() {
			return this.foundRuntime;
		}
	}	
}
