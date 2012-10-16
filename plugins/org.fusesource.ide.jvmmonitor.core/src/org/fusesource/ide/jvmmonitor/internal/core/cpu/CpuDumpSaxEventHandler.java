/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core.cpu;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ITreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;
import org.fusesource.ide.jvmmonitor.core.dump.IProfileInfo;
import org.fusesource.ide.jvmmonitor.internal.core.ProfileInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX event handler for CPU dump.
 */
public class CpuDumpSaxEventHandler extends DefaultHandler {

    /** The error message for illegal number format in dump file */
    private String ILLEGAL_NUMBER_FORMAT = "ERROR: " //$NON-NLS-1$
            + "profile result data contains illegal number format."; //$NON-NLS-1$

    /** The progress monitor */
    private IProgressMonitor monitor;

    /** The currently parsed call tree thread node */
    private ThreadNode<CallTreeNode> currentCallTreeThreadNode;

    /** The currently parsed hot spot thread node */
    private ThreadNode<MethodNode> currentHotSpotThreadNode;

    /** The currently parsed root frame node */
    private CallTreeNode currentRootFrameNode;

    /** The currently parsed frame node */
    private CallTreeNode currentFrameNode;

    /** The CPU model */
    private CpuModel cpuModel;

    /** The total time [ns] for currently parsed thread */
    private long threadTotalTime;

    /** The total time [ns] for currently parsed frame tree */
    private long frameTotalTime;

    /** The profile info. */
    private IProfileInfo info;

    /**
     * The constructor.
     * 
     * @param cpuModel
     *            The CPU model
     * @param monitor
     *            The progress monitor
     */
    public CpuDumpSaxEventHandler(CpuModel cpuModel, IProgressMonitor monitor) {
        this.monitor = monitor;
        this.cpuModel = cpuModel;
        threadTotalTime = 0;
        frameTotalTime = 0;
    }

    /*
     * @see DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        cpuModel.clear();
    }

    /*
     * @see DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        cpuModel.notifyModelChanged(new CpuModelEvent(
                CpuModelState.CpuModelChanged));
    }

    /*
     * @see DefaultHandler#startElement(String, String, String, Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }

        // cpu-profile
        if ("cpu-profile".equals(name)) { //$NON-NLS-1$
            String date = attributes.getValue("date"); //$NON-NLS-1$
            String runtime = attributes.getValue("runtime"); //$NON-NLS-1$
            String mainClass = attributes.getValue("mainClass"); //$NON-NLS-1$
            String arguments = attributes.getValue("arguments"); //$NON-NLS-1$
            String comments = attributes.getValue("comments"); //$NON-NLS-1$
            info = new ProfileInfo(date, runtime, mainClass, arguments,
                    comments);
            return;
        }

        // thread
        if ("thread".equals(name)) { //$NON-NLS-1$
            String threadName = attributes.getValue("name"); //$NON-NLS-1$
            currentCallTreeThreadNode = cpuModel
                    .getCallTreeThread(threadName);
            if (currentCallTreeThreadNode == null) {
                currentCallTreeThreadNode = new ThreadNode<CallTreeNode>(threadName);
                cpuModel.addCallTreeThread(currentCallTreeThreadNode);
            }
            currentHotSpotThreadNode = cpuModel
                    .getHotSpotThread(threadName);
            if (currentHotSpotThreadNode == null) {
                currentHotSpotThreadNode = new ThreadNode<MethodNode>(threadName);
                cpuModel.addHotSpotThread(currentHotSpotThreadNode);
            }
            return;
        }

        // frame
        if ("frame".equals(name)) { //$NON-NLS-1$
            parseFrame(attributes);
        }
    }

    /*
     * @see DefaultHandler#endElement(String, String, String)
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        // thread
        if ("thread".equals(name)) { //$NON-NLS-1$
            currentCallTreeThreadNode.setTotalTime(threadTotalTime);
            currentCallTreeThreadNode = null;
            currentHotSpotThreadNode.setTotalTime(threadTotalTime);
            currentHotSpotThreadNode = null;
            currentRootFrameNode = null;
            currentFrameNode = null;
            threadTotalTime = 0;
            return;
        }

        // frame
        if ("frame".equals(name)) { //$NON-NLS-1$
            ITreeNode parrentNode = currentFrameNode.getParent();
            if (parrentNode instanceof CallTreeNode) {
                CallTreeNode parentFrameNode = (CallTreeNode) parrentNode;
                long selfTime = parentFrameNode.getSelfTime()
                        - currentFrameNode.getTotalTime();
                parentFrameNode.setSelfTime(selfTime);
                storeMethods(currentFrameNode);
                currentFrameNode = parentFrameNode;
            } else {
                storeMethods(currentFrameNode);

                currentRootFrameNode.setTotalTime(frameTotalTime);
                currentRootFrameNode = null;
                currentFrameNode = null;
                threadTotalTime += frameTotalTime;
                frameTotalTime = 0;
            }
        }
    }

    /**
     * Gets the profile info.
     * 
     * @return The profile info
     */
    public IProfileInfo getProfileInfo() {
        return info;
    }

    /**
     * Parses the frame attribute.
     * 
     * @param attributes
     *            The frame attribute
     */
    private void parseFrame(Attributes attributes) {
        String methodName = attributes.getValue("name"); //$NON-NLS-1$
        int count = Integer.parseInt(attributes.getValue("cnt")); //$NON-NLS-1$
        long time = Long.parseLong(attributes.getValue("time")); //$NON-NLS-1$

        if (currentRootFrameNode == null) {
            currentRootFrameNode = (CallTreeNode) currentCallTreeThreadNode
                    .getChild(methodName);
            if (currentRootFrameNode == null) {
                currentRootFrameNode = new CallTreeNode(cpuModel, methodName,
                        time, count, currentCallTreeThreadNode);
                currentCallTreeThreadNode.addChild(currentRootFrameNode);
            } else {
                currentRootFrameNode.setTotalTime(time);
                currentRootFrameNode.setInvocationCount(count);
            }
            currentFrameNode = currentRootFrameNode;
            frameTotalTime += time;
        } else {
            CallTreeNode childFrameNode = currentFrameNode.getChild(methodName);
            if (childFrameNode == null) {
                childFrameNode = new CallTreeNode(cpuModel, methodName, time,
                        count, currentFrameNode, currentCallTreeThreadNode);
                currentFrameNode.addChild(childFrameNode);
            } else {
                childFrameNode.setTotalTime(time);
                childFrameNode.setInvocationCount(count);
            }
            currentFrameNode = childFrameNode;
        }
        currentFrameNode.setSelfTime(time);
    }

    /**
     * Stores the methods into cpuModel.
     * 
     * @param frame
     *            The frame
     */
    private void storeMethods(CallTreeNode frame) {
        String methodName = frame.getName();
        MethodNode method = (MethodNode) currentHotSpotThreadNode
                .getChild(methodName);

        // get time and count of frame
        long time = frame.getSelfTime();
        int count = 0;
        try {
            count = Integer.valueOf(frame.getInvocationCount());
        } catch (NumberFormatException e) {
            Activator.log(IStatus.ERROR, ILLEGAL_NUMBER_FORMAT, e);
            return;
        }

        // increment the time and count
        if (method == null) {
            method = new MethodNode(cpuModel, methodName,
                    currentHotSpotThreadNode);
            currentHotSpotThreadNode.addChild(method);
        }
        method.incrementTime(time);
        method.incrementCount(count);
    }
}
