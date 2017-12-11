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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * Second page of the wsdl2rest wizard that collects secondary details.
 * @author brianf
 */
public class Wsdl2RestWizardSecondPage extends Wsdl2RestWizardBasePage {

	/**
	 * Constructor
	 * @param pageName
	 */
	public Wsdl2RestWizardSecondPage(String pageName) {
		this(pageName, null, null);
	}

	/**
	 * Constructor
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public Wsdl2RestWizardSecondPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(UIMessages.wsdl2RestWizardSecondPagePageTwoDescription);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		setDescription(UIMessages.wsdl2RestWizardSecondPagePageTwoDescription);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());

		Text javaPathTextControl = createLabelAndText(composite, UIMessages.wsdl2RestWizardSecondPageJavaFolderLabel, 2);
		Button javaPathBrowseBtn = createButton(composite, "..."); //$NON-NLS-1$
		javaPathBrowseBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// browse
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
				String path = selectFolder(project);
				if (path != null) {
					getOptionsFromWizard().setDestinationJava(path);
					javaPathTextControl.notifyListeners(SWT.Modify, new Event());
				}
			}
		});

		Text camelPathTextControl = createLabelAndText(composite, UIMessages.wsdl2RestWizardSecondPageCamelFolderLabel, 2);
		Button outPathBrowseButton = createButton(composite, "..."); //$NON-NLS-1$
		outPathBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// browse
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
				String path = selectFolder(project);
				if (path != null) {
					getOptionsFromWizard().setDestinationCamel(path);
					camelPathTextControl.notifyListeners(SWT.Modify, new Event());
				}
			}
		});

		Text targetAddressText = createLabelAndText(composite, UIMessages.wsdl2RestWizardSecondPageTargetServiceAddressLabel, 3);

		Text beanClassText = createLabelAndText(composite, UIMessages.wsdl2RestWizardSecondPageBeanClassLabel, 2);
		Button classBrowse = createButton(composite, "..."); //$NON-NLS-1$
		classBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
				String beanClass = handleClassBrowse(project, Display.getCurrent().getActiveShell());
				if (!Strings.isEmpty(beanClass)) {
					getOptionsFromWizard().setBeanClassName(beanClass);
				}
			}
		});

		// define the data bindings
		Binding javaPathBinding = createBinding(javaPathTextControl, "destinationJava", new PathValidator()); //$NON-NLS-1$
		ControlDecorationSupport.create(javaPathBinding, SWT.LEFT | SWT.TOP);

		Binding camelPathBinding = createBinding(camelPathTextControl, "destinationCamel", new PathValidator()); //$NON-NLS-1$
		ControlDecorationSupport.create(camelPathBinding, SWT.LEFT | SWT.TOP);

		Binding targetAddressBinding = createBinding(targetAddressText, "targetServiceAddress", new TargetURLValidator()); //$NON-NLS-1$
		ControlDecorationSupport.create(targetAddressBinding, SWT.LEFT | SWT.TOP);

		Binding beanClassBinding = createBinding(beanClassText, "beanClassName", new ClassExistsInProjectValidator(this)); //$NON-NLS-1$
		ControlDecorationSupport.create(beanClassBinding, SWT.LEFT | SWT.TOP);

		// set initial values
		initIfNotEmpty(javaPathTextControl, getOptionsFromWizard().getDestinationJava());
		initIfNotEmpty(camelPathTextControl, getOptionsFromWizard().getDestinationCamel());
		initIfNotEmpty(targetAddressText, getOptionsFromWizard().getTargetServiceAddress());
		initIfNotEmpty(beanClassText, getOptionsFromWizard().getBeanClassName());

		setControl(composite);
		setPageComplete(isPageComplete());
		setErrorMessage(null); // clear any error messages at first
	}
}
