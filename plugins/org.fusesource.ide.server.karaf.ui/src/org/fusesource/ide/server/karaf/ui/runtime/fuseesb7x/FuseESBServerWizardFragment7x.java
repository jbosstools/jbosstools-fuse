package org.fusesource.ide.server.karaf.ui.runtime.fuseesb7x;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.fusesource.ide.server.karaf.core.internal.FuseESBUtils;
import org.fusesource.ide.server.karaf.core.internal.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.ui.Messages;
import org.fusesource.ide.server.karaf.ui.runtime.v2x.KarafServerWizardFragment2x;


/**
 * @author lhein
 */
public class FuseESBServerWizardFragment7x extends
		KarafServerWizardFragment2x {
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.ui.runtime.AbstractKarafServerWizardFragment#createComposite(org.eclipse.swt.widgets.Composite, org.eclipse.wst.server.ui.wizard.IWizardHandle)
	 */
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		Composite c = super.createComposite(parent, handle);
		handle.setTitle(Messages.FuseESBServerPorpertiesComposite_wizard_title);
		handle.setDescription(Messages.FuseESBServerPorpertiesComposite_wizard_desc);
		handle.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_NEW_RUNTIME));
		return c;
	}
	
	/**
	 * determines the version of the karaf installation from the manifest of the main bundle
	 * 
	 * @param runtime	the runtime to use for grabbing the install location
	 * @return	the version as string or null on errors
	 */
	protected String determineVersion(IKarafRuntime runtime) {
		String version = null;
		
		if (runtime != null && runtime.getKarafInstallDir() != null) {
			File folder = new File(runtime.getKarafInstallDir());
			if (folder.exists() && folder.isDirectory()) {
				version = FuseESBUtils.getVersion(folder);
			}
		}
		
		return version;
	}
}
