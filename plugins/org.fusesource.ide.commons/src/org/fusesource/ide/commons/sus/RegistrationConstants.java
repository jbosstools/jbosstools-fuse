/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.commons.sus;

/**
 * @author lhein
 */
public final class RegistrationConstants {
	public static final String SUBSCRIPTION_VALID = "REGISTERED";
	public static final String SUBSCRIPTION_ABOUT_TO_EXPIRE = "ABOUT TO EXPIRE";
	public static final String SUBSCRIPTION_EVAL = "EVALUATION";
	
	public static final String PREF_SUS_NAME = "fuse.subscriber.name";
	public static final String PREF_SUS_PASS = "fuse.subscriber.pass";
	public static final String PREF_SUS_EXPDATE = "fuse.subscriber.expdate";
	public static final String PREF_SUS_STATE = "fuse.subscriber.state";
	
	public static final String PROP_NAME = "name";
	public static final String PROP_EXPDATE = "expiryDate";
}
