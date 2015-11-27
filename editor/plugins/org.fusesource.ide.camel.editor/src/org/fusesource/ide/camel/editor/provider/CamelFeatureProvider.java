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

package org.fusesource.ide.camel.editor.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.features.custom.GEFLayoutDiagramFeature;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.features.custom.ZestLayoutDiagramFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteFigureFeature;
import org.fusesource.ide.camel.editor.features.delete.RemoveFigureFeature;
import org.fusesource.ide.camel.editor.features.misc.ResizeNodeFeature;
import org.fusesource.ide.camel.editor.internal.CamelModelIndependenceSolver;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.commons.camel.tools.BeanDef;

/**
 * @author lhein
 */
public class CamelFeatureProvider extends DefaultFeatureProvider {

//	private AddNodeMenuFactory menuFactory = new AddNodeMenuFactory();
	private CamelModelIndependenceSolver modelIndependenceSolver;
	
	public CamelFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
		if(modelIndependenceSolver == null)
			modelIndependenceSolver = new CamelModelIndependenceSolver();
		
		setIndependenceSolver(modelIndependenceSolver);
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass or EReference?
		if (context.getNewObject() instanceof CamelElementConnection) {
			return new AddFlowFeature(this);
		} else if (context.getNewObject() instanceof CamelModelElement) {
			return new AddNodeFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		ICreateFeature[] features = ProviderHelper.getCreateFeatures(this);
		AbstractNode selectedNode = null;
		CamelDesignEditor editor = (CamelDesignEditor)getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer();
//		selectedNode = editor.getSelectedNode();
//		if (selectedNode == null) {
//			selectedNode = rider.getSelectedRoute();
//		}
//
//		if (selectedNode != null) {
//			List<ICreateFeature> featureList = new ArrayList<ICreateFeature>();
//			featureList.addAll(Arrays.asList(features));
//
//			Set<Endpoint> endpoints = AbstractNodes.getAllEndpoints(selectedNode);
//			Map<String, BeanDef> beans = AbstractNodes.getAllBeans(selectedNode);
//			addEndpointInstances(featureList, endpoints);
//			addBeanInstances(featureList, beans);
//
//			return featureList.toArray(new ICreateFeature[featureList.size()]);
//		}
		return features;
	}

	private void addBeanInstances(List<ICreateFeature> featureList, Map<String, BeanDef> beans) {
//		ArrayList<String> processedBeans = new ArrayList<String>();
//		Set<Entry<String, BeanDef>> entrySet = beans.entrySet();
//		for (Entry<String, BeanDef> entry : entrySet) {
//			String name = entry.getKey();
//			String aClass = entry.getValue().getClassName();
//
//			if ((Strings.isBlank(name) && Strings.isBlank(aClass)) || !entry.getValue().getBeanType().equalsIgnoreCase("bean")) {
//				continue;
//			}
//
//			if (processedBeans.contains(name)) continue;
//			processedBeans.add(name);
//			
//			CamelModel model = CamelModelFactory.getModelForVersion(CamelModelFactory.getCamelVersion(null));
//			Eip eip = model.getEipModel().getEIPByName("bean");
//			UniversalEIPNode bean = new UniversalEIPNode(eip);
//			bean.setName(name);
//			bean.setShortPropertyValue("ref", name); 
//			bean.setShortPropertyValue("beanType", aClass); 
//
//			String title = bean.getDisplayText();
//			String description = "bean '" + name + "' of type " + aClass;
//
//			featureList.add(new CreateDefinedBeanFigureFeature(this, title, description, bean));
//		}
	}

	private void addEndpointInstances(List<ICreateFeature> featureList, Set<Endpoint> endpoints) {
//		ArrayList<String> processedURIs = new ArrayList<String>();
//		for (Endpoint endpoint : endpoints) {
//			String id = endpoint.getId();
//			String url = endpoint.getUri();
//			if (Strings.isBlank(id) && Strings.isBlank(url)) {
//				continue;
//			}
//			if (processedURIs.contains(url)) continue;
//			processedURIs.add(url);
//			String description = endpoint.getDescription();
//			String title = endpoint.getDisplayText();
//			featureList.add(new CreateDefinedEndpointFigureFeature(this, title, description, endpoint));
//		}

	}

//	@Override
//	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		if (pictogramElement instanceof ContainerShape) {
//			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
//			if (bo instanceof CamelModelElement) {
//				return new UpdateNodeFeature(this);
//			}
//		}
//		return super.getUpdateFeature(context);
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getDeleteFeature(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		return new DeleteFigureFeature(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getRemoveFeature(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new RemoveFigureFeature(this);
	}
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof CamelModelElement) {
			return new ResizeNodeFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}
	
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getReconnectionFeature(org.eclipse.graphiti.features.context.IReconnectionContext)
//	 */
//	@Override
//	public IReconnectionFeature getReconnectionFeature(
//			IReconnectionContext context) {
//		return new ReconnectNodesFeature(this);
//	}

//	@Override
//	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
//		if (bo instanceof CamelModelElement) {
//			return new LayoutNodeFeature(this);
//		}
//		return super.getLayoutFeature(context);
//	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getCustomFeatures(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[]
				{
				new LayoutDiagramFeature(this),
//				new SetEndpointBreakpointFeature(this),
//				new SetConditionalBreakpointFeature(this),
//				new EditConditionalBreakpoint(this),
//				new EnableCamelBreakpointFeature(this),
//				new DisableCamelBreakpointFeature(this),
//				new DeleteEndpointBreakpointFeature(this),
//				new DeleteAllEndpointBreakpointsFeature(this),
				new GEFLayoutDiagramFeature(this),
				new ZestLayoutDiagramFeature(this),
				new CollapseFeature(this)
				};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getCreateConnectionFeatures()
	 */
	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new CreateFlowFeature(this) };
	}

	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}

//	@Override
//	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
//		//		PictogramElement pe = context.getPictogramElement();
//		//		Object bo = getBusinessObjectForPictogramElement(pe);
//		//		if (bo instanceof AbstractNode) {
//		//			return new DirectEditNodeFeature(this);
//		//		}
//		return super.getDirectEditingFeature(context);
//	}
//
//	@Override
//	public ICopyFeature getCopyFeature(ICopyContext context) {
//		return new CopyNodeFeature(this);
//	}
//
//	@Override
//	public IPasteFeature getPasteFeature(IPasteContext context) {
//		return new PasteNodeFeature(this);
//	}
	
	public CamelModelIndependenceSolver getModelIndependenceSolver() {
		return modelIndependenceSolver;
	}

	public void setModelIndependenceSolver(CamelModelIndependenceSolver modelIndependenceSolver) {
		this.modelIndependenceSolver = modelIndependenceSolver;
		this.setIndependenceSolver(this.modelIndependenceSolver);
	}
}
