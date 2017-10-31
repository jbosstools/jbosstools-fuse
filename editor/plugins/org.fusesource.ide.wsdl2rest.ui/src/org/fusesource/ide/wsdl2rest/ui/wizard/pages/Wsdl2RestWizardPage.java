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
import java.net.MalformedURLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.wsdl2rest.ui.internal.Wsdl2RestUIActivator;

/**
 * @author brianf
 *
 */
public class Wsdl2RestWizardPage extends WizardPage {

	private String wsdlURL;
	private String outputPathURL;
	private Text urlTextControl;
	private Text targetAddressText;
	private String targetAddress;
	private Text beanClassText;
	private String beanClass;

	public Wsdl2RestWizardPage(String pageName) {
		this(pageName, null, null);
	}

	public Wsdl2RestWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage("Provide a URL to your WSDL and select the project and folder to put the generated artifacts into.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());

		urlTextControl = createLabelAndText(composite, "WSDL File URL:", 2);
		urlTextControl.addModifyListener( e ->
		wsdlURL = urlTextControl.getText()
				);
		Button urlBrowseBtn = createButton(composite, "...");
		urlBrowseBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWSDL();
			}
		});

		Text outputPathControl = createLabelAndText(composite, "Output Path:", 2);
		outputPathControl.addModifyListener( e ->
		outputPathURL = outputPathControl.getText()
				);
		Button outPathBrowseButton = createButton(composite, "...");
		outPathBrowseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				outputPathURL = selectFolder(Display.getCurrent().getActiveShell());
			}
		});

		targetAddressText = createLabelAndText(composite, "Target Address:", 3);
		targetAddressText.addModifyListener( e ->
		targetAddress  = targetAddressText.getText()
				);
		beanClassText = createLabelAndText(composite, "Bean Class:", 2);
		beanClassText.addModifyListener( e ->
		beanClass  = beanClassText.getText()
				);
		Button classBrowse = createButton(composite, "...");
		classBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IProject project = sampleGetSelectedProject();
				beanClass = handleClassBrowse(project, Display.getCurrent().getActiveShell());
				if (!Strings.isEmpty(beanClass)) {
					beanClassText.setText(beanClass);
				}
			}
		});

		if (!Strings.isEmpty(wsdlURL)) {
			urlTextControl.setText(wsdlURL);
		}
		if (!Strings.isEmpty(outputPathURL)) {
			outputPathControl.setText(outputPathURL);
		}
		if (!Strings.isEmpty(targetAddress)) {
			targetAddressText.setText(targetAddress);
		}
		if (!Strings.isEmpty(beanClass)) {
			beanClassText.setText(beanClass);
		}

		setControl(composite);
	}

	/**
	 * @param composite
	 */
	protected Text createLabelAndText(Composite composite, String labelText) {
		return createLabelAndText(composite, labelText, 3);
	}

	protected Text createLabelAndText(Composite composite, String labelText, int span) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		Text textControl = new Text(composite, SWT.BORDER);
		textControl.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(span, 1).create());
		return textControl;
	}

	protected Button createButton(Composite composite, String labelText) {
		Button buttonControl = new Button(composite, SWT.PUSH);
		buttonControl.setText(labelText);
		return buttonControl;
	}

	public String getWsdlURL() {
		return wsdlURL;
	}

	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}

	public String getOutputPathURL() {
		return outputPathURL;
	}

	public void setOutputPathURL(String outputPathURL) {
		this.outputPathURL = outputPathURL;
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
				wsdlURL = actualFile.toURI().toURL().toExternalForm();
				urlTextControl.setText(wsdlURL);
			} catch (MalformedURLException e) {
				Wsdl2RestUIActivator.pluginLog().logError(e);
			}
		}
	}

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}

	public String getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}

	public String handleClassBrowse(IProject project, Shell shell) {
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

	private IProject sampleGetSelectedProject() {
		// This is a hack for some prototyping to make sure we know what project to stick the stuff in.
		ISelectionService ss = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		String projExpID = "org.eclipse.ui.navigator.ProjectExplorer";
		ISelection sel = ss.getSelection(projExpID);
		Object selectedObject=sel;
		if(sel instanceof IStructuredSelection) {
			selectedObject=
					((IStructuredSelection)sel).getFirstElement();
		}
		if (selectedObject instanceof IAdaptable) {
			IResource res = ((IAdaptable) selectedObject).getAdapter(IResource.class);
			return res.getProject();
		}
		return null;
	}

	private String selectFolder(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		if (!Strings.isEmpty(outputPathURL)) {
			dialog.setFileName(outputPathURL);
		}
		
		return dialog.open();
	}
}
