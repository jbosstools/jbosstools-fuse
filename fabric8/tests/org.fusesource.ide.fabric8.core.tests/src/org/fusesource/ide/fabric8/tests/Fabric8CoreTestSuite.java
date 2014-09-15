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
package org.fusesource.ide.fabric8.tests;

import org.fusesource.ide.fabric8.tests.cases.Fabric8ConnectorTest;
import org.fusesource.ide.fabric8.tests.cases.Fabric8DTOTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
					Fabric8ConnectorTest.class,
					Fabric8DTOTest.class
			 })
public class Fabric8CoreTestSuite {
}
