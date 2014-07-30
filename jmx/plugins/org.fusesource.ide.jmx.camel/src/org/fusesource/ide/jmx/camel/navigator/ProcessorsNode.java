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

package org.fusesource.ide.jmx.camel.navigator;

import org.fusesource.ide.commons.tree.RefreshableCollectionNode;

public class ProcessorsNode extends RefreshableCollectionNode {
	private final CamelContextNode camelContextNode;

	public ProcessorsNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}

	@Override
	public String toString() {
		return "Processors";
	}

	@Override
	protected void loadChildren() {
		// TODO Auto-generated method stub
		
	}


}
