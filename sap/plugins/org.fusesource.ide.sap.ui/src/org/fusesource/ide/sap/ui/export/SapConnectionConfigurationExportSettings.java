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
package org.fusesource.ide.sap.ui.export;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.fusesource.ide.sap.ui.Messages;

public class SapConnectionConfigurationExportSettings {
	
	public static final String BLUEPRINT_FILE = Messages.SapConnectionConfigurationExportSettings_BlueprintFile;

	public static final String SPRING_FILE = Messages.SapConnectionConfigurationExportSettings_SpringFile;
	
	///////////////////////////////////////////////////////
	// Property Names

	public static final String EXPORT_LOCATION = "exportLocation"; //$NON-NLS-1$
	
	public static final String EXPORT_FILE_TYPE = "exportFileType"; //$NON-NLS-1$
	
	//
	///////////////////////////////////////////////////////

	public enum ExportFileType {
		BLUEPRINT(BLUEPRINT_FILE), SPRING(SPRING_FILE);

		private final String display;
		
		ExportFileType(String display) {
			this.display = display;
		}
		
		public String getDisplay() {
			return display;
		}

	}
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private String exportLocation = ""; //$NON-NLS-1$
	
	private ExportFileType exportFileType = ExportFileType.BLUEPRINT;

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

	public String getExportLocation() {
		return exportLocation;
	}

	public void setExportLocation(String exportLocation) {
		firePropertyChange(EXPORT_LOCATION, this.exportLocation, this.exportLocation = exportLocation);
	}

	public ExportFileType getExportFileType() {
		return exportFileType;
	}

	public void setExportFileType(ExportFileType exportFileType) {
		firePropertyChange(EXPORT_FILE_TYPE, this.exportFileType, this.exportFileType = exportFileType);
	}

}
