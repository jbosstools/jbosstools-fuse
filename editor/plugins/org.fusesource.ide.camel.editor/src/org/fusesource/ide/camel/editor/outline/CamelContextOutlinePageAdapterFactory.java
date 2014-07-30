/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
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

package org.fusesource.ide.camel.editor.outline;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.graphiti.internal.pref.GFPreferences;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;

/**
 * @author lhein
 */
public class CamelContextOutlinePageAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTERS = new Class[] { IContentOutlinePage.class };

	@Override
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("rawtypes") Class adapterType) {
		if (GFPreferences.getInstance().isGenericOutlineActive()) {
			if (IContentOutlinePage.class.equals(adapterType)) {
				if (adaptableObject instanceof RiderDesignEditor) {
					RiderDesignEditor diagramEditor = (RiderDesignEditor) adaptableObject;
					if (diagramEditor.getConfigurationProvider() != null) { // diagram
																			// editor
																			// initialized?
						CamelContextOutlinePage outlinePage = new CamelContextOutlinePage(
								new TreeViewer(),
								diagramEditor.getGraphicalViewer(),
								diagramEditor.getActionRegistry(),
								diagramEditor.getEditDomain(),
								diagramEditor.getKeyHandler(),
								diagramEditor.getAdapter(ZoomManager.class),
								diagramEditor.getSelectionSyncer(),
								diagramEditor);
						return outlinePage;
					}
				}
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return ADAPTERS;
	}
}
