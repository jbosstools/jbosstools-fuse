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
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;


/**
 * @author lhein
 */
public class DeleteNodeCommand extends RecordingCommand {
	private final CamelDesignEditor designEditor;
	private final AbstractCamelModelElement selectedNode;

	public DeleteNodeCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractCamelModelElement selectedNode) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.selectedNode = selectedNode;
	}

	@Override
	protected void doExecute() {
		if (selectedNode != null) {
			PictogramElement[] pes = designEditor.getDiagramTypeProvider().getFeatureProvider().getAllPictogramElementsForBusinessObject(selectedNode);
			if (pes != null && pes.length>0) {
				for (PictogramElement pe : pes) {
					DeleteContext ctx = new DeleteContext(pe);
					IDeleteFeature f = designEditor.getFeatureProvider().getDeleteFeature(ctx);
					if (f.canDelete(ctx)) {
						f.delete(ctx);
					}
				}
			}
		}
	}
}
