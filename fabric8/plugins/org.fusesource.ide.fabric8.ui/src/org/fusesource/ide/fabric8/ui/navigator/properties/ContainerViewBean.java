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

package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;
import org.fusesource.ide.fabric8.core.dto.ContainerDTO;
import org.fusesource.ide.fabric8.core.dto.ProfileDTO;
import org.fusesource.ide.fabric8.core.dto.VersionDTO;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class ContainerViewBean {
	public static final Joiner spaceJoiner = Joiner.on(" ");

	private final ContainerDTO container;

	public static ContainerViewBean toContainerViewBean(Object element) {
		if (element instanceof BeanPropertySource) {
			BeanPropertySource source = (BeanPropertySource) element;
			return toContainerViewBean(source.getBean());
		}
		if (element instanceof ContainerViewBean) {
			return (ContainerViewBean) element;
		}
		if (element instanceof ContainerDTO) {
			return new ContainerViewBean((ContainerDTO) element);
		}
		return null;
	}

	public ContainerViewBean(ContainerDTO container) {
		this.container = container;
	}

	public List<ContainerDTO> getChildren() {
		return container.getChildren();
	}

	public String getId() {
		return container.getId();
	}

	public String getVersion() {
		return Fabrics.getVersionName(version());
	}
	
	public VersionDTO version() {
		return container.getVersion();
	}


	public List<String> getJmxDomains() {
		return container.getJmxDomains();
	}

	public String getJmxUrl() {
		return container.getJMXUrl();
	}

	public String getLocation() {
		return container.getLocation();
	}

	public ContainerDTO getParent() {
		return container.getParent();
	}

	public String getProfileIds() {
		List<String> ids = container.getProfileIDs();
		if (ids == null || ids.size() == 0) {
			return null;
		}
		return spaceJoiner.join(ids);
	}

	public List<ProfileDTO> getProfiles() {
		return container.getProfiles();
	}

	public String getSshUrl() {
		return container.getSshUrl();
	}

	public String getType() {
		return container.getType();
	}


	public boolean isAlive() {
		return container.isAlive();
	}

	public String getStatus() {
		String status = container.getProvisionStatus();
		if ((isRoot() || !isAlive()) && status != null && status.startsWith("not provisioned")) {
			return "";
		}
		return status;
	}

	public boolean isProvisioningComplete() {
		return container.isProvisioningComplete();
	}

	public boolean isRoot() {
		return container.isRoot();
	}

	public void setLocation(String newLocation) {
		container.setLocation(newLocation);
	}

	public void setProfiles(List<ProfileDTO> arg0) {
		container.setProfileDTOs(arg0);
	}

	public void setVersion(VersionDTO arg0) {
		container.setVersion(arg0);
	}

	public void start() {
		container.start();
	}

	public void stop() {
		container.stop();
	}

	public ContainerDTO container() {
		return container;
	}

}
