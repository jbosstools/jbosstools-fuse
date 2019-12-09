/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.features.create.ext;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;

/**
 * @author lhein
 */
public class CreateConnectorFigureFeature extends AbstractComponentBasedCreateFigurefeature {
    
    /**
     * creates a component create feature
     * 
     * @param fp
     * @param component
     */
    public CreateConnectorFigureFeature(IFeatureProvider fp, Component component) {
		super(fp, component.getDisplayTitle(), component.getDescription());
		setComponent(component);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#
	 * getIconName()
	 */
    @Override
    protected String getIconName() {
        return this.component.getSchemeTitle();
    }
        
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#getCategoryName()
     */
    @Override
    public String getCategoryName() {
    	return "components";
    }
    
    /**
	 * @return the component
	 */
	public Component getConnector() {
		return this.component;
	}
}
