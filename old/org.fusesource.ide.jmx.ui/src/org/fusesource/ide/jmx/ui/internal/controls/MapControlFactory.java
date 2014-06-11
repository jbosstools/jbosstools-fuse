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

import java.util.Map;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.Messages;


public class MapControlFactory extends AbstractTabularControlFactory {

	@Override
	protected void fillTable(final Table table, final Object value) {
        TableColumn keyColumn = new TableColumn(table, SWT.NONE);
        keyColumn.setText(Messages.key);
        keyColumn.setWidth(150);
        TableColumn valueColumn = new TableColumn(table, SWT.NONE);
        valueColumn.setText(Messages.value);
        valueColumn.setWidth(250);
        
        for (Object o : ((Map) value).entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, StringUtils.toString(entry.getKey(), false));
            item.setText(1, StringUtils.toString(entry.getValue(), false));
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
