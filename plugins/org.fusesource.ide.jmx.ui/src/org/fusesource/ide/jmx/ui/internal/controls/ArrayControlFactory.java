/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Benjamin Walstrum (issue #24)
 *******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.controls;

import java.lang.reflect.Array;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.Messages;


public class ArrayControlFactory extends AbstractTabularControlFactory {

	@Override
	protected void fillTable(final Table table, final Object value) {
        TableColumn columnName = new TableColumn(table, SWT.NONE);
        columnName.setText(Messages.name);
        columnName.setWidth(400);
        
        int length = Array.getLength(value);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(value, i);
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(StringUtils.toString(element, false));
        }
	}

	@Override
	protected boolean getVisibleHeader() {
		return false;
	}

	@Override
	protected boolean getVisibleLines() {
		return true;
	}

}
