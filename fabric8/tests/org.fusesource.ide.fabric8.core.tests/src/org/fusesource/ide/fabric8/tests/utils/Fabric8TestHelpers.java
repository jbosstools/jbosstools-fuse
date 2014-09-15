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
package org.fusesource.ide.fabric8.tests.utils;

import org.fusesource.ide.fabric8.core.connector.Fabric8ConnectorType;
import org.fusesource.ide.fabric8.core.connector.JolokiaFabric8Connector;

/**
 * @author lhein
 */
public class Fabric8TestHelpers {
	
	/**
	 * returns the connection to a local fabric8 via jolokia
	 * @return
	 */
	public static Fabric8ConnectorType getJolokiaConnector() {
		return JolokiaFabric8Connector.getFabric8Connector("admin", "admin", "http://localhost:8181/jolokia/");
	}
}
