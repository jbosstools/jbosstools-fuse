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

package org.fusesource.ide.foundation.ui.util;

import java.net.URL;
import java.util.Enumeration;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;


/**
 * Helper activator for working with images
 */
public abstract class ImagesActivatorSupport extends AbstractUIPlugin {

	protected static void showUserError(final String pluginId, final RiderLogFacade logger, final String title, final String message, final Exception e) {
		Throwable t = unwrapException(e);
		String text = t.getMessage();
		final IStatus errorStatus = new Status(IStatus.ERROR, pluginId, IStatus.ERROR, text, e);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(
						Shells.getShell(),
						title,
						message, errorStatus);
			}
		});
		logger.error(message + ". Exception: " + text, e);
	}

	protected static Throwable unwrapException(Exception e) {
		if (e instanceof JAXBException) {
			JAXBException jaxbe = (JAXBException) e;
			return jaxbe.getLinkedException();
		}
		return e;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		String prefix = "/icons/";
		Enumeration<URL> enu = getBundle().findEntries(prefix, "*", true);
		while (enu.hasMoreElements()) {
			URL u = enu.nextElement();
			String file = u.getFile();
			String fileName = file;
			if (!file.startsWith(prefix)) {
				FoundationUIActivator.pluginLog().logWarning("Warning: image: " + fileName + " does not start with prefix: " + prefix);
			}
			fileName = fileName.substring(prefix.length());
			registerImage(reg, fileName, fileName);
		}
	}

	/**
	 * registers the given file under the given key
	 * @param reg
	 * @param key		the key to register under
	 * @param fileName	the file name
	 */
	private void registerImage(ImageRegistry reg, String key, String fileName) {
		reg.put(key, imageDescriptorFromPlugin(getBundle().getSymbolicName(), String.format("icons/%s", fileName)));
	}

	/**
	 * returns the image stored under the given key
	 * 
	 * @param key	the key to lookup the image
	 * @return	the image or null if not found
	 */
	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * returns the image descriptor stored under the given key
	 * 
	 * @param key	the key to lookup the image descriptor
	 * @return	the image descriptor or null if not found
	 */
	public ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
	}
}