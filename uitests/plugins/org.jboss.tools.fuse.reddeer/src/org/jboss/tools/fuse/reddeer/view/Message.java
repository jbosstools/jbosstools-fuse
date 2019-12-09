/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.view;

/**
 * Represents a message from 'Messages View'
 * 
 * @author tsedmik
 */
public class Message {

	int traceId;
	String traceNode;
	String messageBody;

	public int getTraceId() {
		return traceId;
	}

	public void setTraceId(int traceId) {
		this.traceId = traceId;
	}

	public String getTraceNode() {
		return traceNode;
	}

	public void setTraceNode(String traceNode) {
		this.traceNode = traceNode;
	}
	
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public String getMessageBody() {
		return this.messageBody;
	}
}
