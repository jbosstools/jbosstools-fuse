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

package org.fusesource.ide.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author lhein
 */
public class PreferenceManager {

	private static PreferenceManager instance;

	/**
	 * 
	 */
	private PreferenceManager() {
		// do possible initialization
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static synchronized PreferenceManager getInstance() {
		if (instance == null) {
			instance = new PreferenceManager();
		}
		return instance;
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, boolean value) {
		getPreferencesStore().setValue(key, value);
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, double value) {
		getPreferencesStore().setValue(key, value);
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, float value) {
		getPreferencesStore().setValue(key, value);
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, int value) {
		getPreferencesStore().setValue(key, value);
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, long value) {
		getPreferencesStore().setValue(key, value);
	}

	/**
	 * saves a property
	 * 
	 * @param key	the key
	 * @param value	the value
	 */
	public void savePreference(String key, String value) {
		getPreferencesStore().setValue(key, value);
	}


	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public boolean loadPreferenceAsBoolean(String key) {
		return getPreferencesStore().getBoolean(key);
	}

	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public double loadPreferenceAsDouble(String key) {
		return getPreferencesStore().getDouble(key);
	}
	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public float loadPreferenceAsFloat(String key) {
		return getPreferencesStore().getFloat(key);
	}
	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public int loadPreferenceAsInt(String key) {
		return getPreferencesStore().getInt(key);
	}
	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public long loadPreferenceAsLong(String key) {
		return getPreferencesStore().getLong(key);
	}
	/**
	 * loads the property
	 * 
	 * @param key	the key
	 * @return	the property
	 */
	public String loadPreferenceAsString(String key) {
		return getPreferencesStore().getString(key);
	}

	/**
	 * checks if there is a value or default value stored for the key
	 * @param key
	 * @return
	 */
	public boolean containsPreference(String key) {
		IPreferenceStore store = getPreferencesStore();
		return store != null && store.contains(key);
	}

	protected IPreferenceStore getPreferencesStore() {
		Activator activator = Activator.getDefault();
		if (activator == null) return null;
		return activator.getPreferenceStore();
	}

	/**
	 * returns the underlying preference storage
	 * 
	 * @return
	 */
	public IPreferenceStore getUnderlyingStorage() {
		return getPreferencesStore();
	}
}
