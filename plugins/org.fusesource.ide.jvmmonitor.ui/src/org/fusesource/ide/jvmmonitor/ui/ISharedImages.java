/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.ui;

/**
 * The shared image paths to access image in this plug-in with
 * {@link Activator#getImageDescriptor(String)}.
 */
public interface ISharedImages {

    /** The local host image path. */
    public static final String LOCAL_HOST_IMG_PATH = "icons/full/obj16/local_host_obj.png";//$NON-NLS-1$

    /** The remote host image path. */
    public static final String REMOTE_HOST_IMG_PATH = "icons/full/obj16/remote_host_obj.png";//$NON-NLS-1$

    /** The connected JVM image path. */
    public static final String CONNECTED_JVM_IMG_PATH = "icons/full/obj16/connected_jvm_obj.png";//$NON-NLS-1$

    /** The disconnected JVM image path. */
    public static final String DISCONNECTED_JVM_IMG_PATH = "icons/full/obj16/disconnected_jvm_obj.png";//$NON-NLS-1$

    /** The terminated JVM image path. */
    public static final String TERMINATED_JVM_IMG_PATH = "icons/full/obj16/terminated_jvm_obj.gif";//$NON-NLS-1$

    /** The thread object image path. */
    public static final String THREAD_IMG_PATH = "icons/full/obj16/thread_obj.gif";//$NON-NLS-1$

    /** The thread runnable object image path. */
    public static final String THREAD_RUNNABLE_IMG_PATH = "icons/full/obj16/thread_runnable_obj.gif";//$NON-NLS-1$

    /** The thread waiting object image path. */
    public static final String THREAD_WAITING_IMG_PATH = "icons/full/obj16/thread_waiting_obj.png";//$NON-NLS-1$

    /** The thread blocked object image path. */
    public static final String THREAD_BLOCKED_IMG_PATH = "icons/full/obj16/thread_blocked_obj.png";//$NON-NLS-1$

    /** The thread suspended object image path. */
    public static final String THREAD_SUSPENDED_IMG_PATH = "icons/full/obj16/thread_suspended_obj.gif";//$NON-NLS-1$

    /** The thread deadlocked object image path. */
    public static final String THREAD_DEADLOCKED_IMG_PATH = "icons/full/obj16/thread_deadlocked_obj.png";//$NON-NLS-1$

    /** The method image path. */
    public static final String METHOD_IMG_PATH = "icons/full/obj16/method_obj.gif";//$NON-NLS-1$

    /** The stack frame object image path. */
    public static final String STACK_FRAME_OBJ_IMG_PATH = "icons/full/obj16/stckframe_obj.gif";//$NON-NLS-1$

    /** The add JVM image path. */
    public static final String ADD_JVM_IMG_PATH = "icons/full/elcl16/add_jvm.png";//$NON-NLS-1$

    /** The connect image path. */
    public static final String CONNECT_IMG_PATH = "icons/full/elcl16/connect.png";//$NON-NLS-1$

    /** The disconnect image path. */
    public static final String DISCONNECT_IMG_PATH = "icons/full/elcl16/disconnect.png";//$NON-NLS-1$

    /** The resume image path. */
    public static final String RESUME_IMG_PATH = "icons/full/elcl16/resume_co.gif";//$NON-NLS-1$

    /** The disabled resume image path. */
    public static final String DISABLED_RESUME_IMG_PATH = "icons/full/dlcl16/resume.gif";//$NON-NLS-1$

    /** The suspend image path. */
    public static final String SUSPEND_IMG_PATH = "icons/full/elcl16/suspend_co.gif";//$NON-NLS-1$

    /** The disabled suspend image path. */
    public static final String DISABLED_SUSPEND_IMG_PATH = "icons/full/dlcl16/suspend.gif";//$NON-NLS-1$

    /** The clear image path. */
    public static final String CLEAR_IMG_PATH = "icons/full/elcl16/clear_co.gif";//$NON-NLS-1$

    /** The disabled clear image path. */
    public static final String DISABLED_CLEAR_IMG_PATH = "icons/full/dlcl16/clear.gif";//$NON-NLS-1$

    /** The snapshot image path. */
    public static final String SNAPSHOT_IMG_PATH = "icons/full/elcl16/snapshot.gif";//$NON-NLS-1$

    /** The class object image path. */
    public static final String CLASS_OBJ_IMG_PATH = "icons/full/obj16/class_obj.gif";//$NON-NLS-1$

    /** The package object image path. */
    public static final String PACKAGE_OBJ_IMG_PATH = "icons/full/obj16/package_obj.gif";//$NON-NLS-1$

    /** The collapse all image path. */
    public static final String COLLAPSE_ALL_IMG_PATH = "icons/full/elcl16/collapseall.gif"; //$NON-NLS-1$

    /** The expand all image path. */
    public static final String EXPAND_ALL_IMG_PATH = "icons/full/elcl16/expandall.gif"; //$NON-NLS-1$

