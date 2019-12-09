/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lheinema
 */
public class GlobalBeanEIP extends Eip {
	public static final String PROP_ID = "id"; //$NON-NLS-1$
	public static final String PROP_CLASS = "class"; //$NON-NLS-1$
	public static final String PROP_SCOPE = "scope"; //$NON-NLS-1$
	public static final String PROP_DEPENDS_ON = "depends-on"; //$NON-NLS-1$
	public static final String PROP_INIT_METHOD = "init-method"; //$NON-NLS-1$
	public static final String PROP_DESTROY_METHOD = "destroy-method"; //$NON-NLS-1$
	public static final String PROP_FACTORY_METHOD = "factory-method"; //$NON-NLS-1$
	public static final String PROP_FACTORY_BEAN = "factory-bean"; //$NON-NLS-1$
	public static final String ARG_TYPE = "type"; //$NON-NLS-1$
	public static final String ARG_VALUE = "value"; //$NON-NLS-1$
	public static final String PROP_NAME = "name"; //$NON-NLS-1$
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$
	public static final String TAG_PROPERTY = "property"; //$NON-NLS-1$
	public static final String TAG_ARGUMENT = "argument"; //$NON-NLS-1$
	public static final String TAG_CONSTRUCTOR_ARG = "constructor-arg"; //$NON-NLS-1$
	public static final String PROP_FACTORY_REF = "factory-ref"; //$NON-NLS-1$
	
	private Map<String, Parameter> parameters = new HashMap<>();
	
	public GlobalBeanEIP() {
		createParameter(PROP_ID, String.class.getName(), true);
		createParameter(PROP_CLASS, String.class.getName(), true);
		createParameter(PROP_SCOPE, String.class.getName());
		createParameter(PROP_DEPENDS_ON, String.class.getName());
		createParameter(PROP_INIT_METHOD, String.class.getName());
		createParameter(PROP_DESTROY_METHOD, String.class.getName());
		createParameter(PROP_FACTORY_METHOD, String.class.getName());
		createParameter(PROP_FACTORY_BEAN, String.class.getName());
		createParameter(PROP_FACTORY_REF, String.class.getName());
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
		return AbstractCamelModelElement.BEAN_NODE;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.eips.Eip#getTitle()
	 */
	@Override
	public String getTitle() {
		return AbstractCamelModelElement.BEAN_NODE;
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
