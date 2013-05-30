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
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class CreateFlowFeature extends AbstractCreateConnectionFeature implements PaletteCategoryItemProvider {

	public CreateFlowFeature(IFeatureProvider fp) {
		// provide name and description for the UI, e.g. the palette
		super(fp, "Flow", "Create Flow"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public CATEGORY_TYPE getCategoryType() {
		String name = getCategoryName();
		return CATEGORY_TYPE.getCategoryType(name);
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// return true if both anchors belong to a EClass
		// and those EClasses are not identical
		AbstractNode source = getNode(context.getSourceAnchor());
		AbstractNode target = getNode(context.getTargetAnchor());
		if (target != null && source != target) {
			// source == null indicates its a new node with the route being the source
			return source == null || source.canConnectTo(target);
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
		AbstractNode source = getNode(context.getSourceAnchor());
		AbstractNode target = getNode(context.getTargetAnchor());

		if (target != null) {
			if (source == null) {
				// lets add the target to the diagram
				RiderDesignEditor editor = RiderDesignEditor.toRiderDesignEditor(getDiagramBehavior());
				if (editor != null) {
					RouteSupport route = editor.getSelectedRoute();
					if (route != null) {
						route.addChild(target);
					}
				}
			} else {
				// create new business object
				Flow eReference = createFlow(source, target);

				// add connection for business object
				AddConnectionContext addContext = new AddConnectionContext(
						context.getSourceAnchor(), context.getTargetAnchor());
				addContext.setNewObject(eReference);
				newConnection = (Connection) getFeatureProvider().addIfPossible(
						addContext);
			}
		}

		return newConnection;
	}

	/**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	private AbstractNode getNode(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (obj instanceof AbstractNode) {
				return (AbstractNode) obj;
			}
		}
		return null;
	}

	/**
	 * Creates a EReference between two EClasses.
	 */
	private Flow createFlow(AbstractNode source, AbstractNode target) {
		Flow flow = new Flow(source, target);
		flow.setName("new Flow"); //$NON-NLS-1$

		return flow;
	}

	@Override
	public String getCategoryName() {
		return null;
	}
}