    /** The refresh image path. */
    public static final String REFRESH_IMG_PATH = "icons/full/elcl16/refresh.gif"; //$NON-NLS-1$

    /** The disabled refresh image path. */
    public static final String DISABLED_REFRESH_IMG_PATH = "icons/full/dlcl16/refresh.gif"; //$NON-NLS-1$

    /** The trash image path. */
    public static final String TRASH_IMG_PATH = "icons/full/elcl16/trash.gif"; //$NON-NLS-1$

    /** The disabled trash image path. */
    public static final String DISABLED_TRASH_IMG_PATH = "icons/full/dlcl16/trash.gif"; //$NON-NLS-1$

    /** The thread dump image path. */
    public static final String THREAD_DUMP_IMG_PATH = "icons/full/obj16/thread_dump.gif"; //$NON-NLS-1$

    /** The hprof dump image path. */
    public static final String HPROF_DUMP_IMG_PATH = "icons/full/obj16/hprof_dump.gif"; //$NON-NLS-1$

    /** The heap dump image path. */
    public static final String HEAP_DUMP_IMG_PATH = "icons/full/obj16/heap_dump.gif"; //$NON-NLS-1$

    /** The CPU dump image path. */
    public static final String CPU_DUMP_IMG_PATH = "icons/full/obj16/cpu_dump.gif"; //$NON-NLS-1$

    /** The take thread dump image path. */
    public static final String TAKE_THREAD_DUMP_IMG_PATH = "icons/full/elcl16/take_thread_dump.gif"; //$NON-NLS-1$

    /** The disabled take thread dump image path. */
    public static final String DISABLED_TAKE_THREAD_DUMP_IMG_PATH = "icons/full/dlcl16/take_thread_dump.gif"; //$NON-NLS-1$

    /** The take hprof dump image path. */
    public static final String TAKE_HPROF_DUMP_IMG_PATH = "icons/full/elcl16/take_hprof_dump.gif"; //$NON-NLS-1$

    /** The disabled take hprof dump image path. */
    public static final String DISABLED_TAKE_HPROF_DUMP_IMG_PATH = "icons/full/dlcl16/take_hprof_dump.gif"; //$NON-NLS-1$

    /** The take heap dump image path. */
    public static final String TAKE_HEAP_DUMP_IMG_PATH = "icons/full/elcl16/take_heap_dump.gif"; //$NON-NLS-1$

    /** The disabled take heap dump image path. */
    public static final String DISABLED_TAKE_HEAP_DUMP_IMG_PATH = "icons/full/dlcl16/take_heap_dump.gif"; //$NON-NLS-1$

    /** The take CPU dump image path. */
    public static final String TAKE_CPU_DUMP_IMG_PATH = "icons/full/elcl16/take_cpu_dump.gif"; //$NON-NLS-1$

    /** The disabled take CPU dump image path. */
    public static final String DISABLED_TAKE_CPU_DUMP_IMG_PATH = "icons/full/dlcl16/take_cpu_dump.gif"; //$NON-NLS-1$

    /** The automatic layout image path. */
    public static final String AUTOMATIC_LAYOUT_IMG_PATH = "icons/full/etool16/layout_automatic.gif"; //$NON-NLS-1$

    /** The horizontal layout image path. */
    public static final String HORIZONTAL_LAYOUT_IMG_PATH = "icons/full/etool16/layout_horizontal.gif"; //$NON-NLS-1$

    /** The vertical layout image path. */
    public static final String VERTICAL_LAYOUT_IMG_PATH = "icons/full/etool16/layout_vertical.gif"; //$NON-NLS-1$

    /** The single layout image path. */
    public static final String SINGLE_LAYOUT_IMG_PATH = "icons/full/etool16/layout_single.gif"; //$NON-NLS-1$

    /** The new JVM connection image path. */
    public static final String NEW_JVM_CONNECTION_IMG_PATH = "icons/full/wizban/new_jvm_connection_wiz.png"; //$NON-NLS-1$

    /** The MBean image path. */
    public static final String MBEAN_IMG_PATH = "icons/full/obj16/mbean.gif"; //$NON-NLS-1$

    /** The MBean folder image path. */
    public static final String MBEAN_FOLDER_IMG_PATH = "icons/full/obj16/mbean_folder.gif"; //$NON-NLS-1$

    /** The attribute image path. */
    public static final String ATTRIBUTE_IMG_PATH = "icons/full/obj16/attribute.gif"; //$NON-NLS-1$

    /** The attribute folder image path. */
    public static final String ATTRIBUTE_FOLDER_IMG_PATH = "icons/full/obj16/attribute_folder.gif"; //$NON-NLS-1$

    /** The notification image path. */
    public static final String NOTIFICATION_IMG_PATH = "icons/full/obj16/notification.png"; //$NON-NLS-1$

