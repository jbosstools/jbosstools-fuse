package org.fusesource.ide.fabric.views.logs;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.insight.log.LogFilter;
import org.fusesource.insight.log.rest.LogRequest;
import org.fusesource.insight.log.rest.LogResponse;
import org.fusesource.insight.log.rest.LogResponseHit;
import org.fusesource.insight.log.rest.LogResponseHits;
import org.fusesource.fabric.groups.ZooKeeperGroupFactory;
import org.fusesource.fabric.zookeeper.IZKClient;

import scala.actors.threadpool.Arrays;

import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class FabricLogBrowser implements ILogBrowser {
	protected static final MediaType JSON = MediaType.APPLICATION_JSON_TYPE;
	private final Fabric fabric;
	private Client client;

	public FabricLogBrowser(Fabric fabric) {
		this.fabric = fabric;
	}

	@Override
	public void queryLogs(LogContext context, boolean filterChanged) {
		System.out.println("================ Querying logs.....");

		boolean changeClassLoader = false;

		String path = "/insight/log/_search";
		LogFilter logFilter = context.getLogFilter();
		LogRequest search = LogRequest.newInstance(logFilter.getAfterTimestamp());

		if (fabric.getConnector() == null) return;
		
		IZKClient zooKeeper = fabric.getConnector().getZooKeeper();
		Collection<byte[]> members = Collections.EMPTY_LIST;
		try {
			members = ZooKeeperGroupFactory.members(zooKeeper, "/fabric/registry/clusters/elastic-search").values();
		} catch (Exception e) {
			System.out.println("Warning: " + e);
		}
		try {
			checkClientLoaded(changeClassLoader);
		} catch (Throwable e) {
			FabricPlugin.getLogger().warning("Could not configure Jersey REST client: " + e, e);
		}
		if (members.isEmpty()) {
			System.out.println("No elastic search nodes running!");
		}
		for (byte[] data : members) {
			String uriText;
			URI base;
			try {
				uriText = new String(data, "UTF-8");
			} catch (Exception e) {
				FabricPlugin.getLogger().warning("Could not parse data: " + Arrays.toString(data) + " into a URI:  "+ e, e);
				continue;
			}
			try {
				base = new URI(uriText);
			} catch (Exception e) {
				FabricPlugin.getLogger().warning("Failed to parse URI: " + uriText + ". " + e, e);
				continue;
			}

			URI newURI = null;
			String url;

			try {
				newURI = base.resolve(path);
				url = newURI.toURL().toExternalForm();
				System.out.println("Querying: " + url);
			} catch (Exception e) {
				FabricPlugin.getLogger().warning("Failed to create URL from: " + newURI + ". " + e, e);
				continue;
			}

			ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
			try {
				checkClientLoaded(changeClassLoader);
				// .accept(JSON)
				Builder resource = client.resource(url).type(JSON);

				LogResponse result = resource.post(LogResponse.class, search);
				if (result != null) {
					LogResponseHits hits = result.getHits();
					if (hits != null) {
						List<LogResponseHit> hits2 = hits.getHits();
						if (hits2 != null) {
							List<LogEventBean> events = Lists.newArrayList();
							for (LogResponseHit rh : hits2) {
								LogEventBean event = LogEventBean.toLogEventBean(rh.getEvent());
								if (event != null) {
									events.add(event);

									Long seq = event.getSeq();
									if (seq != null) {
										// TODO we should really be getting the maximum ID from the result, not from the actual found items!
										Long maxLogSeq = logFilter.getAfterTimestamp();
										if (maxLogSeq == null || seq > maxLogSeq) {
											logFilter.setAfterTimestamp(seq);
										}
									}

								}
							}
							context.addLogResults(events);
						}
					}
				}
			} catch (Throwable e) {
				FabricPlugin.getLogger().warning("Failed to fetch logs from: " + newURI + ". " + e, e);
				continue;
			} finally {
				if (changeClassLoader) {
					Thread.currentThread().setContextClassLoader(oldClassLoader);
				}

			}
		}
	}


	protected void checkClientLoaded(boolean changeClassLoader) {
		if (client == null) {
			// lets try setting the context class loader
			ClassLoader classLoader = FabricPlugin.getDefault().getClass().getClassLoader();
			if (changeClassLoader) {
				Thread.currentThread().setContextClassLoader(classLoader);
			}
			ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(JacksonJsonProvider.class);
			client = Client.create(config);
			client.setFollowRedirects(true);
			client.addFilter(new LoggingFilter(System.out));
		}
	}

}
