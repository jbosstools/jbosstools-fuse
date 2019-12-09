/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.camel.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.foundation.ui.views.PageTabDescriptor;
import org.jboss.tools.jmx.core.tree.Node;


public class ProcessorCallViewTabDescriptor extends PageTabDescriptor {
	private final Node node;

	ProcessorCallViewTabDescriptor(String label, Node node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new ProcessorCallView(node);
	}
}