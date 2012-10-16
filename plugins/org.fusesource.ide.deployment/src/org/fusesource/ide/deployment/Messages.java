package org.fusesource.ide.deployment;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.deployment.l10n.messages";

	public static String clearDeployConfigButtonLabel;
	public static String addDeployConfigButtonLabel;
	public static String updateDeployConfigButtonLabel;
	public static String removeDeployConfigButtonLabel;
	public static String makeDefaultConfigButtonLabel;
	public static String tableHeaderDefaultLabel;
	public static String tableHeaderNameLabel;
	public static String tableHeaderDescriptionLabel;
	public static String tableHeaderDeployPathLabel;
	public static String tableHeaderDeployPathToolTip;
	public static String containerFolderDialogTitle;
	public static String containerFolderDialogDescription;
	public static String configureDeploymentsMenuLabel;
	public static String nameTooShortError;
	public static String nameNotUniqueError;
	public static String deployFolderTooShortError;
	public static String deployFolderNotValidError;
	public static String invalidCharsError;
	public static String deployToolbarLabel;
	public static String deployToolbarTooltip;

	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}