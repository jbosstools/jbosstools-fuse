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

package org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.pages.DataFormatSelectionPage;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.provider.ext.GlobalConfigurationTypeWizard;
import org.fusesource.ide.camel.editor.utils.MavenUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public class NewDataFormatWizard extends Wizard implements GlobalConfigurationTypeWizard {

    private CamelModel model;
	private Element dataformatNode;

	private DataFormatSelectionPage dataFormatSelectionPage;
	private DataFormat dataformatSelected;
	private CamelFile camelFile;

	public NewDataFormatWizard(CamelFile camelFile, CamelModel model) {
		this.model = model;
		this.camelFile = camelFile;
		setNeedsProgressMonitor(true);
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
		this.dataFormatSelectionPage = new DataFormatSelectionPage(model);
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

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					dataformatSelected = dataFormatSelectionPage.getDataFormatSelected();
					dataformatNode = createDataFormatNode(dataformatSelected, dataFormatSelectionPage.getId(), monitor);
				}
			});
		} catch (InvocationTargetException ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		} catch (InterruptedException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
			Thread.currentThread().interrupt();
		}
		
		return true;
	}

	/**
	 * /!\ Public for test purpose only
	 *
	 * @param dataformat
	 * @param id
	 * @param monitor 
	 * @return
	 */
	public Element createDataFormatNode(DataFormat dataformat, String id, IProgressMonitor monitor) {
		SubMonitor subMonitor =SubMonitor.convert(monitor, 2);
		final String prefixNS = camelFile.getRouteContainer().getXmlNode().getPrefix();
		Element newDataformatNode = camelFile.createElement(dataformat.getModelName(), prefixNS); // $NON-NLS-1$
		newDataformatNode.setAttribute("id", id); //$NON-NLS-1$
		for (Parameter parameter : dataformat.getParameters()) {
			String defaultValue = parameter.getDefaultValue();
			if (defaultValue != null) {
				newDataformatNode.setAttribute(parameter.getName(), defaultValue);
			}
		}
		subMonitor.worked(1);
		
		List<Dependency> dependencies = dataformat.getDependencies();
		MavenUtils utils = new MavenUtils();
		utils.updateMavenDependencies(dependencies, camelFile.getResource().getProject(), subMonitor.split(1));
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
