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

import static org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement.ENDPOINT_TYPE_FROM;
import static org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement.ENDPOINT_TYPE_TO;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.features.misc.ReconnectNodesFeature;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ProviderHelper;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
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
		
		// creating on the Camel Context / Diagram
		if (container instanceof Diagram) {
			CamelFile cf = ((CamelDesignEditor)getDiagramBehavior().getDiagramContainer()).getModel();
			// check if the CamelFile has a Camel Context
			if (cf.isEmpty()) {
				// if not, then we need to add one right away to prevent further problems
				cf.addChildElement(new CamelContextElement(cf, null));
			}
			
			// if we got an EIP reference then we can check if thats allowed
			// to be added to the camel context
			if (eip != null) {
				return NodeUtils.canBeAddedToCamelContextDirectly(eip);
			// if we only have a class defined
			} else if (clazz != null) { 
				// then we need to instantiate it
				Object obj = newInstance(clazz);
				if (obj != null && 
					obj instanceof AbstractCamelModelElement) {
					// and check if the element can be added to a context
					return NodeUtils.canBeAddedToCamelContextDirectly((AbstractCamelModelElement)obj);
				}
				return false;
			}

		// special handling for creating on choices (only one otherwise allowed)
		} else if (	containerBO instanceof AbstractCamelModelElement && 
					((AbstractCamelModelElement)containerBO).getNodeTypeId().equalsIgnoreCase("choice")) {
			// only one otherwise per choice
			AbstractCamelModelElement choice = (AbstractCamelModelElement)containerBO;
			
			if (this.eip != null) {
				if (this.eip.getName().equalsIgnoreCase("otherwise") && 
					choice.getParameter("otherwise") != null) {
					// this choice already has an otherwise case - adding more isn't allowed
					return false;
				}
				// allow when and otherwise children on choice
				return this.eip.getName().equalsIgnoreCase("when") || 
					   this.eip.getName().equalsIgnoreCase("otherwise");
			
			} else if (clazz != null) {
				Object obj = newInstance(clazz);
				if (obj instanceof AbstractCamelModelElement && 
					((AbstractCamelModelElement)obj).getNodeTypeId().equalsIgnoreCase("otherwise") && 
					choice.getParameter("otherwise") != null) {
					// this choice already has an otherwise case - adding more isn't allowed
					return false;
				}
			}
		}
		
		// we have to prevent some eips being dropped on the route even if camel catalog says its allowed
		// probably issues in the catalog which will be fixed at later time, then we can
		// remove the handling for these exceptions
		if (eip.getName().equalsIgnoreCase("when") || eip.getName().equalsIgnoreCase("otherwise") && 
			containerBO != null && 
			!((AbstractCamelModelElement)containerBO).getNodeTypeId().equalsIgnoreCase("choice")) {
			return false;
		}
		
		if (containerBO != null && 
			containerBO instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement sourceNode = (AbstractCamelModelElement)containerBO;
			
			if (NodeUtils.isValidChild(sourceNode, eip)) {
				return true;
			} else {
				// only allow drop on node if the node has no outgoing or no incoming connection
				return sourceNode.getOutputElement() == null || sourceNode.getInputElement() == null;
			}
		}
		return false;
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
		AbstractCamelModelElement selectedContainer;
		if (container instanceof Diagram) {
			selectedContainer = editor.getModel().getCamelContext();
		} else {
			selectedContainer = (AbstractCamelModelElement)getBusinessObjectForPictogramElement(container);
		}

		CreateContext ctxNew = null;
		AbstractCamelModelElement node = null;
		if (selectedContainer != null && 
			(!selectedContainer.getUnderlyingMetaModelObject().canHaveChildren() || context.getTargetConnection() != null)) {
			node = createNode(selectedContainer instanceof CamelRouteElement ? selectedContainer : selectedContainer.getParent(), true);
			if (context.getTargetConnection() == null) {
				selectedContainer = selectedContainer.getParent();
			}
			ctxNew = new CreateContext(); 
			ctxNew.setTargetContainer((ContainerShape)getFeatureProvider().getAllPictogramElementsForBusinessObject(selectedContainer)[0]);
			ctxNew.setLocation(context.getX(), context.getY());
		} else {
			node = createNode(selectedContainer, selectedContainer != null);
		}
		if (selectedContainer != null && node != null) {
			selectedContainer.addChildElement(node);
			node.setParent(selectedContainer);
			
			if (Strings.isBlank(node.getId())) {
				node.ensureUniqueID(node);
			}

			// do the add
			if (ctxNew != null) {
				addGraphicalRepresentation(ctxNew, node);
				AbstractCamelModelElement srcNode = (AbstractCamelModelElement)getBusinessObjectForPictogramElement(container);
				if(context.getTargetConnection() != null) {
					// drop on a connection -> insert node into the flow
					insertNode(node, context.getTargetConnection());
				} else {
					if (srcNode.getInputElement() == null && srcNode.getOutputElement() != null) {
						// drop on a figure with no input element -> prepend 
						prependNode(srcNode, node);
					} else {
						// drop on a figure with no output (and maybe also no input) element -> append
						appendNode(srcNode, node);
					}
				}				
			} else {
				addGraphicalRepresentation(context, node);
			}

	        // activate direct editing after object creation
	        getFeatureProvider().getDirectEditingInfo().setActive(true);
	        
			// return newly created business object(s)
			return new Object[] { node };
		}
		return new Object[0];
	}

	/**
	 * the figure has been dropped on a connection between 2 figures. We insert
	 * the new node between the 2 figures that connection wire.
	 * 
	 * @param newNode		the new (to be inserted) node
	 * @param dropTarget	the connection to insert into
	 */
	private void insertNode(AbstractCamelModelElement newNode, Connection dropTarget) {
		AbstractCamelModelElement srcNode  = NodeUtils.getNode(getFeatureProvider(), dropTarget.getStart());
		AbstractCamelModelElement destNode = newNode;
		AbstractCamelModelElement oldNode = srcNode.getOutputElement();
		
		PictogramElement destState 	= getFeatureProvider().getPictogramElementForBusinessObject(destNode);
		PictogramElement oldState = getFeatureProvider().getPictogramElementForBusinessObject(oldNode);

		Anchor oldAnchor = DiagramUtils.getAnchor(oldState);
		Anchor destAnchor 	= DiagramUtils.getAnchor(destState);
		
		if (oldNode != null) {
			// old -> new -> dest
			ReconnectNodesFeature reconnectFeature = new ReconnectNodesFeature(getFeatureProvider());
			ReconnectionContext reconContext = new ReconnectionContext(	dropTarget, 
																		oldAnchor, 
																		destAnchor, 
																		null);
			if (reconnectFeature.canExecute(reconContext)) {
				reconnectFeature.execute(reconContext);
				appendNode(newNode, oldNode);
			} 
		}
		
	}
	
	/**
	 * prepends the new node in front of the drop target
	 * 
	 * @param dropTarget
	 * @param newNode
	 */
	private void prependNode(AbstractCamelModelElement dropTarget, AbstractCamelModelElement newNode) {
		appendNode(newNode, dropTarget);
	}
	
	/**
	 * appends the new node behind the drop target
	 * 
	 * @param dropTarget
	 * @param newNode
	 */
	private void appendNode(AbstractCamelModelElement dropTarget, AbstractCamelModelElement newNode) {
		PictogramElement srcState 	= getFeatureProvider().getPictogramElementForBusinessObject(dropTarget);
		PictogramElement destState 	= getFeatureProvider().getPictogramElementForBusinessObject(newNode);
		Anchor srcAnchor 	= DiagramUtils.getAnchor(srcState);
		Anchor destAnchor 	= DiagramUtils.getAnchor(destState);
		
		CreateFlowFeature createFeature = new CreateFlowFeature(getFeatureProvider());
		CreateConnectionContext connectContext = new CreateConnectionContext();
		connectContext.setSourcePictogramElement(srcState);
		connectContext.setTargetPictogramElement(destState);
		if (destAnchor != null) {
			connectContext.setSourceAnchor(srcAnchor);
			connectContext.setTargetAnchor(destAnchor);
			if (createFeature.canCreate(connectContext)) {
				createFeature.execute(connectContext);
			}
		}	
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
				// if we have a route eip we need to use the specific route model element
				// because otherwise this causes issues
				if (eip.getName().equalsIgnoreCase("route")) {
					return new CamelRouteElement(parent, newNode);
				} else {
					return new CamelBasicModelElement(parent, newNode);
				}
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
    	CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
    	IFacetedProject fproj = ProjectFacetsManager.create(editor.getWorkspaceProject());
    	IProjectFacet camelFacet = ProjectFacetsManager.getProjectFacet("jst.camel");
    	if (fproj != null && fproj.hasProjectFacet(camelFacet)) {
    		String facetVersion = fproj.getInstalledVersion(camelFacet).getVersionString();
    		String m2CamelVersion = CamelModelFactory.getCamelVersionFor(facetVersion);
    		if (m2CamelVersion != null) {
    			updateDepsVersion(compDeps, m2CamelVersion);
    		}
    	}
		new MavenUtils().updateMavenDependencies(compDeps);
    }
    
    private void updateDepsVersion(List<Dependency> compDeps, String newCamelVersion) {
    	for (Dependency dep : compDeps) {
    		// we only update the versions of default camel components
    		if (dep.getGroupId().equalsIgnoreCase("org.apache.camel") && 
    			dep.getArtifactId().toLowerCase().startsWith("camel-")) {
    			dep.setVersion(newCamelVersion);	
    		}
    	}
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

	protected Eip determineEIP(AbstractCamelModelElement parent) {
		if (eip == null ||
				ENDPOINT_TYPE_TO.equals(eip.getName())
				&& parent instanceof CamelRouteElement
				&& parent.getChildElements().isEmpty()) {
			return getEipByName(ENDPOINT_TYPE_FROM);
		}		
		return getEip();
	}
}
