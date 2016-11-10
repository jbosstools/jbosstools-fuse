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

package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.pages.DataFormatSelectionPage;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormatModel;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public class NewDataFormatWizard extends Wizard implements GlobalConfigurationTypeWizard {

    private DataFormatModel dfModel;
	private Element dataformatNode;

	private DataFormatSelectionPage dataFormatSelectionPage;
	private DataFormat dataformatSelected;
	private CamelFile camelFile;

	public NewDataFormatWizard(CamelFile camelFile, DataFormatModel dfModel) {
		this.dfModel = dfModel;
		this.camelFile = camelFile;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle(UIMessages.newGlobalConfigurationTypeDataFormatWizardDialogTitle);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		this.dataFormatSelectionPage = new DataFormatSelectionPage(dfModel);
		addPage(dataFormatSelectionPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		this.dataformatNode = null;
		return super.performCancel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		dataformatSelected = dataFormatSelectionPage.getDataFormatSelected();
		dataformatNode = createDataFormatNode(dataformatSelected, dataFormatSelectionPage.getId());
		return true;
	}

	/**
	 * /!\ Public for test purpose only
	 *
	 * @param dataformat
	 * @param id
	 * @return
	 */
	public Element createDataFormatNode(DataFormat dataformat, String id) {
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		Element newDataformatNode = camelFile.createElement(dataformat.getModelName(), prefixNS); // $NON-NLS-1$
		newDataformatNode.setAttribute("id", id); //$NON-NLS-1$
		for (Parameter parameter : dataformat.getParameters()) {
			String defaultValue = parameter.getDefaultValue();
			if (defaultValue != null) {
				newDataformatNode.setAttribute(parameter.getName(), defaultValue);
			}
		}
		return newDataformatNode;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#getGlobalConfigrationElementNode()
	 */
	@Override
	public Element getGlobalConfigurationElementNode() {
		return this.dataformatNode;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard#setGlobalConfigrationElementNode(org.w3c.dom.Node)
	 */
	@Override
	public void setGlobalConfigurationElementNode(Element node) {
		this.dataformatNode = node;
	}

}
