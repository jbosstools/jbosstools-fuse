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
package org.fusesource.ide.camel;

import org.fusesource.ide.camel.commons.ui.table.TableConfigurationTest;
import org.fusesource.ide.camel.model.AddOrderingTest;
import org.fusesource.ide.camel.model.CanConnectTest;
import org.fusesource.ide.camel.model.CanOutputTest;
import org.fusesource.ide.camel.model.ContentBasedRouterTest;
import org.fusesource.ide.camel.model.DisplayTextTest;
import org.fusesource.ide.camel.model.EndpointSummaryTest;
import org.fusesource.ide.camel.model.ModelTest;
import org.fusesource.ide.camel.model.UnmarshalTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
					TableConfigurationTest.class,
					AddOrderingTest.class,
					CanConnectTest.class,
					CanOutputTest.class,
					ContentBasedRouterTest.class,
					DisplayTextTest.class,
					EndpointSummaryTest.class,
					ModelTest.class,
					UnmarshalTest.class
			 })
public class CamelModelTestSuite {
}
