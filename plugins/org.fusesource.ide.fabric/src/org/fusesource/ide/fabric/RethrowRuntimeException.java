/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.fabric;


/**
 * A {@link RuntimeException} to wrap a real exception we wish to rethrow later
 */
public class RethrowRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -824753052842450329L;

	public RethrowRuntimeException(Throwable cause) {
		super(cause);
	}

	public void rethrowCause() throws Exception {
		Throwable cause = getCause();
		if (cause instanceof Error) {
			throw (Error) cause;
		} else {
			throw (Exception) cause;
		}
	}
}
