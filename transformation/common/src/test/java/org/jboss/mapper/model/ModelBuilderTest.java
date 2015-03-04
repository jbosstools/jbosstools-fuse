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

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class ModelBuilderTest {

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