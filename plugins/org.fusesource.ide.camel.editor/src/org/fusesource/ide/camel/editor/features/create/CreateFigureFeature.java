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
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class CreateFigureFeature<E> extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Class<E> clazz;
	private AbstractNode exemplar;

	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Class<E> clazz) {
		super(fp, name, description);
		this.clazz = clazz;
	}


	@Override
	public CATEGORY_TYPE getCategoryType() {
		String name = getCategoryName();
		return CATEGORY_TYPE.getCategoryType(name);
	}


	@Override
	public String getCategoryName() {
		AbstractNode node = getExemplar();
		if (node != null) {
			return node.getCategoryName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
	 */
	@Override
	public String getCreateImageId() {
		String iconName = getIconName();
		if (iconName != null) iconName = ImageProvider.getKeyForSmallIcon(iconName);
		return iconName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
	 */
	@Override
	public String getCreateLargeImageId() {
		String iconName = getIconName();
		if (iconName != null) iconName = String.format("%s_large", iconName);
		return iconName;
	}

	/**
	 * retrieves the icon name for the given class via reflection
	 * 
	 * @return	the icon name or null
	 */
	private String getIconName() {
		AbstractNode node = getExemplar();
		if (node != null) {
			return node.getIconName();
		}
		return null;
	}

	/**
	 * Returns the singleton exemplar node we can use to access things like icons and category names etc
	 */
	protected AbstractNode getExemplar() {
		if (exemplar == null) {
			try {
				exemplar = (AbstractNode) clazz.newInstance();
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to create instance of " + clazz + ". " + e, e);
			}
		}
		return exemplar;
	}

	protected void setExemplar(AbstractNode exemplar) {
		this.exemplar = exemplar;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		AbstractNode node = createNode();

		RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
		Diagram diagram = getDiagram();

		if (selectedRoute != null) {
			selectedRoute.addChild(node);
		} else {
			System.out.println("Warning could not find currently selectedNode so can't associate this node with the route!: " + node);
		}

		// Add model element to resource.
		// We add the model element to the resource of the diagram for
		// simplicity's sake. Normally, a customer would use its own
		// model persistence layer for storing the business model separately.
//		diagram.eResource().getContents().add(node);

		//		Use the following instead of the above line to store the model
		//		data in a seperate file parallel to the diagram file
		//		try {
		//			try {
		//				TutorialUtil.saveToModelFile(newClass, getDiagram());
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		} catch (CoreException e) {
		//			e.printStackTrace();
		//		}

		// do the add
		PictogramElement pe = addGraphicalRepresentation(context, node);

		getFeatureProvider().link(pe, node);
		
		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		
		// return newly created business object(s)
		return new Object[] { node };
	}


	protected AbstractNode createNode() {
		AbstractNode node = null;

		try {
			node = (AbstractNode)this.clazz.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return node;
	}
}
