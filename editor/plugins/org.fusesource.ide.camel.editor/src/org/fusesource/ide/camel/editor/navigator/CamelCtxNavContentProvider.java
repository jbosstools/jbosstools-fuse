/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;

public class CamelCtxNavContentProvider implements ICommonContentProvider {
	
	private AbstractTreeViewer mViewer;

	@Override
	public Object[] getChildren(Object parent) { 
		if(parent instanceof IFile){	
			return getRoutes((IFile)parent,true);
		} else if(parent instanceof CamelCtxNavRouteNode){
			List<AbstractNode> rootNodes = ((CamelCtxNavRouteNode)parent).getCamelRoute().getRootNodes();
			//show root nodes to differentiate routes with no id
			return rootNodes.toArray(new AbstractNode[rootNodes.size()]); 
		} 
		return new Object[0];
	}
	
	private Object[] getRoutes(IFile camelFile,boolean deferred){
		if(deferred){
			//deferred since load time for context files is unknown
			LoadingPlaceHolder placeHolder = new LoadingPlaceHolder();
			doDeferredLoad(camelFile, placeHolder);
			return new Object[] { placeHolder};
		} else {
			return getRoutes(camelFile);
		}		
	}
	
	private static class LoadingPlaceHolder {
		private final String loadingMsg = "Pending ...";
		@Override
		public String toString() {
			return loadingMsg;
		}		
	}
	
	
	private CamelCtxNavRouteNode[] getRoutes(IFile camelFile){				
		final RouteContainer rc = new XmlContainerMarshaller().loadRoutes(camelFile);
		if (rc != null && rc.getChildren() != null) {
			List<CamelCtxNavRouteNode> routes = new ArrayList<CamelCtxNavRouteNode>();
			for(AbstractNode node:rc.getChildren()){
				if(node instanceof RouteSupport){
					routes.add(new CamelCtxNavRouteNode((RouteSupport)node,camelFile));
				}
			}
			return routes.toArray(new CamelCtxNavRouteNode[routes.size()]);
		}
		return new CamelCtxNavRouteNode[0];
	}
	
	private void doDeferredLoad(final IFile camelFile,final LoadingPlaceHolder placeHolder){
		//instead of DeferredTreeContentManager
		Job job = new Job("Loading " + camelFile.getName()) {
			protected IStatus run(IProgressMonitor monitor) {
				final CamelCtxNavRouteNode[] routes = getRoutes(camelFile);
				if (routes!=null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (mViewer!=null && !mViewer.getControl().isDisposed()) {
								mViewer.add(camelFile,routes);
							}
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		job.addJobChangeListener(new JobChangeAdapter() {
	        public void done(IJobChangeEvent event) {
	        	Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (mViewer!=null && !mViewer.getControl().isDisposed()) {
							mViewer.remove(camelFile, new Object[] { placeHolder});
						}
					}
				});
	        }
	     });
		job.schedule();
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IFile
				||(element instanceof CamelCtxNavRouteNode && getChildren(element).length>0);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof AbstractTreeViewer) {
			mViewer = (AbstractTreeViewer) viewer;
		}
	}
	@Override
	public void dispose() {
		mViewer = null;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}


	@Override
	public void restoreState(IMemento aMemento) {
	}

	@Override
	public void saveState(IMemento aMemento) {
	}

	@Override
	public void init(ICommonContentExtensionSite aConfig) {
	}
	
	
}

