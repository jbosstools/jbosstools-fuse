/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
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
import org.jboss.tools.fuse.reddeer.view.JMXNavigator;

/**
 * Wait condition which tries select an item at JMX Navigator
 * 
 * @author djelinek
 *
 */
public class JMXConnectionIsAvailable extends AbstractWaitCondition {

	private String[] path;

	public JMXConnectionIsAvailable(String... path) {
		this.path = path;
	}

	@Override
	public boolean test() {
		try {
			if(new JMXNavigator().getNode(path) == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

}
