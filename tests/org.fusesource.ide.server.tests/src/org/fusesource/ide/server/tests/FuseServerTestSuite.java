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
package org.fusesource.ide.server.tests;

import org.fusesource.ide.server.tests.bean.FuseESBServerBean6xTest;
import org.fusesource.ide.server.tests.bean.KarafServerBean2xTest;
import org.fusesource.ide.server.tests.bean.KarafServerBean3xTest;
import org.fusesource.ide.server.tests.bean.ServiceMixServerBean4xTest;
import org.fusesource.ide.server.tests.bean.ServiceMixServerBean5xTest;
import org.fusesource.ide.server.tests.locator.FuseESBRuntime6xLocatorTest;
import org.fusesource.ide.server.tests.locator.KarafRuntime2xLocatorTest;
import org.fusesource.ide.server.tests.locator.KarafRuntime3xLocatorTest;
import org.fusesource.ide.server.tests.locator.ServiceMixRuntime4xLocatorTest;
import org.fusesource.ide.server.tests.locator.ServiceMixRuntime5xLocatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
					FuseESBServerBean6xTest.class,
					KarafServerBean2xTest.class, 
					KarafServerBean3xTest.class, 
					ServiceMixServerBean4xTest.class, 
					ServiceMixServerBean5xTest.class,
					
					FuseESBRuntime6xLocatorTest.class,
					KarafRuntime2xLocatorTest.class, 
					KarafRuntime3xLocatorTest.class,
					ServiceMixRuntime4xLocatorTest.class, 
					ServiceMixRuntime5xLocatorTest.class
			 })
public class FuseServerTestSuite {
}
