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
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.Wsdl2RestUIActivator;
import org.fusesource.ide.wsdl2rest.ui.wizard.Wsdl2RestOptions;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizardSecondPage extends Wsdl2RestWizardBasePage {

	public Wsdl2RestWizardSecondPage(String pageName) {
		this(pageName, null, null);
	}

	public Wsdl2RestWizardSecondPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage("Customize these options to change paths for the generated Rest DSL Camel XML file, generated Java files, and so on.");
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());

		Text javaPathTextControl = createLabelAndText(composite, "Destination Java Folder", 2);
		Button javaPathBrowseBtn = createButton(composite, "...");
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

		// define the IObservables
		IObservableValue javaPathTarget = WidgetProperties.text(SWT.Modify).observe(javaPathTextControl);
		IObservableValue javaPathModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"destinationJava").observe(getOptionsFromWizard());		
		Binding javaPathBinding = dbc.bindValue(javaPathTarget, javaPathModel, 
				new UpdateValueStrategy().setBeforeSetValidator(new PathValidator()), null);
		ControlDecorationSupport.create(javaPathBinding, SWT.LEFT | SWT.TOP);

		Text camelPathTextControl = createLabelAndText(composite, "Destination Camel Folder", 2);
		Button outPathBrowseButton = createButton(composite, "...");
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

		// define the IObservables
		IObservableValue camelPathTarget = WidgetProperties.text(SWT.Modify).observe(camelPathTextControl);
		IObservableValue camelPathModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"destinationCamel").observe(getOptionsFromWizard());		
		Binding camelPathBinding = dbc.bindValue(camelPathTarget, camelPathModel, 
				new UpdateValueStrategy().setBeforeSetValidator(new PathValidator()), null);
		ControlDecorationSupport.create(camelPathBinding, SWT.LEFT | SWT.TOP);
		
		Text targetAddressText = createLabelAndText(composite, "Target Service Address", 3);

		// define the IObservables
		IObservableValue targetAddressTarget = WidgetProperties.text(SWT.Modify).observe(targetAddressText);
		IObservableValue targetAddressModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"targetServiceAddress").observe(getOptionsFromWizard());		
		Binding targetServiceBinding = dbc.bindValue(targetAddressTarget, targetAddressModel, 
				new UpdateValueStrategy().setBeforeSetValidator(new TargetURLValidator()), null);
		ControlDecorationSupport.create(targetServiceBinding, SWT.LEFT | SWT.TOP);
		
		Text beanClassText = createLabelAndText(composite, "Bean Class", 2);
		Button classBrowse = createButton(composite, "...");
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

		// define the IObservables
		IObservableValue beanClassTarget = WidgetProperties.text(SWT.Modify).observe(beanClassText);
		IObservableValue beanClassModel = BeanProperties.
		    value(Wsdl2RestOptions.class,"beanClassName").observe(getOptionsFromWizard());		
		Binding beanClassBinding = dbc.bindValue(beanClassTarget, beanClassModel, 
				new UpdateValueStrategy().setBeforeSetValidator(new ClassExistsInProject()), null);
		ControlDecorationSupport.create(beanClassBinding, SWT.LEFT | SWT.TOP);

		if (!Strings.isEmpty(getOptionsFromWizard().getDestinationJava())) {
			javaPathTextControl.setText(getOptionsFromWizard().getDestinationJava());
		}
		if (!Strings.isEmpty(getOptionsFromWizard().getDestinationCamel())) {
			camelPathTextControl.setText(getOptionsFromWizard().getDestinationCamel());
		}
		if (!Strings.isEmpty(getOptionsFromWizard().getTargetServiceAddress())) {
			targetAddressText.setText(getOptionsFromWizard().getTargetServiceAddress());
		}
		if (!Strings.isEmpty(getOptionsFromWizard().getBeanClassName())) {
			beanClassText.setText(getOptionsFromWizard().getBeanClassName());
		}

		setControl(composite);
	}
	
	class TargetURLValidator implements IValidator {
		UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		
		@Override
		public IStatus validate(Object value) {
			if (((value instanceof String) && ((String) value).length() > 0)) {
				if (!urlValidator.isValid((String)value)) {
					return ValidationStatus.error("Target Service Address must be in a valid URL format.");
				}
			}
			return ValidationStatus.ok();
		}
	}
	
	class ClassExistsInProject implements IValidator {
		@Override
		public IStatus validate(Object value) {
			if (((value instanceof String) && ((String) value).length() > 0)) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getOptionsFromWizard().getProjectName());
				IJavaProject javaProject = JavaCore.create(project);
				IType javaClass;
				try {
					javaClass = javaProject == null ? null : javaProject.findType((String) value);
					if (javaClass == null) {
						return ValidationStatus.error("Java class must exist in chosen Fuse Integration Project.");
					}
				} catch (JavaModelException e) {
					return ValidationStatus.error("Java class must exist in chosen Fuse Integration Project.", e);
				}
			}
			return ValidationStatus.ok();
		}
	}
	
	class PathValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			if (!((value instanceof String) && ((String) value).length() > 0)) {
				return ValidationStatus.error("Path is required.");
			}
			if (!isPathAccessible((String) value)) {
				return ValidationStatus.error("Path is not accessible. Please point to an accessible folder in the project.");
			}
			return ValidationStatus.ok();   		
		}
	}
	
	private boolean isPathAccessible(String path) {
		Path testPath = new Path(path);
		IResource container = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(testPath);
		while (container == null && !testPath.isEmpty()) {
			testPath = (Path) testPath.removeLastSegments(1);
			container = ResourcesPlugin.getWorkspace().getRoot().findMember(testPath);
			if (container != null && container.exists()) {
				break;
			}
		}
		return container != null && container.exists();
	}

	class FoldersOnlyContentProvider extends BaseWorkbenchContentProvider {

		@Override
		public Object[] getChildren(Object element) {
			// TODO Auto-generated method stub
			return super.getChildren(element);
		}
		
	}
	
	private String selectFolder(IProject project) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
			    Display.getDefault().getActiveShell(), 
			    new WorkbenchLabelProvider(), 
			    new BaseWorkbenchContentProvider());
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IProject) {
					IProject project= (IProject) element;
					return project.isAccessible();
				} else if (element instanceof IFolder) {
					return true;
				}
				return false;
			}
		});
		dialog.setTitle("Container Selection");
		dialog.setMessage("Select a folder:");
		dialog.setInput(project);
		if (dialog.open() == ElementTreeSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				return (((IFolder) result[0]).getFullPath().toPortableString());
			}
		}
		return null;
	}
	
	private String handleClassBrowse(IProject project, Shell shell) {
		IJavaSearchScope scope = null;
		if (project != null) {
			IJavaProject jproject = JavaCore.create(project);
			if (jproject == null) {
				scope = SearchEngine.createWorkspaceScope();
			} else {
				scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { jproject });
			}
		}

		try {
			SelectionDialog dialog = JavaUI.createTypeDialog(shell, null, scope,
					IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, false, ""); //$NON-NLS-1$
			if (dialog.open() == SelectionDialog.OK) {
				Object[] result = dialog.getResult();
				if (result.length > 0 && result[0] instanceof IType) {
					return ((IType) result[0]).getFullyQualifiedName();
				}
			}
		} catch (JavaModelException e) {
			Wsdl2RestUIActivator.pluginLog().logError(e);
		}
		return null;
	}
}
