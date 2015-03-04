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
package org.jboss.mapper.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ModelBuilder {

    public static Model fromJavaClass(Class<?> javaClass) {
        Model model = new Model(javaClass.getSimpleName(), javaClass.getName());
        addFieldsToModel(getFields(javaClass), model);
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
            String fieldType;
            List<Field> childFields = null;
            boolean isCollection = false;

            if (field.getType().isArray()) {
                isCollection = true;
                fieldType = getListName(field.getType().getComponentType());
                childFields = getFields(field.getType().getComponentType());
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                isCollection = true;
                Type ft = field.getGenericType();
                if (ft instanceof ParameterizedType) {
                    Class<?> ftClass =
                            (Class<?>) ((ParameterizedType) ft).getActualTypeArguments()[0];
                    fieldType = getListName(ftClass);
                    childFields = getFields(ftClass);
                } else {
                    fieldType = getListName(Object.class);
                }
            } else {
                fieldType = field.getType().getName();
                if (!field.getType().isPrimitive()
                        && !field.getType().getName().equals(String.class.getName())
                        && !field.getType().getName().startsWith("java.lang")
                        && !Number.class.isAssignableFrom(field.getType())) {

                    childFields = getFields(field.getType());
                }
            }

            Model child = model.addChild(field.getName(), fieldType);
            child.setIsCollection(isCollection);
            if (childFields != null) {
                addFieldsToModel(childFields, child);
            }
        }
    }

    private static List<Field> getFields(Class<?> clazz) {
        LinkedList<Field> fields = new LinkedList<Field>();
        getFields(clazz, fields);
        return fields;
    }

    private static void getFields(Class<?> clazz, List<Field> fields) {
        // check if we've hit rock bottom
        if (clazz == null || Object.class.equals(clazz)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }
        getFields(clazz.getSuperclass(), fields);
    }
}
