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
package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.provider;

import java.util.Collections;
import java.util.List;

import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.NewDataFormatWizard;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigElementType;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class DataFormatContributor implements ICustomGlobalConfigElementContribution {

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#createGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard createGlobalElement(CamelFile camelFile) {
		final String camelVersion = CamelModelFactory.getCamelVersion(camelFile.getResource().getProject());
		final DataFormatModel dataformatModel = CamelModelFactory.getModelForVersion(camelVersion).getDataformatModel();
		return new NewDataFormatWizard(camelFile, dataformatModel);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#modifyGlobalElement(org.w3c.dom.Document)
	 */
	@Override
	public GlobalConfigurationTypeWizard modifyGlobalElement(CamelFile camelFile) {
		// It is redirected to Properties view
		return null;
	}
	
	@Override
	public List<Dependency> getElementDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void onGlobalElementDeleted(AbstractCamelModelElement cme) {
		// possible actions if one of my nodes got deleted from the context
	}

	@Override
	public boolean canHandle(AbstractCamelModelElement camelModelElementToHandle) {
		// we support it if the parent node is dataFormats and the node is one of the supported languages of our model
		final Node nodeToHandle = camelModelElementToHandle.getXmlNode();
		String nodeName = CamelUtils.getTranslatedNodeName(nodeToHandle);
		DataFormatModel dfModel = CamelModelFactory.getModelForVersion(org.fusesource.ide.camel.editor.utils.CamelUtils.getCurrentProjectCamelVersion()).getDataformatModel();

		String dfName = nodeName;
		// special cases
		if (nodeName.equalsIgnoreCase("json")) {
			// library
			if ( ((Element)nodeToHandle).hasAttribute("library")) {
				dfName += String.format("-%s", ((Element)nodeToHandle).getAttribute("library").toLowerCase().trim());
			} else {
				// xstream as default
				dfName += String.format("-%s", "xstream");
			}
		} else if (nodeName.equalsIgnoreCase("bindy")) {
			// type
			if ( ((Element)nodeToHandle).hasAttribute("type")) {
				dfName += String.format("-%s", ((Element)nodeToHandle).getAttribute("type").toLowerCase().trim());
			} else {
				// keyvalue as default
				dfName += String.format("-%s", "keyvalue");
			}
		}
		return "dataformats".equalsIgnoreCase(CamelUtils.getTranslatedNodeName(nodeToHandle.getParentNode())) &&
			   dfModel.getDataFormatByName(dfName) != null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution#getGlobalConfigElementType()
	 */
	@Override
	public GlobalConfigElementType getGlobalConfigElementType() {
		return GlobalConfigElementType.CONTEXT_DATAFORMAT;
	}
}
