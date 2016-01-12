/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.camel.navigator;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.foundation.ui.util.Selections;
import org.fusesource.ide.jmx.camel.internal.CamelProcessorMBean;

public class RoutesProcessorsTabSection extends PropertySourceTableView {
    
    private RoutesNode current;

    public RoutesProcessorsTabSection() {
        super(CamelProcessorMBean.class.getName());
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        RoutesNode routes = (RoutesNode) Selections.getFirstSelection(selection);
        if (routes == current) {
            return;
        }
        current = routes;
        List<?> propertySources = routes == null ? Collections.emptyList() : routes.getAllProcessorsPropertySourceList();
        setPropertySources(propertySources);
        getViewer().setInput(propertySources);
        recreateColumns();
        getViewer().refresh(true);
    }

    @Override
    protected void recreateColumns() {
        if (current != null) {
            super.recreateColumns();
        }
    }

}
