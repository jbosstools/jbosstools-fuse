/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.commons;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;
import org.osgi.framework.Bundle;

/**
 * @author lhein
 */
public class Activator extends ImagesActivatorSupport {
	public static final String PLUGIN_ID = "org.fusesource.ide.server.commons";

	private static Activator instance;

	private IPreferencesService prefs;

	/**
	 * default constructor
	 */
	public Activator() {
		instance = this;
		this.prefs = Platform.getPreferencesService();
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return instance;
	}

	/**
	 * returns the preference service
	 * 
	 * @return
	 */
	public IPreferencesService getPreferenceService() {
		return prefs;
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

	/**
	 * Display a user error if an operation failed
	 */
	public static void showUserError(String title, String message, Exception e) {
		showUserError(PLUGIN_ID, getLogger(), title, message, e);
	}
}
