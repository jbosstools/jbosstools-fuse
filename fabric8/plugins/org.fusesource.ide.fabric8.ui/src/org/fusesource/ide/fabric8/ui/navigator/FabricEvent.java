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

package org.fusesource.ide.fabric8.ui.navigator;

import java.util.EventObject;

public class FabricEvent extends EventObject  {

	private static final long serialVersionUID = -7690212507338650848L;

	public FabricEvent(Fabric fabric) {
		super(fabric);
	}

	public Fabric getFabric() {
		return (Fabric) getSource();
	}

}
