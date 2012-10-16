/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

    /** The bundle name. */
    private static final String BUNDLE_NAME = "org.fusesource.ide.jvmmonitor.internal.ui.actions.messages";//$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The constructor.
     */
    private Messages() {
        // do not instantiate
    }

    // actions

    /** */
    public static String collapseAllLabel;

    /** */
    public static String copyLabel;

    /** */
    public static String refreshLabel;

    /** */
    public static String preferencesLabel;

    /** */
    public static String showInTimelineLabel;

    /** */
    public static String openDeclarationLabel;

    /** */
    public static String verticalOrientationLabel;

    /** */
    public static String horizontalOrientationLabel;

    /** */
    public static String automaticOrientationLabel;

    /** */
    public static String singleOrientationLabel;

    // configure columns dialog

    /** */
    public static String configureColumnsLabel;

    /** */
    public static String configureColumnsTitle;

    /** */
    public static String configureColumnsMessage;

    /** */
    public static String upLabel;

    /** */
    public static String downLabel;

    /** */
    public static String selectAllLabel;

    /** */
    public static String deselectAllLabel;

    // show in timeline dialog

    /** */
    public static String showInTimelineDialogTitle;

    // open declaration progress monitor dialog

    /** */
    public static String searchingSoruceMsg;

    // error messages

    /** */
    public static String addAttributeFailedMsg;

    /** */
    public static String searchClassFailedMsg;

    /** */
    public static String openEditorFailedMsg;

    /** */
    public static String highlightMethodFailedMsg;
    
    /** */
    public static String openDeclarationFailedMsg;

    /** */
    public static String errorLabel;
}