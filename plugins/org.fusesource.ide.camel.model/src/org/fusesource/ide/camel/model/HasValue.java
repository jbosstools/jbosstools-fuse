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

package org.fusesource.ide.camel.model;

/**
 * Strategy to determine if an object has a value
 * <p/>
 * This is used to determine for example any UI property descriptors has any value assigned
 * by the end user.
 */
public interface HasValue {

	/**
	 * Is there a value.
	 */
	boolean hasValue();

}
