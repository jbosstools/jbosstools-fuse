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

package org.fusesource.ide.jmx.core.tree;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.jmx.core.JMXActivator;


public class MBeansNode extends RefreshableCollectionNode implements ImageProvider{

	public MBeansNode(Node parent) {
		super(parent);
	}

	@Override
	public String toString() {
		return "MBeans";
	}

	@Override
	public Image getImage() {
		return JMXActivator.getDefault().getImage("mbeans.png");
	}

	@Override
	protected void loadChildren() {
	}

	@Override
	public void refresh() {
		// TODO don't use refresh parent & reselect it yet as for now this means
		// reloading the entire JMX connection typically, which doesn't work too well right now
		// as the parent node becomes a JvmConnectionWrapper rather than Root
		// so we can't easily find the nodes to expand again.
		//
		// refreshParent();
		Node p = getParent();
		if (p instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) p;
			refreshable.refresh();
		}
	}


}
