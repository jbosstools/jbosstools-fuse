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
 * Custom mappings are field mappings with a user-supplied class which is used
 * to customize the mapping from source to target.
 */
public interface CustomMapping extends FieldMapping {

    /**
     * Returns the mapping class used for this custom mapping.
     * 
     * @return mapping class name
     */
    String getMappingClass();

    /**
     * Returns the operation in the mapping class used for this custom mapping.
     * 
     * @return mapping operation name
     */
    String getMappingOperation();
}
