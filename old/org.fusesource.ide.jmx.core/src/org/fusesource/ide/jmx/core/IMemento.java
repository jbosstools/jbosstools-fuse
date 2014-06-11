/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core;

import java.util.List;

/**
 * Stolen from webtools wst.server.core,
 * represents a savable memento to be
 * translated into xml
 *
 */
public interface IMemento {
	/**
	 * Creates a new child of this memento with the given type.
	 * <p>
	 * The <code>getChild</code> and <code>getChildren</code> methods
	 * are used to retrieve children of a given type.
	 * </p>
	 *
	 * @param type the type
	 * @return a new child memento
	 * @see #getChild
	 * @see #getChildren
	 */
	public IMemento createChild(String type);

	/**
	 * Returns the first child with the given type id.
	 *
	 * @param type the type id
	 * @return the first child with the given type
	 */
	public IMemento getChild(String type);

	/**
	 * Returns all children with the given type id.
	 *
	 * @param type the type id
	 * @return the list of children with the given type
	 */
	public IMemento[] getChildren(String type);

	/**
	 * Returns the floating point value of the given key.
	 *
	 * @param key the key
	 * @return the value, or <code>null</code> if the key was not found or was found
	 *   but was not a floating point number
	 */
	public Float getFloat(String key);

	/**
	 * Returns the integer value of the given key.
	 *
	 * @param key the key
	 * @return the value, or <code>null</code> if the key was not found or was found
	 *   but was not an integer
	 */
	public Integer getInteger(String key);

	/**
	 * Returns the string value of the given key.
	 *
	 * @param key the key
	 * @return the value, or <code>null</code> if the key was not found or was found
	 *  but was not an integer
	 */
	public String getString(String key);

	/**
	 * Returns the boolean value of the given key.
	 *
	 * @param key the key
	 * @return the value, or <code>null</code> if the key was not found or was found
	 *  but was not a boolean
	 */
	public Boolean getBoolean(String key);

	public List<String> getNames();

	/**
	 * Sets the value of the given key to the given integer.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putInteger(String key, int value);

	/**
	 * Sets the value of the given key to the given boolean value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putBoolean(String key, boolean value);

	/**
	 * Sets the value of the given key to the given string.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putString(String key, String value);
}
