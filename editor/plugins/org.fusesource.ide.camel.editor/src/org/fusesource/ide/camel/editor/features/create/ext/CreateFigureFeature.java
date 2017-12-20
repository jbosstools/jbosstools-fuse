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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.features.misc.ReconnectNodesFeature;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ProviderHelper;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElementIDUtil;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CreateFigureFeature extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Eip eip;
	private Class<? extends AbstractCamelModelElement> clazz;
	private Node nodeToDuplicateForCreation = null;

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
	
	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Eip eip, Node nodeToDuplicateForCreation) {
		super(fp, name, description);
		this.eip = eip;
		this.nodeToDuplicateForCreation = nodeToDuplicateForCreation;
	}

	/**
	 * 
	 * @param fp
	 * @param name
	 * @param description
	 * @param clazz
	 */
	public CreateFigureFeature(IFeatureProvider fp, String name, String description,
			Class<? extends AbstractCamelModelElement> clazz) {
		super(fp, name, description);
		this.clazz = clazz;
	}

	public Eip getEip() {
		return eip;
	}

	/**
	 * @param eip
	 *            the eip to set
	 */
	public void setEip(Eip eip) {
		this.eip = eip;
	}

	public Class<? extends AbstractCamelModelElement> getClazz() {
		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider#getCategoryName()
	 */
	@Override
	public String getCategoryName() {
		if (eip != null) {
			return ProviderHelper.getCategoryFromEip(eip);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
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

		// creating a figure on the Camel Context / Diagram directly (for
		// instance Route)
		if (container instanceof Diagram) {
			CamelFile cf = ((CamelDesignEditor) getDiagramBehavior().getDiagramContainer()).getModel();
			// sanity check if the CamelFile has a Camel Context
			if (cf.isEmpty()) {
				// if not, then we need to add one right away to prevent further
				// problems
				cf.addChildElement(new CamelContextElement(cf, null));
			}

			// if we got an EIP reference then we can check if thats allowed
			// to be added to the camel context
			if (eip != null) {
				return eip.canBeAddedToCamelContextDirectly();
				// if we only have a class defined
			} else if (clazz != null) {
				// then we need to instantiate it
				Object obj = newInstance(clazz);
				if (obj instanceof AbstractCamelModelElement) {
					// and check if the element can be added to a context
					return ((AbstractCamelModelElement) obj).canBeAddedToCamelContextDirectly();
				}
				return false;
			}
		}

		AbstractCamelModelElement containerNode = containerBO instanceof AbstractCamelModelElement
				? (AbstractCamelModelElement) containerBO : null;

		if (CollapseFeature.isCollapsed(getFeatureProvider(), containerNode)) {
			// we don't allow drop on a collapsed figure
			return false;
		}

		// special handling for creating on choices (only one otherwise allowed)
		if (containerNode != null
				&& AbstractCamelModelElement.CHOICE_NODE_NAME.equalsIgnoreCase(containerNode.getNodeTypeId())) {
			boolean validChoiceDrop = isValidChoiceDrop(containerNode);
			if (!validChoiceDrop) {
				return false;
			}
		}

		// check if we try to drop a second Otherwise onto a Choice
		if (isInvalidAction(containerNode)) {
			return false;
		}

		if (containerBO instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement sourceNode = (AbstractCamelModelElement) containerBO;

			// checking if the new node is a valid child of the container its
			// dropped on
			if (NodeUtils.isValidChild(sourceNode, eip)) {
				return true;
			} else {
				// this case is when user drops a figure onto a NON-container
				// which then
				// causes the new figure created with a connection to the other
				// one
				// only allow drop on node if the node has no outgoing or no
				// incoming connection
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
	 * checks if the drop on the choice is valid
	 * 
	 * @param choice
	 * @return
	 */
	private boolean isValidChoiceDrop(AbstractCamelModelElement choice) {
		if (this.eip != null) {
			if (AbstractCamelModelElement.OTHERWISE_NODE_NAME.equalsIgnoreCase(this.eip.getName())
					&& choice.getParameter(AbstractCamelModelElement.OTHERWISE_NODE_NAME) != null) {
				// this choice already has an otherwise case - adding more isn't
				// allowed
				return false;
			}
			// allow when and otherwise children on choice
			return AbstractCamelModelElement.WHEN_NODE_NAME.equalsIgnoreCase(this.eip.getName())
					|| AbstractCamelModelElement.OTHERWISE_NODE_NAME.equalsIgnoreCase(this.eip.getName());

		} else if (clazz != null) {
			Object obj = newInstance(clazz);
			if (obj instanceof AbstractCamelModelElement
					&& AbstractCamelModelElement.OTHERWISE_NODE_NAME
							.equalsIgnoreCase(((AbstractCamelModelElement) obj).getNodeTypeId())
					&& choice.getParameter(AbstractCamelModelElement.OTHERWISE_NODE_NAME) != null) {
				// this choice already has an otherwise case - adding more isn't
				// allowed
				return false;
			}
		}
		return true;
	}

	/**
	 * checks for edge cases where the catalog doesn't provide correct
	 * information
	 * 
	 * @param containerBO
	 *            the node to check
	 * @return true if its an invalid action
	 */
	private boolean isInvalidAction(AbstractCamelModelElement containerBO) {
		return isOtherwiseDropOnNonChoice(containerBO) || isInvalidRouteDrop(containerBO);
	}

	/**
	 * checks if we try to add a WHEN or OTHERWISE to a non-CHOICE element
	 * 
	 * @param containerBO
	 * @return
	 */
	private boolean isInvalidRouteDrop(AbstractCamelModelElement containerBO) {
		return AbstractCamelModelElement.ROUTE_NODE_NAME.equalsIgnoreCase(eip.getName()) && containerBO != null
				&& !AbstractCamelModelElement.CAMEL_CONTEXT_NODE_NAME.equalsIgnoreCase(containerBO.getNodeTypeId());
	}

	/**
	 * checks if we try to add a WHEN or OTHERWISE to a non-CHOICE element
	 * 
	 * @param containerBO
	 * @return
	 */
	private boolean isOtherwiseDropOnNonChoice(AbstractCamelModelElement containerBO) {
		return AbstractCamelModelElement.OTHERWISE_NODE_NAME.equalsIgnoreCase(eip.getName()) && containerBO != null
				&& !AbstractCamelModelElement.CHOICE_NODE_NAME.equalsIgnoreCase(containerBO.getNodeTypeId());
	}

	/**
	 * retrieves the icon name for the given class via reflection
	 * 
	 * @return the icon name or null
	 */
	protected String getIconName() {
		String ret = null;
		if (eip != null) {
			ret = eip.getName();
		}
		if (ret == null) {
			AbstractCamelModelElement node = createNode(null, false);
			if (node != null) {
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
		ContainerShape container = context.getTargetContainer();

		CreateContext ctxNew = null;
		AbstractCamelModelElement node;

		// determine the parent figure of our new node
		AbstractCamelModelElement selectedContainerElement = determineContainerElement(container);

		// now create the new node
		if (isAttemptToCreateWiredFigure(selectedContainerElement, context)) {
			node = createNode(selectedContainerElement instanceof CamelRouteElement ? selectedContainerElement
					: selectedContainerElement.getParent(), true);

			// if we have no connection then user dropped the figure on
			// another figure and we need to get the parent of that figure
			// as the parent for the new figure
			if (context.getTargetConnection() == null) {
				selectedContainerElement = selectedContainerElement.getParent();
			}

			// we need our own create context here as the existing one lacks
			// the set methods
			ctxNew = new CreateContext();
			ctxNew.setTargetContainer((ContainerShape) getFeatureProvider()
					.getAllPictogramElementsForBusinessObject(selectedContainerElement)[0]);
			ctxNew.setTargetConnection(context.getTargetConnection());
			ctxNew.setLocation(context.getX(), context.getY());
		} else {
			// simple drop on a container
			node = createNode(selectedContainerElement, selectedContainerElement != null);
		}

		if (selectedContainerElement != null && node != null) {
			// initialize the node
			node.initialize();
			
			// add the new node to the parent container
			selectedContainerElement.addChildElement(node);
			// and update the parent link
			node.setParent(selectedContainerElement);

			// make sure we have a unique id for the new figure
			if (Strings.isBlank(node.getId())) {
				new CamelModelElementIDUtil().ensureUniqueID(node);
			}

			// add the node to the diagram
			addNodeToDiagram(ctxNew != null ? ctxNew : context, ctxNew != null, node, container);

			// select the new node
			NodeUtils.setSelectedNode(node, getFeatureProvider());

			// return newly created business object(s)
			return new Object[] { node };
		}

		return new Object[0];
	}

	private void addNodeToDiagram(ICreateContext context, boolean isCreationWithConnections, AbstractCamelModelElement node, ContainerShape container) {
		// add node to diagram
		PictogramElement newGraphicalRepresentation = addGraphicalRepresentation(context, node);
		addChildrenGraphicalRepresentation(node, newGraphicalRepresentation);

		// if we create with connections...
		if (isCreationWithConnections) {
			AbstractCamelModelElement srcNode = (AbstractCamelModelElement) getBusinessObjectForPictogramElement(
					container);
			if (context.getTargetConnection() != null) {
				// drop on a connection -> insert node into the flow
				insertNode(node, context.getTargetConnection());
			} else {
				if (srcNode.getInputElement() == null && srcNode.getOutputElement() != null) {
					// drop on a figure with no input element -> prepend
					prependNode(srcNode, node);
				} else {
					// drop on a figure with no output (and maybe also no input)
					// element -> append
					appendNode(srcNode, node);
				}
			}
		}
		
		layoutPictogramElement(container);

		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);
	}

	private void addChildrenGraphicalRepresentation(AbstractCamelModelElement node, PictogramElement newGraphicalRepresentation) {
		PictogramElement previousChild = null;
		AbstractCamelModelElement previousModelChild = null;
		for(AbstractCamelModelElement child: node.getChildElements()){
			CreateContext childCreateContext = new CreateContext();
			childCreateContext.setTargetContainer((ContainerShape) newGraphicalRepresentation);
			PictogramElement currentChild = addGraphicalRepresentation(childCreateContext, child);
			AbstractCamelModelElement currentModelChild = child;
			if(previousChild != null){
				AddFlowFeature addFlowFeature = new AddFlowFeature(getFeatureProvider());
				AddConnectionContext connectContext = new AddConnectionContext(getAnchor(previousChild), getAnchor(currentChild));
				connectContext.setNewObject(new CamelElementConnection(previousModelChild, currentModelChild));
				if(addFlowFeature.canAdd(connectContext)){
					addFlowFeature.add(connectContext);
				}
			}
			addChildrenGraphicalRepresentation(child, currentChild);
			previousChild = currentChild;
			previousModelChild = currentModelChild;
		}
	}

	private AbstractCamelModelElement determineContainerElement(ContainerShape container) {
		CamelDesignEditor editor = (CamelDesignEditor) getDiagramBehavior().getDiagramContainer();
		if (container instanceof Diagram) {
			return editor.getModel().getRouteContainer();
		} else {
			return (AbstractCamelModelElement) getBusinessObjectForPictogramElement(container);
		}
	}

	private boolean isAttemptToCreateWiredFigure(AbstractCamelModelElement selectedContainer, ICreateContext context) {
		if (selectedContainer != null) {
			Eip underlyingMetaModelObject = selectedContainer.getUnderlyingMetaModelObject();
			return (underlyingMetaModelObject != null && !underlyingMetaModelObject.canHaveChildren() && !(selectedContainer instanceof CamelRouteContainerElement))
					|| context.getTargetConnection() != null;
		}
		return false;
	}

	protected boolean isRouteContainer(Eip underlyingMetaModelObject) {
		String tagName = underlyingMetaModelObject.getName();
		return AbstractCamelModelElement.CAMEL_CONTEXT_NODE_NAME.equalsIgnoreCase(tagName) || CamelFile.CAMEL_ROUTES.equalsIgnoreCase(tagName);
	}

	/**
	 * the figure has been dropped on a connection between 2 figures. We insert
	 * the new node between the 2 figures that connection wire.
	 * 
	 * @param newNode
	 *            the new (to be inserted) node
	 * @param dropTarget
	 *            the connection to insert into
	 */
	private void insertNode(AbstractCamelModelElement newNode, Connection dropTarget) {
		AbstractCamelModelElement srcNode = NodeUtils.getNode(getFeatureProvider(), dropTarget.getStart());
		AbstractCamelModelElement destNode = newNode;
		AbstractCamelModelElement oldNode = srcNode.getOutputElement();

		PictogramElement destState = getFeatureProvider().getPictogramElementForBusinessObject(destNode);
		PictogramElement oldState = getFeatureProvider().getPictogramElementForBusinessObject(oldNode);

		Anchor oldAnchor = DiagramUtils.getAnchor(oldState);
		Anchor destAnchor = DiagramUtils.getAnchor(destState);

		if (oldNode != null) {
			// old -> new -> dest
			ReconnectNodesFeature reconnectFeature = new ReconnectNodesFeature(getFeatureProvider());
			ReconnectionContext reconContext = new ReconnectionContext(dropTarget, oldAnchor, destAnchor, null);
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
		PictogramElement srcState = getFeatureProvider().getPictogramElementForBusinessObject(dropTarget);
		PictogramElement destState = getFeatureProvider().getPictogramElementForBusinessObject(newNode);
		Anchor srcAnchor = DiagramUtils.getAnchor(srcState);
		Anchor destAnchor = DiagramUtils.getAnchor(destState);

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
	 * Create a new node of this figure feature's underlying node. Default
	 * implementation will use either an eip or an Abstract Node class.
	 * Subclasses with neither should override this method.
	 * 
	 * @return
	 */
	protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
		if (eip != null) {
			CamelDesignEditor editor = (CamelDesignEditor) getDiagramBehavior().getDiagramContainer();
			if (editor.getModel() != null) {
				Node newNode = null;
				if (createDOMNode && nodeToDuplicateForCreation == null) {
					final String nodeTypeId = getEip().getName();
					final String namespace = parent != null && parent.getXmlNode() != null
							? parent.getXmlNode().getPrefix() : null;
					newNode = editor.getModel().createElement(nodeTypeId, namespace);
				} else if(nodeToDuplicateForCreation != null){
					newNode = nodeToDuplicateForCreation;
				}
				// if we have a route eip we need to use the specific route
				// model element
				// because otherwise this causes issues
				if (eip.getName().equalsIgnoreCase(AbstractCamelModelElement.ROUTE_NODE_NAME)) {
					return new CamelRouteElement(parent, newNode);
				} else {
					return new CamelBasicModelElement(parent, newNode);
				}
			}
		}
		if (clazz != null) {
			Object o = newInstance(clazz);
			if (o instanceof AbstractCamelModelElement) {
				AbstractCamelModelElement e = (AbstractCamelModelElement) o;
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
			CamelEditorUIActivator.pluginLog().logWarning("Failed to create instance of " + aClass.getName() + ". " + e,
					e);
			return null;
		}
	}

	/**
	 * checks if we need to add a maven dependency for the chosen component and
	 * inserts it into the pom.xml if needed
	 */
	public void updateMavenDependencies(final List<Dependency> compDeps) throws CoreException {
		CamelDesignEditor editor = (CamelDesignEditor) getDiagramBehavior().getDiagramContainer();
		IFacetedProject fproj = ProjectFacetsManager.create(editor.getWorkspaceProject());
		if (fproj != null) {
			final String m2CamelVersion = new CamelMavenUtils().getCamelVersionFromMaven(fproj.getProject());
			if (m2CamelVersion != null) {
				updateDepsVersion(compDeps, m2CamelVersion);
				new MavenUtils().updateMavenDependencies(compDeps, fproj.getProject());
			}
		}
	}

	private void updateDepsVersion(List<Dependency> compDeps, String newCamelVersion) {
		for (Dependency dep : compDeps) {
			// we only update the versions of default camel components
			if (dep.getGroupId().equalsIgnoreCase("org.apache.camel")
					&& dep.getArtifactId().toLowerCase().startsWith("camel-")) {
				dep.setVersion(newCamelVersion);
			}
		}
	}

	/**
	 * retrieves the eip meta model for a given eip name
	 * 
	 * @param name
	 * @return the eip or null if not found
	 */
	public Eip getEipByName(String name) {
		// then get the meta model for the given camel version
		CamelModel model = CamelCatalogCacheManager.getInstance().getCamelModelForProject(CamelUtils.project(), new NullProgressMonitor());
		if (model == null) {
			return null;
		}
		// then we get the eip meta model
		return model.getEip(name);
	}

	protected Eip determineEIP(AbstractCamelModelElement parent) {
		if (eip == null || ENDPOINT_TYPE_TO.equals(eip.getName()) && parent instanceof CamelRouteElement
				&& parent.getChildElements().isEmpty()) {
			return getEipByName(ENDPOINT_TYPE_FROM);
		}
		return getEip();
	}
	
	private Anchor getAnchor(PictogramElement element) {
		if (element instanceof AnchorContainer) {
			AnchorContainer container = (AnchorContainer) element;
			EList<Anchor> anchors = container.getAnchors();
			if (anchors != null && !anchors.isEmpty()) {
				return anchors.get(0);
			}
		}
		return null;
	}
}
