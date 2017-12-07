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
package org.fusesource.ide.wsdl2rest.ui.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author brianf
 *
 */
public class Wsdl2RestOptions {
	
	private String wsdlURL;
	private String projectName;
	private String destinationJava;
	private String destinationCamel;
	private String targetServiceAddress;
	private String beanClassName;
	
    private final PropertyChangeSupport changeSupport =
            new PropertyChangeSupport(this);
	
	public String getWsdlURL() {
		return wsdlURL;
	}
	public void setWsdlURL(String wsdlURL) {
		firePropertyChange("wsdlURL", this.wsdlURL, this.wsdlURL = wsdlURL);
	}
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		firePropertyChange("projectName", this.projectName, this.projectName = projectName);
	}
	
	public String getDestinationJava() {
		return destinationJava;
	}
	
	public void setDestinationJava(String destinationJava) {
		firePropertyChange("destinationJava", this.destinationJava, this.destinationJava = destinationJava);
	}
	
	public String getDestinationCamel() {
		return destinationCamel;
	}
	
	public void setDestinationCamel(String destinationCamel) {
		firePropertyChange("destinationCamel", this.destinationCamel, this.destinationCamel = destinationCamel);
	}
	
	public String getTargetServiceAddress() {
		return targetServiceAddress;
	}
	
	public void setTargetServiceAddress(String targetServiceAddress) {
		firePropertyChange("targetServiceAddress", this.targetServiceAddress, this.targetServiceAddress = targetServiceAddress);
	}

	public String getBeanClassName() {
		return beanClassName;
	}
	public void setBeanClassName(String beanClassName) {
		firePropertyChange("beanClassName", this.beanClassName, this.beanClassName = beanClassName);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
