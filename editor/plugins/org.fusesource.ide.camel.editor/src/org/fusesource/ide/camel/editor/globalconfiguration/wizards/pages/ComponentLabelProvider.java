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
package org.fusesource.ide.camel.editor.globalconfiguration.wizards.pages;

import org.eclipse.jface.viewers.LabelProvider;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;

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
		return super.getText(element);
	}
}