package org.fusesource.ide.server.karaf.core.internal.runtime;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
import org.fusesource.ide.server.karaf.core.internal.KarafUtils;


/**
 * this locator looks for folders which contain a subfolder named "lib" containing
 * a file named "karaf.jar". There we open the jar and lookup the bundle version
 * from the manifest file. Max recursion depth is defined as constant.
 * 
 * @author lhein
 */
public class KarafRuntimeLocator extends RuntimeLocatorDelegate {

	protected static final int MAX_RECURSION_DEPTH = 5;

	protected static HashMap<String, IRuntimeWorkingCopy> foundRuntimes = new HashMap<String, IRuntimeWorkingCopy>();
	protected static int recursionLevel;
	
	/**
	 * empty default constructor
	 */
	public KarafRuntimeLocator() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.RuntimeLocatorDelegate#searchForRuntimes(org.eclipse.core.runtime.IPath, org.eclipse.wst.server.core.model.RuntimeLocatorDelegate.IRuntimeSearchListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void searchForRuntimes(IPath path, IRuntimeSearchListener listener,
			IProgressMonitor monitor) {
		
		reset();
		
		if (path == null) {
			monitor.done();
			return;
		}
		
		File f = new File(path.toOSString());
		if (f.isDirectory()) {
			monitor.beginTask("Searching for Apache Karaf in " + f.getPath() + "...", IProgressMonitor.UNKNOWN);
			search(f, listener, monitor);
			monitor.worked(1);
		}
		
		monitor.done();
		reset();
	}
	
	/**
	 * resets the locator
	 */
	private static void reset() {
		recursionLevel = 0;
		foundRuntimes.clear();
	}
	
	/**
	 * 
	 * @param folder
	 * @param listener
	 * @param monitor
	 */
	public static void search(File folder, IRuntimeSearchListener listener, IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}
		if (recursionLevel == MAX_RECURSION_DEPTH) {
			return;
		}
		recursionLevel++;
		
		File[] files = null;
		if (folder == null) {
			files = File.listRoots();
		} else {
			files = folder.listFiles(new FileFilter() {
				/*
				 * (non-Javadoc)
				 * @see java.io.FileFilter#accept(java.io.File)
				 */
				public boolean accept(File pathname) {
					return pathname.isDirectory() || (pathname.isFile() && pathname.getName().equalsIgnoreCase("karaf.jar"));
				}
			});
		}
		
		for (File f: files) {
			if (f.isFile()) {
				// check if the folder containing the file is named "lib"
				if (f.getParentFile().getName().equalsIgnoreCase("lib")) {
					File[] folders = f.getParentFile().getParentFile().listFiles(new FileFilter() {
						/*
						 * (non-Javadoc)
						 * @see java.io.FileFilter#accept(java.io.File)
						 */
						public boolean accept(File checkFile) {
							return checkFile.isDirectory() && 
								(checkFile.getName().equalsIgnoreCase("bin") || 
								 checkFile.getName().equalsIgnoreCase("system"));
						}
					});
					if (folders.length == 2) {
						// both key folders exist - seems to be a Karaf install
						// seems to fit - check if already found
						if (!foundRuntimes.containsKey(f.getPath())) {
							// new runtime - extract the bundle symbolic name and version
							if (checkRuntime(f, listener, monitor)) {
								// discovered runtime - now jump back to save time
								recursionLevel--;
								return;
							}
						}
					}
				}
			} else if (f.isDirectory()) {
				monitor.beginTask("Searching for Apache Karaf in " + f.getPath() + "...", IProgressMonitor.UNKNOWN);
				// increase recursion level
				search(f, listener, monitor);
				monitor.worked(1);
			}
		}
		recursionLevel--;
	}
	
	/**
	 * 
	 * @param karafJar
	 * @param listener
	 * @param monitor
	 * @return
	 */
	private static boolean checkRuntime(File karafJar, IRuntimeSearchListener listener, IProgressMonitor monitor) {
		boolean valid = false;
		
		File baseFolder = karafJar.getParentFile().getParentFile();
		monitor.beginTask("Examine possible Apache Karaf installation at " + baseFolder.getPath() + "...", IProgressMonitor.UNKNOWN);
		
		IRuntimeWorkingCopy runtime = getRuntimeFromDir(baseFolder, monitor);
		if (runtime != null) {
			listener.runtimeFound(runtime);
			foundRuntimes.put(karafJar.getPath(), runtime);
			valid = true;
		}
		
		monitor.worked(1);
		return valid;
	}
	
	/**
	 * retrieves the runtime working copy from the given folder
	 * 
	 * @param dir		the possible base folder
	 * @param monitor	the monitor
	 * @return			the runtime working copy or null if invalid
	 */
	public static IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor) {
		for (int i = 0; i < IKarafRuntime.KARAF_RUNTIME_TYPES_SUPPORTED.length; i++) {
			try {
				IRuntimeType runtimeType = ServerCore.findRuntimeType(IKarafRuntime.KARAF_RUNTIME_TYPES_SUPPORTED[i]);
				String absolutePath = dir.getAbsolutePath();
				
				// now check if the directory is valid
				if (KarafUtils.isValidKarafInstallation(dir, null)) {
					IRuntimeWorkingCopy runtime = runtimeType.createRuntime(runtimeType.getId(), monitor);
// commented out the naming of the runtime as it seems to break server to runtime links
//					runtime.setName(dir.getName());
					runtime.setLocation(new Path(absolutePath));
					IKarafRuntimeWorkingCopy wc = (IKarafRuntimeWorkingCopy) runtime.loadAdapter(IKarafRuntimeWorkingCopy.class, null);
					wc.setKarafInstallDir(absolutePath);
					wc.setKarafVersion(KarafUtils.getVersion(dir));
					wc.setKarafPropertiesFileLocation("");
					runtime.save(true, monitor);
					IStatus status = runtime.validate(monitor);
					if (status == null || status.getSeverity() != IStatus.ERROR) {
						return runtime;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
