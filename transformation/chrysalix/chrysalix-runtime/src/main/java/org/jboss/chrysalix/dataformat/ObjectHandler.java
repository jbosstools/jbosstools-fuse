/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.chrysalix.dataformat;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.chrysalix.DataFormatHandler;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.common.Arg;

/**
 * Handles the interpretation and conversion of Java objects to and from {@link Node nodes}.
 */
// TODO handle generics in type names
public class ObjectHandler implements DataFormatHandler {

	private static final Map<String, Class<?>> PRIMITIVE_CLASSES_BY_NAME = new HashMap<>();

	static {
		PRIMITIVE_CLASSES_BY_NAME.put("byte", byte.class);
		PRIMITIVE_CLASSES_BY_NAME.put("char", char.class);
		PRIMITIVE_CLASSES_BY_NAME.put("boolean", boolean.class);
		PRIMITIVE_CLASSES_BY_NAME.put("short", short.class);
		PRIMITIVE_CLASSES_BY_NAME.put("int", int.class);
		PRIMITIVE_CLASSES_BY_NAME.put("long", long.class);
		PRIMITIVE_CLASSES_BY_NAME.put("float", float.class);
		PRIMITIVE_CLASSES_BY_NAME.put("double", double.class);
	}

	private void addFieldsToNodes(Object object,
                                  Class<?> type,
                                  Node node) throws Exception {
		for (Field field : type.getDeclaredFields()) {
			if (field.isSynthetic() || field.isEnumConstant()) {
				continue;
			}
			// Skip if already exists with same namespace and name
			Node fieldNode = node.child(type.getName(), field.getName());
			if (fieldNode != null) {
				continue;
			}
			fieldNode = node.addChild(type.getName(), field.getName(), field.getType().getTypeName());
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			Object val = field.get(object);
			if (val == null) {
				fieldNode.setList(field.getType().isArray()
				                  || Collection.class.isAssignableFrom(field.getType())
				                  || Map.class.isAssignableFrom(field.getType()));
			} else {
				addObjectFieldsToNode(val, field.getType(), fieldNode);
			}
		}
	}

	private void addObjectFieldsToNode(Object object,
	                                   Class<?> type,
	                                   Node node) throws Exception {
		if (type == null) { // Ends recursion for superclasses
			return;
		}
		node.setValue(object);
		if (isPrimitiveOrWrapper(type)) {
			return;
		}
		if (type.isEnum()) {
			addFieldsToNodes(object, type, node);
			return;
		}
		if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			node.setList(true);
			if (object instanceof Collection) {
				object = ((Collection<?>)object).toArray();
			}
			if (object instanceof Map) {
				Map<?, ?> map = (Map<?, ?>)object;
				Object[][] array = new Object[map.size()][1];
				int ndx = 0;
				for (Entry<?, ?> entry : map.entrySet()) {
					array[ndx++][0] = entry.getValue();
				}
				object = array;
			}
		}
		if (type.isArray() || (object != null && object.getClass().isArray())) {
			node.setList(true);
			if (object == null || Array.getLength(object) == 0) {
				return;
			}
			type = object.getClass().getComponentType();
    		int len = Array.getLength(object);
    		for (int ndx = 0; ndx < len; ndx++) {
    			Node elemNode = node.addChild(namespace(type), name(type), type.getTypeName());
				Object val = Array.get(object, ndx);
				addObjectFieldsToNode(val, val == null ? type : val.getClass(), elemNode);
			}
			return;
		}
		if (!type.isInterface()) {
			addObjectFieldsToNode(object, type.getSuperclass(), node); // Add superclass nodes
		}
		for (Class<?> objInterface : type.getInterfaces()) {
			addObjectFieldsToNode(object, objInterface, node);
		}
		addFieldsToNodes(object, type, node);
	}

	private boolean isPrimitiveOrWrapper(Class<?> type) {
		return (type.isPrimitive()
				|| type == String.class
				|| Number.class.isAssignableFrom(type)
				|| type == Boolean.class
				|| type == Character.class
				|| type == Byte.class);
	}

	private String name(Class<?> type) {
		if (type.isAnonymousClass()) {
			String name = type.getName();
			return name.substring(name.lastIndexOf('$') + 1);
		}
		return type.getSimpleName();
	}

	private String namespace(Class<?> type) {
		return type.getPackage() == null ? null : type.getPackage().getName();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.jboss.chrysalix.DataFormatHandler#toSourceNode(java.lang.Object, org.jboss.chrysalix.Node)
	 */
	@Override
	public Node toSourceNode(Object object,
							 Node parent) throws Exception {
		Arg.notNull(object, "object");
		Arg.notNull(parent, "parent");
		Class<?> type = object.getClass();
		Node node = parent.addChild(namespace(type), name(type), type.getTypeName());
		addObjectFieldsToNode(object, type, node);
		return node;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.jboss.chrysalix.DataFormatHandler#toTargetData(org.jboss.chrysalix.Node)
	 */
	@Override
	public Object toTargetData(Node targetNode) throws Exception {
		Arg.notNull(targetNode, "targetNode");
		Class<?> valType = targetNode.value().getClass();
		if (isPrimitiveOrWrapper(valType) || valType.isEnum()) {
			return targetNode.value();
		}
		if (targetNode.isList()) {
			String typeName = targetNode.type().substring(0, targetNode.type().indexOf('['));
			Class<?> type = PRIMITIVE_CLASSES_BY_NAME.get(typeName);
			if (type == byte.class) {
				byte[] obj = (byte[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (byte)node.value();
				}
				return obj;
			}
			if (type == char.class) {
				char[] obj = (char[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (char)node.value();
				}
				return obj;
			}
			if (type == boolean.class) {
				boolean[] obj = (boolean[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (boolean)node.value();
				}
				return obj;
			}
			if (type == short.class) {
				short[] obj = (short[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (short)node.value();
				}
				return obj;
			}
			if (type == int.class) {
				int[] obj = (int[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (int)node.value();
				}
				return obj;
			}
			if (type == long.class) {
				long[] obj = (long[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (long)node.value();
				}
				return obj;
			}
			if (type == float.class) {
				float[] obj = (float[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (float)node.value();
				}
				return obj;
			}
			if (type == double.class) {
				double[] obj = (double[])Array.newInstance(type, targetNode.children().length);
				int ndx = 0;
				for (Node node : targetNode.children()) {
					obj[ndx++] = (double)node.value();
				}
				return obj;
			}
			type = Class.forName(typeName);
			Object[] obj = (Object[])Array.newInstance(type, targetNode.children().length);
			int ndx = 0;
			for (Node node : targetNode.children()) {
				obj[ndx++] = toTargetData(node);
			}
			return obj;
		}
		return valType.newInstance();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.jboss.chrysalix.DataFormatHandler#toTargetNode(java.lang.Object, org.jboss.chrysalix.Node)
	 */
	@Override
	public Node toTargetNode(Object object,
							 Node parent) throws Exception {
		return toSourceNode(object, parent);
	}
}
