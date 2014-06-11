package org.fusesource.ide.jvmmonitor.internal.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.Messages;

/**
 * The utility class.
 */
public class Util {

    /**
     * Deletes the given directory and its contents.
     * 
     * @param dir
     *            The directory
     */
    public static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    if (!file.delete()) {
                        Activator.log(
                                IStatus.ERROR,
                                NLS.bind(Messages.removeFileFailedMsg,
                                        file.getAbsolutePath()),
                                new Exception());
                    }
                }
            }
        }
        if (!dir.delete()) {
            Activator.log(
                    IStatus.ERROR,
                    NLS.bind(Messages.removeDirectoryFailedMsg,
                            dir.getAbsolutePath()), new Exception());
        }
    }

    /**
     * Loads the properties from file.
     * 
     * @param filePath
     *            The file path
     * @return The properties, or <tt>null</tt> if loading properties fails
     */
    public static Properties loadProperties(IPath filePath) {
        Properties props = new Properties();
        IFileStore fileStore = EFS.getLocalFileSystem().getStore(filePath);
        try {
            InputStream in = fileStore.openInputStream(EFS.NONE, null);
            props.loadFromXML(in);
        } catch (CoreException e) {
            Activator.log(
                    IStatus.ERROR,
                    NLS.bind(Messages.openInputStreamFailedMsg,
                            filePath.toOSString()), e);
            return null;
        } catch (IOException e) {
            Activator.log(
                    IStatus.ERROR,
                    NLS.bind(Messages.loadPropertiesFileFailedMsg,
                            filePath.toOSString()), e);
            return null;
        }
        return props;
    }

    /**
     * Gets the file store.
     * 
     * @param fileName
     *            The file name
     * @param baseDir
     *            The base directory
     * @return The file store
     */
    public static IFileStore getFileStore(String fileName, IPath baseDir) {
        IPath filePath = baseDir.append(File.separator + fileName);
        return EFS.getLocalFileSystem().getStore(filePath);
    }
}
