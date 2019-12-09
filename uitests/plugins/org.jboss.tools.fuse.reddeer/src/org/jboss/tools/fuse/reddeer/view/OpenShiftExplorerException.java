/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
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
 * OpenShiftExplorerException indicates exceptional situation with OpenShift Explorer view
 * 
 * @author tsedmik
 */
public class OpenShiftExplorerException extends RuntimeException {

	public static final long serialVersionUID = 869290156893427929L;

	public OpenShiftExplorerException(String message, Throwable cause) {
		super(message, cause);
	}

	public OpenShiftExplorerException(String message) {
		super(message);
	}

	public OpenShiftExplorerException(Throwable cause) {
		super(cause);
	}

	
}
