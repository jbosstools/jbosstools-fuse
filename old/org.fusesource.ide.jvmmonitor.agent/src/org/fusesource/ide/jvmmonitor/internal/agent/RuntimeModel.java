/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The runtime model.
 */
@SuppressWarnings("nls")
public class RuntimeModel implements Runnable {

    /** The thread nodes */
    private Map<String, ThreadNode> threadNodes;

    /**
     * The constructor.
     */
    protected RuntimeModel() {
        threadNodes = new ConcurrentHashMap<String, ThreadNode>();

        // to dump the model into file when shutting down application
        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    /*
     * @see Runnable#run()
     */
    @Override
    public void run() {
        Config.getInstance().setProfilerEnabled(false);
        if (Config.getInstance().isAutoDumpEnabled()) {
            doDumpToFile();
        }
    }

    /**
     * Gets the thread node with the given thread name.
     * 
     * @param thread
     *            The thread name
     * @return The thread node
     */
    protected ThreadNode getThread(String thread) {
        ThreadNode threadNode = threadNodes.get(thread);
        if (threadNode == null) {
            threadNode = new ThreadNode(thread);
            threadNodes.put(thread, threadNode);
        }
        return threadNode;
    }

    /**
     * Clears the model.
     */
    protected void clear() {
        threadNodes.clear();
    }

    /**
     * Dumps the profile data with <tt>Callable</tt>.
     * 
     * @return The profile data
     * @throws Exception
     */
    protected String dump() throws Exception {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                return doDump();
            }
        };

        return callable.call();
    }

    /**
     * Dumps the profile data with <tt>Runnable</tt>.
     */
    protected void dumpToFile() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                doDumpToFile();
            }
        };
        runnable.run();
    }

    /**
     * Dumps the profile data.
     * 
     * @return The profile data
     */
    protected String doDump() {

        // get date and time
        Date currentDate = new Date();
        String date = new SimpleDateFormat(Constants.DATE_FORMAT)
                .format(currentDate);
        String time = new SimpleDateFormat(Constants.TIME_FORMAT)
                .format(currentDate);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buffer.append("<?JvmMonitor version=\"");
        buffer.append(Constants.VERSION);
        buffer.append("\"?>\n");

        buffer.append("<cpu-profile date=\"").append(date).append(' ')
                .append(time).append("\" ");
        buffer.append("runtime=\"").append(getRuntime()).append("\" ");
        buffer.append("mainClass=\"").append(getMainClass()).append("\" ");
        buffer.append("arguments=\"").append(getJvmArguments()).append("\">\n");

        long currentTime = System.currentTimeMillis();
        for (ThreadNode threadNode : threadNodes.values()) {
            threadNode.dump(buffer, currentTime);
        }
        buffer.append("</cpu-profile>");
        return buffer.toString();
    }

    /**
     * Dumps into a dump file.
     */
    protected void doDumpToFile() {

        // get date and time
        Date currentDate = new Date();
        String date = new SimpleDateFormat(Constants.DATE_FORMAT)
                .format(currentDate);
        String time = new SimpleDateFormat(Constants.TIME_FORMAT)
                .format(currentDate);

        PrintWriter writer = null;
        try {
            // create writer
            writer = new PrintWriter(new BufferedWriter(new FileWriter(
                    getFile(currentDate))));

            // write into file
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.print("<?JvmMonitor version=\"");
            writer.print(Constants.VERSION);
            writer.println("\"?>");
            writer.printf("<cpu-profile date=\"%s %s\" ", date, time);
            writer.printf("runtime=\"%s\" ", getRuntime());
            writer.printf("mainClass=\"%s\" ", getMainClass());
            writer.printf("arguments=\"%s\">\n", getJvmArguments());
            writer.println("");
            long currentTime = System.currentTimeMillis();
            for (ThreadNode threadNode : threadNodes.values()) {
                threadNode.dump(writer, currentTime);
            }
            writer.println("</cpu-profile>");
            writer.flush();
        } catch (IOException e) {
            Agent.logError(e, Messages.CANNOT_CREATE_DUMP_FILE, Config
                    .getInstance().getDumpDir());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Gets the runtime name (e.g. PID@HOSTNAME depending on JVM).
     * 
     * @return The runtime name
     */
    private String getRuntime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName();
    }

    /**
     * Gets the main class.
     * 
     * @return The main class
     */
    private String getMainClass() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        for (ThreadInfo threadInfo : threadMXBean.dumpAllThreads(false, false)) {
            if (threadInfo.getThreadName().equals(Constants.MAIN_THREAD)) {
                StackTraceElement[] elements = threadInfo.getStackTrace();
                if (elements == null || elements.length == 0) {
                    return "";
                }

                StackTraceElement lastElement = elements[elements.length - 1];
                return lastElement.getClassName();
            }
        }
        return "";
    }

    /**
     * Gets the JVM arguments.
     * 
     * @return The JVM arguments
     */
    private String getJvmArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMXBean.getInputArguments();
        StringBuffer buffer = new StringBuffer();
        for (String argument : arguments) {
            if (buffer.length() > 0) {
                buffer.append(" ");
            }
            buffer.append(argument);
        }
        return buffer.toString();
    }

    /**
     * Gets the file.
     * 
     * @param date
     *            The date
     * @return The file
     */
    private File getFile(Date date) {
        String time = new SimpleDateFormat(Constants.TIME_FORMAT_FOR_FILENAME)
                .format(date);
        String fileName = Config.getInstance().getDumpDir()
                + Constants.DUMP_FILE_PREFIX + time
                + Constants.DUMP_FILE_SUFFIX;
        return new File(fileName);
    }
}
