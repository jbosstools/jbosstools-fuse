/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

/**
 * The constants.
 */
@SuppressWarnings("nls")
public class Constants {

    /** The version. */
    static final String VERSION = "3.7.0";

    /** The logger name. */
    static final String LOGGER_NAME = "org.fusesource.ide.jvmmonitor.agent";

    /** The default package. */
    static final String DEFAULT_PACKAGE = "<default>";

    /** The key for CPU profiling properties file. */
    static final String CONFIG_FILE_PROP_KEY = "jvmmonitor.config";

    /** The key for agent jar. */
    static final String AGENT_JAR_PROP_KEY = "jvmmonitor.agent.jar";

    /** The key for deferred. */
    static final String DEFERRED_PROP_KEY = "jvmmonitor.deferred";

    /** The key for automatic dump file output.. */
    static final String DUMP_PROP_KEY = "jvmmonitor.dump";

    /** The key for dump file output directory. */
    static final String DUMP_DIR_PROP_KEY = "jvmmonitor.dump.dir";

    /** The key for ignored java packages. */
    static final String IGNORED_PACKAGES_PROP_KEY = "jvmmonitor.ignored.packages";

    /** The key for profiled java packages. */
    static final String PROFILED_PACKAGES_PROP_KEY = "jvmmonitor.profiled.packages";

    /** The key for profiled class loaders. */
    static final String PROFILED_CLASSLOADER_PROP_KEY = "jvmmonitor.profiled.classloaders";

    /** The key for user home directory. */
    static final String USER_HOME_PROP_KEY = "user.home";

    /** The key for current directory. */
    static final String USER_DIR_PROP_KEY = "user.dir";

    /** The CPU profiler class. */
    static final String CLASS_CPU_PROFILER = "org/jvmmonitor/internal/agent/CpuBciProfiler";

    /** The method CpuProfiler#stepInto. */
    static final String METHOD_STEP_INTO = "stepInto";

    /** The method CpuProfiler#stepReturn. */
    static final String METHOD_STEP_RETURN = "stepReturn";

    /** The method CpuProfiler#dropToFrame. */
    static final String METHOD_DROP_TO_FRAME = "dropToFrame";

    /** The method name representing the class initialization method. */
    static final String METHOD_CLINIT = "<clinit>";

    /** The descriptor for two strings. */
    static final String DESC_STRING_STRING = "(Ljava/lang/String;Ljava/lang/String;)V";

    /** The descriptor for three strings. */
    static final String DESC_STRING_STRING_STRING = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    /** The -javaagent option for JVM. */
    static final String JAVA_AGENT_OPTION = "-javaagent:";

    /** The main thread. */
    static final String MAIN_THREAD = "main";

    /** The date format for dump file name. */
    static final String TIME_FORMAT_FOR_FILENAME = "HHmmss";

    /** The dump file suffix. */
    static final String DUMP_FILE_SUFFIX = ".cpu";

    /** The dump file prefix. */
    public static final String DUMP_FILE_PREFIX = "jvmmonitor_";

    /** The date format. */
    public static final String DATE_FORMAT = "yyyy/MM/dd";

    /** The time format. */
    public static final String TIME_FORMAT = "HH:mm:ss";
}
