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

package org.fusesource.ide.camel.model;

import java.util.Map;
import java.util.Set;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ToDefinition;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.generated.Messages;
import org.fusesource.ide.commons.util.XmlUtilities;

/**
 * @author lhein
 */
public class Endpoint extends AbstractNode {

	public static final String PROPERTY_URI = "Endpoint.Uri";

	private static final String ICON = "endpoint.png";

	private String uri = "";

	public Endpoint() {
	}

	/**
	 * Creates a new instance or reuses an existing instance on a route
	 */
	public static Endpoint newInstance(ToDefinition definition, RouteContainer parent) {
		if (parent instanceof RouteSupport) {
			RouteSupport route = (RouteSupport) parent;
			return route.getOrCreateEndpoint(definition, parent);
		}
		return new Endpoint(definition, parent);
	}

	public Endpoint(ToDefinition definition, RouteContainer parent) {
		super(parent);
		loadPropertiesFromCamelDefinition(definition);
		loadChildrenFromCamelDefinition(definition);
	}

	public Endpoint(FromDefinition definition, RouteContainer parent) {
		super(parent);
		// No children of a From so no need...
		//addChildrenFromCamelDefinition(definition);
		setId(definition.getId());
		setDescription(definition.getDescriptionText());
		setUri(CamelModelHelper.getUri(definition));
	}

	public Endpoint(String uri) {
		this.uri = uri;
		this.uri = XmlUtilities.unescape(uri);
	}

	public Endpoint(Endpoint endpoint) {
		this(endpoint.uri);
		setId(endpoint.getId());
		setDescription(endpoint.getDescription());
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
	    return this.uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		String oldUri = this.uri;
		this.uri = XmlUtilities.unescape(uri);
		if (!isSame(uri, oldUri)) {
			clearImages();
			firePropertyChange(PROPERTY_URI, oldUri, uri);	
		}		
	}

//	@Override
//	public String getNewID() {
//		// lets not create a new ID by default
//		return "";
//	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getIconName()
	 */
	@Override
	public String getIconName() {
		String u = getUri();
		if (u != null) {
			if (u.startsWith("drools:")) {
				return "endpointDrools.png";
			} else if (u.startsWith("jms:") || u.startsWith("activemq") || u.startsWith("mq") || u.startsWith("sjms")) {
				return "endpointQueue.png";
			} else if (u.startsWith("file:") || u.startsWith("ftp") || u.startsWith("sftp") || u.startsWith("jcr") || u.startsWith("scp")) {
				return "endpointFolder.png";
			} else if (u.startsWith("log:") || u.startsWith("hdfs") || u.startsWith("paxlogging")) {
				return "endpointFile.png";
			} else if (u.startsWith("timer:") || u.startsWith("quartz")) {
				return "endpointTimer.png";
			} else if (u.startsWith("elasticsearch:") || u.startsWith("hazelcast:") || u.startsWith("hibernate:") || u.startsWith("jpa:")
					|| u.startsWith("jdbc:") || u.startsWith("sql:") || u.startsWith("ibatis:") || u.startsWith("mybatis:")
					|| u.startsWith("javaspace:") || u.startsWith("jcr:") || u.startsWith("ldap:") || u.startsWith("mongodb:") || u.startsWith("zookeeper:")) {
				return "endpointRepository.png";
			} else if (u.startsWith("twitter:")) {
			    return "endpointTwitter.png";
			} else if (u.startsWith("weather:")) {
			    return "endpointWeather.png";
			} else if (u.startsWith("sap-netweaver:")) {
                return "endpointSAPNetweaver.png";
            } else if (u.startsWith("sap:")) {
                return "endpointSAP.png";
            } else if (u.startsWith("salesforce:")) {
                return "endpointSalesforce.png";
            } else if (u.startsWith("facebook:")) {
                return "endpointFacebook.png";
            }
		}
		return ICON;
	}

	@Override
	public String getDocumentationFileName() {
		return "endpoint";
	}

	@Override
	public String getCategoryName() {
		return "Components";
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#addCustomProperties(java.util.Map)
	 */
	@Override
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		super.addCustomProperties(descriptors);

		PropertyDescriptor desc = new TextPropertyDescriptor(PROPERTY_URI, Messages.propertyLabelEndpointUri);
		desc.setValidator(DEFAULT_STRING_VALIDATOR);
		descriptors.put(PROPERTY_URI, desc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_URI.equals(id)) {
			setUri((String)value);
		} else {
			super.setPropertyValue(id, value);
		}
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.AbstractNode#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_URI.equals(id)) {
			return getUri();
		} else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ToDefinition answer = new ToDefinition();
		CamelModelHelper.setUri(answer, this);
		super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@Override
	public void savePropertiesToCamelDefinition(ProcessorDefinition processor) {
		// TODO Auto-generated method stub
		super.savePropertiesToCamelDefinition(processor);
		if (processor instanceof ToDefinition) {
			ToDefinition node = (ToDefinition) processor;
			CamelModelHelper.setUri(node, this);
		} else {
			throw new IllegalArgumentException(
					"ProcessorDefinition not an instanceof ToDefinition. Was "
							+ processor.getClass().getName());
		}
	}

	@Override
	protected void loadPropertiesFromCamelDefinition(
			ProcessorDefinition processor) {

		super.loadPropertiesFromCamelDefinition(processor);

		if (processor instanceof ToDefinition) {
			ToDefinition node = (ToDefinition) processor;
			String value = CamelModelHelper.getUri(node);
			setUri(value);
		} else {
			throw new IllegalArgumentException(
					"ProcessorDefinition not an instanceof ToDefinition. Was "
							+ processor.getClass().getName());
		}
	}

	public void populateCamelDefinition(FromDefinition from) {
		setId(from.getId());
		setDescription(from.getDescriptionText());
	}

	@Override
	public void appendEndpointUris(Set<String> uris) {
		if (uri != null) {
			String trimmed = uri.trim();
			if (trimmed.length() > 0) {
				uris.add(trimmed);
			}
		}
		super.appendEndpointUris(uris);
	}

	/**
	 * this is an input endpoint if it is not the target on another node
	 */
	public boolean isInputEndpoint() {
		return !getSourceConnections().isEmpty() && getTargetConnections().isEmpty();
	}

	/**
	 * this is an output endpoint if it is a target and there are no targets from here
	 */
	public boolean isOutputEndpoint() {
		return getSourceConnections().isEmpty() && !getTargetConnections().isEmpty();
	}

	/**
	 * this method is meant to be overridden by subclasses to build the uri attribute for
	 * the endpoint out of the attributes the subclass knows about
	 * 
	 * the default implementation of Endpoint is to return the current URI
	 * 
	 * @return
	 */
	public String buildUri() {
	    return this.uri;
	}
}
