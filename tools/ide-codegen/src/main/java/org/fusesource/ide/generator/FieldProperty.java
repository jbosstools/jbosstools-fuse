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
package org.fusesource.ide.generator;

import java.lang.reflect.Field;

import org.fusesource.scalate.introspector.Property;

public class FieldProperty<T> implements Property<T> {

    private Field field;

    public FieldProperty(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Class<?> propertyType() {
        return field.getType();
    }

    @Override
    public String label() {
        return field.getName();
    }

    @Override
    public String description() {
        return field.getName();
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public boolean optional() {
        return false;
    }

    @Override
    public Object apply(T t) {
        return null;
    }

    @Override
    public Object evaluate(T t) {
        try {
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void set(T t, Object o) {
        try {
            field.set(t, o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "FieldProperty(" + name() + ": " + propertyType().getName() + ")";
    }

}
