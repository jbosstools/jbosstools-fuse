/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.swt.api.TreeItem;

/**
 * Checks whether the given pod (TreeItem) is running
 * 
 * @author tsedmik
 *
 */
public class OpenShiftPodIsRunning extends AbstractWaitCondition {

	private TreeItem pod;
	
	public OpenShiftPodIsRunning(TreeItem pod) {
		this.pod = pod;
	}

	@Override
	public boolean test() {
		return pod.getText().contains("Pod Running");
	}

}
