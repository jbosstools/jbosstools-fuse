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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;

/**
 * @author lhein
 */
public class CamelUtils {
	
	public static final String CAMEL_EDITOR_ID = "org.fusesource.ide.camel.editor";
	
	/**
	 * tries to figure out the used camel version of the currently opened 
	 * diagram's project and if that fails it will return the latest supported
	 * camel version
	 * 
	 * @return
	 */
	public static String getCurrentProjectCamelVersion() {
		String camelVersion = CamelModelFactory.getLatestCamelVersion();
		if (getDiagramEditor() != null) {
			camelVersion = CamelModelFactory.getCamelVersion(getDiagramEditor().getWorkspaceProject());
		}
		return camelVersion;
	}

	/**
	 * retrieves the camel design editor
	 * 
	 * @return
	 */
	public static CamelDesignEditor getDiagramEditor() {
		IEditorPart ep = org.fusesource.ide.foundation.core.util.CamelUtils.getDiagramEditor();
		if (ep != null && ep instanceof CamelEditor) {
			return ((CamelEditor)ep).getDesignEditor();
		}
		return null;
	}
}
