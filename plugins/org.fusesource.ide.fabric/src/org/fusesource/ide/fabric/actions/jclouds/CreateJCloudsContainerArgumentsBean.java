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

package org.fusesource.ide.fabric.actions.jclouds;

import java.util.Map;

import org.fusesource.fabric.api.CreateJCloudsContainerOptions;
import org.fusesource.fabric.api.JCloudsInstanceType;
import org.fusesource.ide.commons.util.BeanSupport;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;


public class CreateJCloudsContainerArgumentsBean extends BeanSupport {
	private static final long serialVersionUID = 1L;

	private CreateJCloudsContainerOptions.Builder delegate = new CreateJCloudsContainerOptions.Builder();
	private Image image;
	private Location location;
	private Hardware hardware;
	private OsFamily family;

	// those will be added to the CreateJCloudsContainerOptions soon and therefor
	// the two private instance vars are only placeholders meanwhile
	private Map<String,String> serviceOptions;
	private Map<String,String> containerOptions;

	public CreateJCloudsContainerOptions delegate() {
		return delegate.build();
	}
	
	public CreateJCloudsContainerOptions.Builder getBuilder() {
		return delegate;
	}
	
	/**
	 * Sets the properties from the given cloud details object
	 */
	public void setProperties(CloudDetails details) {
		setProviderName(details.getProviderId());
		delegate = delegate.identity(details.getIdentity());
		delegate = delegate.owner(details.getOwnerId());
		delegate = delegate.credential(details.getCredential());
	}

	public String getGroup() {
		return delegate.getGroup();
	}

	public String getHardwareId() {
		return delegate.getHardwareId();
	}

	public String getImageId() {
		return delegate.getImageId();
	}

	public JCloudsInstanceType getInstanceType() {
		return delegate.getInstanceType();
	}

	public String getLocationId() {
		return delegate.getLocationId();
	}

	public String getProviderName() {
		return delegate.getProviderName();
	}

	public String getUser() {
		return delegate.getUser();
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

	public void setGroup(String group) {
		delegate = delegate.group(group);
	}

	public void setHardwareId(String hardwareId) {
		delegate = delegate.hardwareId(hardwareId);
	}

	public void setImageId(String imageId) {
		delegate = delegate.imageId(imageId);
	}

	public void setInstanceType(JCloudsInstanceType instanceType) {
		delegate = delegate.instanceType(instanceType);
	}

	public void setLocationId(String locationId) {
		delegate = delegate.locationId(locationId);
	}

	public void setProviderName(String providerName) {
		delegate = delegate.providerName(providerName);
	}

	public void setUser(String user) {
		delegate = delegate.user(user);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}


	// For editing...

	/**
	 * @param family the family to set
	 */
	public void setFamily(OsFamily value) {
		setOsFamily(value != null ? value.name() : null);
		this.family = value;
	}

	/**
	 * @return the family
	 */
	public OsFamily getFamily() {
		return this.family;
	}

	public void setHardware(Hardware value) {
		setHardwareId(value != null ? value.getId() : null);
		this.hardware = value;
	}

	public Image getImage() {
		return image;
	}

	public Location getLocation() {
		return location;
	}

	public Hardware getHardware() {
		return hardware;
	}

	public void setLocation(Location value) {
		setLocationId(value != null ? value.getId() : null);
		this.location = value;
	}

	public void setImage(Image value) {
		setImageId(value != null ? value.getId() : null);
		this.image = value;
	}

	/**
	 * @return the osFamily
	 */
	public String getOsFamily() {
		return this.delegate.getOsFamily();
	}

	/**
	 * @param osFamily the osFamily to set
	 */
	public void setOsFamily(String osFamily) {
		delegate = this.delegate.osFamily(osFamily);
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion() {
		return this.delegate.getOsVersion();
	}

	/**
	 * @param osVersion the osVersion to set
	 */
	public void setOsVersion(String osVersion) {
		delegate = this.delegate.osVersion(osVersion);
	}

	/**
	 * @return the containerOptions
	 */
	public Map<String, String> getContainerOptions() {
		return this.containerOptions;
	}

	/**
	 * @param containerOptions the containerOptions to set
	 */
	public void setContainerOptions(Map<String, String> containerOptions) {
		this.containerOptions = containerOptions;
	}

	/**
	 * @return the serviceOptions
	 */
	public Map<String, String> getServiceOptions() {
		return this.serviceOptions;
	}

	/**
	 * @param serviceOptions the serviceOptions to set
	 */
	public void setServiceOptions(Map<String, String> serviceOptions) {
		this.serviceOptions = serviceOptions;
	}
}
