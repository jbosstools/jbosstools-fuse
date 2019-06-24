/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.test.util.editor;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.internal.command.CommandExec;
import org.eclipse.graphiti.internal.command.GenericFeatureCommandWithContext;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.util.StatusHandler;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.branding.perspective.FusePerspective;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.fusesource.ide.camel.tests.util.Activator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class AbstractCamelEditorIT {
	
	@Rule
	public FuseProject fuseProject = new FuseProject(getClass().getName());
	
	boolean safeRunnableIgnoreErrorStateBeforeTests;
	protected boolean statusHandlerCalled = false;

	StatusHandler statusHandlerBeforetest;
	
	protected String routeContainerType;

	@Rule
	public TestRule watcher = new TestWatcher() {
		@Override
		protected void starting(Description description) {
			Activator.pluginLog().logInfo("Starting test: " + description.getDisplayName());
		}
	};

	public AbstractCamelEditorIT() {
		super();
	}

	@Before
	public void setup() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart welcomePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		welcomePage.dispose();
		page.closeAllEditors(false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		safeRunnableIgnoreErrorStateBeforeTests = SafeRunnable.getIgnoreErrors();
		SafeRunnable.setIgnoreErrors(false);
		statusHandlerBeforetest = Policy.getStatusHandler();
		statusHandlerCalled = false;
		Policy.setStatusHandler(new StatusHandler() {
			
			@Override
			public void show(IStatus status, String title) {
				statusHandlerCalled = true;
			}
		});
	}

	@After
	public void tearDown() throws Exception {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
		SafeRunnable.setIgnoreErrors(safeRunnableIgnoreErrorStateBeforeTests);
		Policy.setStatusHandler(statusHandlerBeforetest);
	}

	protected IEditorPart openFileInEditor(String filePathPattern) throws Exception {
		String filePath = computeFilePathoUse(filePathPattern);
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);
		final IFile fileWithoutContext = fuseProject.getProject().getFile(filePath.startsWith("/") ? filePath.substring(1) : filePath);
		fileWithoutContext.create(inputStream, true, new NullProgressMonitor());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllPerspectives(false, false);
		PlatformUI.getWorkbench().showPerspective(FusePerspective.ID, page.getWorkbenchWindow());
		readAndDispatch(20);
		IEditorPart editor = IDE.openEditor(page, fileWithoutContext, CamelUtils.CAMEL_EDITOR_ID);
		page.activate(editor);
		editor.setFocus();
		readAndDispatch(20);
		return editor;
	}

	protected String computeFilePathoUse(String filePath) {
		return filePath+"-"+routeContainerType+".xml";
	}

	protected void readAndDispatch(int currentNumberOfTry) {
		try{
			while (Display.getDefault().readAndDispatch()) {
				
			}
		} catch(SWTException swtException){
			//TODO: remove try catch when https://issues.jboss.org/browse/FUSETOOLS-1913 is done (CI with valid GUI)
			swtException.printStackTrace();
			if(currentNumberOfTry < 100){
				readAndDispatch(currentNumberOfTry ++);
			} else {
				System.out.println("Tried 100 times to wait for UI... Continue and see what happens.");
			}
		}
	}
	
	protected void executeCommandInTransactionDomain(IContext context, IFeature feature) {
		if (feature.canExecute(context)) {
			TransactionalEditingDomain editingDomain = CamelUtils.getDiagramEditor().getEditingDomain();
			CommandExec.getSingleton().executeCommand(new GenericFeatureCommandWithContext(feature, context), editingDomain);
		}
	}

}