/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

package org.fusesource.ide.jmx.ui.internal.controls;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.extensions.IAttributeControlFactory;
import org.fusesource.ide.jmx.ui.extensions.IWritableAttributeHandler;


public abstract class AbstractTabularControlFactory 
		implements IAttributeControlFactory {

	public Control createControl(final Composite parent, final FormToolkit toolkit,
			final boolean writable, final String type, final Object value, 
			final IWritableAttributeHandler handler) {

		int style = SWT.SINGLE | SWT.FULL_SELECTION;
        Table table = null;
        if (toolkit != null) {
            table = toolkit.createTable(parent, style);
            toolkit.paintBordersFor(parent);
        } else {
            table = new Table(parent, style | SWT.BORDER);
        }
        
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData(gd);
        
        if (value == null) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setWidth(200);
            column.setMoveable(false);
            column.setResizable(true);
            
            table.setHeaderVisible(false);
            table.setLinesVisible(getVisibleLines());
            
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(StringUtils.NULL);
            
        } else {
            fillTable(table, value);
            table.setHeaderVisible(getVisibleHeader());
            table.setLinesVisible(getVisibleLines());
        }
        
        return table;
	}

	protected abstract void fillTable(Table table, Object value);
	
	protected abstract boolean getVisibleHeader();
	
	protected abstract boolean getVisibleLines();
}
