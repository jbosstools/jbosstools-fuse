/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ModelBuilder {

    public static Model fromJavaClass(Class<?> javaClass) {
        Model model = new Model(javaClass.getSimpleName(), javaClass.getName());
        List<Field> fields = new LinkedList<Field>();
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
        return "[" + listType.getName() + "]";
    }

    public static String getListType(String listName) {
        return listName.split("\\[")[1].split("\\]")[0];
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
                    fieldClass = (Class<?>) ((ParameterizedType) ft).getActualTypeArguments()[0];
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
        LinkedList<Field> fields = new LinkedList<Field>();
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
            || fieldClass.getName().equals(String.class.getName())
            || fieldClass.getName().startsWith("java.")
            || Number.class.isAssignableFrom(fieldClass)
            || (fieldClass instanceof Class<?> && ((Class<?>)fieldClass).isEnum());
        
        return !excluded;
    }
}
