/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.dump;

/**
 * The profile info.
 */
public interface IProfileInfo {

    /**
     * Gets the runtime.
     * 
     * @return The runtime
     */
    String getRuntime();

    /**
     * Gets the main class.
     * 
     * @return The main class
     */
    String getMainClass();

    /**
     * Gets the arguments.
     * 
     * @return The arguments
     */
    String getArguments();

    /**
     * Gets the date.
     * 
     * @return The date
     */
    String getDate();

    /**
     * Gets the comments.
     * 
     * @return The comments
     */
    String getComments();
}
