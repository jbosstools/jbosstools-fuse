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

package org.fusesource.ide.camel.editor.features.create.ext;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.catalog.Dependency;
import org.fusesource.ide.camel.model.catalog.eips.Eip;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;
import org.fusesource.ide.camel.model.generated.UniversalEIPUtility;


/**
 * @author lhein
 */
public class CreateFigureFeature extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Eip eip;
	private Class<? extends AbstractNode> clazz;
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Eip eip) {
		super(fp, name, description);
		this.eip = eip;
	}
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Class<? extends AbstractNode> clazz) {
		super(fp, name, description);
		this.clazz = clazz;;
	}


	public Eip getEip() {
		return eip;
	}
	
	public Class<? extends AbstractNode> getClazz() {
		return clazz;
	}
	
	@Override
	public CATEGORY_TYPE getCategoryType() {
		return CATEGORY_TYPE.getCategoryType(getCategoryName());
	}


	@Override
	public String getCategoryName() {
		if( eip != null )
			return UniversalEIPUtility.getCategoryName(eip.getName());
		AbstractNode an = createNode();
		if( an != null ) {
			return an.getCategoryName();
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
	protected String getIconName() {
		String ret = null;
		if( eip != null )
			ret = UniversalEIPUtility.getIconName(eip.getName());
		if( ret == null ) {
			AbstractNode an = createNode();
			if( an != null ) {
				ret = an.getIconName();
			}
		}
		return ret != null ? ret : "generic.png";
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
			Activator.getLogger().warning("Warning! Could not find currently selectedNode, so can't associate this node with the route!: " + node);
		}

		// do the add
		PictogramElement pe = addGraphicalRepresentation(context, node);

		getFeatureProvider().link(pe, node);
		
		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		
		// return newly created business object(s)
		return new Object[] { node };
	}


	/**
	 * Create a new node of this figure feature's underlying node. 
	 * Default implementation will use either an eip or an Abstract Node class. 
	 * Subclasses with neither should override this method.
	 * 
	 * @return
	 */
	protected AbstractNode createNode() {
		if( eip != null )
			return new UniversalEIPNode(eip);
		if( clazz != null ) {
			Object o = newInstance(clazz);
			if( o instanceof AbstractNode ) {
				return ((AbstractNode)o);
			}
		}
		return null;
	}

	protected Object newInstance(final Class<?> aClass) {
		if( aClass == null ) {
			System.out.println("Dead, left for POC debugging");
		}
		try {
			return aClass.newInstance();
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to create instance of " + aClass.getName() + ". " + e, e);
			return null;
		}
	}

    /**
     * checks if we need to add a maven dependency for the chosen component
     * and inserts it into the pom.xml if needed
     */
    public void updateMavenDependencies(List<Dependency> compDeps) throws CoreException {
    	MavenUtils.updateMavenDependencies(compDeps);
    }
}
