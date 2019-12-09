/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.provider;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.features.custom.DeleteAllEndpointBreakpointsFeature;
import org.fusesource.ide.camel.editor.features.custom.DeleteEndpointBreakpointFeature;
import org.fusesource.ide.camel.editor.features.custom.DisableCamelBreakpointFeature;
import org.fusesource.ide.camel.editor.features.custom.EditConditionalBreakpoint;
import org.fusesource.ide.camel.editor.features.custom.EnableCamelBreakpointFeature;
import org.fusesource.ide.camel.editor.features.custom.GoIntoContainerFeature;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.features.custom.SetConditionalBreakpointFeature;
import org.fusesource.ide.camel.editor.features.custom.SetEndpointBreakpointFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteFigureFeature;
import org.fusesource.ide.camel.editor.features.delete.RemoveFigureFeature;
import org.fusesource.ide.camel.editor.features.misc.MoveNodeFeature;
import org.fusesource.ide.camel.editor.features.misc.ReconnectNodesFeature;
import org.fusesource.ide.camel.editor.features.misc.ResizeNodeFeature;
import org.fusesource.ide.camel.editor.features.misc.UpdateNodeFeature;
import org.fusesource.ide.camel.editor.internal.CamelModelIndependenceSolver;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getAddFeature(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// is object for add request a EClass or EReference?
		if (context.getNewObject() instanceof CamelElementConnection) {
			return new AddFlowFeature(this);
		} else if (context.getNewObject() instanceof AbstractCamelModelElement) {
			return new AddNodeFeature(this);
		}
		return super.getAddFeature(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getCreateFeatures()
	 */
	@Override
	public ICreateFeature[] getCreateFeatures() {
		ICreateFeature[] features = ProviderHelper.getCreateFeatures(this);
//		AbstractNode selectedNode = null;
//		CamelDesignEditor editor = (CamelDesignEditor)getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer();
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getUpdateFeature(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof AbstractCamelModelElement) {
				return new UpdateNodeFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getDeleteFeature(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		return new DeleteFigureFeature(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getRemoveFeature(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new RemoveFigureFeature(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getResizeShapeFeature(org.eclipse.graphiti.features.context.IResizeShapeContext)
	 */
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof AbstractCamelModelElement) {
			return new ResizeNodeFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getCustomFeatures(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[]
				{
				new LayoutDiagramFeature(this),
				new SetEndpointBreakpointFeature(this),
				new SetConditionalBreakpointFeature(this),
				new EditConditionalBreakpoint(this),
				new EnableCamelBreakpointFeature(this),
				new DisableCamelBreakpointFeature(this),
				new DeleteEndpointBreakpointFeature(this),
				new DeleteAllEndpointBreakpointsFeature(this),
//				new GEFLayoutDiagramFeature(this),
//				new ZestLayoutDiagramFeature(this),
				new CollapseFeature(this),
				new GoIntoContainerFeature(this)
				};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getMoveShapeFeature(org.eclipse.graphiti.features.context.IMoveShapeContext)
	 */
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		return new MoveNodeFeature(this);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getReconnectionFeature(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		return new ReconnectNodesFeature(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getCreateConnectionFeatures()
	 */
	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] { new CreateFlowFeature(this) };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeatureProvider#getDragAndDropFeatures(org.eclipse.graphiti.features.context.IPictogramElementContext)
	 */
	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
		// simply return all create connection features
		return getCreateConnectionFeatures();
	}
	
	public CamelModelIndependenceSolver getModelIndependenceSolver() {
		return modelIndependenceSolver;
	}

	public void setModelIndependenceSolver(CamelModelIndependenceSolver modelIndependenceSolver) {
		this.modelIndependenceSolver = modelIndependenceSolver;
		this.setIndependenceSolver(this.modelIndependenceSolver);
	}
}
