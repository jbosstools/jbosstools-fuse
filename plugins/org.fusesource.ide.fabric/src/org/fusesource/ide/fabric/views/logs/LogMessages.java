package org.fusesource.ide.fabric.views.logs;

import org.eclipse.osgi.util.NLS;

public class LogMessages extends NLS {
	private static final String BUNDLE_NAME = LogMessages.class.getName();

	public static String messageLabel;
	public static String messageTooltip;

	public static String exceptionLabel;
	public static String exceptionTooltip;

	public static String openStackTrace;

	static {
		NLS.initializeMessages(BUNDLE_NAME, LogMessages.class);
	}
}
