package org.fusesource.ide.fabric.camel;

import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.AbstractNodeFacade;

import scala.actors.threadpool.Arrays;


public class FabricCamelAdapterFactory implements IAdapterFactory {

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
