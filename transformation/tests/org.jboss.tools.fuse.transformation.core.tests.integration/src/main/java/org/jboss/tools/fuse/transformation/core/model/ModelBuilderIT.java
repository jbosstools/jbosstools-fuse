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
package org.jboss.tools.fuse.transformation.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class ModelBuilderIT {

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {     
			{ NoSuper.class, 2 },
			{ SuperSuper.class, 3 },
			{ ContainsNumber.class, 1 },
			{ ClassWithEnum.class, 1 },
			{ ClassWithDateEtc.class, 4 },
			{ SelfReference.class, 2 },
			{ Parent.class, 7 },
			{ ListOfStringsAndNumbers.class, 3},
			{ CollectionOfCollection.class, 1},
			{ CollectionOfCollectionOfCollection.class, 1}
		});
	}

	@Parameter
	public Class<?> classToTest;
	@Parameter(value = 1)
	public int expectedNumberOfFields;

	@Test
	public void checkGenerationOfFields(){
		Model model = ModelBuilder.fromJavaClass(classToTest);
		assertThat(model.listFields()).hasSize(expectedNumberOfFields);
	}

}

class CollectionOfCollection{
	@SuppressWarnings("unused")
	private Collection<Collection<String>> collectionOfCollection;
}

class CollectionOfCollectionOfCollection{
	@SuppressWarnings("unused")
	private Collection<Collection<Collection<String>>> collectionOfCollectionOfCollection;
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