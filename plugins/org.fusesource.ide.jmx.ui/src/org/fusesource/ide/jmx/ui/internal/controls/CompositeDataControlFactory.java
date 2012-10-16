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

import javax.management.openmbean.CompositeData;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.Messages;


public class CompositeDataControlFactory extends AbstractTabularControlFactory {

	@Override
	protected void fillTable(final Table table, final Object value) {
        TableColumn keyColumn = new TableColumn(table, SWT.NONE);
        keyColumn.setText(Messages.key);
        keyColumn.setWidth(150);
        TableColumn valueColumn = new TableColumn(table, SWT.NONE);
        valueColumn.setText(Messages.value);
        valueColumn.setWidth(250);
        
        CompositeData data = (CompositeData) value;
        for (Object o : data.getCompositeType().keySet()) {
            String key = (String) o;
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, key);
            item.setText(1, StringUtils.toString(data.get(key), false));
        }
	}

	@Override
	protected boolean getVisibleHeader() {
		return true;
	}

	@Override
	protected boolean getVisibleLines() {
		return true;
	}

}
