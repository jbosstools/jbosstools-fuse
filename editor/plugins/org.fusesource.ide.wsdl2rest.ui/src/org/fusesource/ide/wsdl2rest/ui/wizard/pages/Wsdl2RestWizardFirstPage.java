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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.Wsdl2RestUIActivator;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestOptions;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizardFirstPage extends Wsdl2RestWizardBasePage {

	private Text urlTextControl;

	public Wsdl2RestWizardFirstPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage("Provide a URL to your WSDL and select the project for the generated artifacts.");
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		setDescription("Provide a URL to your WSDL and select the project for the generated artifacts.");
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());

		urlTextControl = createLabelAndText(composite, "WSDL File:", 2);
		Button urlBrowseBtn = createButton(composite, "...");
		urlBrowseBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWSDL();
				urlTextControl.notifyListeners(SWT.Modify, new Event());
			}
		});

		// define the IObservables
		IObservableValue wsdlTarget = WidgetProperties.text(SWT.Modify).observe(urlTextControl);
		IObservableValue wsdlModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"wsdlURL").observe(getOptionsFromWizard());		
		Binding wsdlBinding = dbc.bindValue(wsdlTarget, wsdlModel, 
				new UpdateValueStrategy().setBeforeSetValidator(new WsdlValidator()), null);
		ControlDecorationSupport.create(wsdlBinding, SWT.LEFT | SWT.TOP);

		Text outputPathControl = createLabelAndText(composite, "Destination Project:", 2);
		Button outPathBrowseButton = createButton(composite, "...");
		outPathBrowseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IProject selectedProject = selectProject();
				if (selectedProject != null) {
					getOptionsFromWizard().setProjectName(selectedProject.getName());
					outputPathControl.notifyListeners(SWT.Modify, new Event());				
				}
			}
		});
		// define the IObservables
		IObservableValue projectTarget = WidgetProperties.text(SWT.Modify).observe(outputPathControl);
		IObservableValue projectModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"projectName").observe(getOptionsFromWizard());		
		Binding projectTextBinding = dbc.bindValue(projectTarget, projectModel,
				new UpdateValueStrategy().setBeforeSetValidator(new ProjectNameValidator()), null);
		ControlDecorationSupport.create(projectTextBinding, SWT.LEFT | SWT.TOP);

		if (!Strings.isEmpty(getOptionsFromWizard().getWsdlURL())) {
			urlTextControl.setText(getOptionsFromWizard().getWsdlURL());
		}
		if (!Strings.isEmpty(getOptionsFromWizard().getProjectName())) {
			outputPathControl.setText(getOptionsFromWizard().getProjectName());
		}

		setControl(composite);
        setPageComplete(isPageComplete());
        setErrorMessage(null); // clear any error messages at first
	}

	class WsdlValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			if (!((value instanceof String) && ((String) value).length() > 0)) {
				return ValidationStatus.error("WSDL url is required.");
			}
			int responseCode = isURLAccessible((String) value);
			if (responseCode != 200) {
				return ValidationStatus.error("WSDL url is not accessible. Please point to an accessible WSDL file.");
			}
			return ValidationStatus.ok();   		}
	}

	class ProjectNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			if (!((value instanceof String) && ((String) value).length() > 0)) {
				return ValidationStatus.error("Project name is required.");
			}
			try {
				IProject testProject = ResourcesPlugin.getWorkspace().getRoot().getProject((String)value);
				if (!testProject.exists()) {
					return ValidationStatus.error("Project name must refer to a valid Fuse Integration Project in the workspace.");
				}
			} catch (Exception ex) {
				return ValidationStatus.error("Project name must refer to a valid Fuse Integration Project in the workspace.");
			}
			return ValidationStatus.ok();
		}
	}

	private int isURLAccessible(String urlText) {
		int code = 200;
		try {
			final URL url = new URL(urlText);
			InputStream testStream = url.openStream();
			testStream.close();
		} catch (Exception ex) {
			code = -1;
		}
		return code;
	}

	private IProject selectProject() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(),
				new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());

		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);

		if (dialog.open() == Window.OK) {
			IResource resource = (IResource) dialog.getFirstResult();
			if (resource instanceof IProject) {
				return (IProject) resource;
			}
		}
		return null;
	}

	private void browseWSDL() {
		FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(getShell(), false,
				ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE) {
			@Override
			protected ItemsFilter createFilter() {
				return new ResourceFilter() {
					@Override
					public boolean matchItem(Object item) {
						IResource resource = (IResource) item;
						return super.matchItem(item) && "wsdl".equals(resource.getFileExtension()); //$NON-NLS-1$
					}
				};
			}
		};
		dialog.setInitialPattern("* "); //$NON-NLS-1$
		if (dialog.open() == FilteredResourcesSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result == null || result.length != 1 || !(result[0] instanceof IResource)) {
				return;
			}
			try {
				IResource resultFile = (IResource) result[0];
				File actualFile = resultFile.getLocation().toFile();
				getOptionsFromWizard().setWsdlURL(actualFile.toURI().toURL().toExternalForm());
				urlTextControl.setText(getOptionsFromWizard().getWsdlURL());
			} catch (MalformedURLException e) {
				Wsdl2RestUIActivator.pluginLog().logError(e);
			}
		}
	}
}
