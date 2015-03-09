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
 * A Variable represents a key and value mapping, where the variable name is 
 * used as the source in a mapping and is replaced with the variable value.
 */
public class Variable {

    private String name;
    private String value;

    /**
     * Create a new Variable.
     * 
     * @param name variable name
     * @param value variable value
     */
    public Variable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Get the value of this Variable.
     * 
     * @return value of the variable
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Get the name of this Variable.
     * 
     * @return name of the variable
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable) || obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Variable that = (Variable)obj;
        return that.getName().equals(name) && that.getValue().equals(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 37 + name.hashCode();
        hash = hash * 37 + value.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "variable[name:" + name + ",value:" + value + "]";
    }
}
