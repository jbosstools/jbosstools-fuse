/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.debug;

import org.jboss.reddeer.swt.impl.menu.ShellMenu;

/**
 * Represents 'Step Over' button
 * 
 * @author tsedmik
 */
public class StepOverButton extends ShellMenu {

	public StepOverButton() {

		super("Run", "Step Over");
	}
}
