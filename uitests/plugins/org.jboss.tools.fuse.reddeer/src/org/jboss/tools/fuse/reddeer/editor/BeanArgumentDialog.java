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
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.CancelButton;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;

/**
 * 
 * @author apodhrad
 *
 */
public class BeanArgumentDialog extends DefaultShell {

	public BeanArgumentDialog() {
		super("Bean Argument");
	}

	public String getType() {
		return new LabeledText(this, "Type").getText();
	}

	public void setType(String name) {
		new LabeledText(this, "Type").setText(name);
	}

	public String getValue() {
		return new LabeledText(this, "Value").getText();
	}

	public void setValue(String name) {
		new LabeledText(this, "Value").setText(name);
	}

	public void ok() {
		new OkButton(this).click();
		new WaitWhile(new ShellIsAvailable(this));
	}

	public void cancel() {
		new CancelButton(this).click();
		new WaitWhile(new ShellIsAvailable(this));
	}
}
