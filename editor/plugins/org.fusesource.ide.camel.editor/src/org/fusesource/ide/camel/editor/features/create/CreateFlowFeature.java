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
package org.fusesource.ide.camel.editor.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * @author lhein
 */
public class CreateFlowFeature extends AbstractCreateConnectionFeature implements PaletteCategoryItemProvider {

	public CreateFlowFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "Flow", "Create Flow"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider#getCategoryType()
	 */
	@Override
	public CATEGORY_TYPE getCategoryType() {
		return CATEGORY_TYPE.getCategoryType(getCategoryName());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreateConnection#canCreate(org.eclipse.graphiti.features.context.ICreateConnectionContext)
	 */
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// return true if both anchors belong to a EClass
		// and those EClasses are not identical
		AbstractCamelModelElement source = getNode(context.getSourceAnchor());
		AbstractCamelModelElement target = getNode(context.getTargetAnchor());
		
		if (target != null && source != target) {
			// if we only support a single output and we already have one then we can't connect to another output
			return source.getOutputElement() == null && source.getParent().equals(target.getParent()) && target.getInputElement() == null && source instanceof CamelRouteElement == false && target instanceof CamelRouteElement == false;
		}
		return false;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// return true if start anchor belongs to a EClass
		if (getNode(context.getSourceAnchor()) != null) {
			return true;
		}
		return false;
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FLOW;
	}

	@Override
	public String getCreateLargeImageId() {
		return getCreateImageId();
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get EClasses which should be connected
		AbstractCamelModelElement source = getNode(context.getSourceAnchor());
		AbstractCamelModelElement target = getNode(context.getTargetAnchor());

		if (target != null) {
			// create new business object
			CamelElementConnection eReference = createFlow(source, target);

			// add connection for business object
			AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
			addContext.setNewObject(eReference);
			addContext.putProperty(AddFlowFeature.DEACTIVATE_LAYOUT, context.getProperty(AddFlowFeature.DEACTIVATE_LAYOUT));
			newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		}

		return newConnection;
	}

	/**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	private AbstractCamelModelElement getNode(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
			if (obj instanceof AbstractCamelModelElement) {
				return (AbstractCamelModelElement) obj;
			}
		}
		return null;
	}

	/**
	 * Creates a EReference between two EClasses.
	 */
	private CamelElementConnection createFlow(AbstractCamelModelElement source, AbstractCamelModelElement target) {
		return new CamelElementConnection(source, target);
	}
}
