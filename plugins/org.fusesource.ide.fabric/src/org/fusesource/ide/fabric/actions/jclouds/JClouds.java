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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.commons.util.Strings;
import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

public class JClouds {

	public static final ApiMetadata EMPTY_API = new ApiMetadata() {
		
		@Override
		public org.jclouds.apis.ApiMetadata.Builder toBuilder() {
			return null;
		}
		
		@Override
		public Set<TypeToken<? extends View>> getViews() {
			return null;
		}
		
		@Override
		public String getVersion() {
			return null;
		}
		
		@Override
		public String getName() {
			return " ";
		}
		
		@Override
		public String getIdentityName() {
			return " ";
		}
		
		@Override
		public String getId() {
			return "EMPTY_API";
		}
		
		@Override
		public String getEndpointName() {
			return null;
		}
		
		@Override
		public URI getDocumentation() {
			return null;
		}
		
		@Override
		public Properties getDefaultProperties() {
			return null;
		}
		
		@Override
		public Set<Class<? extends Module>> getDefaultModules() {
			return null;
		}
		
		@Override
		public Optional<String> getDefaultIdentity() {
			return null;
		}
		
		@Override
		public Optional<String> getDefaultEndpoint() {
			return null;
		}
		
		@Override
		public Optional<String> getDefaultCredential() {
			return null;
		}
		
		@Override
		public Optional<String> getCredentialName() {
			return null;
		}
		
		@Override
		public TypeToken<? extends Context> getContext() {
			return null;
		}
		
		@Override
		public Optional<String> getBuildVersion() {
			return null;
		}
	};
	
	public static final ProviderMetadata EMPTY_PROVIDER = new ProviderMetadata() {
		
		@Override
		public Builder toBuilder() {
			return null;
		}
		
		@Override
		public String getName() {
			return " ";
		}
		
		@Override
		public Set<String> getLinkedServices() {
			return null;
		}
		
		@Override
		public Set<String> getIso3166Codes() {
			return null;
		}
		
		@Override
		public String getId() {
			return "EMPTY_PROVIDER";
		}
		
		@Override
		public Optional<URI> getHomepage() {
			return null;
		}
		
		@Override
		public String getEndpoint() {
			return null;
		}
		
		@Override
		public Properties getDefaultProperties() {
			return null;
		}
		
		@Override
		public Optional<URI> getConsole() {
			return null;
		}
		
		@Override
		public ApiMetadata getApiMetadata() {
			return EMPTY_API;
		}
	};
	
    public static Iterable<ProviderMetadata> getComputeProviders() {
        Iterable<ProviderMetadata> retVal = Providers.viewableAs(TypeToken.of(ComputeServiceContext.class));
        ArrayList<ProviderMetadata> providers = new ArrayList<ProviderMetadata>();
        providers.add(EMPTY_PROVIDER);
        Iterator<ProviderMetadata> ite = retVal.iterator();
        while (ite.hasNext()) {
        	providers.add(ite.next());
        }
        return providers;
    }

    public static Iterable<ApiMetadata> getComputeApis() {        
    	Iterable<ApiMetadata> retVal = Apis.viewableAs(TypeToken.of(ComputeServiceContext.class));
        ArrayList<ApiMetadata> apis = new ArrayList<ApiMetadata>();
        apis.add(EMPTY_API);
        Iterator<ApiMetadata> ite = retVal.iterator();
        while (ite.hasNext()) {
        	apis.add(ite.next());
        }
        return apis;
    }

    public static ClassLoader getJCloudsClassLoader() {
        return ProviderMetadata.class.getClassLoader();
    }

    public static String text(ComputeMetadata md) {
        return Strings.getOrElse(md.getName(), md.getId());
    }
    public static String text(ApiMetadata md) {
        return Strings.getOrElse(md.getName(), md.getId());
    }

    public static String text(Hardware md) {
        return Strings.getOrElse(md.getName(), md.getId());
    }

    public static String text(OperatingSystem md) {
        return Strings.getOrElse(md.getDescription(), md.getName());
    }

    public static String text(ResourceMetadata<?> rm) {
        return Strings.getOrElse(rm.getName(), rm.getProviderId());
    }

    public static String text(Location location) {
        return Strings.getOrElse(location.getDescription(), location.getId());
    }

    public static String text(ProviderMetadata provider) {
        return Strings.getOrElse(provider.getName(), provider.getId());
    }

    public static ProviderMetadata getProvider(String providerId) {
    	if (providerId != null) {
    		try {
    			return Providers.withId(providerId);
    		} catch (NoSuchElementException ex) {
    			return JClouds.EMPTY_PROVIDER;
    		}
    	}
    	return null;
    }

    public static ApiMetadata getApi(String apiId) {
        if (apiId != null) {
        	try {
        		return Apis.withId(apiId);	
        	} catch (NoSuchElementException ex) {
        		return JClouds.EMPTY_API;
        	}
        	
        }
        return null;
    }

    public static String getId(ProviderMetadata provider) {
        if (provider != null) {
            return provider.getId();
        }
        return null;
    }

    public static String getId(ApiMetadata api) {
        if (api != null) {
            return api.getId();
        }
        return null;
    }


    public static <T extends ResourceMetadata<?>> List<T> sortedList(Collection<T> coll) {
        List<T> answer = Lists.newArrayList(coll);
        Collections.sort(answer, new Comparator<T>(){
            @Override
            public int compare(T o1, T o2) {
                return Objects.compare(text(o1), text(o2));
            }});
        return answer;

    }

    public static <T extends Location> List<T> sortedLocationList(Collection<T> coll) {
        List<T> answer = Lists.newArrayList(coll);
        Collections.sort(answer, new Comparator<T>(){
            @Override
            public int compare(T o1, T o2) {
                return Objects.compare(text(o1), text(o2));
            }});
        return answer;

    }

    public static ComputeMetadata asComputeMetadata(Object element) {
        if (element instanceof ComputeMetadata) {
            return (ComputeMetadata) element;
        }
        return null;
    }

    public static NodeMetadata asNodeMetadata(Object element) {
        if (element instanceof NodeMetadata) {
            return (NodeMetadata) element;
        }
        return null;
    }

    public static Location asLocation(Object element) {
        if (element instanceof Location ) {
            return (Location) element;
        }
        ComputeMetadata value = asComputeMetadata(element);
        if (value != null) {
            return value.getLocation();
        }
        return null;
    }

    public static String id(ComputeMetadata node) {
        if (node != null) {
            return node.getId();
        }
        return null;
    }

    public static Status getState(ComputeMetadata n) {
        NodeMetadata node = asNodeMetadata(n);
        if (node != null) {
            return node.getStatus();
        }
        return Status.ERROR;
    }


}
