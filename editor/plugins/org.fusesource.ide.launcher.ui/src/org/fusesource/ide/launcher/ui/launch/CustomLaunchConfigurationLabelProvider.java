/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.launcher.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.swt.graphics.Image;

/**
 * @author Aurelien Pupier
 *
 */
final class CustomLaunchConfigurationLabelProvider implements ILabelProvider {
	/**
	 * 
	 */
	private final IDebugModelPresentation labelProvider;

	/**
	 * @param labelProvider
	 */
	CustomLaunchConfigurationLabelProvider(IDebugModelPresentation labelProvider) {
		this.labelProvider = labelProvider;
	}

	@Override
	public Image getImage(Object element) {
		return labelProvider.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration configuration = (ILaunchConfiguration) element;
			try {
				return labelProvider
						.getText(element)
						+ " : "
						+ configuration.getAttribute(MavenLaunchConstants.ATTR_GOALS,"");
			} catch (CoreException ex) {
				// ignore
			}
		}
		return labelProvider.getText(element);
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return labelProvider.isLabelProperty(element, property);
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		labelProvider.addListener(listener);
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		labelProvider.removeListener(listener);
	}

	@Override
	public void dispose() {
		labelProvider.dispose();
	}
}