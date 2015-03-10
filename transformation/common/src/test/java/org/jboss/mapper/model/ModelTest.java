/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.mapper.model;

import junit.framework.Assert;

import org.junit.Test;

public class ModelTest {
    
    @Test
    public void testForCycle() {
        // make sure we don't end up with a stack overflow on infinite recursion
        try {
            Model model = new Model("foo", "org.example");
            Model child = model.addChild("abc", "xyz");
            child.setModelClass(Object.class);
            child.addChild("def", "uvw");
            model.hashCode();
        } catch (StackOverflowError ex) {
            Assert.fail("hashCode() has a cycle : " + ex.toString());
        }
    }
    
    @Test
    public void testHashWithNullFields() {
        try {
            Model model = new Model("foo", "org.example");
            model.addChild("abc", "java.lang.String");
            model.hashCode();
        } catch (NullPointerException ex) {
            Assert.fail("hashCode() doesn't deal with nulls : " + ex.toString());
        }
        
    }
}
