/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;

public class DownloadPage extends WizardPage {

	private static final String DOWNLOAD_LINK_URL = "http://service.sap.com/connectors"; //$NON-NLS-1$
	private static final String DOWNLOAD_LINK_TEXT = "<a>http://service.sap.com/connectors</a>"; //$NON-NLS-1$

	protected DownloadPage() {
		super(Messages.DownloadPage_DownloadSAPArchiveFiles);
		setDescription(Messages.DownloadPage_DownloadTheSAPJavaConnectorAndSAPJavaIDocClassLibraryArchiveFiles);
		setTitle(Messages.DownloadPage_DownloadPageTitle);
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		GridData gridData;
		Composite top = new Composite(parent, SWT.NONE);
		setControl(top);
		top.setLayout(new GridLayout(1, false));
		
		Label lblInto = new Label(top, SWT.READ_ONLY | SWT.WRAP);
		lblInto.setText(Messages.DownloadPage_DownloadIntro);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.widthHint = parent.getSize().x;
		lblInto.setLayoutData(gridData);
		
		new Label(top, SWT.NONE);
		
		Label lblDownloadDirections = new Label(top, SWT.READ_ONLY | SWT.WRAP);
		lblDownloadDirections.setText(Messages.DownloadPage_DownloadDirections);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.widthHint = parent.getSize().x;
		lblDownloadDirections.setLayoutData(gridData);
		
		new Label(top, SWT.NONE);
		
		Link link = new Link(top, SWT.NONE);
		link.setText(DOWNLOAD_LINK_TEXT);
		
		new Label(top, SWT.NONE);
		
		Label lblContinueDirections = new Label(top, SWT.READ_ONLY | SWT.WRAP);
		lblContinueDirections.setText(Messages.DownloadPage_DownloadContinueDirections);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gridData.widthHint = parent.getSize().x;
		lblContinueDirections.setLayoutData(gridData);
		
		Label label = new Label(top, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		
		Label lblAccountNote = new Label(top, SWT.NONE);
		lblAccountNote.setText(Messages.DownloadPage_DownloadAccountNote);
		
		Label lblJCoVersionNote = new Label(top, SWT.NONE);
		lblJCoVersionNote.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		lblJCoVersionNote.setText(Messages.DownloadPage_DownloadJCoVersionNote);
		
		Label lblIDocVersionNote = new Label(top, SWT.NONE);
		lblIDocVersionNote.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		lblIDocVersionNote.setText(Messages.DownloadPage_DownloadIDocVersionNote);
		
		link.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(DOWNLOAD_LINK_URL));
				} catch (Exception ex) {
					ErrorDialog.openError(getShell(), Messages.DownloadPage_SAPImportWizard, Messages.DownloadPage_ErrorOpeningTheSAPDownloadPage, 
							new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex));
				}
			}
		});

	}

}
