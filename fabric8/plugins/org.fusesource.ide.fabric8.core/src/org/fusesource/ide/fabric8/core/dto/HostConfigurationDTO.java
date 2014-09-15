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

import java.util.Arrays;
import java.util.List;

/**
 * @author lhein
 *
 */
public abstract class HostConfigurationDTO<T extends HostConfigurationDTO> {
	private String hostName;
	private Integer port;
	private String username;
	private String password;
	private Integer maximumContainerCount;
	private List<String> tags;

	protected HostConfigurationDTO() {
	}

	protected HostConfigurationDTO(String hostName) {
		this.hostName = hostName;
	}

	// Fluid API
	// -------------------------------------------------------------------------
	public T hostName(String hostName) {
		setHostName(hostName);
		return (T) this;
	}

	public T port(Integer port) {
		setPort(port);
		return (T) this;
	}

	public T username(final String username) {
		this.username = username;
		return (T) this;
	}

	public T password(final String password) {
		this.password = password;
		return (T) this;
	}

	public T maximumContainerCount(final Integer maximumContainerCount) {
		this.maximumContainerCount = maximumContainerCount;
		return (T) this;
	}

	public T tags(final List<String> tags) {
		this.tags = tags;
		return (T) this;
	}

	public T tags(String... tags) {
		return tags(Arrays.asList(tags));
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaximumContainerCount() {
		return maximumContainerCount;
	}

	public void setMaximumContainerCount(Integer maximumContainerCount) {
		this.maximumContainerCount = maximumContainerCount;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
