/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import org.fusesource.ide.jvmmonitor.internal.agent.asm.Label;
import org.fusesource.ide.jvmmonitor.internal.agent.asm.MethodAdapter;
import org.fusesource.ide.jvmmonitor.internal.agent.asm.MethodVisitor;
import org.fusesource.ide.jvmmonitor.internal.agent.asm.Opcodes;

/**
 * The method visitor.
 */
public class MethodVisitorImpl extends MethodAdapter {

    /** The class name */
    private String className;

    /** The method name */
    private String methodName;

    /** The state indicating if this is the class initialization method. */
    private boolean isClinit;

    /**
     * The constructor.
     * 
     * @param methodVisitor
     *            The method visitor
     * @param className
     *            The class name
     * @param methodName
     *            The method name
     */
    public MethodVisitorImpl(MethodVisitor methodVisitor, String className,
            String methodName) {
        super(methodVisitor);
        this.className = className;
        this.methodName = methodName;
        isClinit = methodName.startsWith(Constants.METHOD_CLINIT);
    }

    /*
     * @see MethodAdapter#visitCode()
     */
    @Override
    public void visitCode() {
        instrumentMethodInvocation(Constants.METHOD_STEP_INTO, className,
                methodName);

        super.visitCode();
    }

    /*
     * @see MethodAdapter#visitInsn(int)
     */
    @Override
    public void visitInsn(int opcode) {

        // the JVM opcode to return method
        if ((Opcodes.IRETURN <= opcode && opcode <= Opcodes.RETURN)
                || Opcodes.ATHROW == opcode) {
            instrumentMethodInvocation(Constants.METHOD_STEP_RETURN, className,
                    methodName);
        }

        super.visitInsn(opcode);
    }

    /*
     * @see MethodAdapter#visitTryCatchBlock(Label, Label, Label, String)
     */
    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler,
            String type) {
        super.visitTryCatchBlock(start, end, handler, type);

        // set exception
        handler.info = type;
    }

    /*
     * @see MethodAdapter#visitLabel(Label)
     */
    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);

        if (label.info != null) {
            instrumentMethodInvocation(Constants.METHOD_DROP_TO_FRAME,
                    className, methodName, (String) label.info);
        }
    }

    /**
     * Instruments the method invocation.
     * 
     * @param name
     *            The method name
     * @param args
     *            The method arguments
     */
    private void instrumentMethodInvocation(String name, String... args) {
        if (isClinit) {
            return;
        }

        // get descriptor
        String desc = null;
        if (args.length == 2) {
            desc = Constants.DESC_STRING_STRING;
        } else if (args.length == 3) {
            desc = Constants.DESC_STRING_STRING_STRING;
        } else {
            return;
        }

        // instrument
        for (String arg : args) {
            visitLdcInsn(arg);
        }
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                Constants.CLASS_CPU_PROFILER, name, desc);
    }
}
