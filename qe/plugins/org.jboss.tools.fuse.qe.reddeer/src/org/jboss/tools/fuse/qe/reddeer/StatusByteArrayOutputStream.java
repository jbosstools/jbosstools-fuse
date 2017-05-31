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
package org.jboss.tools.fuse.qe.reddeer;

import java.io.ByteArrayOutputStream;

/**
 * Implementation of ByteArrayOutputStream knowing it's status
 * 
 * @author tsedmik
 */
public class StatusByteArrayOutputStream extends ByteArrayOutputStream {

	private boolean closeStatus = false;

	/**
	 * Checks whether is stream closed
	 * 
	 * @return <b>true</b> - stream is closed, <b>false</b> - otherwise
	 */
	public boolean isClosed() {

		return closeStatus;
	}

	/**
	 * Closes the output stream
	 */
	public void close() {

		this.close();
		closeStatus = true;
	}
}
