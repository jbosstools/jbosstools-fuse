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

import java.util.HashMap;
import java.util.Map;

/**
 * @author lhein
 */
public class CreateContainerMetadataDTO {

	private String containerName = "<not available>";
	private CreateContainerOptionsDTO createOptions;
	private transient Throwable failure;
	private transient ContainerDTO container;
	private String overridenResolver;
	private String containerType = "karaf";
	private final Map<String, String> containerConfiguration = new HashMap<String, String>();

	public boolean isSuccess() {
		return failure == null;
	}

	public Throwable getFailure() {
		return failure;
	}

	public void setFailure(Throwable failure) {
		this.failure = failure;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public ContainerDTO getContainer() {
		return container;
	}

	public void setContainer(ContainerDTO container) {
		this.container = container;
	}

	public CreateContainerOptionsDTO getCreateOptions() {
		return createOptions;
	}

	public void setCreateOptions(CreateContainerOptionsDTO createOptions) {
		this.createOptions = createOptions;
	}

	public Map<String, String> getContainerConfiguration() {
		return containerConfiguration;
	}

	public String getOverridenResolver() {
		return overridenResolver;
	}

	public void setOverridenResolver(String overridenResolver) {
		this.overridenResolver = overridenResolver;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Container: ").append(containerName).append(".");
		if (createOptions.isEnsembleServer() && createOptions.getZookeeperPassword() != null) {
			sb.append("Registry Password: ").append(createOptions.getZookeeperPassword());
		}
		return sb.toString();
	}
}
