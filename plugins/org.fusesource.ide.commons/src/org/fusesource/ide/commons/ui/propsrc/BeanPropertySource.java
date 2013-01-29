package org.fusesource.ide.commons.ui.propsrc;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.fusesource.ide.commons.tree.HasOwner;


public class BeanPropertySource implements IPropertySource2, HasOwner {
	public static final long THROTTLE = 10 * 1000L; // 10 secs
	
	private static long lastGCRun = System.currentTimeMillis();	
	private static Map<String, BeanCache> cachedValues = new HashMap<String, BeanCache>();
			
	class BeanCache {
		private long cacheTime;
		private Object value;
		
		/**
		 * 
		 */
		public BeanCache(Object value) {
			this.cacheTime = System.currentTimeMillis();
			this.value = value;
		}
		
		/**
		 * @return the cacheTime
		 */
		private long getCacheTime() {
			return this.cacheTime;
		}
		
		/**
		 * @return the value
		 */
		public Object getValue() {
			if ((System.currentTimeMillis() - lastGCRun) > THROTTLE) {
				cleanup();
			}
			return this.value;
		}
		
		private void cleanup() {
			ArrayList<String> keysToBeCollected = new ArrayList<String>();
			Iterator<String> it = cachedValues.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				BeanCache bc = cachedValues.get(key);
				if ((System.currentTimeMillis() - bc.cacheTime) > THROTTLE) {
					// cache expired...clean it up
					keysToBeCollected.add(key);
				}
			}
			for (String k : keysToBeCollected) {
				cachedValues.remove(k);
			}	
			lastGCRun = System.currentTimeMillis();
		}
	}
	
	private final Object bean;
	private final BeanTypePropertyMetadata metadata;
	private Object owner;


	public BeanPropertySource(Object bean) throws IntrospectionException {
		this(bean, bean.getClass());
	}

	public BeanPropertySource(Object bean, Class<?> beanType) throws IntrospectionException {
		this.bean = bean;
		metadata = BeanTypePropertyMetadata.beanMetadata(beanType);

	}

	@Override
	public Object getOwner() {
		return owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	public Object getBean() {
		return bean;
	}

	@Override
	public boolean isPropertyResettable(Object id) {
		return false;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return metadata.getPropertyDescriptors();
	}

	@Override
	public Object getPropertyValue(Object id) {
		String cacheKey = id + "~" + bean.hashCode();
		if (cachedValues.containsKey(cacheKey)) {
			return cachedValues.get(cacheKey).getValue();
		}
		Object value = metadata.getPropertyValue(bean,  id);
		cachedValues.put(cacheKey, new BeanCache(value));
		return value;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return getPropertyValue(id) != null;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		metadata.setPropertyValue(bean,  id, value);
	}

	@Override
	public void resetPropertyValue(Object id) {
	}
}
