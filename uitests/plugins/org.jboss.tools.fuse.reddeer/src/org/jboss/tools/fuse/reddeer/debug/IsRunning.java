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
package org.jboss.tools.fuse.reddeer.debug;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;

/**
 * Checks whether debugging is running
 * 
 * @author tsedmik
 */
public class IsRunning extends AbstractWaitCondition {

	@Override
	public boolean test() {

		if (new SuspendButton().isEnabled()) {
			AbstractWait.sleep(TimePeriod.SHORT);
			return true;
		}
		return false;
	}

	@Override
	public String description() {

		return "Debugger didn't running";
	}
}
