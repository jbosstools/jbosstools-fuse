/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.ui.IEditorPart;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;

/**
 * @author lhein
 */
public class CamelUtils {

	public static final String CAMEL_EDITOR_ID = "org.fusesource.ide.camel.editor";

	/**
	 * Tries to figure out the used camel version of the currently opened
	 * diagram's project and if that fails it will return the latest supported
	 * camel version
	 *
	 * @return the Camel version
	 */
	public static String getCurrentProjectCamelVersion() {
	    return getCurrentProjectCamelVersion(null);
	}

	/**
	 * Tries to figure out the used camel version of the currently opened
	 * diagram's project and if that fails it will return the latest supported
	 * camel version
	 *
	 * @param editor	the camel design editor instance
	 * @return the Camel version
	 */
	public static String getCurrentProjectCamelVersion(CamelDesignEditor editor) {
		if (editor == null) {
			editor = getDiagramEditor();
		}
		return editor == null ? CamelCatalogUtils.getLatestCamelVersion()
		                      : new CamelMavenUtils().getCamelVersionFromMaven(editor.getWorkspaceProject());
	}
	
	/**
	 * @return the currently open and focused Camel editor
	 */
	public static CamelDesignEditor getDiagramEditor() {
		IEditorPart ep = org.fusesource.ide.foundation.core.util.CamelUtils.getDiagramEditor();
		return ep instanceof CamelEditor ? ((CamelEditor)ep).getDesignEditor() : null;
	}

	public static CamelDesignEditor getDiagramEditor(IDiagramTypeProvider diagramTypeProvider) {
		if(diagramTypeProvider != null
				&& diagramTypeProvider.getDiagramBehavior() != null
				&& diagramTypeProvider.getDiagramBehavior().getDiagramContainer() instanceof CamelDesignEditor){
			return (CamelDesignEditor)diagramTypeProvider.getDiagramBehavior().getDiagramContainer();
		}
		return null;
	}
	
	public static String getRuntimeProvider(IFeatureProvider fp) {
		CamelDesignEditor diagramEditor = CamelUtils.getDiagramEditor(fp.getDiagramTypeProvider());
		return CamelCatalogUtils.getRuntimeprovider(diagramEditor != null ? diagramEditor.getWorkspaceProject() : project(), new NullProgressMonitor());
	}
	
	public static IProject project() {
		CamelDesignEditor editor = CamelUtils.getDiagramEditor();
		return editor == null ? null : editor.getWorkspaceProject();
	}
	
	public static IProject project(IFeatureProvider fp) {
		CamelDesignEditor editor = CamelUtils.getDiagramEditor(fp.getDiagramTypeProvider());
		return editor == null ? null : editor.getWorkspaceProject();
	}
}
