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
package org.jboss.tools.fuse.reddeer.dialog;

import org.eclipse.reddeer.swt.impl.label.DefaultLabel;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;

public class MessageDialog extends DefaultShell {
	
	public MessageDialog(String shellTitle) {
		super(shellTitle);
	}

	public String getMessage() {
		// Label message is the second Widget
		return new DefaultLabel(this, 1).getText();
	}

}
