/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.preferences;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;

/**
 * Simple dialog to manage a new staging repository entry.
 * 
 * @author brianf
 *
 */
public class StagingRepositoryDialog extends TitleAreaDialog {

	private Text txtRepositoryName;
	private String strRepositoryName = ""; //$NON-NLS-1$

	private Text txtRepositoryURL;
	private String strRepositoryURL = ""; //$NON-NLS-1$
	
	private String strUniquenessList = ""; //$NON-NLS-1$
	
	private Label lblNameErrors;
	private Label lblURLErrors;

	/**
	 * Constructor.
	 * @param parentShell
	 */
	public StagingRepositoryDialog(Shell parentShell) {
		super(parentShell);

		// initialize the uniqueness list to the stored preferences
		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		strUniquenessList = initializer.getStagingRepositoriesString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		getShell().setText("New Staging Repository");
		setTitle(Messages.newStagingRepositoryDialogTitle);
		resetMessage();	    
	}

	private void resetMessage() {
		isValid();
		setErrorMessage(null);
		setMessage(Messages.newRepoDialogMessage, IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, true);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		Label lblName = new Label(container, SWT.NONE);
		GridData gdlblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		lblName.setLayoutData(gdlblNewLabel);
		lblName.setText(Messages.repositoryNameField);
		lblName.setToolTipText(Messages.repositoryNameTooltip);

		txtRepositoryName = new Text(container, SWT.BORDER);
		txtRepositoryName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRepositoryName.setText(strRepositoryName);
		txtRepositoryName.addModifyListener( (ModifyEvent e) -> {
			Text textWidget = (Text) e.getSource();
			String nameText = textWidget.getText();
			strRepositoryName = nameText;
			isValid();
		});
		lblNameErrors = new Label(container, SWT.WRAP);
		GridData gdLblNameErrors = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gdLblNameErrors.minimumHeight= 30;
		lblNameErrors.setLayoutData(gdLblNameErrors);
		lblNameErrors.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));

		Label lblURL = new Label(container, SWT.NONE);
		gdlblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		lblURL.setLayoutData(gdlblNewLabel);
		lblURL.setText(Messages.repositoryURLField);
		lblURL.setToolTipText(Messages.repositoryURLTooltip);

		txtRepositoryURL = new Text(container, SWT.BORDER);
		txtRepositoryURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRepositoryURL.setText(strRepositoryURL);
		txtRepositoryURL.addModifyListener( (ModifyEvent e) -> {
			Text textWidget = (Text) e.getSource();
			String urlText = textWidget.getText();
			strRepositoryURL = urlText;
			isValid();
		});

		lblURLErrors = new Label(container, SWT.WRAP);
		lblURLErrors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
		lblURLErrors.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
		
		return container;
	}

	@Override
	protected void okPressed() {
		strRepositoryURL = txtRepositoryURL.getText();
		strRepositoryName = txtRepositoryName.getText();
		super.okPressed();
	}

	/**
	 * @return repository url
	 */
	public String getURL() {
		return strRepositoryURL;
	}

	/**
	 * @param oldURL
	 *            set old repository url
	 */
	public void setURL(String oldURL) {
		this.strRepositoryURL = oldURL;
		if (this.txtRepositoryURL != null && !this.txtRepositoryURL.isDisposed()) {
			this.txtRepositoryURL.setText(this.strRepositoryURL);
		}
	}

	/**
	 * @return name of repository
	 */
	public String getName() {
		return strRepositoryName;
	}

	/**
	 * @param oldName set old repository name
	 */
	public void setName(String oldName) {
		this.strRepositoryName = oldName;
		if (this.txtRepositoryName != null && !this.txtRepositoryName.isDisposed()) {
			this.txtRepositoryName.setText(this.strRepositoryName);
		}
	}

	/**
	 * Simple URL validator.
	 * 
	 * @param url incoming url to validate
	 * @return true if valid
	 */
	private boolean isValidURL(String url) {
		if (Strings.isBlank(url)) { // can't be null or blank
			return false;
		}
		URL u = null;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}

		try {
			u.toURI();
		} catch (URISyntaxException e) {
			return false;
		}

		return true;
	}

	/**
	 * Simple repository name validator.
	 * @param name incoming
	 * @return true/false
	 */
	private boolean isValidName(String name) {
		return  !Strings.isBlank(name) && 
				!name.contains(" ") &&
				!name.contains(StagingRepositoriesConstants.REPO_SEPARATOR) &&
				!name.contains(StagingRepositoriesConstants.NAME_URL_SEPARATOR);
	}

	public void pressOK() {
		this.okPressed();
	}

	private boolean isValid() {
		boolean isURLValid = isValidURL(strRepositoryURL);
		boolean isNameValid = isValidName(strRepositoryName);
		boolean isNameUnique = isNameUnique(strRepositoryName);
		boolean isValid = isURLValid && isNameValid && isNameUnique;
		getButton(IDialogConstants.OK_ID).setEnabled(isValid);
		lblNameErrors.setText("");
		lblURLErrors.setText("");
		if (!isNameValid) {
			setErrorMessage(Messages.newRepoDialogNameInvalid);
			lblNameErrors.setText(Messages.newRepoDialogNameInvalid);
			return isNameValid;
		} else if (!isNameUnique) {
			setErrorMessage(Messages.newRepoDialogNameNotUnique);
			lblNameErrors.setText(Messages.newRepoDialogNameNotUnique);
			return isNameUnique;
		} else if (!isURLValid) {
			setErrorMessage(Messages.newRepoDialogUrlInvalid);
			lblURLErrors.setText(Messages.newRepoDialogUrlInvalid);
			return isURLValid;
		}
		setErrorMessage(null);
		return isValid;
	}

	private boolean isNameUnique(String name) {
		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		List<List<String>> repoList = initializer.getStagingRepositoriesAsList(strUniquenessList);
		for (List<String> list : repoList) {
			String test = list.get(0);
			if (test.contentEquals(name)) {
				return false;
			}
		}
		return true;
	}

	public String getUniquenessList() {
		return strUniquenessList;
	}

	public void setUniquenessList(String list) {
		this.strUniquenessList = list;
	}
}
