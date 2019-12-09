/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tools.fuse.transformation.core.dozer;

import java.util.Arrays;
import java.util.List;

import org.jboss.tools.fuse.transformation.core.dozer.DozerUtil;
import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;
import org.junit.Assert;
import org.junit.Test;

import example.DeepList;
import example.ListsAndNestedTypes;

public class DozerUtilTest {
    
    @Test
    public void getIndex() throws Exception {
        Assert.assertEquals(new Integer(1), DozerUtil.getIndex("field[1]"));
        Assert.assertEquals(new Integer(22), DozerUtil.getIndex("field[22]"));
        Assert.assertNull(DozerUtil.getIndex("field"));
    }
    
    @Test
    public void getFieldIndexes() throws Exception {
        List<Integer> twoLists = Arrays.asList(new Integer[] {null, 3, null, 2});
        List<Integer> oneField = Arrays.asList(new Integer[] {null});
        List<Integer> oneList = Arrays.asList(new Integer[] {null, 0});
        
        Assert.assertEquals(twoLists, DozerUtil.getFieldIndexes("a.b[3].c.d[2]"));
        Assert.assertEquals(oneField, DozerUtil.getFieldIndexes("a"));
        Assert.assertEquals(oneList, DozerUtil.getFieldIndexes("a.b[0]"));
    }
    
    @Test
    public void removeIndexes() throws Exception {
        Assert.assertEquals("a.b.c", DozerUtil.removeIndexes("a[0].b.c"));
        Assert.assertEquals("a.b.c",  DozerUtil.removeIndexes("a[0].b[0].c[0]"));
        Assert.assertEquals("a.b.c",  DozerUtil.removeIndexes("a.b.c"));
    }
    
    @Test
    public void getFieldName() throws Exception {
        final String directChild = "listOfAs";
        final String nestedChild = "nested1.classB.B1";
        final String fieldL1name = "fieldL1";
        final String fieldL2name = "listL1[0].fieldL2";
        final String fieldL3name = "listL1[0].listL2[1].fieldL3";
        Model fieldL1Model = ModelBuilder.fromJavaClass(DeepList.class).get(fieldL1name);
        Model fieldL2Model = ModelBuilder.fromJavaClass(DeepList.class).get("listL1.fieldL2");
        Model fieldL3Model = ModelBuilder.fromJavaClass(DeepList.class).get("listL1.listL2.fieldL3");
        Model nestedChildModel = ModelBuilder.fromJavaClass(ListsAndNestedTypes.class).get(nestedChild);
        Model directChildModel = ModelBuilder.fromJavaClass(ListsAndNestedTypes.class).get(directChild);
        
        Assert.assertEquals(nestedChild, DozerUtil.getFieldName(
                nestedChildModel, ListsAndNestedTypes.class.getName()));
        Assert.assertEquals(directChild, DozerUtil.getFieldName(
                directChildModel, ListsAndNestedTypes.class.getName()));
        Assert.assertEquals(fieldL1name, DozerUtil.getFieldName(
                fieldL1Model, DeepList.class.getName(), Arrays.asList(new Integer[] {null})));
        Assert.assertEquals(fieldL2name, DozerUtil.getFieldName(
                fieldL2Model, DeepList.class.getName(), Arrays.asList(new Integer[] {0, null})));
        Assert.assertEquals(fieldL3name, DozerUtil.getFieldName(
                fieldL3Model, DeepList.class.getName(), Arrays.asList(new Integer[] {0, 1, null})));
        
    }
}

