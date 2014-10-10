/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
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
