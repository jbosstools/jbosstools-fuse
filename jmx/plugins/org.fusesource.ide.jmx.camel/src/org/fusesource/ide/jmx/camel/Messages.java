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

package org.fusesource.ide.jmx.camel;

import org.eclipse.osgi.util.NLS;

/**
 * @author lhein
 *
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.fusesource.ide.jmx.camel.messages";


	public static String TraceAction;
	public static String TraceActionToolTip;

	public static String StopTraceAction;
	public static String StopTraceActionToolTip;

	public static String CreateEndpointAction;
	public static String CreateEndpointActionToolTip;
	public static String CreateEndpointDialogMessage;
	public static String CreateEndpointDialogTitle;

	public static String DeleteEndpointAction;
	public static String DeleteEndpointActionToolTip;
	public static String DeleteEndpointDialogMessage;
	public static String DeleteEndpointDialogTitle;

	public static String camelContextNodeEditRouteToolTip;

	public static String EditRoutesAction;
	public static String EditRoutesActionToolTip;

	public static String SuspendCamelContextAction;
	public static String SuspendCamelContextActionToolTip;
	public static String ResumeCamelContextAction;
	public static String ResumeCamelContextActionToolTip;

	public static String StopCamelContextAction;
	public static String StopCamelContextActionToolTip;

	public static String StartRouteAction;
	public static String StartRouteActionToolTip;
	public static String StopRouteAction;
	public static String StopRouteActionToolTip;


	
	static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}