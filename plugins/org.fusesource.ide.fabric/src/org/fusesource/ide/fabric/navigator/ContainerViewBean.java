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

package org.fusesource.ide.fabric.navigator;

import java.util.List;

import io.fabric8.api.Container;
import io.fabric8.api.Profile;
import io.fabric8.api.Version;
import org.fusesource.ide.commons.ui.propsrc.BeanPropertySource;

import scala.actors.threadpool.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

public class ContainerViewBean {
	public static final Joiner spaceJoiner = Joiner.on(" ");

	private final Container container;


	public static ContainerViewBean toContainerViewBean(Object element) {
		if (element instanceof BeanPropertySource) {
			BeanPropertySource source = (BeanPropertySource) element;
			return toContainerViewBean(source.getBean());
		}
		if (element instanceof ContainerViewBean) {
			return (ContainerViewBean) element;
		}
		if (element instanceof Container) {
			return new ContainerViewBean((Container) element);
		}
		return null;
	}

	public ContainerViewBean(Container container) {
		this.container = container;
	}

	public Container[] getChildren() {
		return container.getChildren();
	}

	public String getId() {
		return container.getId();
	}

	public String getVersion() {
		return Fabrics.getVersionName(version());
	}

	public Version version() {
		return container.getVersion();
	}


	public List<String> getJmxDomains() {
		return container.getJmxDomains();
	}

	public String getJmxUrl() {
		return container.getJmxUrl();
	}

	public String getLocation() {
		return container.getLocation();
	}

	public Container getParent() {
		return container.getParent();
	}

	public String getProfileIds() {
		Profile[] profiles = getProfiles();
		if (profiles == null || profiles.length == 0) {
			return null;
		}
		Iterable<String> ids = Iterables.transform(Arrays.asList(profiles), new Function<Profile,String>() {

			@Override
			public String apply(Profile profile) {
				return profile.getId();
			}});
		return spaceJoiner.join(ids);
	}

	public Profile[] getProfiles() {
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

	public void setLocation(String arg0) {
		container.setLocation(arg0);
	}

	public void setProfiles(Profile[] arg0) {
		container.setProfiles(arg0);
	}

	public void setVersion(Version arg0) {
		container.setVersion(arg0);
	}

	public void start() {
		container.start();
	}

	public void stop() {
		container.stop();
	}

	public Container container() {
		return container;
	}

}
