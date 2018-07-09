/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model.eips;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractRestCamelModelElement;

/**
 * @author bfitzpat
 */
public class RestConfigurationElementEIP extends Eip {
	public static final String PROP_HOST = "host"; //$NON-NLS-1$
	public static final String PROP_PORT = "port"; //$NON-NLS-1$
	public static final String PROP_COMPONENT = "component"; //$NON-NLS-1$
	public static final String PROP_BINDINGMODE = "bindingMode"; //$NON-NLS-1$
	public static final String PROP_CONTEXTPATH = "contextPath"; //$NON-NLS-1$
	public static final String PROP_NAME = "name"; //$NON-NLS-1$
	
	private Map<String, Parameter> parameters = new HashMap<>();
	
	public RestConfigurationElementEIP() {
		createParameter(PROP_HOST, String.class.getName(), true);
		createParameter(PROP_PORT, String.class.getName());
		createParameter(PROP_COMPONENT, String.class.getName());
		createParameter(PROP_BINDINGMODE, String.class.getName());
		createParameter(PROP_CONTEXTPATH, String.class.getName());
		setProperties(parameters);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#canHaveChildren()
	 */
	@Override
	public boolean canHaveChildren() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#canBeAddedToCamelContextDirectly()
	 */
	@Override
	public boolean canBeAddedToCamelContextDirectly() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getAllowedChildrenNodeTypes()
	 */
	@Override
	public List<String> getAllowedChildrenNodeTypes() {
		return Arrays.asList(PROP_NAME);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getKind()
	 */
	@Override
	public String getKind() {
		return "model";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getName()
	 */
	@Override
	public String getName() {
		return AbstractRestCamelModelElement.REST_CONFIGURATION_NODE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getTitle()
	 */
	@Override
	public String getTitle() {
		return AbstractRestCamelModelElement.REST_CONFIGURATION_NODE_NAME;
	}
	
	private void createParameter(String name, String jType) {
		createParameter(name, jType, false);
	}
	
	private void createParameter(String name, String jType, boolean mandatory) {
		Parameter outParm = new Parameter();
		outParm.setName(name);
		outParm.setJavaType(jType);
		outParm.setRequired(Boolean.toString(mandatory));
		parameters.put(name, outParm);
	}
}
