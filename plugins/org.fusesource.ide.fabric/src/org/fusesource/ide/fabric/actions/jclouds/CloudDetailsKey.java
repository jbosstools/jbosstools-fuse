package org.fusesource.ide.fabric.actions.jclouds;

public class CloudDetailsKey {

	private final String identity;
	private final String credential;
	private final String ownerId;
	private final String providerId;

	public CloudDetailsKey(String identity, String credential, String ownerId, String providerId) {
		this.identity = identity;
		this.credential = credential;
		this.ownerId = ownerId;
		this.providerId = providerId;
	}


	@Override
	public String toString() {
		return "CloudDetailsKey [identity=" + identity + ", providerId=" + providerId + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credential == null) ? 0 : credential.hashCode());
		result = prime * result + ((identity == null) ? 0 : identity.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((providerId == null) ? 0 : providerId.hashCode());
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
		if (credential == null) {
			if (other.credential != null)
				return false;
		} else if (!credential.equals(other.credential))
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


}
