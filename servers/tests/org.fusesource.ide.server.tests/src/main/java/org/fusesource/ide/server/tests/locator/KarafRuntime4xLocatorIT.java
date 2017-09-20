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
package org.fusesource.ide.server.tests.locator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.fusesource.ide.server.karaf.core.runtime.KarafRuntimeLocator;
import org.fusesource.ide.server.tests.FuseServerTestActivator;
import org.fusesource.ide.server.tests.util.KarafMockRuntimeCreationUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

@RunWith(value = Parameterized.class)
public class KarafRuntime4xLocatorIT extends TestCase {
	
	private String fRuntimeType;
	
	/**
	 * create a runtime locator test for the given runtime type id
	 * 
	 * @param runtimeType	the runtime type id to test
	 */
	public KarafRuntime4xLocatorIT(String runtimeType) {
		this.fRuntimeType = runtimeType;
	}
	
	/**
	 * returns all runtime types to test in this test case
	 * @return
	 */
	@Parameters
	public static Collection<Object[]> data() {
		return KarafMockRuntimeCreationUtil.SUPPORTED_4X_RUNTIMES;
	}
			
	/**
	 * tests the runtime locator for the given runtime type id
	 */
	@Test
	public void testKaraf() {
		IPath dest = FuseServerTestActivator.getDefault().getStateLocation().append(fRuntimeType);
		KarafMockRuntimeCreationUtil.create4xRuntimeMock(fRuntimeType, dest);
		KarafRuntimeLocator locator = new KarafRuntimeLocator();
		MockListener listener = new MockListener();
		locator.searchForRuntimes(dest, listener, new NullProgressMonitor());
		assertThat(listener.getFoundRuntime().getName()).contains("Karaf 4");
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
