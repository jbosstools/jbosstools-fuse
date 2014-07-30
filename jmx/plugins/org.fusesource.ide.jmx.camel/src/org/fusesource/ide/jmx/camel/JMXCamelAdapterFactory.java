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

package org.fusesource.ide.jmx.camel;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.AbstractNodeFacade;

public class JMXCamelAdapterFactory implements IAdapterFactory {

	private Class<?>[] classes = {AbstractNode.class};
	private List<Class<?>> adapterClasses = Arrays.asList(classes);

	public List<Class<?>> getAdapterClasses() {
		// TODO Auto-generated method stub
		return adapterClasses;
	}

	public Class<?>[] getAdapterList() {
		return classes;
	}

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (AbstractNode.class.equals(adapterType)) {
			return toAbstractNode(adaptableObject);
		}
		return null;
	}

	protected Object toAbstractNode(Object adaptableObject) {
		if (adaptableObject instanceof AbstractNodeFacade) {
			AbstractNodeFacade facade = (AbstractNodeFacade) adaptableObject;
			return facade.getAbstractNode();
		}
		return null;
	}
}
