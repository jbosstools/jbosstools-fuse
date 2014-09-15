/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.core.dto;

/**
 * @author lhein
 *
 */
public class SshHostConfigurationDTO extends HostConfigurationDTO<SshHostConfigurationDTO> {
	
	private String path;
	private String passPhrase;
	private String privateKeyFile;
	private String preferredAddress;

	public SshHostConfigurationDTO() {
	}

	public SshHostConfigurationDTO(String hostName) {
		super(hostName);
	}

	public SshHostConfigurationDTO path(String path) {
		setPath(path);
		return this;
	}

	public SshHostConfigurationDTO passPhrase(final String passPhrase) {
		this.passPhrase = passPhrase;
		return this;
	}

	public SshHostConfigurationDTO privateKeyFile(final String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
		return this;
	}

	public SshHostConfigurationDTO preferredAddress(final String preferredAddress) {
		this.preferredAddress = preferredAddress;
		return this;
	}

	// Properties
	// -------------------------------------------------------------------------
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public void setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
	}

	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public String getPreferredAddress() {
		return preferredAddress;
	}

	public void setPreferredAddress(String preferredAddress) {
		this.preferredAddress = preferredAddress;
	}
}
