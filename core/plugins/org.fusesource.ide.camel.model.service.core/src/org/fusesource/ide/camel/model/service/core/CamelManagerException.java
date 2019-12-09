/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core;

/**
 * @author André Dietisheim
 */
public class CamelManagerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CamelManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CamelManagerException(Throwable cause) {
		super(cause);
	}

	public CamelManagerException(String message) {
		super(message);
	}

}
