/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.commons;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.jmx.commons.l10n.messages";

	public static String MessageDetailBodyTextToolTip;
	public static String MessageDetailFormTitle;

	public static String MessageDetailHeadersTableToolTip;
	public static String MessageDetailHeadersTableNameColumn;
	public static String MessageDetailHeadersTableNameColumnTooltip;
	public static String MessageDetailHeadersTableValueColumn;
	public static String MessageDetailHeadersTableValueColumnTooltip;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}