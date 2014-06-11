/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core.cpu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ITreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.CpuModelEvent.CpuModelState;


/**
 * The model that contains CPU profiling result data. The data is given with
 * either an input stream or a file using
 * {@link org.fusesource.ide.jvmmonitor.core.dump.CpuDumpParser}.
 */
public class CpuModel implements ICpuModel {

    /** The agent version. */
    static final String AGENT_VERSION = "3.7.0"; //$NON-NLS-1$

    /** The call tree threads. */
    private List<ThreadNode<CallTreeNode>> callTreeThreads;

    /** The hot spot threads. */
    private List<ThreadNode<MethodNode>> hotSpotThreads;

    /** The callers. */
    private List<MethodNode> callers;

    /** The callees. */
    private List<MethodNode> callees;

    /** The CPU model change listeners. */
    private List<ICpuModelChangeListener> listeners;

    /** The focus target. */
    private CallTreeNode focusTarget;

    /** The focused hot spot root nodes. */
    private Map<String, MethodNode> focusedHotSpotRoots;

    /** The target method for callers/callees. */
    private IMethodNode callersCalleesTarget;

    /** The max value of total time in all method invocations. */
    private long maxTotalTime;

    /** The max value of self time in all method invocations. */
    private long maxSelfTime;

    /** The max value of method invocation count in all method invocations. */
    private int maxInvocationCount;

    /**
     * The constructor.
     */
    public CpuModel() {
        callTreeThreads = new CopyOnWriteArrayList<ThreadNode<CallTreeNode>>();
        hotSpotThreads = new CopyOnWriteArrayList<ThreadNode<MethodNode>>();
        callers = new ArrayList<MethodNode>();
        callees = new ArrayList<MethodNode>();
        focusedHotSpotRoots = new HashMap<String, MethodNode>();
        listeners = new ArrayList<ICpuModelChangeListener>();
    }

    /*
     * @see ICpuModel#getCallTreeRoots()
     */
    @Override
    public ITreeNode[] getCallTreeRoots() {
        if (focusTarget == null) {
            return callTreeThreads.toArray(new ThreadNode[0]);
        }
        return new ICallTreeNode[] { focusTarget };
    }

    /*
     * @see ICpuModel#getHotSpotRoots()
     */
    @Override
    public ITreeNode[] getHotSpotRoots() {
        if (focusTarget == null) {
            return hotSpotThreads.toArray(new ThreadNode[0]);
        }
        return focusedHotSpotRoots.values().toArray(new IMethodNode[0]);
    }

    /*
     * @see ICpuModel#getCallers()
     */
    @Override
    public IMethodNode[] getCallers() {
        return callers.toArray(new IMethodNode[0]);
    }

    /*
     * @see ICpuModel#getCallees()
     */
    @Override
    public IMethodNode[] getCallees() {
        return callees.toArray(new IMethodNode[0]);
    }

    /*
     * @see ICpuModel#setCallersCalleesTarget(IMethodNode)
     */
    @Override
    public void setCallersCalleesTarget(IMethodNode targetMethod) {
        callers.clear();
        callees.clear();
        this.callersCalleesTarget = targetMethod;

        String thread = getThread(targetMethod);

        List<CallTreeNode> frameRootNodes = getFrameRootNodes(thread);
        if (frameRootNodes.isEmpty() || targetMethod == null) {
            notifyModelChanged(new CpuModelEvent(
                    CpuModelState.CallersCalleesTargetChanged));
            return;
        }

        List<String> callerNames = new ArrayList<String>();
        List<String> calleeNames = new ArrayList<String>();
        refreshCallersCallees(callerNames, calleeNames, frameRootNodes,
                targetMethod.getName());

        List<MethodNode> methodNodes = getMethodNodes(thread);

        for (MethodNode methodNode : methodNodes) {
            if (callerNames.contains(methodNode.getName())) {
                callers.add(methodNode);
            }
            if (calleeNames.contains(methodNode.getName())) {
                callees.add(methodNode);
            }
        }

        notifyModelChanged(new CpuModelEvent(
                CpuModelState.CallersCalleesTargetChanged));
    }

    /*
     * @see ICpuModel#getCallersCalleesTarget()
     */
    @Override
    public IMethodNode getCallersCalleesTarget() {
        return callersCalleesTarget;
    }

    /*
     * @see ICpuModel#setFocusTarget(ICallTreeNode)
     */
    @Override
    public void setFocusTarget(ICallTreeNode node) {
        focusTarget = (CallTreeNode) node;
        if (focusTarget != null) {
            focusedHotSpotRoots.clear();
            addFocusedHotSpotNodes(focusTarget);
        }
        setCallersCalleesTarget(null);
        notifyModelChanged(new CpuModelEvent(CpuModelState.FocusedMethodChanged));
    }

    /*
     * @see ICpuModel#getFocusTarget()
     */
    @Override
    public ICallTreeNode getFocusTarget() {
        return focusTarget;
    }

