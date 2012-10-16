/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The agent load handler. A client plug-in that contributes to the extension
 * point <tt>org.fusesource.ide.jvmmonitor.core.agentLoadHandler</tt> will implement this
 * interface.
 */
public interface IAgentLoadHandler {

    /**
     * Loads the CPU BCI profiler agent.
     * 
     * @param jvm
     *            The JVM to which agent is loaded
     * @throws JvmCoreException
     */
    void loadAgent(IActiveJvm jvm) throws JvmCoreException;

    /**
     * Gets the state indicating if agent is loaded.
     * 
     * @return <tt>true</tt> if agent is loaded
     */
    boolean isAgentLoaded();
}
