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
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null && page.getActiveEditor() != null) {
					IEditorReference[] refs = page.getEditorReferences();
					for (IEditorReference ref : refs) {
						// we need to check if the id of the editor ref matches our editor id
						// and also if the active editor is equal to the ref editor otherwise we might pick
						// a wrong editor and return it...bad thing
						if (ref.getId().equals(CAMEL_EDITOR_ID) && 
							page.getActiveEditor().equals(ref.getEditor(false))) {
							// ok, we found a route editor and it is also the acitve editor
							CamelEditor ed = (CamelEditor)ref.getEditor(true);
							// so return it
							return ed.getDesignEditor();
						}
					}
				}
			}
		}
		return null;
	}
}
