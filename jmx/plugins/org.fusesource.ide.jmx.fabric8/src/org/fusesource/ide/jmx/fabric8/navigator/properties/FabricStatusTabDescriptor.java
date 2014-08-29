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
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;

public class FabricStatusTabDescriptor extends PageTabDescriptor {
	
	private final Fabric8Node node;

	/**
	 * 
	 * @param label
	 * @param node
	 */
	public FabricStatusTabDescriptor(String label, Fabric8Node node) {
		super(label);
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.PageTabDescriptor#createPage()
	 */
	@Override
	protected IPage createPage() {
		return new FabricStatusTableSheetPage(node);
	}
}