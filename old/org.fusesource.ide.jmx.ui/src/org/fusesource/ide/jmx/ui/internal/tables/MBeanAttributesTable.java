/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Code was inspired by org.eclipse.equinox.client source, (c) 2006 IBM
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.tables;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.ui.Messages;


public class MBeanAttributesTable {

    private TableViewer viewer;

    public MBeanAttributesTable(Composite parent, final FormToolkit toolkit) {
        final Table attrTable = toolkit.createTable(parent, SWT.FULL_SELECTION);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        attrTable.setLayoutData(gd);
        toolkit.paintBordersFor(parent);
        createColumns(attrTable);
        attrTable.setLinesVisible(true);
        attrTable.setHeaderVisible(true);
        viewer = new TableViewer(attrTable);
        viewer.setContentProvider(new AttributesContentProvider());
        viewer.setLabelProvider(new AttributesLabelProvider());
    }

    private void createColumns(final Table attrTable) {
        final TableColumn attrName = new TableColumn(attrTable, SWT.NONE);
        attrName.setText(Messages.name);
        attrName.setWidth(150);
        final TableColumn attrValue = new TableColumn(attrTable, SWT.NONE);
        attrValue.setText(Messages.value);
        attrValue.setWidth(350);

        Listener sortListener = new Listener() {
            public void handleEvent(Event e) {
                // determine new sort column and direction
                TableColumn sortColumn = attrTable.getSortColumn();
                TableColumn currentColumn = (TableColumn) e.widget;

                int dir = attrTable.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                } else {
                    attrTable.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }

                int colIndex;
                if (currentColumn == attrName)
                    colIndex = 0;
                else if (currentColumn == attrValue)
                    colIndex = 1;
                else
                    return;

                // sort the data based on column and direction
                attrTable.setSortDirection(dir);
                viewer.setSorter(new AttributesViewerSorter(dir, colIndex));
            }
        };
        attrName.addListener(SWT.Selection, sortListener);
        attrTable.setSortColumn(attrName);
        attrTable.setSortDirection(SWT.UP);
    }

    public void setInput(MBeanInfoWrapper input) {
        if (input == null || input.getMBeanInfo() == null)
            viewer.setInput(null);
        else
            viewer.setInput(input.getMBeanAttributeInfoWrappers());
        viewer.getTable().redraw();
    }

    public Viewer getViewer() {
        return viewer;
    }
}
