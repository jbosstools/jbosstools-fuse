package org.fusesource.ide.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.preferences.l10n.messages";

	public static String subscriptionPageDescription;
	public static String subscriberName;
	public static String subscriberPassword;
	public static String downloadLicenseLabel;
	public static String subscriberExpDate;
	public static String installLicenseLabel;
	public static String installLicenseFileDialogText;
	public static String linkBlueLabelPart;
	public static String linkToFuseSourceLabel;
	public static String licenseInstalledTitle;
	public static String licenseInstalledText;
	public static String licenseNotInstalledTitle;
	public static String licenseNotInstalledText;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
