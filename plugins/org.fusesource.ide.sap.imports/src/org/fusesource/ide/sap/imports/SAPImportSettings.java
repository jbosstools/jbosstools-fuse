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

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		firePropertyChange(BUNDLE_NAME, this.bundleName, this.bundleName = bundleName);
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String bundleVersion) {
		firePropertyChange(BUNDLE_VERSION, this.bundleVersion, this.bundleVersion = bundleVersion);
	}

	public String getBundleVendor() {
		return bundleVendor;
	}

	public void setBundleVendor(String bundleVendor) {
		firePropertyChange(BUNDLE_VENDOR, this.bundleVendor, this.bundleVendor = bundleVendor);
	}

	public int getRequiredExecutionEnvironmentIndex() {
		return requiredExecutionEnvironmentIndex;
	}

	public void setRequiredExecutionEnvironmentIndex(int requiredExecutionEnvironment) {
		firePropertyChange(REQUIRED_EXECUTION_ENVIRONMENT, this.requiredExecutionEnvironmentIndex,
				this.requiredExecutionEnvironmentIndex = requiredExecutionEnvironment);
	}

	public String getBundleDeployLocation() {
		return bundleDeployLocation;
	}

	public void setBundleDeployLocation(String jco3BundlesExportLocation) {
		firePropertyChange(BUNDLE_DEPLOY_LOCATION, this.bundleDeployLocation, this.bundleDeployLocation = jco3BundlesExportLocation);
	}

	public String getArchiveVersion() {
		return archiveVersion;
	}

	public void setArchiveVersion(String archiveVersion) {
		firePropertyChange(ARCHIVE_VERSION, this.archiveVersion, this.archiveVersion = archiveVersion);
	}

	public String getBundleManifestEntry() {
		return BUNDLE_MANIFEST_ENTRY;
	}

}
