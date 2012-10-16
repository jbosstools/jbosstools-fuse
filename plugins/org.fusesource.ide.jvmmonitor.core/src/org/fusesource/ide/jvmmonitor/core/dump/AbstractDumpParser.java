package org.fusesource.ide.jvmmonitor.core.dump;

import java.io.File;

import javax.xml.parsers.SAXParser;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The dump parser.
 */
abstract class AbstractDumpParser {

    /** The dump file. */
    protected File file;

    /** The profile info. */
    protected IProfileInfo info;

    /** The progress monitor. */
    protected IProgressMonitor monitor;

    /** The SAX parser. */
    protected SAXParser parser;

    /**
     * The constructor.
     * 
     * @param monitor
     *            The progress monitor
     */
    protected AbstractDumpParser(IProgressMonitor monitor) {
        Assert.isNotNull(monitor);
        this.monitor = monitor;
    }

    /**
     * Gets the profile info.
     * 
     * @return The profile info, or <tt>null</tt> if not available
     */
    public IProfileInfo getProfileInfo() {
        return info;
    }
}
