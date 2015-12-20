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
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class UpdateCommand extends RecordingCommand {
	private final CamelDesignEditor designEditor;
	private CamelModelElement node;

	public UpdateCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, CamelModelElement node) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.node = node;
	}

	@Override
	protected void doExecute() {
		CamelModelElement selectedNode = this.node == null ? designEditor.getSelectedNode() : node;
		if (selectedNode == null) {
			// use the route node in this case
			selectedNode = designEditor.getSelectedRoute();
		}
		PictogramElement pe = selectedNode instanceof CamelContextElement ? designEditor.getDiagramTypeProvider().getDiagram() : designEditor.getFeatureProvider().getPictogramElementForBusinessObject(selectedNode);
		if (pe == null) {
			CamelEditorUIActivator.pluginLog().logInfo("Warning could not find PictogramElement for selectedNode: " + selectedNode);
		}
		UpdateContext ctx = new UpdateContext(pe);
		IUpdateFeature updateFeature = designEditor.getFeatureProvider().getUpdateFeature(ctx);
		updateFeature.update(ctx);
	}
}
