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
package org.fusesource.ide.fabric8.tests.cases;

import java.io.IOException;

import junit.framework.TestCase;

import org.fusesource.ide.fabric8.core.connector.Fabric8Connector;
import org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType;
import org.fusesource.ide.fabric8.tests.utils.Fabric8TestHelpers;
import org.junit.Test;

/**
 * @author lhein
 */
public class Fabric8ConnectorTest extends TestCase {
	
	@Test
	public void testConnection() throws Exception {
		Fabric8ConnectorType connectorType = Fabric8TestHelpers.getJolokiaConnector();
		Fabric8Connector con = new Fabric8Connector(connectorType);
		try {
			assertNotNull("Connector is null!", con);
			con.connect();
			assertNotNull("Connector Type is null!", con.getConnection());
		} catch (IOException ex) {
			fail(ex.getMessage());
		} finally {
			con.disconnect();	
		}
	}	
}
