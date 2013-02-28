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

import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.jmx.core.util.StringUtils;


public class TabularDataControlFactory extends AbstractTabularControlFactory {

	@Override
	protected void fillTable(final Table table, final Object value) {
		TabularData data = (TabularData) value;
		
		Set keySet = data.getTabularType().getRowType().keySet();
		
        for (Object o : keySet) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText((String) o);
            column.setWidth(150);
            column.setMoveable(true);
            column.setResizable(true);
        }
        
        for (Object o : data.values()) {
            CompositeData rowData = (CompositeData) o;
            TableItem item = new TableItem(table, SWT.NONE);

            int i = 0;
            for (Object o2 : keySet) {
                String key = (String) o2;
                item.setText(i, StringUtils.toString(rowData.get(key), false));
                i++;
            }
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
