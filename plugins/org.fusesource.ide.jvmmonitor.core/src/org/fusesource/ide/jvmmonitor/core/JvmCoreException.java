/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

/**
 * The JVM core exception.
 */
public class JvmCoreException extends CoreException {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor.
     * 
     * @param severity
     *            The severity (e.g. IStatus.ERROR)
     * @param message
     *            the message
     * @param t
     *            The exception
     */
    public JvmCoreException(int severity, String message, Throwable t) {
        super(new Status(severity, Activator.PLUGIN_ID, message, t));
    }
}