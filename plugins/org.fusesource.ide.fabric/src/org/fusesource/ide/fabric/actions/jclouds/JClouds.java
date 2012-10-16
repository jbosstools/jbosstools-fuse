package org.fusesource.ide.fabric.actions.jclouds;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.fusesource.fabric.service.jclouds.JcloudsContainerProvider;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.commons.util.Strings;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.ProviderPredicates;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class JClouds {
	private static List<ProviderMetadata> providers;

	public static List<ProviderMetadata> getProviders() {
		if (providers == null) {
			ServiceLoader<ProviderMetadata> services = ServiceLoader.load(ProviderMetadata.class, getJCloudsClassLoader());
			if (Iterables.isEmpty(services)) {
				services = ServiceLoader.load(ProviderMetadata.class, JClouds.class.getClassLoader());
			}
			/**
			 * TODO how to filter compute providers?
			 */
			/*
			Iterable<ProviderMetadata> computeProviders = filter(services, ProviderPredicates.type(ProviderMetadata.COMPUTE_TYPE));
			providers = Lists.newArrayList(computeProviders);
			 */
			providers = Lists.newArrayList(services);

			Collections.sort(providers, new Comparator<ProviderMetadata>(){

				@Override
				public int compare(ProviderMetadata o1, ProviderMetadata o2) {
					return Objects.compare(o1.getName(), o2.getName());
				}});

			/*
			for (ProviderMetadata provider : providers) {
				System.out.println("Provider: " + provider.getName());
			}
			 */
		}
		return providers;
	}

	public static ClassLoader getJCloudsClassLoader() {
		return ProviderMetadata.class.getClassLoader();
	}


	public static String text(ComputeMetadata md) {
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
		if (!Strings.isBlank(providerId)) {
			return Iterables.find(getProviders(), ProviderPredicates.id(providerId));
		}
		return null;
	}

	public static String getId(ProviderMetadata provider) {
		if (provider != null) {
			return provider.getId();
		}
		return null;
	}

	/**
	 * Binds the various providers we've found to the agent provider
	 */
	public static void bindProviders(JcloudsContainerProvider jclouds) {
		List<ProviderMetadata> list = getProviders();
		for (ProviderMetadata provider : list) {
		}

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
