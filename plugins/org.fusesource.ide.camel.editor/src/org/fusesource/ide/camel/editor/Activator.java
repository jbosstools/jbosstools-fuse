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

package org.fusesource.ide.camel.editor;


import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.editor.RiderEditor;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.commons.ui.UIConstants;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends ImagesActivatorSupport {

	// The shared instance
	private static Activator plugin;
	public  static final String PLUGIN_ID = "org.fusesource.ide.camel.editor";
	public  static final String RIDER_EDITOR_ID = "org.fusesource.ide.camel.editor";

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private PreferredPerspectivePartListener perspectiveListener;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//redirectContextSensitiveHelp();
		perspectiveListener = new PreferredPerspectivePartListener();
		perspectiveListener.earlyStartup();
//		IAdapterManager manager = Platform.getAdapterManager();
//        manager.registerAdapters(new CamelContextOutlinePageAdapterFactory(), RiderDesignEditor.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Display a user error if an operation failed
	 */
	public static void showUserError(String title, String message, Exception e) {
		showUserError(PLUGIN_ID, getLogger(), title, message, e);
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	private void redirectContextSensitiveHelp() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null) {
					try {
						if (page.findView(UIConstants.PROPERTIES_VIEW_ID) == null) {
							IViewPart propView = page.showView(UIConstants.PROPERTIES_VIEW_ID);
							IWorkbenchHelpSystem helpSystem = propView.getSite().getWorkbenchWindow().getWorkbench().getHelpSystem();
							//System.err.println(propView.getClass().getName());
							//helpSystem.setHelp(propView, PLUGIN_ID);
						}
					} catch (PartInitException ex) {
						Activator.getLogger().error(ex);
					}
				}
			}
		}
	}

	/**
	 * retrieves the rider design editor
	 * 
	 * @return
	 */
	public static RiderDesignEditor getDiagramEditor() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
			if (wbw != null) {
				IWorkbenchPage page = wbw.getActivePage();
				if (page != null && page.getActiveEditor() != null) {
					IEditorReference[] refs = page.getEditorReferences();
					for (IEditorReference ref : refs) {
						// we need to check if the id of the editor ref matches our editor id
						// and also if the active editor is equal to the ref editor otherwise we might pick
						// a wrong editor and return it...bad thing
						if (ref.getId().equals(RIDER_EDITOR_ID) && 
							page.getActiveEditor().equals(ref.getEditor(false))) {
							// ok, we found a route editor and it is also the acitve editor
							RiderEditor ed = (RiderEditor)ref.getEditor(true);
							// so return it
							return ed.getDesignEditor();
						}
					}
				}
			}
		}
		return null;
	}
}
