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
package org.jboss.mapper;

/**
 * A Literal is a String value which can be assigned to a target field in a
 * mapping.
 */
public class Literal {

    private String value;

    /**
     * Create a new Literal.
     * 
     * @param value literal value
     */
    public Literal(String value) {
        this.value = value;
    }

    /**
     * Get the value of this Literal.
     * 
     * @return string value of the literal
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Literal) || obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        return ((Literal) obj).getValue().equals(value);
    }

    @Override
    public int hashCode() {
        return 37 * 7 + value.hashCode();
    }

    @Override
    public String toString() {
        return "literal: " + value;
    }
}
