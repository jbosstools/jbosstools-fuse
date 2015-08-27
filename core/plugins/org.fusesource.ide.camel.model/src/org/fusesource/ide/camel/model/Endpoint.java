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
import org.fusesource.ide.foundation.core.xml.XmlEscapeUtility;

/**
 * @author lhein
 */
public class Endpoint extends AbstractNode {

	public static final String PROPERTY_URI = "Endpoint.Uri";
	public static final String PROPERTY_PATTERN = "Endpoint.Pattern";

	private static final String ICON = "endpoint.png";

	private String uri = "";
	private String pattern = null;
	
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
		this.uri = XmlEscapeUtility.unescape(uri);
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
		this.uri = XmlEscapeUtility.unescape(uri);
		if (!isSame(uri, oldUri)) {
			clearImages();
			firePropertyChange(PROPERTY_URI, oldUri, uri);	
		}		
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		String oldPattern = this.pattern;
		this.pattern = pattern;
		if (!isSame(pattern, oldPattern)) {
			firePropertyChange(PROPERTY_PATTERN, oldPattern, pattern);	
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
		if (u != null && u.trim().length()>0) {
			String scheme = null;
			if (u.startsWith("ref:")) {
				// if its a ref we lookup what is the reference scheme
				String refId = u.substring(u.indexOf(":") + 1);
				RouteContainer c = getParent().getParent();
				String refUri = c.getCamelContextEndpointUris().get(refId);
				if (refUri != null) {
					scheme = refUri.substring(0, refUri.indexOf(":")+1);
				} else {
					// seems we have a broken ref
					return ICON;
				}
			} else {
				scheme = u.substring(0, u.indexOf(":")+1);
			}
			
			if (scheme.startsWith("drools:")) {
				return "endpointDrools.png";
			} else if (scheme.startsWith("jms:") || scheme.startsWith("activemq") || scheme.startsWith("mq") || scheme.startsWith("sjms")) {
				return "endpointQueue.png";
			} else if (scheme.startsWith("file:") || scheme.startsWith("ftp") || scheme.startsWith("sftp") || scheme.startsWith("jcr") || scheme.startsWith("scp")) {
				return "endpointFolder.png";
			} else if (scheme.startsWith("log:") || scheme.startsWith("hdfs") || scheme.startsWith("paxlogging")) {
				return "endpointFile.png";
			} else if (scheme.startsWith("timer:") || scheme.startsWith("quartz")) {
				return "endpointTimer.png";
			} else if (scheme.startsWith("elasticsearch:") || scheme.startsWith("hazelcast:") || scheme.startsWith("hibernate:") || scheme.startsWith("jpa:")
					|| scheme.startsWith("jdbc:") || scheme.startsWith("sql:") || scheme.startsWith("ibatis:") || scheme.startsWith("mybatis:")
					|| scheme.startsWith("javaspace:") || scheme.startsWith("jcr:") || scheme.startsWith("ldap:") || scheme.startsWith("mongodb:") || scheme.startsWith("zookeeper:")) {
				return "endpointRepository.png";
			} else if (scheme.startsWith("twitter:")) {
			    return "endpointTwitter.png";
			} else if (scheme.startsWith("weather:")) {
			    return "endpointWeather.png";
			} else if (scheme.startsWith("sap-netweaver:")) {
                return "endpointSAPNetweaver.png";
            } else if (scheme.startsWith("sap:")) {
                return "endpointSAP.png";
            } else if (scheme.startsWith("salesforce:")) {
                return "endpointSalesforce.png";
            } else if (scheme.startsWith("facebook:")) {
                return "endpointFacebook.png";
            } else if (scheme.startsWith("dozer:")) {
                return "endpointDozer.png";
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
		
		desc = new TextPropertyDescriptor(PROPERTY_PATTERN, Messages.propertyLabelEndpointPattern);
		desc.setValidator(DEFAULT_STRING_VALIDATOR);
		descriptors.put(PROPERTY_PATTERN, desc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_URI.equals(id)) {
			setUri((String)value);
		} else if (PROPERTY_PATTERN.equals(id)) {
			setPattern((String)value);
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
		} else if (PROPERTY_PATTERN.equals(id)) {
			return getPattern();
		} else {
			return super.getPropertyValue(id);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcessorDefinition createCamelDefinition() {
		ToDefinition answer = new ToDefinition();
		CamelModelHelper.setUri(answer, this);
		CamelModelHelper.setExchangePattern(answer, this);
		super.savePropertiesToCamelDefinition(answer);
		return answer;
	}

	@Override
	public void savePropertiesToCamelDefinition(ProcessorDefinition processor) {
		super.savePropertiesToCamelDefinition(processor);
		if (processor instanceof ToDefinition) {
			ToDefinition node = (ToDefinition) processor;
			CamelModelHelper.setUri(node, this);
			CamelModelHelper.setExchangePattern(node, this);
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
			value = CamelModelHelper.getExchangePattern(node);
			setPattern(value);
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
