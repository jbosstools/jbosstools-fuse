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
package org.fusesource.ide.jmx.camel.navigator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.foundation.ui.util.Selections;

public class ProcessorCallsTabSection extends ProcessorCallView {

    public ProcessorCallsTabSection() {
        super(null);
    }

    @Override
    public void setInput(IWorkbenchPart part, ISelection selection) {
        Object selected = Selections.getFirstSelection(selection);
        /*
         * we need both setInput() calls because the tab is probably hidden and
         * thus won't get set on the viewer.
         */
        setInput(selected);
        getViewer().setInput(selected);
        getViewer().refresh();
    }

}
