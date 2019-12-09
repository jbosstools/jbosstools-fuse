/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.jboss.tools.fuse.transformation.core.model;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.tools.fuse.transformation.core.model.Model;
import org.jboss.tools.fuse.transformation.core.model.ModelBuilder;
import org.junit.Assert;
import org.junit.Test;



public class ModelBuilderIT {

    @Test
    public void noSuper() {
        Model model = ModelBuilder.fromJavaClass(NoSuper.class);
        Assert.assertEquals(2, model.listFields().size());
    }

    @Test
    public void superSuper() {
        Model model = ModelBuilder.fromJavaClass(SuperSuper.class);
        Assert.assertEquals(3, model.listFields().size());
    }

    @Test
    public void screenForNumbers() {
        Model model = ModelBuilder.fromJavaClass(ContainsNumber.class);
        Assert.assertEquals(1, model.listFields().size());
    }

    @Test
    public void buildWithEnum() {
        Model model = ModelBuilder.fromJavaClass(ClassWithEnum.class);
        Assert.assertEquals(1, model.listFields().size());
    }

    @Test
    public void buildWithDateEtc() {
        Model model = ModelBuilder.fromJavaClass(ClassWithDateEtc.class);
        Assert.assertEquals(4, model.listFields().size());
    }

    @Test
    public void selfReferenceCycle() {
        Model model = ModelBuilder.fromJavaClass(SelfReference.class);
        Assert.assertEquals(2, model.listFields().size());
    }

    @Test
    public void childAncestorCycle() {
        Model model = ModelBuilder.fromJavaClass(Parent.class);
        Assert.assertEquals(7, model.listFields().size());
    }

    @Test
    public void listsOfStringsAndNumbers() {
        Model model = ModelBuilder.fromJavaClass(ListOfStringsAndNumbers.class);
        Assert.assertEquals(3, model.listFields().size());
    }
}

@SuppressWarnings("unused")
class ListOfStringsAndNumbers {
    private List<Number> numbers;
    private List<String> strings;
    private String field1;
}

class NoSuper {
    private String fieldOne;
    private String fieldTwo;

    public String getFieldOne() {
        return fieldOne;
    }

    public void setFieldOne(String fieldOne) {
        this.fieldOne = fieldOne;
    }

    public String getFieldTwo() {
        return fieldTwo;
    }

    public void setFieldTwo(String fieldTwo) {
        this.fieldTwo = fieldTwo;
    }
}

@SuppressWarnings("unused")
class ClassWithDateEtc {
    private String field1;
    private Date field2;
    private Calendar field3;
    private InputStream field4;
}

class ClassWithEnum {
    enum MY_ENUM {one, two, three}
    private MY_ENUM myEnum;

    public MY_ENUM getEnum() {
        return myEnum;
    }

    public void setEnum(MY_ENUM myEnum) {
        this.myEnum = myEnum;
    }
}

class SuperSuper extends NoSuper {
    private String fieldThree;

    public String getFieldThree() {
        return fieldThree;
    }

    public void setFieldThree(String fieldThree) {
        this.fieldThree = fieldThree;
    }

}

class ContainsNumber {
    private BigDecimal bigNum;

    public BigDecimal getBigNum() {
        return bigNum;
    }

    public void setBigNum(BigDecimal bigNum) {
        this.bigNum = bigNum;
    }

}

@SuppressWarnings("unused")
class SelfReference {
    private String field1;
    private SelfReference self;
}

@SuppressWarnings("unused")
class Parent {
    private Child child;
    private String field1;
}

@SuppressWarnings("unused")
class Child {
    private Parent parent;
    private String field2;
    private Grandchild grandchild;
}

@SuppressWarnings("unused")
class Grandchild {
    private Parent grandparent;
    private String field3;
}