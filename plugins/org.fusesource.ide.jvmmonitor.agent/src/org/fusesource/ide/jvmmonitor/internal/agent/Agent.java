/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * The agent class that is loaded to the target JVM. The manifest file in agent
 * jar file has attributes <tt>Premain-Class</tt> and <tt>Agent-Class</tt> for
 * this class.
 */
@SuppressWarnings("nls")
public class Agent {

    /**
     * The method that is invoked by system before main method is invoked, if
     * the option <tt>-javaagent</tt> is specified for JVM arguments.
     * <p>
     * The typical use case is to profile Java application on remote host where
     * eclipse is not available.
     * 
     * @param agentArgs
     *            The agent arguments specified at the option
     *            <tt>-javaagent</tt>. For this agent, nothing will be given
     * @param inst
     *            The instrumentation service
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        try {
            init(getAgentJar(), inst);
        } catch (Throwable t) {
            logError(t, Messages.CANNOT_REGISTER_CPU_PROFILER_MXBEAN);
        }
    }

    /**
     * The method that is invoked by system if agent is loaded via
     * <tt>VirtualMachine.loadAgent(String, String)</tt>.
     * 
     * @param options
     *            The options given by loadAgent(). For this agent, the agent
     *            jar file path will be given
     * 
     * @param inst
     *            The instrumentation service
     */
    public static void agentmain(String options, Instrumentation inst) {
        try {
            init(options, inst);
        } catch (Throwable t) {
            logError(t, Messages.CANNOT_REGISTER_CPU_PROFILER_MXBEAN);
        }
    }

    /**
     * Logs the info message.
     * 
     * @param format
     *            The format for info message
     * @param args
     *            The arguments for the format
     */
    protected static void logInfo(String format, Object... args) {
        Logger.getLogger(Constants.LOGGER_NAME).fine(
                String.format(format, args));
    }

    /**
     * Logs the error message.
     * 
     * @param t
     *            The exception
     * @param format
     *            The format for error message
     * @param args
     *            The arguments for the format
     */
    protected static void logError(Throwable t, String format, Object... args) {
        Logger.getLogger(Constants.LOGGER_NAME).log(Level.SEVERE,
                String.format(format, args), t);
    }

    /**
     * Initialize the agent.
     * 
     * @param agentJar
     *            The path for agent jar file
     * @param inst
     *            The instrumentation service
     * @throws Throwable
     */
    private static void init(String agentJar, Instrumentation inst)
            throws Throwable {
        inst.appendToBootstrapClassLoaderSearch(new JarFile(agentJar));

        if (registerMXBeans(inst)) {
            logInfo(Messages.AGENT_LOADED);
        } else {
            logInfo(Messages.AGENT_ALREADY_LOADED);
        }
    }

    /**
     * Gets the agent jar file from input arguments.
     * 
     * @return The agent jar file
     */
    private static String getAgentJar() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMXBean.getInputArguments();
        for (String argument : arguments) {
            if (argument.contains(Constants.JAVA_AGENT_OPTION)) {
                return argument.replace(Constants.JAVA_AGENT_OPTION, "");
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Registers the MXBeans.
     * 
     * @param inst
     *            The instrumentation
     * @return <tt>true</tt> if registered, and <tt>false</tt> if nothing was
     *         done since MBeans had already been registered.
     * @throws Throwable
     */
    private static boolean registerMXBeans(Instrumentation inst)
            throws Throwable {
        boolean agentLoaded = false;

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName profilerObjectName = new ObjectName(
                CpuBciProfilerMXBean.PROFILER_MXBEAN_NAME);
        ObjectName dataTransferObjectName = new ObjectName(
                DataTransferMXBean.DATA_TRANSFER_MXBEAN_NAME);
        ObjectName swtResourceMonitorObjectName = new ObjectName(
                SWTResourceMonitorMXBean.SWT_RESOURCE_MONITOR_MXBEAN_NAME);

        if (!server.isRegistered(profilerObjectName)) {
            CpuBciProfilerMXBeanImpl profiler = new CpuBciProfilerMXBeanImpl(
                    inst);
            server.registerMBean(profiler, profilerObjectName);
            agentLoaded = true;
        }

        if (!server.isRegistered(dataTransferObjectName)) {
            DataTransferMXBeanImpl dataTransfer = new DataTransferMXBeanImpl();
            server.registerMBean(dataTransfer, dataTransferObjectName);
            agentLoaded = true;
        }

        if (!server.isRegistered(swtResourceMonitorObjectName)) {
            SWTResourceMonitorMXBeanImpl swtResourceMonitor = new SWTResourceMonitorMXBeanImpl(
                    inst);
            if (swtResourceMonitor.isSuppoted()) {
                server.registerMBean(swtResourceMonitor,
                        swtResourceMonitorObjectName);
            }
            agentLoaded = true;
        }

        return agentLoaded;
    }
}
