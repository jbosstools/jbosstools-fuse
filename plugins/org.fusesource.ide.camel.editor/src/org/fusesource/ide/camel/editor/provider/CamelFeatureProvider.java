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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.CamelModelIndependenceSolver;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.add.AddFlowFeature;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;
import org.fusesource.ide.camel.editor.features.create.CreateBeanFigureFeature;
import org.fusesource.ide.camel.editor.features.create.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.features.create.CreateFlowFeature;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteNodeFeature;
import org.fusesource.ide.camel.editor.features.delete.RemoveNodeFeature;
import org.fusesource.ide.camel.editor.features.other.CopyNodeFeature;
import org.fusesource.ide.camel.editor.features.other.LayoutNodeFeature;
import org.fusesource.ide.camel.editor.features.other.MoveNodeFeature;
import org.fusesource.ide.camel.editor.features.other.PasteNodeFeature;
import org.fusesource.ide.camel.editor.features.other.ResizeNodeFeature;
import org.fusesource.ide.camel.editor.features.other.UpdateNodeFeature;
import org.fusesource.ide.camel.editor.provider.generated.AddNodeMenuFactory;
import org.fusesource.ide.camel.editor.provider.generated.ProviderHelper;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.generated.Bean;
import org.fusesource.ide.commons.util.Strings;


/**
 * @author lhein
 */
public class CamelFeatureProvider extends DefaultFeatureProvider {

	private AddNodeMenuFactory menuFactory = new AddNodeMenuFactory();
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
		if (context.getNewObject() instanceof Flow) {
			return new AddFlowFeature(this);
		} else if (context.getNewObject() instanceof AbstractNode) {
			return new AddNodeFeature(this);
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		ICreateFeature[] features = ProviderHelper.getCreateFeatures(this);
		AbstractNode selectedNode = null;
		IDiagramTypeProvider dtp = getDiagramTypeProvider();
		IDiagramEditor diagramEditor = dtp.getDiagramEditor();
		if (diagramEditor instanceof RiderDesignEditor) {
			RiderDesignEditor rider = (RiderDesignEditor) diagramEditor;
			selectedNode = rider.getSelectedNode();
			if (selectedNode == null) {
				selectedNode = rider.getSelectedRoute();
			}
		}

		if (selectedNode != null) {
			List<ICreateFeature> featureList = new ArrayList<ICreateFeature>();
			featureList.addAll(Arrays.asList(features));

			Set<Endpoint> endpoints = AbstractNodes.getAllEndpoints(selectedNode);
			Map<String, String> beans = AbstractNodes.getAllBeans(selectedNode);
			addEndpointInstances(featureList, endpoints);
			addBeanInstances(featureList, beans);

			return featureList.toArray(new ICreateFeature[featureList.size()]);
		}
		return features;
	}

	private void addBeanInstances(List<ICreateFeature> featureList, Map<String, String> beans) {
		ArrayList<String> processedBeans = new ArrayList<String>();
		Set<Entry<String, String>> entrySet = beans.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String name = entry.getKey();
			String aClass = entry.getValue();

			if (Strings.isBlank(name) && Strings.isBlank(aClass)) {
				continue;
			}

			if (processedBeans.contains(name)) continue;
			processedBeans.add(name);
			
			Bean bean = new Bean();
			bean.setName(name);
			bean.setRef(name);
			bean.setBeanType(aClass);

			String title = bean.getDisplayText();
			String description = "bean '" + name + "' of type " + aClass;

			featureList.add(new CreateBeanFigureFeature(this, title, description, bean));
		}
	}

	private void addEndpointInstances(List<ICreateFeature> featureList, Set<Endpoint> endpoints) {
		ArrayList<String> processedURIs = new ArrayList<String>();
		for (Endpoint endpoint : endpoints) {
			String id = endpoint.getId();
			String url = endpoint.getUri();
			if (Strings.isBlank(id) && Strings.isBlank(url)) {
				continue;
			}
			if (processedURIs.contains(url)) continue;
			processedURIs.add(url);
			String description = endpoint.getDescription();
			String title = endpoint.getDisplayText();
			featureList.add(new CreateEndpointFigureFeature(this, title, description, endpoint));
		}

	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof AbstractNode) {
				return new UpdateNodeFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof AbstractNode) {
			return new MoveNodeFeature(this);
		}
		return super.getMoveShapeFeature(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getDeleteFeature(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		return new DeleteNodeFeature(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultFeatureProvider#getRemoveFeature(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new RemoveNodeFeature(this);
	}
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		Shape shape = context.getShape();
		Object bo = getBusinessObjectForPictogramElement(shape);
		if (bo instanceof AbstractNode) {
			return new ResizeNodeFeature(this);
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractNode) {
			return new LayoutNodeFeature(this);
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		return new ICustomFeature[]
				{ /**new RenameNodeFeature(this),
				new DrillDownNodeFeature(this),
				new AssociateDiagramNodeFeature(this),**/
				new LayoutDiagramFeature(this)
				//				, new CollapseDummyFeature(this)
				//				, new SetGridVisibilityFeature(this)
				/*
				, new AddRouteFeature(this)
				, new DeleteRouteFeature(this)
				 */
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

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		//		PictogramElement pe = context.getPictogramElement();
		//		Object bo = getBusinessObjectForPictogramElement(pe);
		//		if (bo instanceof AbstractNode) {
		//			return new DirectEditNodeFeature(this);
		//		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		return new CopyNodeFeature(this);
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		return new PasteNodeFeature(this);
	}
	
	public CamelModelIndependenceSolver getModelIndependenceSolver() {
		return modelIndependenceSolver;
	}

	public void setModelIndependenceSolver(CamelModelIndependenceSolver modelIndependenceSolver) {
		this.modelIndependenceSolver = modelIndependenceSolver;
		this.setIndependenceSolver(this.modelIndependenceSolver);
	}
}
