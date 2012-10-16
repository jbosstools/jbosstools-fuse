/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import org.fusesource.ide.jvmmonitor.core.dump.IProfileInfo;

/**
 * The profile info.
 */
public class ProfileInfo implements IProfileInfo {

    /** The date. */
    private String date;

    /** The runtime. */
    private String runtime;

    /** The main class. */
    private String mainClass;

    /** The arguments. */
    private String arguments;

    /** The comments. */
    private String comments;

    /**
     * The constructor.
     * 
     * @param date
     *            The date
     * @param runtime
     *            The runtime
     * @param mainClass
     *            The main class
     * @param arguments
     *            The arguments
     * @param comments
     *            The comments
     */
    public ProfileInfo(String date, String runtime, String mainClass,
            String arguments, String comments) {
        this.date = date;
        this.runtime = runtime;
        this.mainClass = mainClass;
        this.arguments = arguments;
        this.comments = comments;
    }

    /*
     * @see IProfileInfo#getRuntime()
     */
    @Override
    public String getRuntime() {
        return runtime != null ? runtime : ""; //$NON-NLS-1$
    }

    /*
     * @see IProfileInfo#getMainClass()
     */
    @Override
    public String getMainClass() {
        return mainClass != null ? mainClass : ""; //$NON-NLS-1$
    }

    /*
     * @see IProfileInfo#getArguments()
     */
    @Override
    public String getArguments() {
        return arguments != null ? arguments : ""; //$NON-NLS-1$
    }

    /*
     * @see IProfileInfo#getDate()
     */
    @Override
    public String getDate() {
        return date != null ? date : ""; //$NON-NLS-1$
    }

    /*
     * @see IProfileInfo#getComments()
     */
    @Override
    public String getComments() {
        return comments != null ? comments : ""; //$NON-NLS-1$
    }
}
