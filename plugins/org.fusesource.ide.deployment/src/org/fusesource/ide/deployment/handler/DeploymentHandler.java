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

package org.fusesource.ide.deployment.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.commands.ParameterValuesException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.deployment.DeployPlugin;
import org.fusesource.ide.deployment.config.HotfolderDeploymentConfiguration;


/**
 * @author lhein
 */
public class DeploymentHandler extends AbstractHandler {

	public static final String DEPLOY_PARAMETER_KEY = "org.fusesource.ide.deployment.commandParameter.config";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String configSelection = (String)event.getParameters().get(DEPLOY_PARAMETER_KEY);
		HotfolderDeploymentConfiguration cfg = null;
		Command command = event.getCommand();
		IParameter iParam = null;
		try {
			iParam = command.getParameter(DeploymentHandler.DEPLOY_PARAMETER_KEY);
		} catch (NotDefinedException e1) {
			throw new ExecutionException("Missing parameter in hot folder deployment. " + e1.getMessage());
		}
		IParameterValues vals = null;
		try {
			vals = iParam.getValues();
		} catch (ParameterValuesException e) {
			throw new ExecutionException("Missing parameter value in hot folder deployment. " + e.getMessage());
		}
		
		cfg = (HotfolderDeploymentConfiguration)vals.getParameterValues().get(configSelection);
		DeployInNamedContainerAction deployAction = new DeployInNamedContainerAction(cfg);
		
		ISelection isel = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		// if there is no project / file / folder selected we use the opened editor if available
		if (isel == null || isel.isEmpty() || ( ((IStructuredSelection)isel).getFirstElement() instanceof IJavaElement == false && 
												((IStructuredSelection)isel).getFirstElement() instanceof IResource == false ) ) {
			IEditorPart editor = DeployPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			deployAction.launch(editor, "run");
		} else {
			// use the selected file/folder/project as parameter
			deployAction.launch(isel, "run");	
		}		
		return null;
	}
}
