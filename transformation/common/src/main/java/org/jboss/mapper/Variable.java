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
public interface Variable {

    /**
     * Get the value of this Variable.
     * 
     * @return value of the variable
     */
    public String getValue();
    
    /**
     * Set the variable value.
     * 
     * @param value variable value.
     */
    public void setValue(String value);
    
    /**
     * Get the name of this Variable.
     * 
     * @return name of the variable
     */
    public String getName();
    
    /**
     * Set the variable name.
     * 
     * @param name variable name
     */
    public void setName(String name);
}
