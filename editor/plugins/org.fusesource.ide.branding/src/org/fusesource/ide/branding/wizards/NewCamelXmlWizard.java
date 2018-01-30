/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.branding.Activator;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;



/**
 * This is a sample new wizard. Its role is to create a new file
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "xml". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewCamelXmlWizard extends Wizard implements INewWizard {
	private NewCamelXmlWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewCamelXmlWizard.
	 */
	public NewCamelXmlWizard() {
		super();
		setWindowTitle(WizardMessages.NewCamelXMLWizard_wizardTitle);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "/icons/new_camel_context_wizard.png"));
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		page = new NewCamelXmlWizardPage(selection);
		addPage(page);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		final String format = page.getXmlFormat();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, format, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
			String containerName,
			String fileName,
			String format, IProgressMonitor monitor)
					throws CoreException {
		// create a sample file
		monitor.beginTask(NLS.bind(WizardMessages.NewCamelXMLWizard_beginTaskMessage, fileName), 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throw (new CoreException(Activator.getLogger().createStatus(IStatus.ERROR, IStatus.ERROR, "RouteContainer \"" + containerName + "\" does not exist.", null)));
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream(format);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName(WizardMessages.NewCamelXMLWizard_endTaskMessage);
		getShell().getDisplay().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				/*
 				// this approach doesn't seem to recognise our content type on the new file!
 				IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
				try {
					page.openEditor(new FileEditorInput(file), desc.getId());
				} catch (PartInitException ex) {
					// we ignore it for now
				}
				 */
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					// we ignore it for now
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream(String format) {
		boolean spring = true;
		boolean osgi = false;
		boolean trace = true;
		String camelContext = "camelContext";
		String namespace =  "http://camel.apache.org/schema/spring";
		if (!Strings.isBlank(format)) {
			if (Objects.equal(format, NewCamelXmlWizardPage.BLUEPRINT_FORMAT)) {
				namespace = "http://camel.apache.org/schema/blueprint";
				spring = false;
				osgi = true;
			} else if (Objects.equal(format, NewCamelXmlWizardPage.ROUTES_FORMAT)) {
				camelContext = "routes";
				spring = false;
				trace = false;
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		if (spring) {
			builder.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n");
			builder.append("       xmlns:camel=\"" + namespace + "\"\n");
			builder.append("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			builder.append("       xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd\n");
			builder.append("       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\">\n");
		} else if (osgi) {
			builder.append("<blueprint xmlns=\"http://www.osgi.org/xmlns/blueprint/v1.0.0\"\n");
			builder.append("       xmlns:camel=\"" + namespace + "\"\n");
			builder.append("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			builder.append("       xsi:schemaLocation=\"http://www.osgi.org/xmlns/blueprint/v1.0.0 https://www.osgi.org/xmlns/blueprint/v1.0.0/blueprint.xsd\n");
			builder.append("       http://camel.apache.org/schema/blueprint http://camel.apache.org/schema/blueprint/camel-blueprint.xsd\">\n");
		}
		builder.append("\n");
		builder.append("  <");
		builder.append(camelContext);
		builder.append(" xmlns=\"");
		builder.append(namespace);
		builder.append("\"");
		if (trace) {
			builder.append(" trace=\"false\"");
		}
		builder.append(">\n");
		builder.append("    <route>\n");
		builder.append("    </route>\n");
		builder.append("  </");
		builder.append(camelContext);
		builder.append(">\n");
		builder.append("\n");
		if (spring) {
			builder.append("</beans>\n");
		} else if (osgi) {
			builder.append("</blueprint>\n");
		}
		return new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}