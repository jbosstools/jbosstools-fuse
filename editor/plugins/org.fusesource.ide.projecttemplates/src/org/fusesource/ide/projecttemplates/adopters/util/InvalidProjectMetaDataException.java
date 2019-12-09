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
package org.fusesource.ide.projecttemplates.adopters.util;

/**
 * @author lheinema
 */
public class InvalidProjectMetaDataException extends Exception {

	private static final long serialVersionUID = 7320130119722874052L;

	public InvalidProjectMetaDataException() {
		super();
	}

	/**
	 * @param message
	 */
	public InvalidProjectMetaDataException(String message) {
		super(message);
	}
}
