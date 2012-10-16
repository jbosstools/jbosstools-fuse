/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The thread node of runtime model.
 */
@SuppressWarnings("nls")
public class ThreadNode {

    /** The thread name */
    private String thread;

    /** The current frame */
    private FrameNode currentFrame;

    /** The root frame nodes */
    private Map<String, FrameNode> rootFrames;

    /**
     * The constructor.
     * 
     * @param thread
     *            The thread name
     */
    protected ThreadNode(String thread) {
        this.thread = thread;
        rootFrames = new ConcurrentHashMap<String, FrameNode>();
    }

    /**
     * Gets the root frame.
     * 
     * @param className
     *            The class name
     * @param methodName
     *            The method name
     * @return The root frame
     */
    protected FrameNode getRootFrame(String className, String methodName) {
        FrameNode frame = rootFrames.get(className + '.' + methodName);
        if (frame == null) {
            frame = new FrameNode(null, className, methodName);
            rootFrames.put(className + '.' + methodName, frame);
        }
        return frame;
    }

    /**
     * Gets the current frame.
     * 
     * @return The current frame, or <tt>null</tt> if not yet set
     */
    protected FrameNode getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Sets the current frame.
     * 
     * @param frame
     *            The current frame
     */
    protected void setCurrentFrame(FrameNode frame) {
        currentFrame = frame;
    }

    /**
     * Dumps into a dump file.
     * 
     * @param writer
     *            The writer
     * @param time
     *            The time
     */
    protected void dump(PrintWriter writer, long time) {
        Collection<FrameNode> frameNodes = rootFrames.values();
        if (frameNodes.size() == 0) {
            return;
        }
        
        writer.printf("\t<thread name=\"%s\">", thread);
        writer.println("");
        for (FrameNode frameNode : frameNodes) {
            frameNode.dump(writer, time, 2);
        }
        writer.println("\t</thread>");
    }

    /**
     * Dumps the profile data.
     * 
     * @param buffer
     *            The string buffer
     * @param time
     *            The time
     */
    protected void dump(StringBuffer buffer, long time) {
        Collection<FrameNode> frameNodes = rootFrames.values();
        if (frameNodes.size() == 0) {
            return;
        }

        buffer.append("\t<thread name=\"").append(thread)
                .append("\">\n");
        for (FrameNode frameNode : frameNodes) {
            frameNode.dump(buffer, time, 2);
        }
        buffer.append("\t</thread>\n");
    }
}
