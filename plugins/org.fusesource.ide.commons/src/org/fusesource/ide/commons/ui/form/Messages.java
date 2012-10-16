package org.fusesource.ide.commons.ui.form;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

	/** The bundle name. */
	private static final String BUNDLE_NAME = "org.fusesource.ide.commons.ui.form.messages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * The constructor.
	 */
	private Messages() {
		// do not instantiate
	}

	public static String mandatoryValidationMessage;

}