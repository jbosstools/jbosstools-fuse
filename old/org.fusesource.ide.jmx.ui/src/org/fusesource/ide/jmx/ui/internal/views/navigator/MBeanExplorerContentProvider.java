/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
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

package org.fusesource.ide.jmx.ui.internal.views.navigator;

import java.util.HashMap;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.tree.HasChildrenArray;
import org.fusesource.ide.commons.tree.HasViewer;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionProviderListener;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.MBeanFeatureInfoWrapper;
import org.fusesource.ide.jmx.core.tree.DomainNode;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;
import org.fusesource.ide.jmx.core.tree.Root;
import org.fusesource.ide.jmx.ui.JMXUIActivator;


/**
 * Content provider for the view
 */
public class MBeanExplorerContentProvider implements IConnectionProviderListener,
IStructuredContentProvider, ITreeContentProvider, RefreshableUI, HasViewer {

	public static class DelayProxy {
		public IConnectionWrapper wrapper;
		public DelayProxy(IConnectionWrapper wrapper) {
			this.wrapper = wrapper;
		}
	}

	private Viewer viewer;
	private HashMap<IConnectionWrapper, DelayProxy> loading;

	public MBeanExplorerContentProvider() {
		ExtensionManager.addConnectionProviderListener(this);
		loading = new HashMap<IConnectionWrapper, DelayProxy>();
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		this.viewer = v;
		JMXUIActivator.addExplorer(this);
	}

	public void dispose() {
		JMXUIActivator.removeExplorer(this);
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof Node) {
			Node node = (Node) child;
			return node.getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if( parent == null ) return new Object[] {};
		if (parent instanceof HasChildrenArray) {
			HasChildrenArray hasChildren = (HasChildrenArray) parent;
			return hasChildren.getChildObjectArray();
		}
		if (parent instanceof Navigator) {
			Navigator navigator = (Navigator) parent;
			return navigator.getRootNodes();
		}
		/*
		 * now uses hasChildren.getChildObjectArray();
		if( parent instanceof LocalConnectionsNode) {
			LocalConnectionsNode node = (LocalConnectionsNode) parent;
			return node.getConnectionWrappers();
		}
		 */

		if( parent instanceof IConnectionWrapper) {
			IConnectionWrapper wrapper = (IConnectionWrapper)parent;
			if (wrapper.isConnected()) {
				return loadAndGetRootChildren(parent);
			}
		}
		if (parent instanceof Root) {
			Root root = (Root) parent;
			return root.getChildren();
		}
		if (parent instanceof DomainNode) {
			DomainNode node = (DomainNode) parent;
			return node.getChildren();
		}
		if (parent instanceof ObjectNameNode) {
			ObjectNameNode node = (ObjectNameNode) parent;
			return node.getMbeanInfoWrapper().getMBeanFeatureInfos();
		}
		if (parent instanceof Node) {
			Node node = (Node) parent;
			return node.getChildren();
		}
		return new Object[0];
	}

	protected synchronized Object[] loadAndGetRootChildren(final Object parent) {
		final IConnectionWrapper w = (IConnectionWrapper)parent;

		Root root = w.getRoot();
		if( root != null ) {
			return getChildren(root);
		}

		// Must load the model
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					w.loadRoot();
				} catch (Throwable re) {
					JMXUIActivator.getLogger().error("Error while loading from connection...", re);
				}
				loading.remove(w);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if( viewer instanceof StructuredViewer)
							((StructuredViewer)viewer).refresh(parent);
						else
							viewer.refresh();
					}
				});
			}
		};

		if( loading.containsKey(parent)) {
			DelayProxy delayProxy = loading.get(parent);
			return new Object[] { delayProxy};
		}
		DelayProxy p = new DelayProxy(w);
		loading.put(w, p);
		t.start();
		return new Object[] { p };
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof HasChildrenArray) {
			HasChildrenArray hasChildren = (HasChildrenArray) parent;
			return true;
			/*
        	Object[] array = hasChildren.getChildObjectArray();
        	return array != null && array.length > 0;
			 */
		}
		if (parent instanceof ObjectNameNode) {
			ObjectNameNode node = (ObjectNameNode) parent;
			return (node.getMbeanInfoWrapper().getMBeanFeatureInfos().length > 0);
		}
		if (parent instanceof Node) {
			Node node = (Node) parent;
			return (node.getChildren().length > 0);
		}
		if (parent instanceof MBeanFeatureInfoWrapper) {
			return false;
		}
		if( parent instanceof IConnectionWrapper ) {
			return ((IConnectionWrapper)parent).isConnected();
		}
		return true;
	}

	public void connectionAdded(IConnectionWrapper connection) {
		fireRefresh(connection, true);
	}

	public void connectionChanged(IConnectionWrapper connection) {
		fireRefresh(connection, false);
	}

	public void connectionRemoved(IConnectionWrapper connection) {
		fireRefresh(connection, true);
	}

	/**
	 * Refreshes a given node in the viewer
	 */
	public void fireRefresh(final Object node, final boolean full) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if( Viewers.isVisible(viewer)) {
					ISelection sel = viewer.getSelection();
					IStructuredSelection isel = (IStructuredSelection)sel;
					Object o = isel.getFirstElement();

					if (o instanceof Node) {
						Node n = (Node)o;
						while (n.getParent() != null) {
							n = n.getParent();
						}

						if (n instanceof Root) {
							Root r = (Root)n;

							IConnectionWrapper iconwrap = r.getConnection();
							viewer.setSelection(null);
							if (iconwrap != null && iconwrap.isConnected()) {
								viewer.setSelection(sel);
							} else {
								Tree tree = (Tree)viewer.getControl();
								TreeItem[] items = tree.getItems();
								if (items.length>0) {
									tree.setSelection(items[0]);
									tree.showSelection();
								}
							}
						}
					}

					if(full || !(viewer instanceof StructuredViewer)) {
						viewer.refresh();
					} else {
						StructuredViewer structuredViewer = (StructuredViewer)viewer;
						/*
						// if we have refreshed a node we need to make sure we expand it first...
						if (structuredViewer instanceof TreeViewer) {
							TreeViewer treeViewer = (TreeViewer) structuredViewer;
							treeViewer.expandToLevel(node,  1);
						}
						 */
						structuredViewer.refresh(node);
					}
				}
			}
		});
	}

	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * Performs a full refresh
	 */
	public void fireRefresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if( viewer != null ) {
					viewer.refresh();
				}
			}
		});
	}

}