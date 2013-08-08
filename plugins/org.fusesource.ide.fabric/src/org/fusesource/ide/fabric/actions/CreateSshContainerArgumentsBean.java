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

package org.fusesource.ide.fabric.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.fusesource.fabric.service.ssh.CreateSshContainerOptions;
import org.fusesource.ide.commons.util.BeanSupport;


public class CreateSshContainerArgumentsBean extends BeanSupport {

	private CreateSshContainerOptions.Builder delegate = CreateSshContainerOptions.builder();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		propertyChangeSupport.firePropertyChange(e);
	}

	public CreateSshContainerOptions delegate() {
		return delegate.build();
	}

	public String getHost() {
		return delegate.getHost();
	}

	public String getPassword() {
		return delegate.getPassword();
	}

	public String getPath() {
		return delegate.getPath();
	}

	public int getPort() {
		return delegate.getPort();
	}

	public int getRetryDelay() {
		return delegate.getRetryDelay();
	}

	public int getSshRetries() {
		return delegate.getSshRetries();
	}

	public String getUsername() {
		return delegate.getUsername();
	}
	
	public String getName() {
		return delegate.getName();
	}

	public boolean isDebugAgent() {
		return false;
		// TODO
		// return delegate.isDebugContainer();
	}

	public void setDebugAgent(boolean debugAgent) {
		// TODO
		// delegate.setDebugContainer(debugAgent);
	}

	public void setHost(String host) {
		delegate.setHost(host);
	}

	public void setPassword(String password) {
		delegate.setPassword(password);
	}

	public void setPath(String path) {
		delegate.setPath(path);
	}

	public void setPort(int port) {
		delegate.setPort(port);
	}

	public void setRetryDelay(int retryDelay) {
		delegate.setRetryDelay(retryDelay);
	}

	public void setSshRetries(int sshRetries) {
		delegate.setSshRetries(sshRetries);
	}

	public void setUsername(String username) {
		delegate.setUsername(username);
	}
	
	public void setName(String name) {
		delegate = delegate.name(name);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
