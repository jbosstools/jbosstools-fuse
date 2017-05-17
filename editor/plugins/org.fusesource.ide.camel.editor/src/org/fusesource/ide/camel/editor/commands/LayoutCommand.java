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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;


public class LayoutCommand extends RecordingCommand {
	private final IFeatureProvider featureProvider;
	private AbstractCamelModelElement container;
	private PictogramElement diagram;

	public LayoutCommand(IFeatureProvider featureProvider, Diagram diagram, AbstractCamelModelElement container, TransactionalEditingDomain editingDomain) {
		super(editingDomain);
		this.featureProvider = featureProvider;
		this.container = container;
		this.diagram = diagram;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		layout(featureProvider, container);
		if (container instanceof CamelRouteContainerElement) {
			layout(featureProvider, diagram);
		}
	}
	
	private void layout(IFeatureProvider featureProvider, AbstractCamelModelElement container) {
		if (container == null) {
			return;
		}

		if (!CollapseFeature.isCollapsed(featureProvider, container)) {
			for (AbstractCamelModelElement cme : container.getChildElements()) {
				layout(featureProvider, cme);
			}
		}
		
		if (container instanceof CamelRouteElement || (container.getUnderlyingMetaModelObject() != null && container.getUnderlyingMetaModelObject().canHaveChildren())) {
			layout(featureProvider, featureProvider.getPictogramElementForBusinessObject(container));
		}
	}
	
	private void layout(IFeatureProvider featureProvider, PictogramElement pe) {
		// do not layout collapsed figures
		if (CollapseFeature.isCollapsed(pe)) {
			return;
		}
		
		CustomContext cc = new CustomContext(new PictogramElement[] { pe });
		ICustomFeature[] cfs = featureProvider.getCustomFeatures(null);
		for (ICustomFeature cf : cfs) {
			if (cf instanceof LayoutDiagramFeature) {
				cf.execute(cc);
				break;
			}
		}
	}
}
