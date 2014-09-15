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

package org.fusesource.ide.fabric8.ui.navigator;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.fabric8.ui.navigator.cloud.CloudsNode;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;

public class FabricsContentProvider implements ITreeContentProvider,
	ILabelProvider, IStructuredContentProvider, FabricListener, IChangeListener {

	public static class DelayProxy {
		
		public RefreshableNode wrapper;
		
		public DelayProxy(RefreshableNode node) {
			this.wrapper = node;
		}
	}
	
	private HashMap<RefreshableNode, DelayProxy> loading;
	private Viewer viewer;
	private FabricNavigator navigator;

	public FabricsContentProvider() {
		loading = new HashMap<RefreshableNode, DelayProxy>();
	}

	@Override
	public void dispose() {
		removeFabricListener();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (newInput instanceof FabricNavigator) {
			removeFabricListener();
			navigator = (FabricNavigator) newInput;
			navigator.getFabrics().addFabricListener(this);
			navigator.getCloudsNode().addChangeListener(this);
		}
	}

	protected void removeFabricListener() {
		if (navigator != null) {
			navigator.getFabrics().removeFabricListener(this);
			navigator.getCloudsNode().removeChangeListener(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof FabricNavigator) {
			FabricNavigator navigator = (FabricNavigator) parentElement;
			return new Object[] { navigator.getFabrics(), navigator.getCloudsNode() };
		} else if (parentElement instanceof CloudsNode) {
			return ((CloudsNode)parentElement).getChildren();
		} else if (parentElement instanceof Fabrics) {
			return ((Fabrics)parentElement).getChildren();
		} else if (parentElement instanceof RefreshableNode) {
			return loadAndGetChildren(parentElement);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Node) {
			return ((Node)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof DelayProxy) return false;
		if (element instanceof Fabric) {
			return ((Fabric)element).isConnected();
		}
		if (element instanceof RefreshableNode) {
			RefreshableNode node = (RefreshableNode)element;
			if (node.isLoaded()) return node.getChildren().length>0;
		}
		return true;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	// ILabelProvider interface

	@Override
	public Image getImage(Object element) {
		if (element instanceof ImageProvider) {
			ImageProvider provider = (ImageProvider) element;
			Image image = provider.getImage();
			if (image != null) {
				return image;
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof FabricNavigator) {
			return "Fabrics";
		} else if (element instanceof DelayProxy) {
			return "...loading...";
		}
		return element.toString();
	}

	
    protected synchronized Object[] loadAndGetChildren(final Object parent) {
		final RefreshableNode w = (RefreshableNode)parent;
		
		if(w.isLoaded()) return w.getChildren();
				
		// Must load the model
		Job job = new Job("Loading " + parent + "...") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Loading children of " + w + "...", 1);
					w.getChildren();
					monitor.done();					
				} finally {
					loading.remove(w);
				}

				Display.getDefault().asyncExec(new Runnable() { 
					public void run() {
						if( viewer instanceof StructuredViewer) 
							((StructuredViewer)viewer).refresh(parent);
						else
							viewer.refresh();
					}
				});
				return Status.OK_STATUS;
			}
		};
		
		if( loading.containsKey(((RefreshableNode)parent))) {
			return new Object[] { loading.get((RefreshableNode)parent)};
		}
		
		DelayProxy p = new DelayProxy(w);
		loading.put(w, p);
		job.schedule();
		
		return new Object[] { p };
    }

	
	
	@Override
	public void onFabricEvent(FabricEvent fabricEvent) {
		fireRefresh(fabricEvent.getFabric(), true);
	}

	@Override
	public void handleChange(ChangeEvent event) {
		fireRefresh(navigator.getCloudsNode(), true);
	}

	private void fireRefresh(final Object node, final boolean full) {
		refreshTreeViewer(viewer, node, full);
	}

	public void refreshTreeViewer(final Viewer viewer,
			final Object node, final boolean full) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (Viewers.isValid(viewer)) {
					TreeViewer tv = (TreeViewer)viewer;
					if (full || 
						node == null || 
						!(viewer instanceof StructuredViewer)) {
						viewer.refresh();
						if (node != null) {
							tv.expandToLevel(node, 1);
						} else {
							tv.expandToLevel(navigator.getFabrics(), 1);
						}
					} else {
						((StructuredViewer) viewer).refresh(node);
						tv.expandToLevel(node, 1);
					}
				}
			}
		});
	}
}
