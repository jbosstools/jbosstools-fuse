/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.decorator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.navigator.CamelCtxNavRouteNode;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.camel.validation.diagram.IFuseMarker;
import org.fusesource.ide.project.Activator;
import org.fusesource.ide.project.providers.CamelVirtualFolder;

/**
 * @author Aurelien Pupier
 *
 */
public class CamelRouteProblemDecorator implements ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof CamelVirtualFolder) {
			decorationForCamelVirtualFolder(element, decoration);
		} else if (element instanceof CamelRouteElement) {
			decorationForCamelRoute((CamelRouteElement) element, decoration);
		} else if (element instanceof CamelCtxNavRouteNode) {
			decorationForCamelRoute(((CamelCtxNavRouteNode) element).getCamelRoute(), decoration);
		}
	}

	/**
	 * @param element
	 * @param decoration
	 */
	private void decorationForCamelVirtualFolder(Object element, IDecoration decoration) {
		for (IResource resource : ((CamelVirtualFolder) element).getCamelFiles()) {
			try {
				int maxProblemSeverity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				decoration.addOverlay(getOverlay(maxProblemSeverity));
			} catch (CoreException e) {
				Activator.getLogger().error(e);
			}
		}
	}

	/**
	 * @param camelRoute
	 * @param decoration
	 */
	private void decorationForCamelRoute(CamelRouteElement camelRoute, IDecoration decoration) {
		IResource resource = camelRoute.getCamelFile().getResource();
		try {
			IMarker[] markers = resource.findMarkers(IFuseMarker.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				String id = (String) marker.getAttribute(IFuseMarker.CAMEL_ID);
				if (id != null) {
					if (isInsideRoute(camelRoute, id)) {
						decoration.addOverlay(getOverlay((int) marker.getAttribute(IMarker.SEVERITY)));
						return;
					}
				}
			}
		} catch (CoreException e) {
			Activator.getLogger().error(e);
		}
	}

	/**
	 * @param camelRoute
	 * @param id
	 * @return if the Camel Element with 'id' is inside the 'camelRoute'
	 */
	private boolean isInsideRoute(CamelRouteElement camelRoute, String id) {
		CamelModelElement current = ((CamelRouteElement) camelRoute).getCamelFile().findNode(id);
		while (current != null) {
			if (camelRoute.equals(current)) {
				return true;
			} else {
				current = current.getParent();
			}
		}
		return false;
	}

	/**
	 * @return The overlay image corresponding to the Marker severity
	 */
	private ImageDescriptor getOverlay(int problemSeverity) {
		if (problemSeverity == IMarker.SEVERITY_ERROR) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
		} else if (problemSeverity == IMarker.SEVERITY_WARNING) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING);
		} else {
			return null;
		}
	}

}
