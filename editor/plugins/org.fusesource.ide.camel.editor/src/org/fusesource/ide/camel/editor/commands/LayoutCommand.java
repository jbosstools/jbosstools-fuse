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

import java.util.ArrayList;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.utils.NodeUtils;


public class LayoutCommand extends RecordingCommand {
	private final CamelDesignEditor designEditor;

	public LayoutCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain) {
		super(editingDomain);
		this.designEditor = designEditor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		ArrayList<PictogramElement> containers = new ArrayList<PictogramElement>();
        containers.add(designEditor.getDiagramTypeProvider().getDiagram());
        NodeUtils.getAllContainers(designEditor.getFeatureProvider(), designEditor.getModel().getChildElements().get(0), containers);
        for (int i=0; i<containers.size(); i++) {
	        for (PictogramElement pe : containers) {
	        	CustomContext cc = new CustomContext(new PictogramElement[] {pe});
	        	ICustomFeature[] cfs = designEditor.getFeatureProvider().getCustomFeatures(null);
	        	for (ICustomFeature cf : cfs) {
	        		if (cf instanceof LayoutDiagramFeature) {
	        			cf.execute(cc);		
	        			break;
	        		}
	        	}        	
	        }
        }
	}
}
