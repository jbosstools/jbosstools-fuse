/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.features.impl.IIndependenceSolver;

/**
 * @author lhein
 */
public class CamelModelIndependenceSolver implements IIndependenceSolver {
	
	private static Map<String, Object> objectMap = new HashMap<String, Object>();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.IIndependenceSolver#getKeyForBusinessObject(java.lang.Object)
	 */
	@Override
	public String getKeyForBusinessObject(Object bo) {
		String result = null;
		if(bo != null) {
			result = String.valueOf(bo.hashCode());
			
			if(!objectMap.containsKey(result))
				objectMap.put(result, bo);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.IIndependenceSolver#getBusinessObjectForKey(java.lang.String)
	 */
	@Override
	public Object getBusinessObjectForKey(String key) {
		return objectMap.get(key);
	}
}
