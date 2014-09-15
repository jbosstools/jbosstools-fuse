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

package org.fusesource.ide.fabric8.ui.actions.jclouds;

public class CloudDetailsKey {

	private final String identity;
	private final String credential;
	private final String ownerId;
	private final String providerId;
	private final String apiId;
	private final String endpoint;


	public CloudDetailsKey(String identity, String credential, String ownerId,
			String providerId, String apiId, String endpoint) {
		super();
		this.identity = identity;
		this.credential = credential;
		this.ownerId = ownerId;
		this.providerId = providerId;
		this.apiId = apiId;
		this.endpoint = endpoint;
	}


	public String getIdentity() {
		return identity;
	}


	public String getCredential() {
		return credential;
	}


	public String getOwnerId() {
		return ownerId;
	}


	public String getProviderId() {
		return providerId;
	}


	public String getApiId() {
		return apiId;
	}


	public String getEndpoint() {
		return endpoint;
	}


	@Override
	public String toString() {
		return "CloudDetailsKey [identity=" + identity + ", credential="
				+ credential + ", ownerId=" + ownerId + ", providerId="
				+ providerId + ", apiId=" + apiId + ", endpoint=" + endpoint
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiId == null) ? 0 : apiId.hashCode());
		result = prime * result
				+ ((credential == null) ? 0 : credential.hashCode());
		result = prime * result
				+ ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result
				+ ((providerId == null) ? 0 : providerId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloudDetailsKey other = (CloudDetailsKey) obj;
		if (apiId == null) {
			if (other.apiId != null)
				return false;
		} else if (!apiId.equals(other.apiId))
			return false;
		if (credential == null) {
			if (other.credential != null)
				return false;
		} else if (!credential.equals(other.credential))
			return false;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (providerId == null) {
			if (other.providerId != null)
				return false;
		} else if (!providerId.equals(other.providerId))
			return false;
		return true;
	}
	
	

}
