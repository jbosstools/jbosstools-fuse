/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui;

import org.fusesource.ide.jvmmonitor.ui.Activator;

/**
 * The help context ids for JVM Monitor.
 */
@SuppressWarnings("nls")
public interface IHelpContextIds {

    /** */
    public static final String PREFIX = Activator.PLUGIN_ID + '.';

    /** */
    public static final String ATTRIBUTE_SELECTION_DIALOG = PREFIX
            + "attribute_selection_dialog_content";

    /** */
    public static final String NEW_CHART_DIALOG = PREFIX
            + "new_chart_dialog_content";

    /** */
    public static final String CONFIGURE_CHART_DIALOG = PREFIX
            + "configure_chart_dialog_content";

    /** */
    public static final String SAVE_CHART_SET_AS_DIALOG = PREFIX
            + "save_chart_set_as_dialog_content";

    /** */
    public static final String LOAD_CHART_SET_DIALOG = PREFIX
            + "load_chart_set_dialog_content";

    /** */
    public static final String FIND_DIALOG = PREFIX + "find_dialog_content";

    /** */
    public static final String SHOW_IN_TIMELINE_DIALOG = PREFIX
            + "show_in_timeline_dialog_content";

    /** */
    public static final String INVOKE_DIALOG = PREFIX + "invoke_dialog_content";

    /** */
    public static final String NOTIFICATION_DETAILS_DIALOG = PREFIX
            + "notification_details_dialog_content";

    /** */
    public static final String NEW_JVM_CONNECTION_WIZARD_PAGE = PREFIX
            + "new_jvm_connection_wizard_page_context";

    /** */
    public static final String JVM_EXPLORER_VIEW = PREFIX
            + "jvm_explorer_view_conent";

    /** */
    public static final String TIMELINE_PAGE = PREFIX + "timeline_page_content";

    /** */
    public static final String THREADS_PAGE = PREFIX + "threads_page_content";

    /** */
    public static final String MEMORY_PAGE = PREFIX + "memory_page_content";

    /** */
    public static final String CPU_PAGE = PREFIX + "cpu_page_content";

    /** */
    public static final String MBEANS_PAGE = PREFIX + "mbeans_page_content";

    /** */
    public static final String OVERVIEW_PAGE = PREFIX + "overview_page_content";

    /** */
    public static final String HEAP_DUMP_EDITOR = PREFIX
            + "heap_dump_editor_content";

    /** */
    public static final String THREADS_DUMP_EDITOR = PREFIX
            + "threads_dump_editor_content";

    /** */
    public static final String CPU_DUMP_EDITOR = PREFIX
            + "cpu_dump_editor_content";

    /** */
    public static final String JAVA_MONITOR_PREFERENCE_PAGE = PREFIX
            + "java_monitor_preference_page_context";
}
