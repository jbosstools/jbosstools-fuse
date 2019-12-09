/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Repository;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;

public interface ICamelManagerService {
	/**
	 * creates and returns the camel model for a specific version and karaf runtime 
	 * 
	 * @param camelVersion the camel version to load
	 * @return	the camel model using karaf runtime provider
	 */
	CamelModel getCamelModelForKarafRuntimeProvider(String camelVersion);
	
	/**
	 * creates and returns the camel model for a specific version and runtime
	 * 
	 * @param runtimeProvider	the name of the runtime provider
	 * @return	the camel model
	 */
	CamelModel getCamelModel(String camelVersion, String runtimeProvider);
	
	/**
	 * use this method to add additional repositories to use for dependency lookups
	 * 
	 * @param repositories
	 * @param coords
	 */
	void updateMavenRepositoryLookup(List<Repository> repositories, CamelCatalogCoordinates coords);
	
	/**
	 * returns the schema provider 
	 * 
	 * @param coords
	 * @return
	 */
	CamelSchemaProvider getCamelSchemaProvider(CamelCatalogCoordinates coords);

	/**
	 * returns a map of properties for a given URI of a camel endpoint
	 * 
	 * @param uri
	 * @param coords
	 * @return
	 * @throws URISyntaxException
	 */
	Map<String, String> getEndpointProperties(String uri, CamelCatalogCoordinates coords) throws URISyntaxException;
	
	/**
	 * tests an expression in a default camel context ran locally
	 * 
	 * @param language
	 * @param expression
	 * @return	null if test ok otherwise the exception text
	 */
	String testExpression(String language, String expression);
	
	/**
	 * tests a duration string and returns the amount of millis
	 *  
	 * @param duration	the duration string like 10h(ours)5m(inutes)30s(econds)
	 * @return	the value in milliseconds or -1 on failure
	 */
	long durationToMillis(String duration);
	
	/**
	 * parses a uri query
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	Map<String, Object> parseQuery(String uri) throws URISyntaxException;

	/**
	 * creates a uri query
	 * 
	 * @param parameters
	 * @return
	 * @throws URISyntaxException
	 */
	String createQuery(Map<String, Object> parameters) throws URISyntaxException;
	
	/**
	 * checks wether the camel version is available from the m2 repos
	 * 
	 * @param camelVersion	the version to check for 
	 * @return	true if available
	 */
	boolean isCamelVersionExisting(String camelVersion);
		
}
