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
package org.fusesource.ide.camel.editor.features.create.ext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author Aurelien Pupier
 *
 */
public class AbstractComponentBasedCreateFigurefeature extends CreateFigureFeature {

	protected Component component;

	public AbstractComponentBasedCreateFigurefeature(IFeatureProvider fp, String name, String description) {
		super(fp, name, description, (Eip) null);
		setEip(getEipByName(AbstractCamelModelElement.ENDPOINT_TYPE_TO));
	}

	/**
	 * @param component
	 */
	protected void setComponent(Component component) {
		this.component = component;
	}

	@Override
	protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
		if (getEip() != null) {
			CamelFile camelFile = parent.getCamelFile();
			if (camelFile != null) {
				Element newNode = null;
				final Eip determinedEIP = determineEIP(parent);
				if (createDOMNode) {
					final String prefixNS = parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null;
					newNode = camelFile.createElement(determinedEIP.getName(), prefixNS);
				}
				final String uri = component.getSyntax() != null ? component.getSyntax() : String.format("%s:", component.getScheme());
				CamelEndpoint ep = new CamelEndpoint(uri); // we use the first found protocol string
				ep.setParent(parent);
				ep.setUnderlyingMetaModelObject(determinedEIP);
				if (createDOMNode) {
					ep.setXmlNode(newNode);
					ep.updateXMLNode();
				}
				try {
					updateMavenDependencies(component.getDependencies());
				} catch (CoreException ex) {
					CamelEditorUIActivator.pluginLog().logError("Unable to add the component dependency to the project maven configuration file.", ex);
				}
				return ep;
			}
		}
	    return null;
	}
}
