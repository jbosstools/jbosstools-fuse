package org.fusesource.ide.launcher;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.fusesource.ide.launcher.l10n.messages";

	public static String msgStatusLaunch;
	public static String msgVerifyLaunchAttribs;
	public static String msgCreatingSourceLocator;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
