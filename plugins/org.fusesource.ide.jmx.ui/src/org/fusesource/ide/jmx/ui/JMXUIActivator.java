/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

package org.fusesource.ide.jmx.ui;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.jmx.ui.internal.adapters.JMXAdapterFactory;
import org.fusesource.ide.jmx.ui.internal.views.navigator.MBeanExplorerContentProvider;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class JMXUIActivator extends ImagesActivatorSupport {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.fusesource.ide.jmx.ui"; //$NON-NLS-1$

    // The shared instance
    private static JMXUIActivator plugin;

	private static List<RootJmxNodeProvider> rootJmxNodeProviders = new ArrayList<RootJmxNodeProvider>();
	private static List<MBeanExplorerContentProvider> contentProviders = new ArrayList<MBeanExplorerContentProvider>();

    private JMXAdapterFactory adapterFactory;
    private MBeanServerConnection connection;


    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        registerAdapters();
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        unregisterAdapters();
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static JMXUIActivator getDefault() {
        return plugin;
    }

    public static Shell getActiveWorkbenchShell() {
         IWorkbenchWindow window= getActiveWorkbenchWindow();
         if (window != null) {
            return window.getShell();
         }
         return null;
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    public static IWorkbenchPage getActivePage() {
        return getDefault().internalGetActivePage();
    }

    private IWorkbenchPage internalGetActivePage() {
        IWorkbenchWindow window= getWorkbench().getActiveWorkbenchWindow();
        if (window == null)
            return null;
        return window.getActivePage();
    }

    public void setCurrentConnection(MBeanServerConnection connection) {
    	this.connection  =  connection;
    }

    public MBeanServerConnection getCurrentConnection() {
    	return this.connection;
    }

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
	
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Log the given exception along with the provided message and severity
     * indicator
     */
    public static void log(int severity, String message, Throwable e) {
        log(new Status(severity, PLUGIN_ID, 0, message, e));
    }

    private void registerAdapters() {
        adapterFactory = new JMXAdapterFactory();
        for (Class<?> aClass : adapterFactory.getAdapterClasses()) {
        	Platform.getAdapterManager().registerAdapters(adapterFactory, aClass);
		}
    }

    private void unregisterAdapters() {
        for (Class<?> aClass : adapterFactory.getAdapterClasses()) {
        	Platform.getAdapterManager().unregisterAdapters(adapterFactory, aClass);
		}
    }

	public static void provideRootNodes(RefreshableUI explorerContentProvider, List list) {
		for (RootJmxNodeProvider provider: rootJmxNodeProviders) {
			provider.provideRootJmxNodes(explorerContentProvider, list);
		}
	}
	
	public static void addRootJmxNodeProvider(RootJmxNodeProvider provider) {
		rootJmxNodeProviders.add(provider);
		
		// lets notify the content providers to update themselves...
		for (RefreshableUI contentProvider : contentProviders) {
			contentProvider.fireRefresh();
		}
	}
	
	public static void removeRootJmxNodeProvider(RootJmxNodeProvider provider) {
		rootJmxNodeProviders.remove(provider);
	}

	public static void addExplorer(MBeanExplorerContentProvider contentProvider) {
		contentProviders.add(contentProvider);
	}

	public static void removeExplorer(RefreshableUI contentProvider) {
		contentProviders.remove(contentProvider);
	}
}
