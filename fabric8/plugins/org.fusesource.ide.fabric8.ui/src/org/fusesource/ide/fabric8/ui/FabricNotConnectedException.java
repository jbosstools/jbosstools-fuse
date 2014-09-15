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

package org.fusesource.ide.fabric8.ui;

public class FabricNotConnectedException extends RuntimeException {

	private static final long serialVersionUID = 2064085104231391228L;

	public FabricNotConnectedException(FabricConnector fabricConnector,
			Exception e) {
		super("Could not connect to Fabric " + fabricConnector.getUrl() + " due to: " + e, e);
	}

}
