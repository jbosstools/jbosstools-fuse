package org.fusesource.ide.jvmmonitor.core.cpu;

/**
 * The CPU model.
 */
public interface ICpuModel {

    /**
     * Gets the call tree roots that can be either thread nodes or focused call
     * tree node.
     * 
     * @return The call tree roots
     */
    ITreeNode[] getCallTreeRoots();

    /**
     * Gets the hot spot roots that can be either thread nodes or method nodes
     * of thread that has focused call tree node.
     * 
     * @return The hot spot roots
     */
    ITreeNode[] getHotSpotRoots();

    /**
     * Gets the callers.
     * 
     * @return The callers
     */
    IMethodNode[] getCallers();

    /**
     * Gets the callees.
     * 
     * @return The callees
     */
    IMethodNode[] getCallees();

    /**
     * Sets the callers/callees target.
     * 
     * @param targetMethod
     *            The callers/callees target, or <tt>null</tt> to clear
     *            callers/callees
     */
    void setCallersCalleesTarget(IMethodNode targetMethod);

    /**
     * Gets the callers/callees target.
     * 
     * @return The callers/callees target, or <tt>null</tt> if not found
     */
    IMethodNode getCallersCalleesTarget();

    /**
     * Sets the focus target.
     * 
     * @param node
     *            The focus target, or <tt>null</tt> to clear the focus
     */
    void setFocusTarget(ICallTreeNode node);

    /**
     * Gets the focus target.
     * 
     * @return The focus target, or <tt>null</tt> if not focused
     */
    ICallTreeNode getFocusTarget();

    /**
     * Adds the CPU model change listener.
     * 
     * @param listener
     *            The CPU model change listener
     */
    void addModelChangeListener(ICpuModelChangeListener listener);

    /**
     * Removes the CPU model change listener.
     * 
     * @param listener
     *            The CPU model change listener
     */
    void removeModelChangeListener(ICpuModelChangeListener listener);

    /**
     * Refreshes the max values.
     */
    void refreshMaxValues();

    /**
     * Gets the max total time.
     * 
     * @return The max total time
     */
    long getMaxTotalTime();

    /**
     * Gets the max self time.
     * 
     * @return The max self time
     */
    long getMaxSelfTime();

    /**
     * Gets the max invocation count.
     * 
     * @return The max invocation count
     */
    long getMaxInvocationCount();
}