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
package org.fusesource.ide.wsdl2rest.ui.wizard.pages;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * Main page of the wsdl2rest Wizard, which collects the main data for the utility.
 * @author brianf
 *
 */
public class Wsdl2RestWizardFirstPage extends Wsdl2RestWizardBasePage {

	private Text urlTextControl;

	/**
	 * Constructor
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public Wsdl2RestWizardFirstPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(UIMessages.wsdl2RestWizardFirstPagePageOneDescription);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		setDescription(UIMessages.wsdl2RestWizardFirstPagePageOneDescription);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		urlTextControl = createLabelAndText(composite, UIMessages.wsdl2RestWizardFirstPageWSDLFileLabel, 2);
		Button urlBrowseBtn = createButton(composite, "..."); //$NON-NLS-1$
		urlBrowseBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectWSDL();
				setTargetAddressFromWsdlSelection();
				urlTextControl.notifyListeners(SWT.Modify, new Event());
			}
		});

		Text projectTextControl = createLabelAndText(composite, UIMessages.wsdl2RestWizardFirstPageProjectLabel, 2);
		Button outPathBrowseButton = createButton(composite, "..."); //$NON-NLS-1$
		outPathBrowseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IProject selectedProject = selectProject();
				if (selectedProject != null) {
					getOptionsFromWizard().setProjectName(selectedProject.getName());
					setPathsFromProjectSelection(selectedProject);
					projectTextControl.notifyListeners(SWT.Modify, new Event());				
				}
			}
		});

		// define the data bindings
		Binding wsdlBinding = createBinding(urlTextControl, "wsdlURL", new WsdlValidator()); //$NON-NLS-1$
		wsdlBinding.getModel().addChangeListener(new WsdlChangeListener());
		ControlDecorationSupport.create(wsdlBinding, SWT.LEFT | SWT.TOP);

		Binding projectTextBinding = createBinding(projectTextControl, "projectName", new ProjectNameValidator()); //$NON-NLS-1$
		projectTextBinding.getModel().addChangeListener(new ProjectChangeListener());
		ControlDecorationSupport.create(projectTextBinding, SWT.LEFT | SWT.TOP);

		// set initial values
		initIfNotEmpty(urlTextControl, getOptionsFromWizard().getWsdlURL());
		initIfNotEmpty(projectTextControl, getOptionsFromWizard().getProjectName());

		if (!Strings.isEmpty(getOptionsFromWizard().getProjectName())) {
			setPathsFromProjectSelection(null);
		}

		setControl(composite);
		setPageComplete(isPageComplete());
		setErrorMessage(null); // clear any error messages at first
	}

	class WsdlChangeListener implements IChangeListener {

		@Override
		public void handleChange(ChangeEvent arg0) {
			setTargetAddressFromWsdlSelection();
		}
	}
	
	class ProjectChangeListener implements IChangeListener {

		@Override
		public void handleChange(ChangeEvent arg0) {
			IProject selectedProject = 
					ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
			if (selectedProject != null) {
				setPathsFromProjectSelection(selectedProject);
			}
		}
	}

	/**
	 * Uses the selected project to default the Java and Camel config paths.
	 * @param selectedProject
	 */
	private void setPathsFromProjectSelection(IProject selectedProject) {
		if (selectedProject == null) {
			selectedProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
		}
		StringBuilder pathSrcJava = new StringBuilder().append("src") //$NON-NLS-1$
				.append(Path.SEPARATOR).append("main") //$NON-NLS-1$
				.append(Path.SEPARATOR).append("java"); //$NON-NLS-1$ 
		StringBuilder pathSrcResources = new StringBuilder().append("src") //$NON-NLS-1$
				.append(Path.SEPARATOR).append("main") //$NON-NLS-1$
				.append(Path.SEPARATOR).append("resources"); //$NON-NLS-1$
		IPath javaPath = new Path(pathSrcJava.toString());
		String projectJavaPath = selectedProject.getFullPath().append(javaPath).toPortableString();
		String projectConfigPath = null;
		if (isProjectBlueprint()) {
			StringBuilder pathBlueprint = pathSrcResources.append(Path.SEPARATOR).append("OSGI-INF") //$NON-NLS-1$
					.append(Path.SEPARATOR).append("blueprint"); //$NON-NLS-1$
			IPath configPath = new Path(pathBlueprint.toString());
			projectConfigPath = selectedProject.getFullPath().append(configPath).toPortableString();
		} else { // use spring 
			StringBuilder pathSpring = pathSrcResources.append(Path.SEPARATOR).append("META-INF") //$NON-NLS-1$
					.append(Path.SEPARATOR).append("spring"); //$NON-NLS-1$
			IPath configPath = new Path(pathSpring.toString());
			projectConfigPath = selectedProject.getFullPath().append(configPath).toPortableString();
		}
		getOptionsFromWizard().setDestinationJava(projectJavaPath);
		getOptionsFromWizard().setDestinationCamel(projectConfigPath);
	}
	
	/**
	 * Use the selected wsdl to determine a default target address URL for the generated REST service. 
	 */
	private void setTargetAddressFromWsdlSelection() {
		String address = getLocationFromWSDL();
		if (!Strings.isEmpty(address)) {
			getOptionsFromWizard().setTargetServiceAddress(address);
		}
	}

}
