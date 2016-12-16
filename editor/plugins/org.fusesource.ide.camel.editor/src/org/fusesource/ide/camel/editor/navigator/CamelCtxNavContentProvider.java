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
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.ui.util.Widgets;

/**
 * @author Renjith M. 
 */
public class CamelCtxNavContentProvider implements ICommonContentProvider {
	
	private AbstractTreeViewer mViewer;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parent) { 
		if(parent instanceof IFile){	
			return getRoutes((IFile)parent,true);
		} 
//		else if(parent instanceof CamelCtxNavRouteNode){
//			List<CamelModelElement> rootNodes = ((CamelCtxNavRouteNode)parent).getCamelRoute().getChildElements();
//			//show root nodes to differentiate routes with no id
//			return rootNodes.toArray(new CamelModelElement[rootNodes.size()]); 
//		} 
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
	
	private CamelCtxNavRouteNode[] getRoutes(IFile camelFile) {
		CamelIOHandler ioHandler = new CamelIOHandler();
		final CamelFile rc = ioHandler.loadCamelModel(camelFile, new NullProgressMonitor());
		if (rc != null && rc.getRouteContainer() != null) {
			List<CamelCtxNavRouteNode> routes = new ArrayList<CamelCtxNavRouteNode>();
			for(AbstractCamelModelElement node : rc.getRouteContainer().getChildElements()) {
				if(node instanceof CamelRouteElement) {
					routes.add(new CamelCtxNavRouteNode((CamelRouteElement)node, camelFile));
				}
			}
			return routes.toArray(new CamelCtxNavRouteNode[routes.size()]);
		}
		return new CamelCtxNavRouteNode[0];
	}
	
	private void doDeferredLoad(final IFile camelFile,final LoadingPlaceHolder placeHolder){
		//instead of DeferredTreeContentManager
		Job job = new Job("Loading " + camelFile.getName()) {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final CamelCtxNavRouteNode[] routes = getRoutes(camelFile);
				if (routes!=null) {
					Display.getDefault().asyncExec(new Runnable() {
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							if (!Widgets.isDisposed(mViewer)) {
								mViewer.add(camelFile,routes);
							}
						}
					});
				}
				return Status.OK_STATUS;
			}
		};
		
		job.addJobChangeListener(new JobChangeAdapter() {
	        /*
	         * (non-Javadoc)
	         * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	         */
			@Override
			public void done(IJobChangeEvent event) {
	        	Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!Widgets.isDisposed(mViewer)) {
							mViewer.remove(camelFile, new Object[] { placeHolder});
						}
					}
				});
	        }
	     });
		
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IFile ||
			   (element instanceof CamelCtxNavRouteNode && getChildren(element).length>0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof AbstractTreeViewer) {
			mViewer = (AbstractTreeViewer) viewer;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		mViewer = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.navigator.IMementoAware#restoreState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void restoreState(IMemento aMemento) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.navigator.IMementoAware#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento aMemento) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.navigator.ICommonContentProvider#init(org.eclipse.ui.navigator.ICommonContentExtensionSite)
	 */
	@Override
	public void init(ICommonContentExtensionSite aConfig) {
	}
	
	
	private static class LoadingPlaceHolder {
		
		private final String loadingMsg = "Pending ...";
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return loadingMsg;
		}		
	}
}