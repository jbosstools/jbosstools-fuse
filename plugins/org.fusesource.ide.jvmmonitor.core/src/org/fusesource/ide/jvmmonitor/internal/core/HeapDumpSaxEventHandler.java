/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;
import org.fusesource.ide.jvmmonitor.core.dump.IProfileInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX event handler for heap dump.
 */
public class HeapDumpSaxEventHandler extends DefaultHandler {

    /** The progress monitor */
    private IProgressMonitor monitor;

    /** The heap list elements. */
    private List<IHeapElement> heapListElements;

    /** The profile info. */
    private IProfileInfo info;

    /**
     * The constructor.
     * 
     * @param heapListElements
     *            The heap list elements
     * @param monitor
     *            The progress monitor
     */
    public HeapDumpSaxEventHandler(List<IHeapElement> heapListElements,
            IProgressMonitor monitor) {
        this.monitor = monitor;
        this.heapListElements = heapListElements;
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

        // memory-profile
        if ("heap-profile".equals(name)) { //$NON-NLS-1$
            String date = attributes.getValue("date"); //$NON-NLS-1$
            String runtime = attributes.getValue("runtime"); //$NON-NLS-1$
            String mainClass = attributes.getValue("mainClass"); //$NON-NLS-1$
            String arguments = attributes.getValue("arguments"); //$NON-NLS-1$
            String comments = attributes.getValue("comments"); //$NON-NLS-1$
            info = new ProfileInfo(date, runtime, mainClass, arguments,
                    comments);
        }

        // class
        if ("class".equals(name)) { //$NON-NLS-1$
            String className = attributes.getValue("name"); //$NON-NLS-1$
            String size = attributes.getValue("size"); //$NON-NLS-1$
            String count = attributes.getValue("count"); //$NON-NLS-1$
            String baseSize = attributes.getValue("baseSize"); //$NON-NLS-1$
            HeapElement element = new HeapElement(className,
                    Long.parseLong(size), Long.parseLong(count),
                    Long.parseLong(baseSize));
            heapListElements.add(element);
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
}