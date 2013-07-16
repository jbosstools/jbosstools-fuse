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

package org.fusesource.ide.fabric;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.Bundles;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.commons.ui.UIConstants;
import org.fusesource.ide.fabric.navigator.FabricNavigator;
import org.fusesource.ide.fabric.navigator.FabricNodeProvider;
import org.fusesource.ide.fabric.navigator.FabricPreferenceInitializer;
import org.fusesource.ide.fabric.navigator.NodeProvider;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * Represents a connector to Fuse Fabric for viewing the agents and features
 * available in a fabric
 */
public class FabricPlugin extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.fabric";
	public static final String TERMINAL_VIEW_ID = "org.fusesource.ide.server.karaf.view.TerminalView";

	private static IViewPart part;
	private static FabricPlugin plugin;
	private static List<NodeProvider> nodeProviders = new CopyOnWriteArrayList<NodeProvider>();
	private static FabricNodeProvider nodeProvider;
	private static AtomicBoolean started = new AtomicBoolean(false);


	public static FabricPlugin getPlugin() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		// lets make sure we can find the MBeanServer!
		MBeanServer mbeanServer = Bundles.lookupService(context, MBeanServer.class);
		if (mbeanServer == null) {
			System.out.println("================== no MBeanServer in OSGi registry so creating one");
			mbeanServer = ManagementFactory.getPlatformMBeanServer();
			System.out.println("================== found platform MBeanServer: " + mbeanServer);
			context.registerService(MBeanServer.class, mbeanServer, null);
			System.out.println("================== registered MBeanServer!" + mbeanServer);
		}
//		// if jersey is started already lets stop it first!
//		// as it must be started explicitly each time - restarting
//		// JVMs often thinks the bundle is started but it must be restarted :)
//		Bundles.stopBundle(context, "jersey-client");
//		Bundles.stopBundle(context, "jersey-core");
//
//		// lets make sure Jersey has been started already!
//		Bundles.startBundle(context, "jersey-core");
//		Bundles.startBundle(context, "jersey-client");
//
//		Bundles.startBundle(context, "jackson");
//		Bundles.startBundle(context, "jsr311");
//		Bundles.startBundle(context, "aries.util");
//		Bundles.startBundle(context, "aries.jmx");
//		Bundles.startBundle(context, "aries.proxy");
//		boolean useBlueprint = false;
//		if (useBlueprint) {
//			Bundles.startBundle(context, "aries.quiesce");
//			Bundles.startBundle(context, "blueprint");
//		}
//		Bundles.startBundle(context, "fabric-core-agent-ssh");
//		Bundles.startBundle(context, "fabric-core-agent-jclouds");
//		Bundles.startBundle(context, "fabric-core");
//		Bundles.startBundle(context, "maven-proxy");
		plugin = this;
		registerPlugins();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		unregisterPlugins();
		plugin = null;
		super.stop(context);
	}

	public static ImagesActivatorSupport getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	private static void setImageDescriptors(IAction action, String type,
			String relPath) {
		ImageDescriptor id = create("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);

		ImageDescriptor descriptor = create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor);
	}

	public static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$

	private static ImageDescriptor create(String prefix, String name,
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(getDefault().getBundle(), path,
				useMissingImageDescriptor);
	}

	private static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}


	// TODO use the OSGi registry instead :)
	public static void addNodeProvider(NodeProvider nodeProvider) {
		if (nodeProvider != null && !nodeProviders.contains(nodeProvider)) {
			nodeProviders.add(nodeProvider);
		}
	}

	public static void removeNodeProvider(NodeProvider nodeProvider) {
		if (nodeProvider != null) {
			nodeProviders.remove(nodeProvider);
		}
	}

	public static List<NodeProvider> getNodeProviders() {
		return nodeProviders;
	}

	/**
	 * Display a user error if an operation failed
	 */
	public static void showUserError(String title, String message, Exception e) {
		showUserError(PLUGIN_ID, getLogger(), title, message, e);
	}

	public static void registerPlugins() {
		if (started.compareAndSet(false, true)) {
			new FabricPreferenceInitializer().initializeDefaultPreferences();
			nodeProvider = new FabricNodeProvider();
			FabricPlugin.addNodeProvider(nodeProvider);
			JMXActivator.addNodeProvider(nodeProvider);
		}
	}

	public static void unregisterPlugins() {
		if (nodeProvider != null) {
			JMXActivator.removeNodeProvider(nodeProvider);
			FabricPlugin.removeNodeProvider(nodeProvider);
		}
		started.set(false);
	}

	/**
	 * opens the properties view if not already open
	 */
	public static IViewPart openTerminalView() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbench wb = PlatformUI.getWorkbench();
				if (wb != null) {
					IWorkbenchWindow activeWindow = wb.getActiveWorkbenchWindow();
					if (activeWindow != null) {
						IWorkbenchPage activePage = activeWindow.getActivePage();
						if (activePage != null) {
							try {
								part = activePage.showView(TERMINAL_VIEW_ID);
							} catch (CoreException ex) {
								getLogger().error("Unable to create the terminal view!", ex);
							}
						}
					}
				}
			}
		});
		return part;
	}
	
	/**
	 * retrieves the rider design editor
	 * 
	 * @return
	 */
	public static FabricNavigator getFabricNavigator() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null) {
					IViewPart part = page.findView(UIConstants.FABRIC_EXPLORER_VIEW_ID);
					if (part != null) {
						// ok, we found the view
						FabricNavigator nav = (FabricNavigator)part;
						// so return it
						return nav;
					}
				}
			}
		}
		return null;
	}
}
