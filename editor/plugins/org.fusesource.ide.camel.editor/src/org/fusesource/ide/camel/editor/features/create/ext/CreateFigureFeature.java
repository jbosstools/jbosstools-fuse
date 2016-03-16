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
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CreateFigureFeature extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Eip eip;
	private Class<? extends AbstractCamelModelElement> clazz;
	
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
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Class<? extends AbstractCamelModelElement> clazz) {
		super(fp, name, description);
		this.clazz = clazz;;
	}

	public Eip getEip() {
		return eip;
	}
	
	/**
	 * @param eip the eip to set
	 */
	public void setEip(Eip eip) {
		this.eip = eip;
	}
	
	public Class<? extends AbstractCamelModelElement> getClazz() {
		return clazz;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		if (eip != null) {
			return ProviderHelper.getCategoryFromEip(eip);
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
		Object containerBO = getBusinessObjectForPictogramElement(container);
		if (container instanceof Diagram) {
			// we want to create something on the diagram rather than inside a container element - thats only allowed on camelContexts
			CamelFile cf = ((CamelDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel();
			if (cf.getCamelContext() == null && cf.getChildElements().size()==0) {
				cf.addChildElement(new CamelContextElement(cf, null));
			}
			if (eip != null) return eip.getName().equalsIgnoreCase("route") || 
				eip.getName().equalsIgnoreCase("rest") || eip.getName().equalsIgnoreCase("restConfiguration");
			if (clazz != null) { 
				Object obj = newInstance(clazz);
				if (obj != null && obj instanceof AbstractCamelModelElement) return ((AbstractCamelModelElement)obj).getNodeTypeId().equalsIgnoreCase("route");
				return false;
			}
		} else if (containerBO instanceof AbstractCamelModelElement && ((AbstractCamelModelElement)containerBO).getNodeTypeId().equalsIgnoreCase("choice")) {
			// only one otherwise per choice
			AbstractCamelModelElement choice = (AbstractCamelModelElement)containerBO;
			if (this.eip != null) {
				if (this.eip.getName().equalsIgnoreCase("otherwise") && choice.getParameter("otherwise") != null) return false;
				return this.eip.getName().equalsIgnoreCase("when") || this.eip.getName().equalsIgnoreCase("otherwise");
			} else if (clazz != null) {
				Object obj = newInstance(clazz);
				if (obj instanceof AbstractCamelModelElement && ((AbstractCamelModelElement)obj).getNodeTypeId().equalsIgnoreCase("otherwise") && choice.getParameter("otherwise") != null) return false;
			}
		}
		
		// we have to prevent some eips being dropped on the route even if camel catalog says its allowed
		if (eip.getName().equalsIgnoreCase("when") || eip.getName().equalsIgnoreCase("otherwise") && containerBO != null && ((AbstractCamelModelElement)containerBO).getNodeTypeId().equalsIgnoreCase("choice") == false) return false;
		
		return (containerBO != null && containerBO instanceof AbstractCamelModelElement && ((AbstractCamelModelElement)containerBO).getUnderlyingMetaModelObject().canHaveChildren() && ((AbstractCamelModelElement)containerBO).getUnderlyingMetaModelObject().getAllowedChildrenNodeTypes().contains(eip.getName()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
	 */
	@Override
	public String getCreateImageId() {
		String iconName = getIconName();
		if (iconName != null) {
			iconName = ImageProvider.getKeyForDiagramIcon(getCategoryType().equals(CATEGORY_TYPE.COMPONENTS), iconName);
		}
		return iconName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
	 */
	@Override
	public String getCreateLargeImageId() {
		String iconName = getIconName();
		if (iconName != null) {
			iconName = ImageProvider.getKeyForDiagramIcon(getCategoryType().equals(CATEGORY_TYPE.COMPONENTS), iconName);
		}
		return iconName;
	}

	/**
	 * retrieves the icon name for the given class via reflection
	 * 
	 * @return	the icon name or null
	 */
	protected String getIconName() {
		String ret = null;
		if (eip != null) {
			ret = eip.getName();
		}
		if (ret == null) {
			AbstractCamelModelElement node = createNode(null, false);
			if(node != null ) {
				ret = node.getIconName();
			}
		}
		return ret != null ? ret : "generic";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
		ContainerShape container = context.getTargetContainer();
		AbstractCamelModelElement selectedContainer = null;
		if (container instanceof Diagram) {
			selectedContainer = editor.getModel().getCamelContext();
		} else {
			selectedContainer = (AbstractCamelModelElement)getBusinessObjectForPictogramElement(container);
		}

		AbstractCamelModelElement node = createNode(selectedContainer, selectedContainer != null);
		if (selectedContainer != null && node != null) {
			selectedContainer.addChildElement(node);
			node.setParent(selectedContainer);
			
			if (Strings.isBlank(node.getId())) {
				node.ensureUniqueID(node);
			}

			// do the add
	        addGraphicalRepresentation(context, node);

	        // activate direct editing after object creation
	        getFeatureProvider().getDirectEditingInfo().setActive(true);
	        
			// return newly created business object(s)
			return new Object[] { node };
		}
		return new Object[0];
	}

	/**
	 * Create a new node of this figure feature's underlying node. 
	 * Default implementation will use either an eip or an Abstract Node class. 
	 * Subclasses with neither should override this method.
	 * 
	 * @return
	 */
	protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
		if( eip != null ) {
			CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
			if (editor.getModel() != null) { 
				Node newNode = null;
				if (createDOMNode) {
					final String nodeTypeId = getEip().getName();
					final String namespace = parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null;
					newNode = editor.getModel().createElement(nodeTypeId, namespace);
				}
				return new CamelBasicModelElement(parent, newNode);
			}
		}
		if( clazz != null ) {
			Object o = newInstance(clazz);
			if( o instanceof AbstractCamelModelElement ) {
				AbstractCamelModelElement e = (AbstractCamelModelElement)o;
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
		new MavenUtils().updateMavenDependencies(compDeps);
    }
    
	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return	the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		// TODO: project camel version vs latest camel version
		String prjCamelVersion = CamelUtils.getCurrentProjectCamelVersion();
		// then get the meta model for the given camel version
		CamelModel model = CamelModelFactory.getModelForVersion(prjCamelVersion);
		if (model == null) {
			return null;
		}
		// then we get the eip meta model
		Eip eip = model.getEipModel().getEIPByName(name);
		// and return it
		return eip;
	}
}
