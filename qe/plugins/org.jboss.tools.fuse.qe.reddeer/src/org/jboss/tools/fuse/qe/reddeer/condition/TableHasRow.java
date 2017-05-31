/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.condition;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.swt.api.Table;
import org.jboss.reddeer.swt.api.TableItem;

public class TableHasRow extends AbstractWaitCondition {

	private Table table;
	private Matcher<String> matcher;
	private List<TableItem> items;

	public TableHasRow(Table table, Matcher<String> matcher) {
		this.table = table;
		this.matcher = matcher;
		this.items = new ArrayList<TableItem>();
	}

	@Override
	public boolean test() {
		items = table.getItems();
		for (TableItem item : items) {
			if (matcher.matches(item.getText())) {
				item.select();
				return true;
			}
		}
		return false;
	}

	@Override
	public String description() {
		StringBuffer message = new StringBuffer();
		message.append("an item matching ").append(matcher.toString()).append(" in\n");
		for (TableItem item : items) {
			message.append("\t").append(item.getText()).append("\n");
		}
		return message.toString();
	}

}
