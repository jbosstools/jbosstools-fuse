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

package org.fusesource.ide.branding.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.branding.RiderHelpContextIds;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class NewCamelXmlWizardPage extends WizardPage {
	public static final String SPRING_FORMAT = "Spring";
	public static final String BLUEPRINT_FORMAT = "OSGi Blueprint";
	public static final String ROUTES_FORMAT = "Routes";

	protected static final String DEFAULT_CAMEL_XML_NAME = "camelContext.xml";

	private Text containerText;
	private Text fileText;

	private ISelection selection;
	private Combo formatCombo;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewCamelXmlWizardPage(ISelection selection) {
		super(WizardMessages.NewCamelXMLWizardPage_pageTitle);
		setTitle(WizardMessages.NewCamelXMLWizardPage_pageTitle);
		setDescription(WizardMessages.NewCamelXMLWizardPage_description);
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&RouteContainer:");

		// Sets up the context sensitive help for this page
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, RiderHelpContextIds.NEW_CAMEL_XML_WIZARD_PAGE);

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(WizardMessages.NewCamelXMLWizardPage_browseButton);
		button.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText(WizardMessages.NewCamelXMLWizardPage_labelFile);

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText(WizardMessages.NewCamelXMLWizardPage_labelFormat);

		formatCombo = new Combo(container, SWT.NONE);
		/*
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		formatCombo.setLayoutData(gd);
		 */
		formatCombo.setItems(new String[] { SPRING_FORMAT, BLUEPRINT_FORMAT, ROUTES_FORMAT });
		// TODO remember the last selection each time??
		formatCombo.setText(SPRING_FORMAT);

		initialize();
		dialogChanged();
		setControl(container);
	}

	public String getXmlFormat() {
		return formatCombo.getText();
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1) return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IJavaElement) {
				IJavaElement element = (IJavaElement) obj;
				try {
					obj = element.getUnderlyingResource();
				} catch (JavaModelException e) {
					Activator.getLogger().error("Failed to examine IJavaElement: " + e, e);
				}
			}
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			} else {
				Activator.getLogger().warning("Selection is not a resource so can't select it for the container: " + obj + " " + obj.getClass().getCanonicalName());
			}
		}
		fileText.setText(DEFAULT_CAMEL_XML_NAME);
		updateStatus(null);
		dialogChanged();
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				WizardMessages.NewCamelXMLWizardPage_containerSelectionLabel);
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus(WizardMessages.NewCamelXMLWizardPage_statusUnspecifiedContainer);
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(WizardMessages.NewCamelXMLWizardPage_statusContainerNotExisting);
			return;
		}
		if (!container.isAccessible()) {
			updateStatus(WizardMessages.NewCamelXMLWizardPage_statusProjectReadOnly);
			return;
		}
		if (fileName.length() == 0) {
			updateStatus(WizardMessages.NewCamelXMLWizardPage_statusUnspecifiedFileName);
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus(WizardMessages.NewCamelXMLWizardPage_statusInvalidFileName);
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("xml") == false) {
				updateStatus(WizardMessages.NewCamelXMLWizardPage_statusInvalidExtension);
				return;
			}
		}
		if (container.getLocation().append(fileName).toFile().exists()) {
			updateWarning(WizardMessages.NewCamelXMLWizardPage_statusFileAlreadyExists);
			return;
		}
		updateStatus(null);
		updateWarning(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	private void updateWarning(String message) {
		setMessage(message, WizardPage.WARNING);
		setPageComplete(true);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
}