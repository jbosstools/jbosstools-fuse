/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.swt.api.TreeItem;

/**
 * Checks whether a TreeItem in OpenShift Explorer View does not contains "Loading"
 * 
 * @author tsedmik
 */
public class OpenShiftExplorerLoadingIsCompleted extends AbstractWaitCondition {

	private TreeItem item;

	public OpenShiftExplorerLoadingIsCompleted(TreeItem item) {
		this.item = item;
	}

	@Override
	public boolean test() {
		return !item.getItems().get(0).getText().startsWith("Loading");
	}

}
