/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.RGB;
import org.fusesource.ide.jvmmonitor.core.Activator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.JMXUtils;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.MBeanDomain;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.MBeanName;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean.MBeanType;


/**
 * The attribute content provider.
 */
public class AttributeContentProvider implements ITreeContentProvider {

	/** The MBeans. */
	protected Map<String, MBeanDomain> domains;

	/** The attributes. */
	protected Map<ObjectName, List<AttributeNode>> attributes;

	/**
	 * The constructor.
	 */
	public AttributeContentProvider() {
		domains = new HashMap<String, MBeanDomain>();
		attributes = new HashMap<ObjectName, List<AttributeNode>>();
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

		ObjectName objectName = null;
		if (parentElement instanceof MBeanType) {
			MBeanName[] names = ((MBeanType) parentElement).getMBeanNames();
			if (names.length > 1) {
				return names;
			} else if (names.length == 1) {
				objectName = names[0].getObjectName();
			}
		}

		if (parentElement instanceof MBeanName) {
			objectName = ((MBeanName) parentElement).getObjectName();
		}

		if (objectName != null) {
			return attributes.get(objectName).toArray(new AttributeNode[0]);
		}

		if (parentElement instanceof AttributeNode) {
			return ((AttributeNode) parentElement).getChildren().toArray(
					new AttributeNode[0]);
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

		ObjectName objectName = null;
		if (element instanceof MBeanType) {
			MBeanName[] names = ((MBeanType) element).getMBeanNames();
			if (names.length > 1) {
				return true;
			} else if (names.length == 1) {
				objectName = names[0].getObjectName();
			}
		}

		if (element instanceof MBeanName) {
			objectName = ((MBeanName) element).getObjectName();
		}

		if (objectName != null) {
			List<AttributeNode> list = attributes.get(objectName);
			return list != null && list.size() > 0;
		}

		if (element instanceof AttributeNode) {
			return ((AttributeNode) element).hasChildren();
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
		domains = new HashMap<String, MBeanDomain>();

		// add or update elements
		for (ObjectName objectName : getObjectNames(jvm)) {
			MBeanInfo mBeanInfo = getMBeanInfo(jvm, objectName);
			if (mBeanInfo == null) {
				continue;
			}

			List<AttributeNode> mBeanAttributes = new ArrayList<AttributeNode>();
			for (MBeanAttributeInfo attributeInfo : mBeanInfo.getAttributes()) {
				String attributeName = attributeInfo.getName();

				Object value = getContents(jvm, objectName, attributeName);
				addAttributeRoots(mBeanAttributes, new AttributeNode(
						attributeName, null, objectName), value);
			}

			for (AttributeNode node : mBeanAttributes
					.toArray(new AttributeNode[0])) {
				validateAttributes(node);
				if (node.getChildren().size() == 0 && !node.isValidLeaf()) {
					mBeanAttributes.remove(node);
				}
			}
			if (mBeanAttributes.size() == 0) {
				continue;
			}

			attributes.put(objectName, mBeanAttributes);

			String domainName = objectName.getDomain();
			MBeanDomain domain;
			if (domains.containsKey(domainName)) {
				domain = domains.get(domainName);
			} else {
				domain = new MBeanDomain(domainName);
			}
			domains.put(domainName, domain);

			String typeName = JMXUtils.getTypeName(objectName);
			MBeanType type = domain.getMBeanType(typeName);
			if (type == null) {
				type = new MBeanType(jvm, typeName, domain);
				domain.putMBeanType(typeName, type);
			}

			MBeanName mBeanName = type.getMBeanName(objectName);
			if (mBeanName == null) {
				MBeanInfo info = getMBeanInfo(jvm, objectName);
				mBeanName = new MBeanName(objectName, jvm,
						info.getNotifications().length > 0);
			}
			type.addMBeanName(mBeanName);
		}
	}

	/**
	 * Validates the attributes belonging to the given node and its child nodes.
	 * 
	 * @param node
	 *            The attribute node
	 */
	private void validateAttributes(AttributeNode node) {
		AttributeNode[] nodes = node.getChildren().toArray(
				new AttributeNode[0]);
		if (nodes.length == 0 && !node.isValidLeaf()) {
			node.remove();
		}
		for (AttributeNode child : nodes) {
			validateAttributes(child);
		}
		nodes = node.getChildren().toArray(new AttributeNode[0]);
		if (nodes.length == 0 && !node.isValidLeaf()) {
			node.remove();
		}
	}

	/**
	 * Adds the attribute to the given list.
	 * 
	 * @param mBeanAttributes
	 *            The attributes
	 * @param root
	 *            The root attribute
	 * @param value
	 *            The value
	 */
	private void addAttributeRoots(List<AttributeNode> mBeanAttributes,
			AttributeNode root, Object value) {
		if (value instanceof CompositeData) {
			mBeanAttributes.add(root);
			CompositeData compositeData = (CompositeData) value;
			CompositeType type = compositeData.getCompositeType();
			for (String key : type.keySet()) {
				AttributeNode attribute = new AttributeNode(key, root);

				root.addChild(attribute);
				addAttributeItems(attribute, compositeData.get(key));
			}
		} else if (value instanceof TabularData) {
			mBeanAttributes.add(root);
			TabularData tabularData = (TabularData) value;
			for (Object keyList : tabularData.keySet()) {
				@SuppressWarnings("unchecked")
				Object[] keys = ((List<Object>) keyList).toArray(new Object[0]);
				AttributeNode attribute = new AttributeNode(
						String.valueOf(keys[0]), root);

				root.addChild(attribute);
				addAttributeItems(attribute, tabularData.get(keys));
			}
		} else if (value instanceof Long || value instanceof Integer
				|| value instanceof Double) {
			root.setValidLeaf(true);
			root.setRgb(getRGB(root.getQualifiedName()));
			mBeanAttributes.add(root);
		}
	}

	/**
	 * Gets the arbitrary RGB with given string.
	 * 
	 * @param string
	 *            The string to determine RGB
	 * @return The RGB
	 */
	private RGB getRGB(String string) {
		int hashCode = string.hashCode();
		int r = (hashCode >> 3) % 256;
		int g = (hashCode >> 1) % 256;
		int b = hashCode % 256;
		return new RGB(Math.abs(r), Math.abs(g), Math.abs(b));
	}

	/**
	 * Adds the attribute to the given list.
	 * 
	 * @param parent
	 *            The parent attribute
	 * @param value
	 *            The value
	 */
	private void addAttributeItems(AttributeNode parent, Object value) {
		if (value instanceof CompositeData) {
			CompositeData compositeData = (CompositeData) value;
			CompositeType type = compositeData.getCompositeType();
			for (String key : type.keySet()) {
				AttributeNode attribute = new AttributeNode(key, parent);
				parent.addChild(attribute);
				addAttributeItems(attribute, compositeData.get(key));
			}
		} else if (value instanceof TabularData) {
			TabularData tabularData = (TabularData) value;
			for (Object keyList : tabularData.keySet()) {
				@SuppressWarnings("unchecked")
				Object[] keys = ((List<Object>) keyList).toArray(new Object[0]);
				AttributeNode attribute = new AttributeNode(
						String.valueOf(keys[0]), parent);
				parent.addChild(attribute);
				addAttributeItems(attribute, tabularData.get(keys));
			}
		} else if (value instanceof Long || value instanceof Integer
				|| value instanceof Double) {
			parent.setRgb(getRGB(parent.getQualifiedName()));
			parent.setValidLeaf(true);
		}
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
			Activator.log(IStatus.ERROR, Messages.getMBeanNamesFailedMsg, e);
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

	/**
	 * Gets the contents.
	 * 
	 * @param jvm
	 *            The JVM
	 * @param objectName
	 *            The object name
	 * @param attributeName
	 *            The attribute name
	 * @return The attribute contents
	 */
	private Object getContents(IActiveJvm jvm, ObjectName objectName,
			String attributeName) {
		try {
			return jvm.getMBeanServer().getAttribute(objectName, attributeName);
		} catch (JvmCoreException e) {
			Activator
			.log(IStatus.ERROR, Messages.getMBeanAttributeFailedMsg, e);
		} catch (RuntimeMBeanException e) {
			// not supported
			return null;
		}
		return null;
	}
}
