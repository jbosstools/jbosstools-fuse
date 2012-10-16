/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse protected License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * The profile configuration.
 */
@SuppressWarnings("nls")
public class Config {

    /** The separator. */
    private final static String SEPARATOR = ",";

    /** The flag for enabling profiler. */
    private boolean profilerEnabled;

    /** The flag for enabling automatic dump. */
    private boolean autoDumpEnabled;

    /** The output directory for dump file. */
    private String dumpDir;

    /** The list of ignored java packages. */
    protected Set<String> ignoredPackages;

    /** The list of profiled java packages. */
    protected Set<String> profiledPackages;

    /** The list of profiled class loaders. */
    protected Set<String> profiledClassLoaders;

    /** The shared instance of this class. */
    private static Config config;

    /**
     * The constructor.
     */
    private Config() {
        ignoredPackages = new LinkedHashSet<String>();
        profiledPackages = new LinkedHashSet<String>();
        profiledClassLoaders = new LinkedHashSet<String>();
        load();
    }

    /**
     * Gets the shared instance of this class.
     * 
     * @return The shared instance of this class
     */
    synchronized protected static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    /**
     * Adds the elements into list.
     * 
     * @param list
     *            The list to add elements
     * @param string
     *            The string of elements separated with comma
     */
    protected void addElements(Set<String> list, String string) {
        if (string == null) {
            return;
        }

        String[] elements = string.split(SEPARATOR);
        if (elements != null && elements.length > 0) {
            for (String element : elements) {
                list.add(element.trim());
            }
        }
    }

    /**
     * Gets the profiler enabled state.
     * 
     * @return <tt>true</tt> if profiler is enabled
     */
    protected boolean isProfilerEnabled() {
        return profilerEnabled;
    }

    /**
     * Sets the profiler enabled state.
     * 
     * @param enabled
     *            <tt>true</tt> to enable profiler
     */
    protected void setProfilerEnabled(boolean enabled) {
        this.profilerEnabled = enabled;
    }

    /**
     * Gets the auto dump state.
     * 
     * @return true if auto dump is enabled
     */
    protected boolean isAutoDumpEnabled() {
        return autoDumpEnabled;
    }

    /**
     * Sets the auto dump state.
     * 
     * @param enabled
     *            true to enable auto dump
     */
    protected void setAutoDumpEnabled(boolean enabled) {
        this.autoDumpEnabled = enabled;
    }

    /**
     * Gets the directory for dump file.
     * 
     * @return The dump directory
     */
    protected String getDumpDir() {
        return dumpDir;
    }

    /**
     * Sets the directory for dump file.
     * 
     * @param dir
     *            The dump directory
     */
    protected void setDumpDir(String dir) {
        dumpDir = dir;
    }

    /**
     * Loads the configuration.
     */
    private void load() {
        String fileName = System.getProperty(Constants.CONFIG_FILE_PROP_KEY);
        if (fileName != null) {
            setProperties(fileName);
        }

        profilerEnabled = System
                .getProperty(Constants.DEFERRED_PROP_KEY,
                        Boolean.TRUE.toString()).toLowerCase().trim()
                .equals(Boolean.FALSE.toString());
        autoDumpEnabled = System
                .getProperty(Constants.DUMP_PROP_KEY, Boolean.FALSE.toString())
                .toLowerCase().trim().equals(Boolean.TRUE.toString());

        String outputDirStr = System.getProperty(Constants.DUMP_DIR_PROP_KEY,
                getDefaultDir());
        String ignoredPackagesStr = System
                .getProperty(Constants.IGNORED_PACKAGES_PROP_KEY);
        String profiledPackagesStr = System
                .getProperty(Constants.PROFILED_PACKAGES_PROP_KEY);
        String profiledClassLoadersStr = System
                .getProperty(Constants.PROFILED_CLASSLOADER_PROP_KEY);

        dumpDir = outputDirStr.trim();
        if (!dumpDir.endsWith(File.separator)) {
            dumpDir = dumpDir.concat(File.separator);
        }
        ignoredPackages.add("org.fusesource.ide.jvmmonitor.internal.agent.*");
        if (ignoredPackagesStr != null) {
            ignoredPackages.clear();
            addElements(ignoredPackages, ignoredPackagesStr);
        }
        if (profiledPackagesStr != null) {
            profiledPackages.clear();
            addElements(profiledPackages, profiledPackagesStr);
        }
        if (profiledClassLoadersStr != null) {
            profiledClassLoaders.clear();
            addElements(profiledClassLoaders, profiledClassLoadersStr);
        }
    }

    /**
     * Gets the default directory for dump file.
     * 
     * @return The directory
     */
    private String getDefaultDir() {
        String dir = System.getProperty(Constants.USER_HOME_PROP_KEY);
        if (dir != null) {
            return dir;
        }

        dir = System.getProperty(Constants.USER_DIR_PROP_KEY);
        if (dir != null) {
            return dir;
        }

        return new File(".").getAbsolutePath();
    }

    /**
     * Sets the properties with file.
     * 
     * @param fileName
     *            name The properties with name
     */
    private void setProperties(String fileName) {
        if (fileName == null) {
            return;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Entry<Object, Object> set : properties.entrySet()) {
                System.setProperty((String) set.getKey(),
                        (String) set.getValue());
            }
        } catch (FileNotFoundException e) {
            Agent.logError(e, Messages.CANNOT_OPEN_CONFIG_FILE, fileName);
        } catch (IOException e) {
            Agent.logError(e, Messages.CANNOT_OPEN_CONFIG_FILE, fileName);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }
}
