/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

/**
 * The CPU profiler.
 */
public class CpuBciProfiler {

    /** The runtime model. */
    private static RuntimeModel model;

    /**
     * The method to be invoked when stepping into frame.
     * 
     * @param className
     *            The class name
     * @param methodName
     *            The method name
     */
    public static void stepInto(String className, String methodName) {
        if (!Config.getInstance().isProfilerEnabled()) {
            return;
        }

        long time = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();

        // get the current thread
        ThreadNode threadNode = model.getThread(thread);

        // update the current frame
        FrameNode frame;
        FrameNode previousFrame = threadNode.getCurrentFrame();
        if (previousFrame == null) {
            frame = threadNode.getRootFrame(className, methodName);
        } else {
            frame = previousFrame.getChild(className, methodName);
        }
        threadNode.setCurrentFrame(frame);

        // set the time
        frame.setStepIntoTime(time, System.currentTimeMillis() - time);
    }

    /**
     * The method to be invoked when stepping out from frame.
     * 
     * @param className
     *            The class name
     * @param methodName
     *            The method name
     */
    public static void stepReturn(String className, String methodName) {
        if (!Config.getInstance().isProfilerEnabled()) {
            return;
        }

        long time = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();

        // get the current thread
        ThreadNode threadNode = model.getThread(thread);

        // update the current frame
        FrameNode previousFrame = threadNode.getCurrentFrame();
        if (previousFrame == null) {
            return;
        }
        threadNode.setCurrentFrame(previousFrame.getParent());

        // set the time stepping return from this frame
        long overhead = System.currentTimeMillis() - time;
        previousFrame.setStepReturnTime(time + overhead, overhead);
    }

    /**
     * The method to be executed when dropping to frame due to exception.
     * 
     * @param className
     *            The class name
     * @param methodName
     *            The method name
     * @param exception
     *            The exception
     */
    public static void dropToFrame(String className, String methodName,
            String exception) {
        if (!Config.getInstance().isProfilerEnabled()) {
            return;
        }

        long time = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();

        // get the current thread
        ThreadNode threadNode = model.getThread(thread);

        // update the current frame
        FrameNode previousFrame = threadNode.getCurrentFrame();
        if (previousFrame == null) {
            return;
        }
        FrameNode frame = previousFrame.searchFrame(className, methodName);
        threadNode.setCurrentFrame(frame);

        // set the time dropping to this frame
        long overhead = System.currentTimeMillis() - time;
        if (frame.equals(previousFrame)) {
            // stay at frame
            previousFrame.incrementOverhead(overhead);
            return;
        }

        FrameNode iterator = previousFrame;
        while (!frame.equals(iterator)) {
            iterator.setStepReturnTime(time + overhead, overhead);
            iterator = iterator.getParent();
        }
    }

    /**
     * Initialize the profiler.
     */
    protected static void initialize() {
        model = new RuntimeModel();
    }

    /**
     * Gets the runtime model.
     * 
     * @return The runtime model
     */
    protected static RuntimeModel getModel() {
        return model;
    }
}
