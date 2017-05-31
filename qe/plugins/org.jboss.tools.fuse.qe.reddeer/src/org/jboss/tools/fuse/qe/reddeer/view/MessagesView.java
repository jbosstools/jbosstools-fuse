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
package org.jboss.tools.fuse.qe.reddeer.view;

import java.util.ArrayList;
import java.util.List;

import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.swt.api.Table;
import org.jboss.reddeer.swt.api.TableItem;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;

/**
 * Represents 'Messages View'
 * 
 * @author tsedmik
 */
public class MessagesView extends WorkbenchView {

	private static final String TITLE = "Messages View";
	private static final Logger log = Logger.getLogger(MessagesView.class);

	public MessagesView() {
		super(TITLE);
	}

	/**
	 * Gets all messages in 'Messages View'
	 * 
	 * @return list of all messages in 'Messages View'
	 */
	public List<Message> getAllMessages() {

		log.debug("Getting all data from 'Messages View'");
		activate();
		List<Message> data = new ArrayList<Message>();
		Table table = new DefaultTable();
		for (TableItem item : table.getItems()) {
			Message msg = new Message();
			msg.setTraceId(Integer.parseInt(item.getText()));
			msg.setTraceNode(item.getText(14));
			data.add(msg);
		}
		log.debug(data.size() + "items were found.");
		return data;
	}

	/**
	 * Return message on given position in 'Messages View'
	 * 
	 * @param i
	 *            row in the table
	 * @return message on given row
	 */
	public Message getMessage(int i) {

		log.debug("Getting message on " + i + " row.");
		activate();
		Table table = new DefaultTable();
		TableItem item = table.getItem(i - 1);
		Message msg = new Message();
		msg.setTraceId(Integer.parseInt(item.getText()));
		msg.setTraceNode(item.getText(15));

		return msg;
	}
}

