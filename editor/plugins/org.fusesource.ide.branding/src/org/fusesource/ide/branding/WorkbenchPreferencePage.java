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

package org.fusesource.ide.branding;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author lhein
 */
public class WorkbenchPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public WorkbenchPreferencePage() {
	}

	/**
	 * @param title
	 */
	public WorkbenchPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public WorkbenchPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NULL);
		c.setLayout(new FillLayout(SWT.VERTICAL));
		
		Label description = new Label(c, SWT.NONE);
		description.setText("Expand this preferences category to set specific Fuse Tooling preferences.");
		
		return c;
	}
}
