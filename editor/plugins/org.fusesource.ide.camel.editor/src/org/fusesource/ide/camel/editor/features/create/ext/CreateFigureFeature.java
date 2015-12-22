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
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ProviderHelper;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CreateFigureFeature extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Eip eip;
	private Class<? extends CamelModelElement> clazz;
	
	/**
	 * 
	 * @param fp
	 * @param name
	 * @param description
	 * @param eip
	 */
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Eip eip) {
		super(fp, name, description);
		this.eip = eip;
	}
	
	/**
	 * 
	 * @param fp
	 * @param name
	 * @param description
	 * @param clazz
	 */
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Class<? extends CamelModelElement> clazz) {
		super(fp, name, description);
		this.clazz = clazz;;
	}

	public Eip getEip() {
		return eip;
	}
	
	public Class<? extends CamelModelElement> getClazz() {
		return clazz;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		if (eip != null) {
			return ProviderHelper.getCategoryFromTags(eip.getTags());
		}
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
	 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		ContainerShape container = context.getTargetContainer();
		Object bo = getBusinessObjectForPictogramElement(container);
		if (container instanceof Diagram) {
			// we want to create something on the diagram rather than inside a container element - thats only allowed on camelContexts
			CamelFile cf = ((CamelDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel();
			if (cf.getCamelContext() == null && cf.getChildElements().size()==0) {
				cf.addChildElement(new CamelContextElement(cf, null));
			}
			if (eip != null) return eip.getName().equalsIgnoreCase("route");
			if (clazz != null) { 
				Object obj = newInstance(clazz);
				if (obj != null && obj instanceof CamelModelElement) return ((CamelModelElement)obj).getNodeTypeId().equalsIgnoreCase("route");
				return false;
			}
		} 
		// make sure we only have a single otherwise per choice
		if (bo instanceof CamelModelElement && ((CamelModelElement)bo).getNodeTypeId().equals("choice")) {
			// only one otherwise per choice
			CamelModelElement choice = (CamelModelElement)bo;
			if (this.eip != null) {
				if (this.eip.getName().equals("otherwise") && choice.getParameter("otherwise") != null) return false;
			} else if (clazz != null) {
				Object obj = newInstance(clazz);
				if (obj instanceof CamelModelElement && ((CamelModelElement)obj).getNodeTypeId().equalsIgnoreCase("otherwise") && choice.getParameter("otherwise") != null) return false;
			}
		}
		return (bo != null && bo instanceof CamelModelElement && ((CamelModelElement)bo).getUnderlyingMetaModelObject().canHaveChildren());
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
		CamelModelElement node = createNode(null, false);
		if(node != null ) {
			ret = node.getIconName();
		}
		return ret != null ? ret : "generic.png";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
		ContainerShape container = context.getTargetContainer();
		CamelModelElement selectedContainer = null;
		if (container instanceof Diagram) {
			selectedContainer = editor.getModel().getCamelContext();
		} else {
			selectedContainer = (CamelModelElement)getBusinessObjectForPictogramElement(container);
		}

		CamelModelElement node = createNode(selectedContainer, selectedContainer != null);
		if (selectedContainer != null) {
			selectedContainer.addChildElement(node);
			node.setParent(selectedContainer);
		} else {
			CamelEditorUIActivator.pluginLog().logWarning("Warning! Could not find currently selected node, so can't associate this node with the container! Node: " + node.getName());
		}

		// do the add
        addGraphicalRepresentation(context, node);

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
	protected CamelModelElement createNode(CamelModelElement parent, boolean createDOMNode) {
		if( eip != null ) {
			CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
			if (editor.getModel() != null) { 
				Node newNode = null;
				if (createDOMNode) {
					newNode = editor.getModel().getDocument().createElement(eip.getName());
				}
				return new CamelModelElement(parent, newNode);
			}
		}
		if( clazz != null ) {
			Object o = newInstance(clazz);
			if( o instanceof CamelModelElement ) {
				CamelModelElement e = (CamelModelElement)o;
				e.setParent(parent);
				return e;
			}
		}
		return null;
	}

	protected Object newInstance(final Class<?> aClass) {
		try {
			return aClass.newInstance();
		} catch (Exception e) {
			CamelEditorUIActivator.pluginLog().logWarning("Failed to create instance of " + aClass.getName() + ". " + e, e);
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
