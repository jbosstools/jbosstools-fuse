/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ModelBuilder {
	
	private ModelBuilder(){
	}

    public static Model fromJavaClass(Class<?> javaClass) {
        Model model = new Model(javaClass.getSimpleName(), javaClass.getName());
        List<Field> fields = new LinkedList<>();
        getFields(javaClass, fields);
        addFieldsToModel(fields, model);
        model.setModelClass(javaClass);
        return model;
    }

    public static Class<?> getFieldType(Field field) {
        Class<?> type;

        if (field.getType().isArray()) {
            return field.getType().getComponentType();
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            Type fieldType = field.getGenericType();
            if (fieldType instanceof ParameterizedType) {
                type = (Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            } else {
                type = Object.class;
            }
        } else {
            type = field.getType();
        }

        return type;
    }

    public static String getListName(Class<?> listType) {
        return "[" + listType.getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String getListType(String listName) {
        return listName.split("\\[")[1].split("\\]")[0]; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private static void addFieldsToModel(List<Field> fields, Model model) {
        for (Field field : fields) {
            Class<?> fieldClass;
            boolean isCollection = false;

            if (field.getType().isArray()) {
                isCollection = true;
                fieldClass = field.getType().getComponentType();
            } else if (Collection.class.isAssignableFrom(field.getType())) {
            	isCollection = true;
            	Type ft = field.getGenericType();
            	if (ft instanceof ParameterizedType) {
            		Object testObject = ((ParameterizedType) ft).getActualTypeArguments()[0];
            		if (testObject instanceof Class) {
            			fieldClass = (Class<?>) testObject;
            		} else {
            			//TODO : support imbricated Collections
            			fieldClass = Object.class;
            		}
            	} else {
            		fieldClass = Object.class;
            	}
            } else {
            	fieldClass = field.getType();
            }
            
            // Create the model for this field
            String fieldTypeName = isCollection ? getListName(fieldClass) : fieldClass.getName();
            Model child = model.addChild(field.getName(), fieldTypeName);
            child.setIsCollection(isCollection);

            // Deal with child fields if necessary
            if (parseChildren(fieldClass)) {
                addFieldsToModel(getFields(fieldClass, model), child);
            }
        }
    }

    private static List<Field> getFields(Class<?> clazz, Model parent) {
        LinkedList<Field> fields = new LinkedList<>();
        boolean cycle = false;
        // convenient place to check for a cycle where a child field references an ancestor
        for (Model pm = parent; pm != null; pm = pm.getParent()) {
            String parentType = pm.isCollection() ? getListType(pm.getType()) : pm.getType();
            if (clazz.getName().equals(parentType)) {
                cycle = true;
                break;
            }
        }
        if (!cycle) {
            getFields(clazz, fields);
        }
        return fields;
    }

    private static void getFields(Class<?> clazz, List<Field> fields) {
        // check if we've hit rock bottom
        if (clazz == null || Object.class.equals(clazz)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                fields.add(field);
            }
        }
        getFields(clazz.getSuperclass(), fields);
    }
    
    private static boolean parseChildren(Class<?> fieldClass) {
        boolean excluded = 
            fieldClass.isPrimitive()
            || String.class.getName().equals(fieldClass.getName())
            || fieldClass.getName().startsWith("java.") //$NON-NLS-1$
            || Number.class.isAssignableFrom(fieldClass)
            || fieldClass.isEnum();
        
        return !excluded;
    }
}
