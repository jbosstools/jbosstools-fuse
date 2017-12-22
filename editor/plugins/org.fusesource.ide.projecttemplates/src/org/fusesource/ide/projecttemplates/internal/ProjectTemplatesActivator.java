/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class ProjectTemplatesActivator extends BaseUIPlugin {

	public static final String PLUGIN_ID = "org.fusesource.ide.projecttemplates";
	public static final String IMAGE_CAMEL_CONTEXT_ICON = "icons/camel_context_icon.png";
	public static final String IMAGE_CAMEL_PROJECT_ICON = "icons/camel_project_64x64.png";
	public static final String IMAGE_CAMEL_ROUTE_FOLDER_ICON = "icons/camel_route_folder.png";
	public static final String IMAGE_FUSE_ICON = "icons/fuse_icon_16c.png";

	private static ProjectTemplatesActivator instance;
	private IResourceChangeListener listener;
	private AtomicBoolean listenForEvents = new AtomicBoolean(true);
	
	/**
	 * default constructor
	 */
	public ProjectTemplatesActivator() {
		instance = this;
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static ProjectTemplatesActivator getDefault() {
		return instance;
	}

	public static BundleContext getBundleContext() {
		return instance.getBundle().getBundleContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
		registerWorkspaceListener();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		unregisterWorkspaceListener();
		super.stop(context);
	}

	@Override
	protected BaseUISharedImages createSharedImages() {
		return new ProjectTemplatesSharedImages(getBundle());
	}

	private void registerWorkspaceListener() {
		listener = new POMChangedReporter();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,
				IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE);
	}

	private void unregisterWorkspaceListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		listener = null;
	}

	/**
	 * Gets message from plugin.properties
	 * 
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		return Platform.getResourceString(instance.getBundle(), key);
	}

	/**
	 * Get the IPluginLog for this plugin. This method helps to make logging easier,
	 * for example:
	 * 
	 * FoundationCorePlugin.pluginLog().logError(etc)
	 * 
	 * @return IPluginLog object
	 */
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

	/**
	 * Get a status factory for this plugin
	 * 
	 * @return status factory
	 */
	public static StatusFactory statusFactory() {
		return getDefault().statusFactoryInternal();
	}

	private static class ProjectTemplatesSharedImages extends BaseUISharedImages {
		public ProjectTemplatesSharedImages(Bundle pluginBundle) {
			super(pluginBundle);
			addImage(IMAGE_CAMEL_CONTEXT_ICON, IMAGE_CAMEL_CONTEXT_ICON);
			addImage(IMAGE_CAMEL_PROJECT_ICON, IMAGE_CAMEL_PROJECT_ICON);
			addImage(IMAGE_CAMEL_ROUTE_FOLDER_ICON, IMAGE_CAMEL_ROUTE_FOLDER_ICON);
			addImage(IMAGE_FUSE_ICON, IMAGE_FUSE_ICON);
		}
	}

	class POMChangedReporter implements IResourceChangeListener {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (!listenForEvents.get()) return;
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				try {
					event.getDelta().accept(new DeltaPrinter());
				} catch (CoreException ex) {
					pluginLog().logError(ex);
				}
			}
		}
	}
	
	class DeltaPrinter implements IResourceDeltaVisitor {
		
		private static final String MARKER_ID = ProjectTemplatesActivator.PLUGIN_ID + ".CamelCatalogVersionProblem";
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
      	public boolean visit(IResourceDelta delta) {
			IResource res = delta.getResource();
			if (!isRelevantResource(res)) return true;
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					if (res.exists()) {
						validateConfiguredCamelVersionMatchesLoadedCamelVersion(res);
					}
					break;
				default: // nothing
			}
			return false; 
      	}
      	
      	private boolean isRelevantResource(IResource res) {
      		return 	res != null &&
      				res.getName().equalsIgnoreCase("pom.xml") && 
      				!res.getParent().getName().equalsIgnoreCase("bin") && 
      				!res.getParent().getName().equalsIgnoreCase("target");
      	}
      	
      	private void validateConfiguredCamelVersionMatchesLoadedCamelVersion(IResource res) {
      		Display.getDefault().asyncExec( () -> {
		  		String loadedCatalogVersion = CamelCatalogCacheManager.getInstance().getCamelModelForProject(res.getProject(), new NullProgressMonitor()).getVersion();
				String configuredCamelVersionFromPOM = new CamelMavenUtils().getCamelVersionFromMaven(res.getProject());
				try {
					while (!listenForEvents.compareAndSet(true, false)) {
						// wait for another validation to finish
					}
		    		if (!loadedCatalogVersion.equalsIgnoreCase(configuredCamelVersionFromPOM)) {
		    			createOrReuseMarker(res, IMarker.SEVERITY_WARNING, NLS.bind(Messages.camelCatalogVersionNotMatching, loadedCatalogVersion, configuredCamelVersionFromPOM));
		    		} else {
		    			res.deleteMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
		    		}
				} catch (CoreException ex) {
					pluginLog().logError(ex);
				} finally {
					listenForEvents.set(true);
				}
      		});
    	}
      	
      	private void createOrReuseMarker(IResource res, int severity, String message) throws CoreException {
			IMarker[] markers = res.findMarkers(MARKER_ID, true, IResource.DEPTH_INFINITE);
			IMarker m = markers.length>0 ? markers[0] : null;
			if (m == null) {
				m = res.createMarker(MARKER_ID);
				m.setAttribute(IMarker.SEVERITY, severity);
			}	      			
      		m.setAttribute(IMarker.MESSAGE, message);
      	}
	}
}
