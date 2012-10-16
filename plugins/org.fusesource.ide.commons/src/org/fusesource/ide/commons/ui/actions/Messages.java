/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.commons.ui.actions;

import org.eclipse.osgi.util.NLS;

/**
 * The messages.
 */
public final class Messages extends NLS {

	/** The bundle name. */
	private static final String BUNDLE_NAME = "org.fusesource.ide.commons.ui.actions.messages";//$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * The constructor.
	 */
	private Messages() {
		// do not instantiate
	}

	// actions

	// configure columns dialog

	/** */
	public static String configureColumnsLabel;

	/** */
	public static String configureColumnsTitle;

	/** */
	public static String configureColumnsMessage;

	public static String topLabel;
	public static String bottomLabel;

	/** */
	public static String upLabel;

	/** */
	public static String downLabel;

	/** */
	public static String selectAllLabel;

	/** */
	public static String deselectAllLabel;

	public static String createChartTitle;

	public static String disconnectLabel;
	public static String connectLabel;


}