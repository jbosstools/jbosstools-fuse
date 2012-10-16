/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fusesource.ide.jvmmonitor.internal.agent.asm.Type;


/**
 * The frame node of runtime model.
 */
@SuppressWarnings("nls")
public class FrameNode {

    /** The class name. */
    private String className;

    /** The method name. */
    private String methodName;

    /** The child frame nodes. */
    private Map<String, FrameNode> childFrames;

    /** The parent frame node. */
    private FrameNode parentFrame;

    /** The time stepped into this frame. */
    private long stepIntoTime;

    /** The total invocation time. */
    private long totalTime;

    /** The overhead time. */
    private long overheadTime;

    /** The invocation count. */
    private int count;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent frame node
     * @param clazz
     *            The class name
     * @param method
     *            The method name
     */
    protected FrameNode(FrameNode parent, String clazz, String method) {
        parentFrame = parent;
        className = clazz;
        methodName = method;
        childFrames = new ConcurrentHashMap<String, FrameNode>();
        stepIntoTime = 0;
        totalTime = 0;
        overheadTime = 0;
        count = 0;
    }

    /**
     * Gets the child frame for the given class name and method name.
     * 
     * @param clazz
     *            The class name
     * @param method
     *            The method name
     * @return The child frame
     */
    protected FrameNode getChild(String clazz, String method) {
        FrameNode frame = childFrames.get(clazz + '.' + method);
        if (frame == null) {
            frame = new FrameNode(this, clazz, method);
            childFrames.put(clazz + '.' + method, frame);
        }
        return frame;
    }

    /**
     * Sets the step into time.
     * 
     * @param time
     *            The time stepping into this frame
     * @param overhead
     *            The overhead time
     */
    protected void setStepIntoTime(long time, long overhead) {
        stepIntoTime = time;
        overheadTime += overhead;
        count++;
    }

    /**
     * Sets the step return time.
     * 
     * @param time
     *            The time stepping return from this frame
     * @param overhead
     *            The overhead time
     */
    protected void setStepReturnTime(long time, long overhead) {
        totalTime += time - stepIntoTime;
        overheadTime += overhead;
        stepIntoTime = 0;
    }

    /**
     * Increments the overhead.
     * 
     * @param overhead
     *            The overhead time
     */
    protected void incrementOverhead(long overhead) {
        overheadTime += overhead;
    }

    /**
     * Gets the parent frame.
     * 
     * @return The parent frame, or <tt>null</tt> if this is root frame node
     */
    protected FrameNode getParent() {
        return parentFrame;
    }

    /**
     * Searches the frame.
     * 
     * @param clazz
     *            The class name
     * @param method
     *            The method name
     * @return The frame
     */
    protected FrameNode searchFrame(String clazz, String method) {

        if (className.equals(clazz) && methodName.equals(method)) {
            return this;
        }

        if (parentFrame == null) {
            return this;
        }

        return parentFrame.searchFrame(clazz, method);
    }

    /**
     * Dumps into a file.
     * 
     * @param writer
     *            The writer
     * @param time
     *            The time
     * @param nest
     *            The nest
     */
    protected void dump(PrintWriter writer, long time, int nest) {
        String name = getFrameName();
        long actualTotalTime = totalTime - overheadTime;
        if (stepIntoTime != 0) {
            actualTotalTime += time - stepIntoTime;
        }

        for (int i = 0; i < nest; i++) {
            writer.print("\t");
        }
        writer.printf("<frame name=\"%s\" cnt=\"%d\" time=\"%d\"", name, count,
                Math.max(actualTotalTime, 0));
        if (childFrames.size() > 0) {
            writer.println(">");
            for (FrameNode frameNode : childFrames.values()) {
                frameNode.dump(writer, time, nest + 1);
            }
            for (int i = 0; i < nest; i++) {
                writer.print("\t");
            }
            writer.println("</frame>");
        } else {
            writer.println("/>");
        }
    }

    /**
     * Dumps the profile data.
     * 
     * @param buffer
     *            The string buffer
     * @param time
     *            The time
     * @param nest
     *            The nest count
     */
    protected void dump(StringBuffer buffer, long time, int nest) {
        String name = getFrameName();
        long actualTotalTime = totalTime - overheadTime;
        if (stepIntoTime != 0) {
            actualTotalTime += time - stepIntoTime;
        }

        for (int i = 0; i < nest; i++) {
            buffer.append('\t');
        }
        buffer.append("<frame name=\"").append(name).append("\" cnt=\"")
                .append(count).append("\" time=\"")
                .append(Math.max(actualTotalTime, 0)).append("\"");
        if (childFrames.size() > 0) {
            buffer.append(">\n");
            for (FrameNode frameNode : childFrames.values()) {
                frameNode.dump(buffer, time, nest + 1);
            }
            for (int i = 0; i < nest; i++) {
                buffer.append('\t');
            }
            buffer.append("</frame>\n");
        } else {
            buffer.append("/>\n");
        }
    }

    /**
     * Gets the frame name.
     * 
     * @return The frame name
     */
    private String getFrameName() {

        // replace '/' with '.'
        String clazz = className.replace('/', '.');

        // convert the parameter descriptor into java type
        StringBuilder builder = new StringBuilder();
        builder.append(methodName.substring(0, methodName.indexOf('(') + 1));
        Type[] types = Type.getArgumentTypes(methodName.substring(methodName
                .indexOf('(')));
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(Type.getType(types[i].getDescriptor())
                    .getClassName());
        }
        builder.append(')');
        String method = builder.toString();

        // convert into escaped characters
        method = method.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        return clazz + '.' + method;
    }
}
