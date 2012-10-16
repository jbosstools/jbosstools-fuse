package org.fusesource.ide.server.karaf.core.internal.runtime;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.internal.KarafUtils;
import org.fusesource.ide.server.karaf.core.internal.ServiceMixUtils;


/**
 * @author lhein
 */
public class ServiceMix4xRuntimeLocator extends KarafRuntimeLocator {
	/**
	 * retrieves the runtime working copy from the given folder
	 * 
	 * @param dir		the possible base folder
	 * @param monitor	the monitor
	 * @return			the runtime working copy or null if invalid
	 */
	public static IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor) {
		for (int i = 0; i < IKarafRuntime.SMX_RUNTIME_TYPES_SUPPORTED.length; i++) {
			try {
				IRuntimeType runtimeType = ServerCore.findRuntimeType(IKarafRuntime.SMX_RUNTIME_TYPES_SUPPORTED[i]);
				String absolutePath = dir.getAbsolutePath();
				
				// now check if the directory is valid
				if (ServiceMixUtils.isValidServiceMixInstallation(dir, null)) {
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
