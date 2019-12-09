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

import java.util.List;

/**
 * @author Aurelien Pupier
 *
 */
public interface IBacklogTracerMessage {

	void setBody(String body);

	String getBody();

	// void setHeaders(List<?> headers);

	List<? extends IBacklogTracerHeader> getHeaders();

	void setExchangeId(String exchangeId);

	String getExchangeId();

}
