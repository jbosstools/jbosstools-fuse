/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * The filtered tree showing heap.
 */
public class HeapFilteredTree extends FilteredTree {

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     */
    protected HeapFilteredTree(Composite parent) {
        super(parent, SWT.MULTI | SWT.FULL_SELECTION, new PatternFilter(), true);
        setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    }

    /*
     * @see FilteredTree#createControl(Composite, int)
     */
    @Override
    protected void createControl(Composite composite, int treeStyle) {
        super.createControl(composite, treeStyle);

        // adjust the indentation of filter composite
        GridData data = (GridData) filterComposite.getLayoutData();
        data.horizontalIndent = 2;
        data.verticalIndent = 2;
        filterComposite.setLayoutData(data);
    }
}
