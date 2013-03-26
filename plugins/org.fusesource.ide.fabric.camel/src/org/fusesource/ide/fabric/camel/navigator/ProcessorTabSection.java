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
package org.fusesource.ide.fabric.camel.navigator;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.propsrc.PropertySourceTableView;

public class ProcessorTabSection extends PropertySourceTableView {

    private ProcessorNodeSupport current;

    public ProcessorTabSection() {
        super("");
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        ProcessorNodeSupport processor = (ProcessorNodeSupport) Selections.getFirstSelection(selection);
        if (processor == current) {
            return;
        }
        if (current == null || current.getClass() != processor.getClass()) {
            // reset the configuration
            setConfiguration(null);
        }
        current = processor;
        List<?> propertySources = processor == null ? Collections.emptyList() : processor
                .getAllProcessorsPropertySourceList();
        setPropertySources(propertySources);
        getViewer().setInput(propertySources);
        recreateColumns();
        getViewer().refresh(true);
    }

    @Override
    public String getId() {
        return current == null ? super.getId() : current.getClass().getName();
    }

    @Override
    protected void recreateColumns() {
        if (current != null) {
            super.recreateColumns();
        }
    }

}
