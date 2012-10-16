/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;


/**
 * The MBean content provider.
 */
public class MBeanContentProvider implements ITreeContentProvider {

	/** The MBeans. */
	private Map<String, MBeanDomain> domains;

	/**
	 * The constructor.
	 */
	public MBeanContentProvider() {
		domains = new HashMap<String, MBeanDomain>();
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return domains.values().toArray(new MBeanDomain[0]);
	}

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MBeanDomain) {
			return ((MBeanDomain) parentElement).getMBeanTypes();
		}

		if (parentElement instanceof MBeanType) {
			return ((MBeanType) parentElement).getMBeanNames();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof MBeanType) {
			return ((MBeanType) element).getMBeanDomain();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof MBeanDomain) {
			return true;
		}

		if (element instanceof MBeanType) {
			return ((MBeanType) element).getMBeanNames().length > 1;
		}

		return false;
	}

	/*
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		domains.clear();
	}

	/**
	 * Refreshes the content provider.
	 * 
	 * @param jvm
	 *            The active JVM
	 */
	public void refresh(IActiveJvm jvm) {
		Map<String, MBeanDomain> newDomains = new HashMap<String, MBeanDomain>();

		// add or update elements
		for (ObjectName objectName : getObjectNames(jvm)) {
			MBeanDomain domain;
			String domainName = objectName.getDomain();
			if (domains.containsKey(domainName)) {
				domain = domains.get(domainName);
				newDomains.put(domainName, domain);
			} else if (newDomains.containsKey(domainName)) {
				domain = newDomains.get(domainName);
			} else {
				domain = new MBeanDomain(domainName);
				newDomains.put(domainName, domain);
			}

			String typeName = JMXUtils.getTypeName(objectName);
			MBeanType type = domain.getMBeanType(typeName);
			if (type == null) {
				type = new MBeanType(jvm, typeName, domain);
				domain.putMBeanType(typeName, type);
			} else {
				type.setJvm(jvm);
			}

			MBeanName mBeanName = type.getMBeanName(objectName);
			if (mBeanName == null) {
				MBeanInfo info = getMBeanInfo(jvm, objectName);
				mBeanName = new MBeanName(objectName, jvm,
						info.getNotifications().length > 0);
			}
			type.addMBeanName(mBeanName);
		}

		domains = newDomains;
	}

	/**
	 * Gets the object names.
	 * 
	 * @param jvm
	 *            The active JVM
	 * @return The object names
	 */
	private Set<ObjectName> getObjectNames(IActiveJvm jvm) {
		try {
			return jvm.getMBeanServer().queryNames(null);
		} catch (JvmCoreException e) {
			Activator.log(IStatus.ERROR, Messages.getMBeanObjectNamesFailedMsg,
					e);
		}
		return new HashSet<ObjectName>();
	}

	/**
	 * Gets the MBean info.
	 * 
	 * @param jvm
	 *            The active JVM
	 * @param objectName
	 *            The object name
	 * @return The MBean info
	 */
	private MBeanInfo getMBeanInfo(IActiveJvm jvm, ObjectName objectName) {
		try {
			return jvm.getMBeanServer().getMBeanInfo(objectName);
		} catch (JvmCoreException e) {
			Activator.log(IStatus.ERROR, Messages.getMBeanInfoFailedMsg, e);
			return null;
		}
	}
}
