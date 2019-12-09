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
package org.fusesource.ide.camel.editor.component.wizard;

import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.editor.provider.DiagramTypeProvider;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author Aurelien Pupier
 *
 */
final class ComponentLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Component) {
			final Component component = (Component) element;
			final String descriptionSuffix = component.getDescription() != null ? " - " + component.getDescription() : "";
			return component.getDisplayTitle() + descriptionSuffix;
		}
		return Strings.humanize(super.getText(element));
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Component) {
			final String keyForComponentSmallIcon = ImageProvider.getKeyForSmallIcon(true, ((Component) element).getScheme());
			return GraphitiUi.getImageService().getImageForId(DiagramTypeProvider.ID, keyForComponentSmallIcon);
		}
		return super.getImage(element);
	}
}