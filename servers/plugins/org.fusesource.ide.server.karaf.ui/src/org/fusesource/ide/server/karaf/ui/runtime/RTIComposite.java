/*******************************************************************************
 * Create a composite to handle the user-specification of a JBoss Fuse Runtime 
 * installation directory.
 * 
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.ui.runtime;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.karaf.ui.Messages;

public class RTIComposite extends Composite implements Listener {
	protected TaskModel taskModel;
	protected IWizardHandle wizardHandle;
	protected Text fuseRTLoc;
	Button browseButton;
	Boolean valid = false;

	public RTIComposite(Composite parent, TaskModel taskModel,
			IWizardHandle wizard) {
		super(parent, SWT.NONE);
		this.taskModel = taskModel;
		this.wizardHandle = wizard;

		createControl();
	}

	@Override
	public void handleEvent(Event event) {
		if (event.widget == fuseRTLoc && validate()) {
			taskModel.putObject(RTITargetFolderWizardFragment.FUSE_RT_LOC, fuseRTLoc.getText());
		}
	}

	public boolean validate() {
		valid = false;
		String dirLocation = fuseRTLoc.getText().trim();

		if (dirLocation != null && !dirLocation.isEmpty()) {
			File file = new File(dirLocation);

			if (!file.exists())
				wizardHandle.setMessage(
						Messages.AbstractKarafRuntimeComposite_no_dir,
						IMessageProvider.ERROR);
			else if (!file.isDirectory())
				wizardHandle.setMessage(
						Messages.AbstractKarafRuntimeComposite_not_a_dir,
						IMessageProvider.ERROR);
			else {
				valid = true;
				wizardHandle.setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
			}
		} else {
			wizardHandle.setMessage(
					Messages.AbstractKarafRuntimeComposite_wizard_help_msg,
					IMessageProvider.NONE); //$NON-NLS-1$
		}
		return valid;
	}

	public boolean isComplete() {
		return this.valid;
	}

	public void createControl() {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.numColumns = 3;
		setLayout(layout);

		Label l = new Label(this, SWT.WRAP);
		l.setText("Target Folder:");

		fuseRTLoc = new Text(this, SWT.SINGLE | SWT.BORDER);
		fuseRTLoc.addListener(SWT.Modify, this);

		GridData fuseRTLocGridData = new GridData();
		fuseRTLocGridData.grabExcessHorizontalSpace = true;
		fuseRTLocGridData.horizontalAlignment = SWT.FILL;
		fuseRTLoc.setLayoutData(fuseRTLocGridData);

		browseButton = new Button(this, SWT.PUSH);
		browseButton.setText(Messages.AbstractKarafRuntimeComposite_browse_text);
		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(browseButton.getShell(), SWT.OPEN);
				dlg.setFilterPath(fuseRTLoc.getText());
				String path = dlg.open();

				if (path != null) {
					fuseRTLoc.setText(path);
				}
			}
		});
	}
}