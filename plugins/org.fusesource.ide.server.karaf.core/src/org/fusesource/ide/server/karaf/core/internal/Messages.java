package org.fusesource.ide.server.karaf.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.fusesource.ide.server.karaf.core.internal.l10n.messages";

	public static String shellViewLabel;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}