/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.wizards.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.projecttemplates.internal.Messages;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lhein
 */
public class FuseIntegrationProjectWizardLocationPage extends WizardPage {

	private Button useDefaultWorkspaceLocationButton;
	private Label locationLabel;
	private Text locationText;
	private Label projectNameLabel;
	private Text projectNameText;
	
	private IPath location;
	
	/**
	 * 
	 */
	public FuseIntegrationProjectWizardLocationPage() {
		super(Messages.newProjectWizardLocationPageName);
		setTitle(Messages.newProjectWizardLocationPageTitle);
		setDescription(Messages.newProjectWizardLocationPageDescription);
		setImageDescriptor(ProjectTemplatesActivator.imageDescriptorFromPlugin(ProjectTemplatesActivator.PLUGIN_ID, ProjectTemplatesActivator.IMAGE_CAMEL_PROJECT_ICON));
		setPageComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		projectNameLabel = new Label(container, SWT.NONE);
		projectNameLabel.setText(Messages.newProjectWizardLocationPageProjectNameLabel);
		projectNameText = new Text(container, SWT.BORDER);
		projectNameText.setLayoutData(gridData);
		projectNameText.setToolTipText(Messages.newProjectWizardLocationPageProjectNameDescription);
		projectNameText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		Group locationGrp = new Group(container, SWT.BORDER);
		GridData locationGrpData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		locationGrp.setLayout(new GridLayout(3, false));
		locationGrp.setLayoutData(locationGrpData);
		locationGrp.setText(Messages.newProjectWizardLocationPageLocationGroupLabel);
		
		locationLabel = new Label(locationGrp, SWT.NONE);
		GridData locationLabelData = new GridData();
		locationLabelData.horizontalIndent = 10;
		locationLabel.setLayoutData(locationLabelData);
		locationLabel.setText(Messages.newProjectWizardLocationPageLocationLabel);
		locationLabel.setEnabled(false);

		locationText = new Text(locationGrp, SWT.BORDER);
		GridData locationTextData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		locationText.setLayoutData(locationTextData);
		locationText.setToolTipText(Messages.newProjectWizardLocationPageLocationDescription);
		locationText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		locationText.setEnabled(false);

		Button locationBrowseButton = new Button(locationGrp, SWT.NONE);
		GridData locationBrowseButtonData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		locationBrowseButton.setLayoutData(locationBrowseButtonData);
		locationBrowseButton.setText(Messages.newProjectWizardLocationPageLocationBrowseButtonLabel);
		locationBrowseButton.setToolTipText(Messages.newProjectWizardLocationPageLocationBrowseButtonDescription);
		locationBrowseButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText(Messages.newProjectWizardLocationPageLocationSelectionDialogTitle);

				String path = locationText.getText();
				if (path.length() == 0) {
					path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
				}
				dialog.setFilterPath(path);

				String selectedDir = dialog.open();
				if (selectedDir != null) {
					locationText.setText(selectedDir);
					useDefaultWorkspaceLocationButton.setSelection(false);
					validate();
				}
			}
		});

		if (location != null && !Platform.getLocation().equals(location)) {
			locationText.setText(location.toOSString());
		}

		useDefaultWorkspaceLocationButton = new Button(locationGrp, SWT.CHECK);
		GridData useDefaultWorkspaceLocationButtonData = new GridData(SWT.LEFT,	SWT.CENTER, false, false, 3, 1);
		useDefaultWorkspaceLocationButton.setLayoutData(useDefaultWorkspaceLocationButtonData);
		useDefaultWorkspaceLocationButton.setText(Messages.newProjectWizardLocationPageLocationDefaultButtonLabel);
		useDefaultWorkspaceLocationButton.setToolTipText(Messages.newProjectWizardLocationPageLocationDefaultButtonDescription);
		useDefaultWorkspaceLocationButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean inWorkspace = isInWorkspace();
				locationLabel.setEnabled(!inWorkspace);
				locationText.setEnabled(!inWorkspace);
			}
		});
		useDefaultWorkspaceLocationButton.setSelection(true);

		new Label(locationGrp, SWT.None);
		
		setControl(container);
		
		projectNameText.setFocus();
	}
	
	/**
	 * Returns whether the user has chosen to create the project in the
	 * workspace or at an external location.
	 * 
	 * @return <code>true</code> if the project is to be created in the
	 *         workspace, <code>false</code> if it should be created at an
	 *         external location.
	 */
	public boolean isInWorkspace() {
		return useDefaultWorkspaceLocationButton.getSelection();
	}
	
	/**
	 * Validates the contents of this wizard page.
	 * <p>
	 * Feedback about the validation is given to the user by displaying error
	 * messages or informative messages on the wizard page. Depending on the
	 * provided user input, the wizard page is marked as being complete or not.
	 * <p>
	 * If some error or missing input is detected in the user input, an error
	 * message or informative message, respectively, is displayed to the user.
	 * If the user input is complete and correct, the wizard page is marked as
	 * begin complete to allow the wizard to proceed. To that end, the following
	 * conditions must be met:
	 * <ul>
	 * <li>The user must have provided a project name.</li>
	 * <li>The project name must be a valid project resource identifier.</li>
	 * <li>A project with the same name must not exist.</li>
	 * <li>A valid project location path must have been specified.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.eclipse.core.resources.IWorkspace#validateName(java.lang.String,
	 *      int)
	 * @see org.eclipse.core.resources.IWorkspace#validateProjectLocation(org.eclipse.core.resources.IProject,
	 *      org.eclipse.core.runtime.IPath)
	 * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setPageComplete(boolean)
	 */
	protected void validate() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final String name = getProjectName();
		
		// check whether the project name field is empty
		if(name.trim().length() == 0) {
			setErrorMessage(Messages.newProjectWizardLocationPageInvalidProjectNameText);
			setPageComplete(false);
			return;
			}

		// check whether the project name is valid
		final IStatus nameStatus = workspace.validateName(name, IResource.PROJECT);
		if(!nameStatus.isOK()) {
			setErrorMessage(nameStatus.getMessage());
			setPageComplete(false);
			return;
		}

		// check whether project already exists
		final IProject handle = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
		if(handle != null && handle.exists()) {
			setErrorMessage(Messages.newProjectWizardLocationPageDuplicateProjectNameText);
			setPageComplete(false);
			return;
		}

		IPath projectPath = getLocationPath();
		String location = projectPath.toOSString();

		// check whether location is empty
		if (location.length() == 0) {
			setErrorMessage(Messages.newProjectWizardLocationPageInvalidProjectLocationText);
			setPageComplete(false);
			return;
		}

		// check whether the location is a syntactically correct path
		if (!Path.ROOT.isValidPath(location)) {
			setErrorMessage(Messages.newProjectWizardLocationPageInvalidProjectLocationText);
			setPageComplete(false);
			return;
		}

		setPageComplete(true);
		setErrorMessage(null);
		setMessage(null);
	}
	
	/**
	 * returns the project name
	 * 
	 * @return
	 */
	public String getProjectName() {
		return this.projectNameText != null ? this.projectNameText.getText() : null;
	}
	
	/**
	 * Returns the path of the location where the project is to be created.
	 * According to the user input, the path either points to the workspace or
	 * to a valid user specified location on the filesystem.
	 * 
	 * @return The path of the location where to create the project. Is never
	 *         <code>null</code>.
	 */
	public IPath getLocationPath() {
		if (isInWorkspace()) {
			return ResourcesPlugin.getWorkspace().getRoot().getLocation();
		}
		return Path.fromOSString(locationText.getText().trim());
	}
}
