package org.fusesource.ide.fabric.servicemix;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.fabric.servicemix.l10n.messages";

	public static String DefaultFabricName;


	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}