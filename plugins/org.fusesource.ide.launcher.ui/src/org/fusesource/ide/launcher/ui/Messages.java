package org.fusesource.ide.launcher.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.fusesource.ide.launcher.ui.l10n.messages";

	public static String pomGroup;
	public static String browseWorkspace;
	public static String choosePomDir;
	public static String browseFs;
	public static String browseVariables;
	public static String goalsLabel;
	public static String goals;
	public static String profilesLabel;
	public static String propName;
	public static String propValue;
	public static String propAddButton;
	public static String propEditButton;
	public static String propRemoveButton;
	public static String propertyDialog_browseVariables;
	public static String mainTabName;
	public static String pomDirectoryEmpty;
	public static String pomDirectoryDoesntExist;
	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