    /** The details image path. */
    public static final String DETAILS_IMG_PATH = "icons/full/elcl16/details.gif"; //$NON-NLS-1$

    /** The next image path. */
    public static final String NEXT_IMG_PATH = "icons/full/elcl16/next.gif"; //$NON-NLS-1$

    /** The previous image path. */
    public static final String PREV_IMG_PATH = "icons/full/elcl16/prev.gif"; //$NON-NLS-1$

    /** The new chart image path. */
    public static final String NEW_CHART_IMG_PATH = "icons/full/elcl16/new_chart.gif"; //$NON-NLS-1$

    /** The disabled new chart image path. */
    public static final String DISABLED_NEW_CHART_IMG_PATH = "icons/full/dlcl16/new_chart.gif"; //$NON-NLS-1$

    /** The write overlay image path. */
    public static final String WRITE_OVR_IMG_PATH = "icons/full/ovr16/write.gif"; //$NON-NLS-1$

    /** The configure image path. */
    public static final String CONFIGURE_IMG_PATH = "icons/full/elcl16/configure.gif"; //$NON-NLS-1$

    /** The call tree image path. */
    public static final String CALL_TREE_IMG_PATH = "icons/full/obj16/call_tree.gif";//$NON-NLS-1$

    /** The hot spots image path. */
    public static final String HOT_SPOTS_IMG_PATH = "icons/full/obj16/hot_spots.gif";//$NON-NLS-1$

    /** The caller callee image path. */
    public static final String CALLER_CALLEE_IMG_PATH = "icons/full/obj16/caller_callee.gif";//$NON-NLS-1$

    /** The information image path. */
    public static final String INFO_IMG_PATH = "icons/full/obj16/info.gif";//$NON-NLS-1$

    /** The memory image path. */
    public static final String MEMORY_IMG_PATH = "icons/full/obj16/memory.gif";//$NON-NLS-1$

    /** The 0 percentage image path. */
    public static final String PERCENT_0_IMG_PATH = "icons/full/obj16/0percent.gif";//$NON-NLS-1$

    /** The 2 percentage image path. */
    public static final String PERCENT_2_IMG_PATH = "icons/full/obj16/2percent.gif";//$NON-NLS-1$

    /** The 4 percentage image path. */
    public static final String PERCENT_4_IMG_PATH = "icons/full/obj16/4percent.gif";//$NON-NLS-1$

    /** The 7 percentage image path. */
    public static final String PERCENT_7_IMG_PATH = "icons/full/obj16/7percent.gif";//$NON-NLS-1$

    /** The 10 percentage image path. */
    public static final String PERCENT_10_IMG_PATH = "icons/full/obj16/10percent.gif";//$NON-NLS-1$

    /** The 15 percentage image path. */
    public static final String PERCENT_15_IMG_PATH = "icons/full/obj16/15percent.gif";//$NON-NLS-1$

    /** The 20 percentage image path. */
    public static final String PERCENT_20_IMG_PATH = "icons/full/obj16/20percent.gif";//$NON-NLS-1$

    /** The 25 percentage image path. */
    public static final String PERCENT_25_IMG_PATH = "icons/full/obj16/25percent.gif";//$NON-NLS-1$

    /** The 30 percentage image path. */
    public static final String PERCENT_30_IMG_PATH = "icons/full/obj16/30percent.gif";//$NON-NLS-1$

    /** The 35 percentage image path. */
    public static final String PERCENT_35_IMG_PATH = "icons/full/obj16/35percent.gif";//$NON-NLS-1$

    /** The 40 percentage image path. */
    public static final String PERCENT_40_IMG_PATH = "icons/full/obj16/40percent.gif";//$NON-NLS-1$

    /** The 50 percentage image path. */
    public static final String PERCENT_50_IMG_PATH = "icons/full/obj16/50percent.gif";//$NON-NLS-1$

    /** The 60 percentage image path. */
    public static final String PERCENT_60_IMG_PATH = "icons/full/obj16/60percent.gif";//$NON-NLS-1$

    /** The 70 percentage image path. */
    public static final String PERCENT_70_IMG_PATH = "icons/full/obj16/70percent.gif";//$NON-NLS-1$

    /** The 80 percentage image path. */
    public static final String PERCENT_80_IMG_PATH = "icons/full/obj16/80percent.gif";//$NON-NLS-1$

    /** The 90 percentage image path. */
    public static final String PERCENT_90_IMG_PATH = "icons/full/obj16/90percent.gif";//$NON-NLS-1$

    /** The 100 percentage image path. */
    public static final String PERCENT_100_IMG_PATH = "icons/full/obj16/100percent.gif";//$NON-NLS-1$

    /** The automatic orientation image path. */
    public static final String AUTOMATIC_ORIENTATION_IMG_PATH = "icons/full/elcl16/automatic_orientation.png";//$NON-NLS-1$

}