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

package org.fusesource.ide.fabric.navigator.jmx;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.jboss.tools.jmx.ui.ImageProvider;



public class JmxNode extends RefreshableNode implements ImageProvider {
	private final ContainerNode agentNode;

	public JmxNode(ContainerNode agentNode) {
		super(agentNode);
		this.agentNode = agentNode;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("releng_gears.gif");
	}


	@Override
	protected void loadChildren() {
		FabricConnectionWrapper wrapper = new FabricConnectionWrapper(agentNode);
		wrapper.loadChildren(this);
	}

}