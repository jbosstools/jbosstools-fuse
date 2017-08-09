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

import org.eclipse.core.resources.IFile;
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
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.foundation.ui.util.Widgets;

/**
 * @author Renjith M. 
 */
public class CamelCtxNavContentProvider implements ICommonContentProvider {
	
	private AbstractTreeViewer mViewer;
	private Job job;
	public static final Object JOB_FAMILY = new Object();
	
	@Override
	public Object[] getChildren(Object parent) { 
		if(parent instanceof IFile){	
			return getRoutes((IFile)parent);
		}
		return new Object[0];
	}
	
	private Object[] getRoutes(IFile camelFile){
		//deferred since load time for context files is unknown
		LoadingPlaceHolder placeHolder = new LoadingPlaceHolder();
		doDeferredLoad(camelFile, placeHolder);
		return new Object[] { placeHolder};
	}
	
	private void doDeferredLoad(final IFile camelFile,final LoadingPlaceHolder placeHolder){
		//instead of DeferredTreeContentManager
		job = new LoadingCamelRoutesForNavigatorViewerJob(NLS.bind(UIMessages.loadingCamelFile, camelFile.getName(), camelFile.getProject().getName()), camelFile, placeHolder);
		
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
		if(mViewer != null && mViewer.getControl() != null) {
			mViewer.getControl().dispose();
		}
		mViewer = null;
		if(job != null){
			job.cancel();
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
		private LoadingPlaceHolder placeHolder;

		private LoadingCamelRoutesForNavigatorViewerJob(String name, IFile camelFile, LoadingPlaceHolder placeHolder) {
			super(name);
			this.camelFile = camelFile;
			this.placeHolder = placeHolder;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final CamelCtxNavRouteNode[] routes = getRoutes(camelFile, monitor);
			if (routes!=null) {
				Display.getDefault().asyncExec(() -> {
					if (!Widgets.isDisposed(mViewer)) {
						clearPreviouslyComputedData();
						mViewer.add(camelFile, routes);
						mViewer.getControl().setData(camelFile.getLocationURI().toString(), routes);
					}
				});
			}
			return Status.OK_STATUS;
		}

		protected void clearPreviouslyComputedData() {
			Object[] storedData = (Object[])mViewer.getControl().getData(camelFile.getLocationURI().toString());
			if(storedData != null) {
				mViewer.remove(camelFile, storedData);
			}
			mViewer.remove(camelFile, new Object[] { placeHolder});
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
}