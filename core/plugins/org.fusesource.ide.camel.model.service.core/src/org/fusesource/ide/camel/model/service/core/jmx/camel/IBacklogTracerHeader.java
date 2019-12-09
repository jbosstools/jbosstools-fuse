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

/**
 * @author Aurelien Pupier
 *
 */
public interface IBacklogTracerHeader {

	@Override
	String toString();

	void setValue(String value);

	String getValue();

	void setType(String type);

	String getType();

	void setKey(String key);

	String getKey();

}
