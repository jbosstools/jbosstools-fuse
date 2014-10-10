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

import org.fusesource.scalate.introspector.Property;

public class ConvertedProperty<T> implements Property<T> {
    private Property<T> property;
    private Class<?> propertyType;

    public ConvertedProperty(Property<T> property, Class<?> propertyType) {
        this.property = property;
        this.propertyType = propertyType;
    }

    @Override
    public String name() {
        return property.name();
    }

    @Override
    public Class<?> propertyType() {
        return propertyType;
    }

    public Class<?> actualPropertyType() {
        return property.propertyType();
    }

    @Override
    public String label() {
        return property.label();
    }

    @Override
    public String description() {
        return property.description();
    }

    @Override
    public boolean readOnly() {
        return property.readOnly();
    }

    @Override
    public boolean optional() {
        return property.optional();
    }

    @Override
    public Object apply(T t) {
        return null;
    }

    @Override
    public Object evaluate(T t) {
        return property.evaluate(t);
    }

    @Override
    public void set(T t, Object o) {
        property.set(t, o);
    }

    @Override
    public String toString() {
        return "ConvertedProperty(" + actualPropertyType() + ": " + property + ")";
    }

}
