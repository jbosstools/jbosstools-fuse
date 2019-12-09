/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.jmx.camel;

import java.util.Date;


/**
 * @author Aurelien Pupier
 *
 */
public interface IBacklogTracerEventMessageMBean {

	String getMessageAsXml();

	// void setMessage(IBacklogTracerMessage message);

	IBacklogTracerMessage getMessage();

	void setExchangeId(String exchangeId);

	String getExchangeId();

	void setToNode(String toNode);

	String getToNode();

	void setRouteId(String routeId);

	String getRouteId();

	void setTimestamp(Date timestamp);

	Date getTimestamp();

	void setUid(long uid);

	long getUid();

}
