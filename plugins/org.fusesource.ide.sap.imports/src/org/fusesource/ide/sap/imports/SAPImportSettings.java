/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 
 */
public class SAPImportSettings {

	///////////////////////////////////////////////////////
	// Property Names
	public static final String ARCHIVE_VERSION = "archiveVersion"; //$NON-NLS-1$
	public static final String BUNDLE_DEPLOY_LOCATION = "bundleDeployLocation"; //$NON-NLS-1$
	public static final String REQUIRED_EXECUTION_ENVIRONMENT = "requiredExecutionEnvironment"; //$NON-NLS-1$
	public static final String BUNDLE_VENDOR = "bundleVendor"; //$NON-NLS-1$
	public static final String BUNDLE_VERSION = "bundleVersion"; //$NON-NLS-1$
	public static final String BUNDLE_NAME = "bundleName"; //$NON-NLS-1$
	//
	///////////////////////////////////////////////////////

	private static final String BUNDLE_MANIFEST_ENTRY = "META-INF/MANIFEST.MF"; //$NON-NLS-1$

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	protected String bundleName;
	protected String bundleVersion;
	protected String bundleVendor;
	protected int requiredExecutionEnvironmentIndex = -1;
	protected String bundleDeployLocation;
	private String archiveVersion;

	/**
	 * 
	 * @return
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		return this.propertyChangeSupport;
	}

	/**
	 * 
	 * @param propertyChangeSupport
	 */
	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * 
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleName() {
		return this.bundleName;
	}
	
	/**
	 * 
	 * @param bundleName
	 */
	public void setBundleName(String bundleName) {
		firePropertyChange(BUNDLE_NAME, this.bundleName, this.bundleName = bundleName);
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleVersion() {
		return this.bundleVersion;
	}

	/**
	 * 
	 * @param bundleVersion
	 */
	public void setBundleVersion(String bundleVersion) {
		firePropertyChange(BUNDLE_VERSION, this.bundleVersion, this.bundleVersion = bundleVersion);
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleVendor() {
		return this.bundleVendor;
	}

	/**
	 * 
	 * @param bundleVendor
	 */
	public void setBundleVendor(String bundleVendor) {
		firePropertyChange(BUNDLE_VENDOR, this.bundleVendor, this.bundleVendor = bundleVendor);
	}

	/**
	 * 
	 * @return
	 */
	public int getRequiredExecutionEnvironmentIndex() {
		return this.requiredExecutionEnvironmentIndex;
	}

	/**
	 * 
	 * @param requiredExecutionEnvironment
	 */
	public void setRequiredExecutionEnvironmentIndex(int requiredExecutionEnvironment) {
		firePropertyChange(REQUIRED_EXECUTION_ENVIRONMENT, this.requiredExecutionEnvironmentIndex,
				this.requiredExecutionEnvironmentIndex = requiredExecutionEnvironment);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBundleDeployLocation() {
		return this.bundleDeployLocation;
	}
	
	/**
	 * 
	 * @param jco3BundlesExportLocation
	 */
	public void setBundleDeployLocation(String jco3BundlesExportLocation) {
		firePropertyChange(BUNDLE_DEPLOY_LOCATION, this.bundleDeployLocation, this.bundleDeployLocation = jco3BundlesExportLocation);
	}

	/**
	 * 
	 * @return
	 */
	public String getArchiveVersion() {
		return this.archiveVersion;
	}

	/**
	 * 
	 * @param archiveVersion
	 */
	public void setArchiveVersion(String archiveVersion) {
		firePropertyChange(ARCHIVE_VERSION, this.archiveVersion, this.archiveVersion = archiveVersion);
	}

	/**
	 * 
	 * @return
	 */
	public String getBundleManifestEntry() {
		return BUNDLE_MANIFEST_ENTRY;
	}
}
