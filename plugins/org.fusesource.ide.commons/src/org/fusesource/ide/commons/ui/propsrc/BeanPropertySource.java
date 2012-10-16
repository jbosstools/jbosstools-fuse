package org.fusesource.ide.commons.ui.propsrc;

import java.beans.IntrospectionException;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.fusesource.ide.commons.tree.HasOwner;


public class BeanPropertySource implements IPropertySource2, HasOwner {
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
		return metadata.getPropertyValue(bean,  id);
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
