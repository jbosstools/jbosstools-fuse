/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;

/**
 * @author lhein
 */
public class UpdateCommand extends RecordingCommand {
	
	private final CamelDesignEditor designEditor;
	private AbstractCamelModelElement node;

	public UpdateCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractCamelModelElement node) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.node = node;
	}

	@Override
	protected void doExecute() {
		AbstractCamelModelElement selectedNode = this.node == null ? designEditor.getSelectedNode() : node;
		if (selectedNode == null) {
			// use the route node in this case
			selectedNode = designEditor.getModel().getRouteContainer();
		}
		updateFigure(selectedNode);
	}
	
	private void updateFigure(AbstractCamelModelElement node) {
		if (node == null){
			return;
		}

		IFeatureProvider featureProvider = designEditor.getFeatureProvider();
		PictogramElement pe = node instanceof CamelContextElement ? designEditor.getDiagramTypeProvider().getDiagram() : featureProvider.getPictogramElementForBusinessObject(node);
		if (pe == null) {
			return;
		}
		
		// do check if underlying xml node changed / document changed
		AbstractCamelModelElement bo2 = designEditor.getModel().findNode(node.getId());
		if (bo2 != null && !bo2.getXmlNode().isEqualNode(node.getXmlNode())) {
			featureProvider.link(pe, bo2);
		}
		
		if (CollapseFeature.isCollapsed(pe)) {
			// do not layout collapsed figures
			return;
		}
		UpdateContext ctx = new UpdateContext(pe);
		IUpdateFeature updateFeature = featureProvider.getUpdateFeature(ctx);
		if(updateFeature != null){
			updateFeature.update(ctx);
		}
		for (AbstractCamelModelElement elem : node.getChildElements()) {
			updateFigure(elem);
		}
	}
}
