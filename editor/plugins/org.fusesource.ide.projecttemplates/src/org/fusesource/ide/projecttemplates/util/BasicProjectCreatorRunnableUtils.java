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
package org.fusesource.ide.projecttemplates.util;

import java.util.Set;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.camel.editor.utils.BuildAndRefreshJobWaiterUtil;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.fusesource.ide.camel.model.service.core.util.JavaCamelFilesFinder;
import org.fusesource.ide.projecttemplates.internal.ProjectTemplatesActivator;

/**
 * @author lheinema
 */
public class BasicProjectCreatorRunnableUtils {
	
	private BasicProjectCreatorRunnableUtils() {
		// util class
	}
	
	/**
	 * @param project
	 * @param monitor
	 * @param holder
	 */
	public static IFile searchCamelContextJavaFile(IProject project, IProgressMonitor monitor) {
		return new JavaCamelFilesFinder().findJavaDSLRouteBuilderClass(project, monitor);
	}

	/**
	 * @param project
	 * @return
	 */
	public static IFile searchCamelContextXMLFile(IProject project) {
		Set<IFile> camelFiles = new CamelFilesFinder().findFiles(project);
		if(!camelFiles.isEmpty()){
			return camelFiles.iterator().next();
		}
		return null;
	}
	
	/**
	 * converts a project name into a bundle symbolic name
	 * 
	 * @param projectName
	 * @return
	 */
	public static String getBundleSymbolicNameForProjectName(String projectName) {
		return projectName.replaceAll("[^a-zA-Z0-9-_]","");
	}
	
	public static void openCamelFile(IFile file, IProgressMonitor monitor, boolean isJavaEditorToOpen) {
		Display.getDefault().asyncExec( () -> {
			try {
				if (!file.exists()) {
					new BuildAndRefreshJobWaiterUtil().waitJob(monitor);
				}
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if(isJavaEditorToOpen){
					IDE.openEditor(activePage, file, OpenStrategy.activateOnOpen());
				} else {
					IDE.setDefaultEditor(file, CamelUtils.CAMEL_EDITOR_ID);
					IDE.openEditor(activePage, file, CamelUtils.CAMEL_EDITOR_ID, OpenStrategy.activateOnOpen());
				}
			} catch (PartInitException e) {
				ProjectTemplatesActivator.pluginLog().logError("Cannot open camel context file in editor", e); //$NON-NLS-1$
			}
		});
	}
	
	public static boolean isCamelVersionBiggerThan220(String camelVersion) {
		return new ComparableVersion("2.20.0").compareTo(new ComparableVersion(camelVersion)) <= 0;
	}
}
