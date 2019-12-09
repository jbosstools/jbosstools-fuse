/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.handler;

import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * @author lhein
 */
public class RouteDblClickHandler implements ICustomDblClickHandler {

	/**
	 * this handler executes a Go Into for Route elements 
	 */
	public RouteDblClickHandler() {
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler#canHandle(org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement)
	 */
	@Override
	public boolean canHandle(AbstractCamelModelElement clickedNode) {
		return clickedNode instanceof CamelRouteElement;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomDblClickHandler#handleDoubleClick(org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement)
	 */
	@Override
	public void handleDoubleClick(AbstractCamelModelElement clickedNode) {
		// if we are in the camel context view and there is more than one
		// route we can then execute the Go Into Feature
		CamelRouteElement route = (CamelRouteElement)clickedNode;
		CamelDesignEditor designEditor = CamelUtils.getDiagramEditor();
		if (designEditor != null && 
			designEditor.getSelectedContainer() instanceof CamelContextElement && 
			designEditor.getModel().getRouteContainer().getChildElements().size()>1) {
			// now execute the go into
			designEditor.setSelectedContainer(route);
		}
	}
}
