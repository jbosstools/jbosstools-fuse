/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The SWT resource.
 */
public interface ISWTResourceElement extends IStackTraceProvider {

    /**
     * Gets the SWT resource name.
     * 
     * @return The SWT resource name
     */
    String getName();
}
