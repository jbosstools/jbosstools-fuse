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

package org.fusesource.ide.camel.editor.features.custom;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;


/**
 * @author lhein
 */
public class DeleteRouteFeature extends AbstractCustomFeature {

	public DeleteRouteFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return EditorMessages.deleteRouteCommandLabel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return EditorMessages.deleteRouteCommandDescription;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		RiderDesignEditor editor = Activator.getDiagramEditor();
		return editor != null && (editor.getSelectedRoute() != null);
		//return editor != null && (editor.getSelectedNode() == null || editor.getSelectedNode() instanceof RouteSupport);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		RiderDesignEditor editor = Activator.getDiagramEditor();
		if (editor != null) {
			editor.deleteRoute();
			if (editor.getModel().getChildren().size()<1) {
				editor.addNewRoute();
			}
		}
	}
}
