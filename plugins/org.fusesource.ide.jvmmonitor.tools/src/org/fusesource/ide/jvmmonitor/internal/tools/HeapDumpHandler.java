/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.jvmmonitor.core.IHeapDumpHandler;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.tools.Activator;


/**
 * The heap dump handler that contributes to the extension point
 * <tt>org.fusesource.ide.jvmmonitor.core.heapDumpHandler</tt>.
 */
public class HeapDumpHandler implements IHeapDumpHandler {

    /** The buffer size to transfer heap dump data from target JVM to eclipse. */
    private static final int BUFFER_SIZE = 2048;

    /*
     * @see IHeapDumpHandler#dumpHeap(int, boolean)
     */
    @Override
    public String dumpHeap(int pid, boolean isLive) throws JvmCoreException {
        Tools tools = Tools.getInstance();

        Object virtualMachine = null;
        try {
            virtualMachine = tools.invokeAttach(pid);
            return getHeapHistogram(virtualMachine, isLive);
        } finally {
            if (virtualMachine != null) {
                try {
                    tools.invokeDetach(virtualMachine);
                } catch (JvmCoreException e) {
                    // ignore
                }
            }
        }
    }

    /*
     * @see IHeapDumpHandler#getMaxClassesNumber()
     */
    @Override
    public int getMaxClassesNumber() {
        return Activator.getDefault().getPreferenceStore()
                .getInt(IConstants.MAX_CLASSES_NUMBER);
    }

    /**
     * Gets the heap histogram from target JVM.
     * <p>
     * e.g.
     * 
     * <pre>
     *  num     #instances         #bytes  class name
     * ----------------------------------------------
     *    1:         18329        2104376  &lt;constMethodKlass&gt;
     *    2:         18329        1479904  &lt;methodKlass&gt;
     *    3:          2518        1051520  [B
     *    4:         11664         989856  [C
     *    5:         11547         277128  java.lang.String
     * </pre>
     * 
     * @param virtualMachine
     *            The virtual machine
     * @param isLive
     *            True to dump only live objects
     * @return The heap histogram
     * @throws JvmCoreException
     */
    private String getHeapHistogram(Object virtualMachine, boolean isLive)
            throws JvmCoreException {
        InputStream in = Tools.getInstance().invokeHeapHisto(virtualMachine,
                isLive);

        byte[] bytes = new byte[BUFFER_SIZE];
        int length;
        StringBuilder builder = new StringBuilder();
        try {
            while ((length = in.read(bytes)) != -1) {
                String string = new String(bytes, 0, length, IConstants.UTF8);
                builder.append(string);
            }
        } catch (UnsupportedEncodingException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.charsetNotSupportedMsg, e);
        } catch (IOException e) {
            throw new JvmCoreException(IStatus.ERROR,
                    Messages.readInputStreamFailedMsg, e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return builder.toString();
    }
}
