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

import org.fusesource.ide.server.tests.bean.ServerBean2xTest;
import org.fusesource.ide.server.tests.bean.ServerBean3xTest;
import org.fusesource.ide.server.tests.locator.Runtime2xLocatorTest;
import org.fusesource.ide.server.tests.locator.Runtime3xLocatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
					ServerBean2xTest.class, 
					ServerBean3xTest.class, 
					Runtime2xLocatorTest.class, 
					Runtime3xLocatorTest.class
			 })
public class FuseServerTestSuite {
}
