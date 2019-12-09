/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.restconfiguration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.fusesource.ide.camel.editor.component.wizard.ComponentManager;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;

/**
 * @author brianf
 *
 */
public class AddComponentDependencyRunnable implements IRunnableWithProgress {

	private final RestConfigEditor restConfigEditor;
	private final String componentName;

	/**
	 * @param restConfigEditor
	 * @param componentName
	 */
	public AddComponentDependencyRunnable(RestConfigEditor restConfigEditor, String componentName) {
		this.restConfigEditor = restConfigEditor;
		this.componentName = componentName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor pm) {
		SubMonitor subMonitor = SubMonitor.convert(pm, 3);
		IProject project = this.restConfigEditor.parentEditor.getDesignEditor().getWorkspaceProject();
		final CamelModel camelModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project, subMonitor.split(1));
		final ComponentManager componentManager = new ComponentManager(camelModel);
		subMonitor.worked(1);
		Component componentFound = componentManager.getComponentById(componentName);
		if (componentFound != null) {
			new MavenUtils().updateMavenDependencies(componentFound.getDependencies(), project, subMonitor.split(1));
		}
	}
}