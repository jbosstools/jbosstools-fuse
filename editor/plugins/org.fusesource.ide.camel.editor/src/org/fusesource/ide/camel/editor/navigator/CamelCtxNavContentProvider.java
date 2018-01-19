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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.ui.util.Widgets;

/**
 * @author Renjith M. 
 */
public class CamelCtxNavContentProvider implements ICommonContentProvider, IResourceChangeListener {
	
	public static final Object JOB_FAMILY = new Object();
	
	private AbstractTreeViewer mViewer;
	private Job job;
	private Map<IFile, Object[]> contents = new HashMap<>();	
	
	public CamelCtxNavContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	@Override
	public Object[] getChildren(Object parent) { 
		if(parent instanceof IFile){	
			return getRoutes((IFile)parent);
		}
		return new Object[0];
	}
	
	private Object[] getRoutes(IFile camelFile){
		//deferred since load time for context files is unknown
		if (!contents.containsKey(camelFile)) {
			doDeferredLoad(camelFile);
			contents.put(camelFile, new Object[] { new LoadingPlaceHolder()});
		}
		return contents.get(camelFile);
	}
	
	private void doDeferredLoad(final IFile camelFile){
		//instead of DeferredTreeContentManager
		job = new LoadingCamelRoutesForNavigatorViewerJob(NLS.bind(UIMessages.loadingCamelFile, camelFile.getName(), camelFile.getProject().getName()), camelFile);
		job.schedule();
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IFile ||
			   (element instanceof CamelCtxNavRouteNode && getChildren(element).length>0);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof AbstractTreeViewer) {
			mViewer = (AbstractTreeViewer) viewer;
		}
	}
	
	@Override
	public void dispose() {
		if(job != null){
			job.cancel();
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			if(event != null) {
				IResourceDelta delta = event.getDelta();
				if (delta != null) {
					delta.accept(new DeltaWalker());
				}
			}
		} catch (CoreException ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		}
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
	public void restoreState(IMemento aMemento) { /*Not supported*/	}
	@Override
	public void saveState(IMemento aMemento)  { /*Not supported*/ }
	@Override
	public void init(ICommonContentExtensionSite aConfig) { /*Not supported*/ }
	
	
	private final class LoadingCamelRoutesForNavigatorViewerJob extends Job {
		private final IFile camelFile;

		private LoadingCamelRoutesForNavigatorViewerJob(String name, IFile camelFile) {
			super(name);
			this.camelFile = camelFile;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final CamelCtxNavRouteNode[] routes = getRoutes(camelFile, monitor);
			if (routes!=null) {
				contents.put(camelFile, routes);
				Display.getDefault().asyncExec(() -> {
					if (!Widgets.isDisposed(mViewer)) {
						mViewer.refresh(true);
					}
				});
			}
			return Status.OK_STATUS;
		}
		
		private CamelCtxNavRouteNode[] getRoutes(IFile camelFile, IProgressMonitor monitor) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 1);
			CamelIOHandler ioHandler = new CamelIOHandler();
			final CamelFile rc = ioHandler.loadCamelModel(camelFile, subMonitor.newChild(1));
			if (rc != null && rc.getRouteContainer() != null) {
				return rc.getRouteContainer().getChildElements()
						.stream()
						.filter(node -> node instanceof CamelRouteElement)
						.map(route -> new CamelCtxNavRouteNode((CamelRouteElement)route, camelFile))
						.toArray(CamelCtxNavRouteNode[]::new);
			}
			return new CamelCtxNavRouteNode[0];
		}
		
		@Override
		public boolean belongsTo(Object family) {
			return JOB_FAMILY.equals(family) || super.belongsTo(family);
		}
	}

	private static class LoadingPlaceHolder {
		
		@Override
		public String toString() {
			return UIMessages.pending;
		}		
	}
	
	private class DeltaWalker implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();

			if (contents.containsKey(resource) && !Widgets.isDisposed(mViewer)) {
	        	contents.remove(resource);
	        	Display.getDefault().asyncExec( () -> mViewer.refresh());
	        	return false;
	        }
			return true; // visit the children
		}
	}
}