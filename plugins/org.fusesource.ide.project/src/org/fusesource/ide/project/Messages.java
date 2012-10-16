package org.fusesource.ide.project;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.project.l10n.messages";

	public static String riderBrowseCamelContextLabel;
	public static String riderBrowseCamelContextButton;
	public static String invalidCamelContextFileMessage;
	public static String riderLaunchConfigTabTitle;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
