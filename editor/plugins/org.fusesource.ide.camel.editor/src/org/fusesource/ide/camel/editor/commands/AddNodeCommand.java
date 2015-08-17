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
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.CreateNodeConnectionFeature;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.catalog.eips.Eip;


public class AddNodeCommand extends RecordingCommand {
	private final RiderDesignEditor editor;
	private final AbstractNode selectedNode;

	private final Class<? extends AbstractNode> aClass;
	private final Eip eip;
	
	public AddNodeCommand(RiderDesignEditor editor, TransactionalEditingDomain editingDomain, Class<? extends AbstractNode> aClass, AbstractNode selectedNode) {
		super(editingDomain);
		this.editor = editor;
		this.selectedNode = selectedNode;
		this.aClass = aClass;
		this.eip = null;
	}

	public AddNodeCommand(RiderDesignEditor editor, TransactionalEditingDomain editingDomain, Eip eip, AbstractNode selectedNode) {
		super(editingDomain);
		this.editor = editor;
		this.selectedNode = selectedNode;
		this.aClass = null;
		this.eip = eip;
	}

	
	@Override
	protected void doExecute() {
		CreateNodeConnectionFeature feature = null;
		if( eip == null )
			feature = new CreateNodeConnectionFeature(editor.getFeatureProvider(), aClass);
		else
			feature = new CreateNodeConnectionFeature(editor.getFeatureProvider(), eip);
		
		
		PictogramElement selectedElement = editor.getFeatureProvider().getPictogramElementForBusinessObject(selectedNode);
		PictogramElement[] selectedElements;
		if (selectedElement != null) {
			selectedElements = new PictogramElement[] {selectedElement};
		} else {
			Activator.getLogger().debug("==== TODO - could not find PictogramElement for node " + selectedNode + " probably its that the RiderOutlinePage is out of sync with the Diagram model!");
			selectedElements = editor.getSelectedPictogramElements();
		}
		CustomContext context = new CustomContext(selectedElements);
		feature.execute(context);
	}


}
