/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.tools;

import static org.fusesource.ide.jvmmonitor.internal.tools.IConstants.BUNDLE_ROOT_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IAgentLoadHandler;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.tools.Activator;


/**
 * The agent load handler that loads the agent jar file
 * <tt>lib/jvmmonitor-agent.jar</tt> to target JVM.
 */
public class AgentLoadHandler implements IAgentLoadHandler {

    /** The path for agent jar. */
    private String agentJarPath;

    /** The state indicating if agent is loaded. */
    private boolean isAgentLoaded;

    /**
     * The constructor.
     */
    public AgentLoadHandler() {
        isAgentLoaded = false;
        searchAgentJar();
    }

    /*
     * @see IAgentLoadHandler#loadAgent(IActiveJvm)
     */
    @Override
    public void loadAgent(IActiveJvm jvm) throws JvmCoreException {
        if (agentJarPath == null) {
            return;
        }

        Tools tools = Tools.getInstance();
        Object virtualMachine = null;

        try {
            virtualMachine = tools.invokeAttach(jvm.getPid());
            tools.invokeLoadAgent(virtualMachine, agentJarPath, agentJarPath);
            isAgentLoaded = true;
        } catch (JvmCoreException e) {
            Activator.log(IStatus.ERROR,
                    NLS.bind(Messages.loadAgentFailedMsg, agentJarPath), e);
        } finally {
            if (virtualMachine != null) {
                try {
                    tools.invokeDetach(virtualMachine);
                } catch (JvmCoreException e) {
                    // ignore
                }
            }
        }
    }

    /*
     * @see IAgentLoadHandler#isAgentLoaded()
     */
    @Override
    public boolean isAgentLoaded() {
        return isAgentLoaded;
    }

    /**
     * Searches the agent jar file.
     */
    private void searchAgentJar() {
        URL entry = org.fusesource.ide.jvmmonitor.core.Activator.getDefault().getBundle()
                .getEntry(BUNDLE_ROOT_PATH);
        String corePluginPath;
        try {
            corePluginPath = FileLocator.resolve(entry).getPath();
        } catch (IOException e) {
            Activator.log(IStatus.ERROR, Messages.corePluginNoFoundMsg,
                    new Exception());
            return;
        }

        File corePlugin = new File(corePluginPath);

        if (!corePlugin.exists()) {
            // the bundle location is a relative path on linux
            String installationLication = Platform.getInstallLocation()
                    .getURL().getPath();
            corePlugin = new File(installationLication + corePluginPath);
            if (!corePlugin.exists()) {
                Activator.log(IStatus.ERROR, Messages.corePluginNoFoundMsg,
                        new Exception());
            }
        }

        File agentJar = new File(corePlugin + IConstants.JVMMONITOR_AGENT_JAR);
        if (!agentJar.exists()) {
            Activator.log(
                    IStatus.ERROR,
                    NLS.bind(Messages.agentJarNotFoundMsg,
                            agentJar.getAbsolutePath()), new Exception());
        }

        agentJarPath = agentJar.getAbsolutePath();

        Activator.log(IStatus.INFO,
                NLS.bind(Messages.agentJarFoundMsg, agentJarPath),
                new Exception());
    }
}
