/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvm;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.core.ITerminatedJvm;
import org.fusesource.ide.jvmmonitor.core.ISnapshot.SnapshotType;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The label provider for JVMs tree viewer.
 */
public class JvmTreeLabelProvider implements IStyledLabelProvider,
        ISharedImages {

    /** The local host image. */
    private Image localHostImage;

    /** The remote host image. */
    private Image remoteHostImage;

    /** The connected JVM image. */
    private Image connectedJvmImage;

    /** The disconnected JVM image. */
    private Image disconnectedJvmImage;

    /** The terminated JVM image. */
    private Image terminatedJvmImage;

    /** The thread dump image. */
    private Image threadDumpImage;

    /** The hprof dump image. */
    private Image hprofDumpImage;

    /** The heap dump image. */
    private Image heapDumpImage;

    /** The CPU dump image. */
    private Image cpuDumpImage;

    /*
     * @see IStyledLabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {

        // host image
        if (element instanceof IHost) {
            if (IHost.LOCALHOST.equals(((IHost) element).getName())) {
                return getLocalHostImage();
            }
            return getRemoteHostImage();
        }

        // JVM image
        if (element instanceof IActiveJvm) {
            if (((IActiveJvm) element).isConnected()) {
                return getConnectedJvmImage();
            }
            return getDisconnectedJvmImage();
        } else if (element instanceof IJvm) {
            return getTerminatedJvmImage();
        }

        // snapshot image
        if (element instanceof ISnapshot) {
            if (((ISnapshot) element).getType() == SnapshotType.Thread) {
                return getThreadDumpImage();
            } else if (((ISnapshot) element).getType() == SnapshotType.Hprof) {
                return getHprofDumpImage();
            } else if (((ISnapshot) element).getType() == SnapshotType.Heap) {
                return getHeapDumpImage();
            } else if (((ISnapshot) element).getType() == SnapshotType.Cpu) {
                return getCpuDumpImage();
            }
        }

        return null;
    }

    /*
     * @see IStyledLabelProvider#getStyledText(Object)
     */
    @Override
    public StyledString getStyledText(Object element) {
        StyledString text = new StyledString();
        if (element instanceof IJvm) {
            String prefix = ""; //$NON-NLS-1$
            String mainClass = ((IJvm) element).getMainClass();
            String suffix = getIdInicator((IJvm) element);
            if (element instanceof ITerminatedJvm) {
                prefix = "<terminated>"; //$NON-NLS-1$
            }
            text.append(prefix).append(mainClass).append(suffix);
            text.setStyle(prefix.length() + mainClass.length(),
                    suffix.length(), StyledString.DECORATIONS_STYLER);
        } else if (element instanceof ISnapshot) {
            String fileName = ((ISnapshot) element).getFileStore().getName();
            text.append(fileName);
            String date = ((ISnapshot) element).getTimeStamp();
            if (date != null) {
                text.append(" (").append(date).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            text.append(element.toString());
        }
        return text;
    }

    /*
     * @see IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    @Override
    public void addListener(ILabelProviderListener listener) {
        // do nothing
    }

    /*
     * @see IBaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (localHostImage != null) {
            localHostImage.dispose();
        }
        if (remoteHostImage != null) {
            remoteHostImage.dispose();
        }
        if (connectedJvmImage != null) {
            connectedJvmImage.dispose();
        }
        if (disconnectedJvmImage != null) {
            disconnectedJvmImage.dispose();
        }
        if (terminatedJvmImage != null) {
            terminatedJvmImage.dispose();
        }
        if (threadDumpImage != null) {
            threadDumpImage.dispose();
        }
        if (heapDumpImage != null) {
            heapDumpImage.dispose();
        }
        if (hprofDumpImage != null) {
            hprofDumpImage.dispose();
        }
        if (cpuDumpImage != null) {
            cpuDumpImage.dispose();
        }
    }

    /*
     * @see IBaseLabelProvider#isLabelProperty(Object, String)
     */
    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /*
     * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    @Override
    public void removeListener(ILabelProviderListener listener) {
        // do nothing
    }

    /**
     * Gets the local host image.
     * 
     * @return The image
     */
    private Image getLocalHostImage() {
        if (localHostImage == null || localHostImage.isDisposed()) {
            localHostImage = Activator.getImageDescriptor(LOCAL_HOST_IMG_PATH)
                    .createImage();
        }
        return localHostImage;
    }

    /**
     * Gets the remote host image.
     * 
     * @return The image
     */
    private Image getRemoteHostImage() {
        if (remoteHostImage == null || remoteHostImage.isDisposed()) {
            remoteHostImage = Activator
                    .getImageDescriptor(REMOTE_HOST_IMG_PATH).createImage();
        }
        return remoteHostImage;
    }

    /**
     * Gets the connected JVM image.
     * 
     * @return The image
     */
    private Image getConnectedJvmImage() {
        if (connectedJvmImage == null || connectedJvmImage.isDisposed()) {
            connectedJvmImage = Activator.getImageDescriptor(
                    CONNECTED_JVM_IMG_PATH).createImage();
        }
        return connectedJvmImage;
    }

    /**
     * Gets the disconnected JVM image.
     * 
     * @return The image
     */
    private Image getDisconnectedJvmImage() {
        if (disconnectedJvmImage == null || disconnectedJvmImage.isDisposed()) {
            disconnectedJvmImage = Activator.getImageDescriptor(
                    DISCONNECTED_JVM_IMG_PATH).createImage();
        }
        return disconnectedJvmImage;
    }

    /**
     * Gets the terminated JVM image.
     * 
     * @return The image
     */
    private Image getTerminatedJvmImage() {
        if (terminatedJvmImage == null || terminatedJvmImage.isDisposed()) {
            terminatedJvmImage = Activator.getImageDescriptor(
                    TERMINATED_JVM_IMG_PATH).createImage();
        }
        return terminatedJvmImage;
    }

    /**
     * Gets the thread dump image.
     * 
     * @return The image
     */
    private Image getThreadDumpImage() {
        if (threadDumpImage == null || threadDumpImage.isDisposed()) {
            threadDumpImage = Activator
                    .getImageDescriptor(THREAD_DUMP_IMG_PATH).createImage();
        }
        return threadDumpImage;
    }

    /**
     * Gets the hprof dump image.
     * 
     * @return The image
     */
    private Image getHprofDumpImage() {
        if (hprofDumpImage == null || hprofDumpImage.isDisposed()) {
            hprofDumpImage = Activator.getImageDescriptor(HPROF_DUMP_IMG_PATH)
                    .createImage();
        }
        return hprofDumpImage;
    }

    /**
     * Gets the heap dump image.
     * 
     * @return The image
     */
    private Image getHeapDumpImage() {
        if (heapDumpImage == null || heapDumpImage.isDisposed()) {
            heapDumpImage = Activator.getImageDescriptor(HEAP_DUMP_IMG_PATH)
                    .createImage();
        }
        return heapDumpImage;
    }

    /**
     * Gets the CPU dump image.
     * 
     * @return The image
     */
    private Image getCpuDumpImage() {
        if (cpuDumpImage == null || cpuDumpImage.isDisposed()) {
            cpuDumpImage = Activator.getImageDescriptor(CPU_DUMP_IMG_PATH)
                    .createImage();
        }
        return cpuDumpImage;
    }

    /**
     * Gets the ID indicator. e.g. [PID: 1234]
     * 
     * @param jvm
     *            The JVM
     * @return the ID
     */
    private String getIdInicator(IJvm jvm) {
        int pid = jvm.getPid();
        int port = jvm.getPort();
        StringBuffer buffer = new StringBuffer();
        if (pid != -1) {
            buffer.append(" [PID: ").append(pid).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (port != -1) {
            buffer.append(" [Port: ").append(port).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return buffer.toString();
    }
}
