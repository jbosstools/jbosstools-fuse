/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core.cpu;

import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;

/**
 * The abstract method node.
 */
abstract public class AbstractMethodNode implements IMethodNode {

    /** The sum of method invocation time. */
    protected long selfTime;

    /** The cpuModel. */
    protected ICpuModel cpuModel;

    /** The qualified method name. */
    protected String qualifiedMethodName;

    /** The method name. */
    private String methodName;

    /** The thread node. */
    protected ThreadNode<? extends IMethodNode> threadNode;

    /** The sum of method invocation count. */
    protected int invocationCount;

    /**
     * The constructor.
     * 
     * @param cpuModel
     *            The cpuModel
     * @param name
     *            The qualified method name
     * @param thread
     *            The thread node
     */
    public AbstractMethodNode(ICpuModel cpuModel, String name,
            ThreadNode<? extends IMethodNode> thread) {
        this.cpuModel = cpuModel;
        qualifiedMethodName = name;
        threadNode = thread;

        // get method name
        int index = qualifiedMethodName.indexOf('(');
        String methodNameWithoutArguments;
        if (index != -1) {
            methodNameWithoutArguments = qualifiedMethodName
                    .substring(0, index);
        } else {
            methodNameWithoutArguments = qualifiedMethodName;
        }
        index = methodNameWithoutArguments.lastIndexOf('.');
        methodName = methodNameWithoutArguments.substring(index + 1);
    }

    /*
     * @see ITreeNode#getName()
     */
    @Override
    public String getName() {
        return qualifiedMethodName;
    }

    /*
     * @see IMethodNode#getMethodName()
     */
    @Override
    public String getNonqualifiedName() {
        return methodName;
    }

    /*
     * @see IMethodNode#getThread()
     */
    @Override
    public String getThread() {
        return threadNode.getName();
    }

    /*
     * @see IMethodNode#getSelfTimeInPercentage()
     */
    @Override
    public double getSelfTimeInPercentage() {
        double rootTotalTime = getRootTotalTime();
        if (rootTotalTime == 0) {
            return 0;
        }
        return selfTime / getRootTotalTime() * 100;
    }

    /*
     * @see IMethodNode#getSelfTime()
     */
    @Override
    public long getSelfTime() {
        return selfTime;
    }

    /*
     * @see IMethodNode#getInvocationCount()
     */
    @Override
    public int getInvocationCount() {
        return invocationCount;
    }

    /*
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode() | qualifiedMethodName.hashCode();
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return qualifiedMethodName;
    }

    /**
     * Clears the attributes and those of child nodes recursively.
     */
    abstract public void clear();

    /**
     * Gets the total time of root node.
     * 
     * @return The total time of root node
     */
    protected double getRootTotalTime() {
        CallTreeNode focusedNode = (CallTreeNode) cpuModel.getFocusTarget();
        if (focusedNode != null) {
            return focusedNode.getTotalTime();
        }
        return threadNode.getTotalTime();
    }
}
