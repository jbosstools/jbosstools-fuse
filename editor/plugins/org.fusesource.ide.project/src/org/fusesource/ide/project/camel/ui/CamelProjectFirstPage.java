/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.project.camel.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.web.ui.internal.wizards.DataModelFacetCreationWizardPage;
import org.fusesource.ide.project.Activator;
import org.fusesource.ide.project.camel.ICamelFacetDataModelProperties;

public class CamelProjectFirstPage extends DataModelFacetCreationWizardPage {

	public CamelProjectFirstPage(IDataModel dataModel, String pageName) {
		super(dataModel, pageName);
		setTitle( org.fusesource.ide.project.Messages.NewCamelProject_FirstPageTitle);
		setDescription( org.fusesource.ide.project.Messages.NewCamelProject_FirstPageDesc);
	}

	protected String getModuleTypeID() {
		return ICamelFacetDataModelProperties.CAMEL_PROJECT_FACET;
	}
	
	protected void createPrimaryFacetComposite(Composite top) {
		super.createPrimaryFacetComposite(top);
		super.handlePrimaryFacetVersionSelectedEvent();
	}

	@Override
	protected void validatePage() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				validatePage(true);
			}
		});
	}
	
	@Override
	protected IDialogSettings getDialogSettings() {
        return Activator.getDefault().getDialogSettings();
    }
	
	
	@Override
	protected Set<IProjectFacetVersion> getFacetConfiguration( final IProjectFacetVersion primaryFacetVersion ) {
	    final Set<IProjectFacetVersion> config = new HashSet<IProjectFacetVersion>();
		IFacetedProjectWorkingCopy fpjwc = (IFacetedProjectWorkingCopy) this.model
				.getProperty(FACETED_PROJECT_WORKING_COPY);
		for (IProjectFacet fixedFacet : fpjwc.getFixedProjectFacets()) {
			if (fixedFacet == primaryFacetVersion.getProjectFacet()) {
				config.add(primaryFacetVersion);
			} else if (fixedFacet == JavaFacet.FACET) {
				config.add(JavaFacet.VERSION_1_6);
			} else {
				config.add(fpjwc.getHighestAvailableVersion(fixedFacet));
			}
		}
	    return config;
	}

    public static String getCompilerLevel() {
        String level = JavaCore.getOption( JavaCore.COMPILER_COMPLIANCE );
        if( level == null ) 
        	level = (String) JavaCore.getDefaultOptions().get( JavaCore.COMPILER_COMPLIANCE );
        return level;
    }
}