    /*
     * @see ICpuModel#addModelChangeListener(ICpuModelChangeListener)
     */
    @Override
    public void addModelChangeListener(ICpuModelChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /*
     * @see ICpuModel#removeModelChangeListener(ICpuModelChangeListener)
     */
    @Override
    public void removeModelChangeListener(ICpuModelChangeListener listener) {
        listeners.remove(listener);
    }

    /*
     * @see ICpuModel#refreshMaxValues()
     */
    @Override
    public void refreshMaxValues() {
        maxTotalTime = 0;
        maxSelfTime = 0;
        maxInvocationCount = 0;

        for (ThreadNode<MethodNode> rootNode : hotSpotThreads) {
            long totalTime = rootNode.getTotalTime();
            if (totalTime > maxTotalTime) {
                maxTotalTime = totalTime;
            }

            for (ITreeNode node : rootNode.getChildren()) {
                int count = ((MethodNode) node).getInvocationCount();
                if (count > maxInvocationCount) {
                    maxInvocationCount = count;
                }

                long selfTime = ((MethodNode) node).getSelfTime();
                if (selfTime > maxSelfTime) {
                    maxSelfTime = selfTime;
                }
            }
        }
    }

    /*
     * @see ICpuModel#getMaxTotalTime()
     */
    @Override
    public long getMaxTotalTime() {
        return maxTotalTime;
    }

    /*
     * @see ICpuModel#getMaxSelfTime()
     */
    @Override
    public long getMaxSelfTime() {
        return maxSelfTime;
    }

    /*
     * @see ICpuModel#getMaxInvocationCount()
     */
    @Override
    public long getMaxInvocationCount() {
        return maxInvocationCount;
    }

    /**
     * Gets the thread on call tree corresponding to the given thread name.
     * 
     * @param threadName
     *            The thread name
     * @return The thread on call tree, or <tt>null</tt> if not found
     */
    public ThreadNode<CallTreeNode> getCallTreeThread(String threadName) {
        for (ThreadNode<CallTreeNode> treeNode : callTreeThreads) {
            if (treeNode.getName().equals(threadName)) {
                return treeNode;
            }
        }
        return null;
    }

    /**
     * Adds the call tree thread node.
     * 
     * @param thread
     *            The thread
     */
    public void addCallTreeThread(ThreadNode<CallTreeNode> thread) {
        if (!callTreeThreads.contains(thread)) {
            callTreeThreads.add(thread);
        }
    }

    /**
     * Gets the thread on hot spot corresponding to the given thread name.
     * 
     * @param threadName
     *            The thread name
     * @return The thread on hot spot, or <tt>null</tt> if not found
     */
    public ThreadNode<MethodNode> getHotSpotThread(String threadName) {
        for (ThreadNode<MethodNode> treeNode : hotSpotThreads) {
            if (treeNode.getName().equals(threadName)) {
                return treeNode;
            }
        }
        return null;
    }

    /**
     * Adds the hot spot thread node.
     * 
     * @param thread
     *            The thread
     */
    public void addHotSpotThread(ThreadNode<MethodNode> thread) {
        if (!hotSpotThreads.contains(thread)) {
            hotSpotThreads.add(thread);
        }
    }

    /**
     * Removes all nodes in this model.
     */
    public void removeAll() {
        callTreeThreads.clear();
        hotSpotThreads.clear();
        callers.clear();
        callees.clear();
        focusedHotSpotRoots.clear();
    }

    /**
     * Clears the attributes for each node without removing nodes.
     */
    protected void clear() {
        for (ThreadNode<CallTreeNode> treeNode : callTreeThreads) {
            treeNode.clear();
        }
        for (ThreadNode<MethodNode> treeNode : hotSpotThreads) {
            treeNode.clear();
        }
    }

    /**
     * Notifies that the CPU model has been changed.
     * 
     * @param event
     *            The CPU model change event
     */
    protected void notifyModelChanged(CpuModelEvent event) {
        for (ICpuModelChangeListener listener : listeners) {
            listener.modelChanged(event);
        }
    }

    /**
     * Gets the CPU dump string.
     * 
     * @param runtime
     *            The runtime
     * @param mainClass
     *            The main class
     * @param arguments
     *            The arguments
     * @return The CPU dump string
     */
    protected String getCpuDumpString(String runtime, String mainClass,
            String arguments) {

        // get date and time
        Date currentDate = new Date();
        String date = new SimpleDateFormat("yyyy/MM/dd").format(currentDate); //$NON-NLS-1$
        String time = new SimpleDateFormat("HH:mm:ss").format(currentDate); //$NON-NLS-1$

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //$NON-NLS-1$
        buffer.append("<?JvmMonitor version=\""); //$NON-NLS-1$
        buffer.append(AGENT_VERSION);
        buffer.append("\"?>\n"); //$NON-NLS-1$

        buffer.append("<cpu-profile date=\"").append(date).append(' ') //$NON-NLS-1$
                .append(time).append("\" "); //$NON-NLS-1$
        buffer.append("runtime=\"").append(runtime).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("mainClass=\"").append(mainClass).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("arguments=\"").append(arguments).append("\">\n"); //$NON-NLS-1$ //$NON-NLS-2$

        for (ThreadNode<CallTreeNode> node : callTreeThreads) {
            buffer.append("\t<thread name=\"").append(node.getName()).append("\">\n"); //$NON-NLS-1$ //$NON-NLS-2$
            for (CallTreeNode frameNode : node.getChildren()) {
                frameNode.dump(buffer, 2);
            }
            buffer.append("\t</thread>\n"); //$NON-NLS-1$
        }
        buffer.append("</cpu-profile>"); //$NON-NLS-1$

        return buffer.toString();
    }

    /**
     * Refreshes the callers and callees.
     * 
     * @param callerNames
     *            The caller names
     * @param calleeNames
     *            The callee names
     * @param frameRootNodes
     *            The frame root nodes
     * @param method
     *            The method
     */
    private void refreshCallersCallees(List<String> callerNames,
            List<String> calleeNames, List<CallTreeNode> frameRootNodes,
            String method) {
        for (CallTreeNode frameNode : frameRootNodes) {
            String parentFrameName = frameNode.getParent().getName();
            String frameName = frameNode.getName();
            if (parentFrameName.equals(method)) {
                calleeNames.add(frameName);
            }
            if (frameName.equals(method)) {
                callerNames.add(parentFrameName);
            }
            refreshCallersCallees(callerNames, calleeNames,
                    frameNode.getChildren(), method);
        }
    }

    /**
     * Adds the focused hot spot nodes.
     * 
     * @param frame
     *            The frame node
     */
    private void addFocusedHotSpotNodes(ICallTreeNode frame) {
        String methodName = frame.getName();
        MethodNode node = new MethodNode(this, methodName, null);
        node.incrementCount(frame.getInvocationCount());
        node.incrementTime(frame.getSelfTime());
        focusedHotSpotRoots.put(methodName, node);

        for (CallTreeNode child : ((CallTreeNode) frame).getChildren()) {
            methodName = child.getName();
            if (focusedHotSpotRoots.containsKey(methodName)) {
                node = focusedHotSpotRoots.get(methodName);
                node.incrementCount(child.getInvocationCount());
                node.incrementTime(child.getSelfTime());
            } else {
                node = new MethodNode(this, methodName, null);
                node.incrementCount(child.getInvocationCount());
                node.incrementTime(child.getSelfTime());
                focusedHotSpotRoots.put(methodName, node);
            }
            addFocusedHotSpotNodes(child);
        }
    }

    /**
     * Gets the frame root nodes.
     * 
     * @param thread
     *            The target thread for callers/callees
     * @return The frame root nodes
     */
    private List<CallTreeNode> getFrameRootNodes(String thread) {
        if (thread == null) {
            return new ArrayList<CallTreeNode>();
        }

        List<CallTreeNode> frameRootNodes = null;
        if (focusTarget == null) {
            for (ThreadNode<CallTreeNode> treeNode : callTreeThreads) {
                ThreadNode<CallTreeNode> threadNode = treeNode;
                if (threadNode.getName().equals(thread)) {
                    frameRootNodes = threadNode.getChildren();
                    break;
                }
            }
            if (frameRootNodes == null) {
                throw new IllegalArgumentException("unknown thread: " + thread); //$NON-NLS-1$
            }
        } else {
            String focusedThread = null;
            ITreeNode node = focusTarget;
            if (node != null) {
                while (node.getParent() != null) {
                    node = node.getParent();
                }
                focusedThread = node.getName();
            }
            frameRootNodes = new ArrayList<CallTreeNode>();
            if (!thread.equals(focusedThread)) {
                return frameRootNodes;
            }
            frameRootNodes.add(focusTarget);
        }
        return frameRootNodes;
    }

    /**
     * Gets the method nodes.
     * 
     * @param thread
     *            The target thread for callers/callees
     * @return The method nodes
     */
    private List<MethodNode> getMethodNodes(String thread) {
        List<MethodNode> methodNodes = null;
        if (focusTarget == null) {
            for (ThreadNode<MethodNode> treeNode : hotSpotThreads) {
                ThreadNode<MethodNode> threadNode = treeNode;
                if (threadNode.getName().equals(thread)) {
                    methodNodes = threadNode.getChildren();
                    break;
                }
            }

            if (methodNodes == null) {
                throw new IllegalArgumentException("unknown thread: " + thread); //$NON-NLS-1$
            }
        } else {
            methodNodes = new ArrayList<MethodNode>();
            for (MethodNode methodNode : focusedHotSpotRoots.values()) {
                methodNodes.add(methodNode);
            }
        }

        return methodNodes;
    }

    /**
     * Gets the thread corresponding to the given node.
     * 
     * @param callersCalleesMethod
     *            The tree node
     * @return The thread
     */
    private String getThread(IMethodNode callersCalleesMethod) {
        if (focusTarget != null) {
            return focusTarget.getThread();
        }

        if (callersCalleesMethod != null) {
            return callersCalleesMethod.getThread();
        }
        return null;
    }
}
