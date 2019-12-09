/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.properties;

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.util.Selections;

public class ContentsTabSection extends PropertySourceTableView {

    private RefreshableCollectionNode current;

    public ContentsTabSection() {
        super("");
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        RefreshableCollectionNode node = (RefreshableCollectionNode) Selections.getFirstSelection(selection);
        if (node == current) {
            return;
        }
        if (current == null || current.getClass() != node.getClass()) {
            // reset the configuration
            setConfiguration(null);
        }
        current = node;
		List<IPropertySource> propertySources = node.getPropertySourceList();
        setPropertySources(propertySources);
        getViewer().setInput(propertySources);
        recreateColumns();
        getViewer().refresh(true);
    }

    @Override
    public String getColumnConfigurationId() {
        return current == null ? super.getColumnConfigurationId() : current.getClass().getName();
    }

    @Override
    protected void recreateColumns() {
        if (current != null) {
            super.recreateColumns();
        }
    }

}