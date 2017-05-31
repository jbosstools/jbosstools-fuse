/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.utils;

/**
 * 
 * @author apodhrad
 *
 */
public class CamelComponentUtils {

	public static final int MAX = 30;
	public static final String DOTS = "...";

	public static String getLabel(String description) {
		if (description.length() <= MAX) {
			return description;
		} else {
			return description.substring(0, MAX - 2) + DOTS;
		}
	}

}
