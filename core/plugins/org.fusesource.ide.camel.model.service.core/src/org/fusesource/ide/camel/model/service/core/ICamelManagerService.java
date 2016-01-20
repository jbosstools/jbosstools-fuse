/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core;

import java.net.URISyntaxException;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;

public interface ICamelManagerService {

	public static final String CAMEL_VERSION_PROPERTY = "camel.version"; //$NON-NLS-1$

	/**
	 * creates and returns the camel model for a specific version
	 * 
	 * @return	the camel model
	 */
	CamelModel getCamelModel();
	
	/**
	 * returns the schema provider 
	 * 
	 * @return
	 */
	CamelSchemaProvider getCamelSchemaProvider();

	/**
	 * returns a map of properties for a given URI of a camel endpoint
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	Map<String, String> getEndpointProperties(String uri) throws URISyntaxException;
	
	/**
	 * returns the scheme used in the endpoint uri
	 * 
	 * @param uri
	 * @return
	 */
	String getEndpointScheme(String uri);
	
	/**
	 * creates an endpoint uri from the given scheme and properties
	 * 
	 * @param scheme
	 * @param properties
	 * @return
	 * @throws URISyntaxException
	 */
	String createEndpointUri(String scheme, Map<String, String> properties) throws URISyntaxException;
	
	/**
	 * creates an endpoint uri from the given scheme and properties
	 * 
	 * @param scheme
	 * @param properties
	 * @param encode
	 * @return
	 * @throws URISyntaxException
	 */
	String createEndpointUri(String scheme, Map<String, String> properties, boolean encode) throws URISyntaxException;
	
	/**
	 * creates the endpoint xml representation for the given scheme and properties
	 * 
	 * @param scheme
	 * @param properties
	 * @return
	 * @throws URISyntaxException
	 */
	String createEndpointXml(String scheme, Map<String, String> properties) throws URISyntaxException;
	
	/**
	 * creates the endpoint xml representation for the given scheme and properties
	 * 
	 * @param scheme
	 * @param properties
	 * @param encode
	 * @return
	 * @throws URISyntaxException
	 */
	String createEndpointXml(String scheme, Map<String, String> properties, boolean encode) throws URISyntaxException;
	
	/**
	 * tests an expression in a default camel context ran locally
	 * 
	 * @param language
	 * @param expression
	 * @return	null if test ok otherwise the exception text
	 */
	String testExpression(String language, String expression);
}
