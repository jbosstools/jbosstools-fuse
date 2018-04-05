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
public class RestVerbElementEIP extends Eip {
	public static final String PROP_ID = "id"; //$NON-NLS-1$
	public static final String PROP_URI = "uri"; //$NON-NLS-1$
	public static final String PROP_METHOD = "method"; //$NON-NLS-1$
	public static final String PROP_CONSUMES = "consumes"; //$NON-NLS-1$
	public static final String PROP_PRODUCES = "produces"; //$NON-NLS-1$
	public static final String PROP_SKIPBINDINGONERRORCODE = "skipBindingOnErrorCode"; //$NON-NLS-1$
	public static final String PROP_ENABLECORS = "enableCORS"; //$NON-NLS-1$
	public static final String PROP_TYPE = "type"; //$NON-NLS-1$
	public static final String PROP_OUTTYPE = "outType"; //$NON-NLS-1$
	public static final String PROP_ROUTEID = "routeId"; //$NON-NLS-1$
	public static final String PROP_APIDOCS = "apiDocs"; //$NON-NLS-1$
	public static final String ARG_TYPE = "type"; //$NON-NLS-1$
	public static final String ARG_VALUE = "value"; //$NON-NLS-1$
	public static final String PROP_NAME = "name"; //$NON-NLS-1$
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$
	public static final String TAG_PROPERTY = "property"; //$NON-NLS-1$
	public static final String TAG_ARGUMENT = "argument"; //$NON-NLS-1$
	public static final String PROP_TO_URI = "toURI"; //$NON-NLS-1$
	
	private Map<String, Parameter> parameters = new HashMap<>();
	
	private String eipLabel = null;
	
	public RestVerbElementEIP() {
		this(AbstractRestCamelModelElement.REST_NODE_NAME);
	}

	public RestVerbElementEIP(String label) {
		this.eipLabel = label;
		createParameter(PROP_ID, String.class.getName(), true);
		createParameter(PROP_URI, String.class.getName(), true);
		createParameter(PROP_METHOD, String.class.getName());
		createParameter(PROP_CONSUMES, String.class.getName());
		createParameter(PROP_PRODUCES, String.class.getName());
		createParameter(PROP_SKIPBINDINGONERRORCODE, Boolean.class.getName());
		createParameter(PROP_ENABLECORS, Boolean.class.getName());
		createParameter(PROP_TYPE, String.class.getName());
		createParameter(PROP_OUTTYPE, String.class.getName());
		createParameter(PROP_ROUTEID, String.class.getName());
		createParameter(PROP_APIDOCS, Boolean.class.getName());
		createParameter(PROP_TO_URI, String.class.getName());
		setProperties(parameters);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#canHaveChildren()
	 */
	@Override
	public boolean canHaveChildren() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#canBeAddedToCamelContextDirectly()
	 */
	@Override
	public boolean canBeAddedToCamelContextDirectly() {
		return false;
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
		return this.eipLabel;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.eipLabel;
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
