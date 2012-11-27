package org.fusesource.ide.fabric.navigator.cloud;

import java.net.URI;

import org.fusesource.ide.fabric.actions.jclouds.CloudDetails;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;


public class CloudDetailsView {

	private final CloudDetails details;

	public CloudDetailsView(CloudDetails details) {
		this.details = details;
	}

	public URI getApiDocumentation() {
		return getProvider().getApiMetadata().getDocumentation();
	}

	public URI getHomepage() {
		if (getProvider() != null) {
		return getProvider().getHomepage().orNull();
		} else {
			return null;
		}
	}

	public String getIdentityName() {
		return getIdentityName();
	}

	public String getType() {
		return getProvider().getId();
	}

	public ProviderMetadata getProvider() {
		return details.getProvider();
	}
	
	public ApiMetadata getApi() {
		return details.getApi();
	}

	public String getName() {
		return details.getName();
	}

	public String getIdentity() {
		return details.getIdentity();
	}

	public String getOwner() {
		return details.getOwnerId();
	}

	public String getProviderName() {
		return details.getProviderName();
	}

	public String getProviderId() {
		return details.getProviderId();
	}


}
