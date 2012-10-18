package org.fusesource.ide.camel.editor;

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
