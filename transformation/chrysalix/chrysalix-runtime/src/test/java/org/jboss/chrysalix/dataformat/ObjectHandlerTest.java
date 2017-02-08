/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved.  See the COPYRIGHT.txt file distributed with this work
 * for information regarding copyright ownership.  Some portions may be
 * licensed to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full isListing of
 * individual contributors.
 *
 * Chrysalix is free software. Unless otherwise indicated, all code in
 * Chrysalix is licensed to you under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * Chrysalix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.chrysalix.dataformat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jboss.chrysalix.InMemoryRepository;
import org.jboss.chrysalix.Node;
import org.junit.Before;
import org.junit.Test;

public class ObjectHandlerTest {

	private static final String OBJECT_NAME = Object.class.getSimpleName();
	private static final String OBJECT_NS = Object.class.getPackage().getName();
	private static final String OBJECT_TYPE = Object.class.getName();
	private static final String INT_NAME = int.class.getSimpleName();
	private static final String INT_TYPE = int.class.getName();
	private static final String STRING_NAME = String.class.getSimpleName();
	private static final String STRING_NS = String.class.getPackage().getName();
	private static final String STRING_TYPE = String.class.getName();

	private ObjectHandler handler;
	private Node rootNode;
	private Node sourceRootNode;

	@Before
	public void before() {
		handler = new ObjectHandler();
		rootNode = new InMemoryRepository().newRootNode("root");
    	sourceRootNode = rootNode.addChild("<source>", null);
	}

	private Node toSourceNode(Object object,
	                          Class<?> expectedValueType,
	                          String expectedType,
	                          boolean expectedIsList,
	                          int expectedChildCount) throws Exception {
    	Node node = handler.toSourceNode(object, sourceRootNode);
    	Class<?> type = object.getClass();
    	verify(node, type.getSimpleName(), type.getPackage() == null ? "" : type.getPackage().getName(), expectedValueType,
    		   expectedType, expectedIsList, 0, expectedChildCount);
    	return node;
    }

	private Node toSourceNode(Object object,
	                          Object expectedValue,
	                          String expectedType,
	                          boolean expectedIsList,
	                          int expectedChildCount) throws Exception {
    	Node node = handler.toSourceNode(object, sourceRootNode);
    	Class<?> type = object.getClass();
    	verify(node, type.getSimpleName(), type.getPackage() == null ? "" : type.getPackage().getName(), expectedValue,
    		   expectedType, expectedIsList, 0, expectedChildCount);
    	return node;
    }

	@Test(expected=IllegalArgumentException.class)
    public void toSourceNodefailsWithObjectNull() throws Exception {
    	handler.toSourceNode(null, null);
    }

	@Test(expected=IllegalArgumentException.class)
    public void toSourceNodefailsWithParentNull() throws Exception {
    	handler.toSourceNode(0, null);
    }

    @Test
    public void toSourceNodeWithBooleanPrimitive() throws Exception {
		toSourceNodeWithPrimitive(false);
    }

    @Test
    public void toSourceNodeWithBooleanWrapper() throws Exception {
		toSourceNodeWithPrimitive(Boolean.FALSE);
    }

    @Test
    public void toSourceNodeWithBytePrimitive() throws Exception {
		toSourceNodeWithPrimitive((byte)0);
    }

    @Test
    public void toSourceNodeWithByteWrapper() throws Exception {
		toSourceNodeWithPrimitive(Byte.MIN_VALUE);
    }

    @Test
    public void toSourceNodeWithCharPrimitive() throws Exception {
		toSourceNodeWithPrimitive('\0');
    }

    @Test
    public void toSourceNodeWithCharWrapper() throws Exception {
		toSourceNodeWithPrimitive(Character.MIN_VALUE);
    }

    @Test
    public void ToSourceNodeWithEmptyArray() throws Exception {
    	toSourceNode(new int[0], int[].class, int[].class.getSimpleName(), true, 0);
    }

    @Test
    public void toSourceNodeWithEnum() throws Exception {
    	Node enumNode = toSourceNode(AnEnum.CONSTANT1, AnEnum.CONSTANT1, AnEnum.class.getName(), false, 1);
    	verify(enumNode.children()[0], "field", AnEnum.class, AnEnum.CONSTANT1.field, INT_TYPE, false, 0, 0);
    }

    @Test
    public void toSourceNodeWithMultiDimensionalPrimitiveArray() throws Exception {
    	Object[] array = { "",
			  			   0,
			  			   null,
			  			   new Object[] { new int[] {1, 2},
			  				   			  new String[] {""} },
			  			   new int[][] { new int[] {3, 4, 5} } };
    	Node arrayNode = toSourceNode(array, Object[].class, Object[].class.getTypeName(), true, 5);
    	Node[] arrayChildren = arrayNode.children();
    	verify(arrayChildren[0], OBJECT_NAME, OBJECT_NS, "", OBJECT_TYPE, false, 0, 0);
    	verify(arrayChildren[1], OBJECT_NAME, OBJECT_NS, 0, OBJECT_TYPE, false, 1, 0);
    	verify(arrayChildren[2], OBJECT_NAME, OBJECT_NS, null, OBJECT_TYPE, false, 2, 0);
    	Node objArrayNode = arrayChildren[3];
    	verify(objArrayNode, OBJECT_NAME, OBJECT_NS, Object[].class, OBJECT_TYPE, true, 3, 2);
    	Node[] objArrayChildren = objArrayNode.children();
    	Node intArrayNode = objArrayChildren[0];
    	verify(intArrayNode, OBJECT_NAME, OBJECT_NS, int[].class, OBJECT_TYPE, true, 0, 2);
    	Node[] intArrayChildren = intArrayNode.children();
    	verify(intArrayChildren[0], INT_NAME, "", 1, INT_TYPE, false, 0, 0);
    	verify(intArrayChildren[1], INT_NAME, "", 2, INT_TYPE, false, 1, 0);
    	Node stringArrayNode = objArrayChildren[1];
    	verify(stringArrayNode, OBJECT_NAME, OBJECT_NS, String[].class, OBJECT_TYPE, true, 1, 1);
    	verify(stringArrayNode.children()[0], STRING_NAME, STRING_NS, "", STRING_TYPE, false, 0, 0);
    	Node int2DArrayNode = arrayChildren[4];
    	verify(int2DArrayNode, OBJECT_NAME, OBJECT_NS, int[][].class, OBJECT_TYPE, true, 4, 1);
    	Node int2DArrayIntArrayNode = int2DArrayNode.children()[0];
    	verify(int2DArrayIntArrayNode, int[].class.getSimpleName(), "", int[].class, int[].class.getTypeName(), true, 0, 3);
    	Node[] int2DArrayIntArrayChildren = int2DArrayIntArrayNode.children();
    	verify(int2DArrayIntArrayChildren[0], INT_NAME, "", 3, INT_TYPE, false, 0, 0);
    	verify(int2DArrayIntArrayChildren[1], INT_NAME, "", 4, INT_TYPE, false, 1, 0);
    	verify(int2DArrayIntArrayChildren[2], INT_NAME, "", 5, INT_TYPE, false, 2, 0);
    }

    @Test
    public void toSourceNodeWithNumberPrimitive() throws Exception {
		toSourceNodeWithPrimitive(0);
    }

    @Test
    public void toSourceNodeWithNumberWrapper() throws Exception {
		toSourceNodeWithPrimitive(Double.MIN_VALUE);
    }

    @Test
    public void toSourceNodeWithObject() throws Exception {
    	Node topLevelClass = toSourceNode(new TopLevelClass(), TopLevelClass.class, TopLevelClass.class.getName(), false, 37);
    	Node[] topLevelClassChildren = topLevelClass.children();
    	int topLevelClassNdx = 0;
    	verify(topLevelClassChildren[topLevelClassNdx++], "CONSTANT", SuperInterface.class,
    	       "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "CONSTANT", Interface.class,
    	       "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "CONSTANT", SuperSuperclass.class,
    	       "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "CONSTANT", Superclass.class,
    	       "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "staticField", Superclass.class,
    	       "static", STRING_TYPE, false, 0, 0);
    	Node superclassField = topLevelClassChildren[topLevelClassNdx++];
    	verify(superclassField, "field", Superclass.class,
    	       SuperSuperclass.class, SuperSuperclass.class.getName(), false, 0, 3);
    	Node[] superclassFieldChildren = superclassField.children();
    	verify(superclassFieldChildren[0], "CONSTANT", SuperInterface.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(superclassFieldChildren[1], "CONSTANT", Interface.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(superclassFieldChildren[2], "CONSTANT", SuperSuperclass.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "CONSTANT", TopLevelClass.class,
    	       "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "staticField", TopLevelClass.class,
    	       "static", STRING_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "bytePrimitive", TopLevelClass.class,
    	       (byte)0, byte.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "booleanPrimitive", TopLevelClass.class,
    	       false, boolean.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "charPrimitive", TopLevelClass.class,
    	       '\0', char.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "numberPrimitive", TopLevelClass.class,
    	       0, INT_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullWrapper", TopLevelClass.class,
    	       null, Double.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "byteWrapper", TopLevelClass.class,
    	       (byte)0, Byte.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "booleanWrapper", TopLevelClass.class,
    	       false, Boolean.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "charWrapper", TopLevelClass.class,
    	       '\0', Character.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "numberWrapper", TopLevelClass.class,
    	       (short)0, Number.class.getName(), false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "string", TopLevelClass.class,
    	       null, STRING_TYPE, false, 0, 0);
    	Node enumeration = topLevelClassChildren[topLevelClassNdx++];
    	verify(enumeration, "enumeration", TopLevelClass.class,
    	       AnEnum.CONSTANT1, AnEnum.class.getName(), false, 0, 1);
    	verify(enumeration.children()[0], "field", AnEnum.class, AnEnum.CONSTANT1.field, INT_TYPE, false, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullArray", TopLevelClass.class,
    	       null, int[].class.getTypeName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "emptyArray", TopLevelClass.class,
    	       int[].class, int[].class.getTypeName(), true, 0, 0);
    	Node primitiveArray = topLevelClassChildren[topLevelClassNdx++];
    	verify(primitiveArray, "primitiveArray", TopLevelClass.class,
    	       int[].class, int[].class.getTypeName(), true, 0, 4);
    	Node[] primitiveArrayChildren = primitiveArray.children();
    	verify(primitiveArrayChildren[0], INT_NAME, "", 1, INT_TYPE, false, 0, 0);
    	verify(primitiveArrayChildren[1], INT_NAME, "", 2, INT_TYPE, false, 1, 0);
    	verify(primitiveArrayChildren[2], INT_NAME, "", 3, INT_TYPE, false, 2, 0);
    	verify(primitiveArrayChildren[3], INT_NAME, "", 4, INT_TYPE, false, 3, 0);
    	Node arrayNode = topLevelClassChildren[topLevelClassNdx++];
    	verify(arrayNode, "multiDimensionalPrimitiveArray", TopLevelClass.class,
    	       Object[].class, Object[].class.getTypeName(), true, 0, 5);
    	Node[] arrayChildren = arrayNode.children();
    	verify(arrayChildren[0], OBJECT_NAME, OBJECT_NS, "", OBJECT_TYPE, false, 0, 0);
    	verify(arrayChildren[1], OBJECT_NAME, OBJECT_NS, 0, OBJECT_TYPE, false, 1, 0);
    	verify(arrayChildren[2], OBJECT_NAME, OBJECT_NS, null, OBJECT_TYPE, false, 2, 0);
    	Node objArrayNode = arrayChildren[3];
    	verify(objArrayNode, OBJECT_NAME, OBJECT_NS, Object[].class, OBJECT_TYPE, true, 3, 2);
    	Node[] objArrayChildren = objArrayNode.children();
    	Node intArrayNode = objArrayChildren[0];
    	verify(intArrayNode, OBJECT_NAME, OBJECT_NS, int[].class, OBJECT_TYPE, true, 0, 2);
    	Node[] intArrayChildren = intArrayNode.children();
    	verify(intArrayChildren[0], INT_NAME, "", 1, INT_TYPE, false, 0, 0);
    	verify(intArrayChildren[1], INT_NAME, "", 2, INT_TYPE, false, 1, 0);
    	Node stringArrayNode = objArrayChildren[1];
    	verify(stringArrayNode, OBJECT_NAME, OBJECT_NS, String[].class, OBJECT_TYPE, true, 1, 1);
    	verify(stringArrayNode.children()[0], STRING_NAME, STRING_NS, "", STRING_TYPE, false, 0, 0);
    	Node int2DArrayNode = arrayChildren[4];
    	verify(int2DArrayNode, OBJECT_NAME, OBJECT_NS, int[][].class, OBJECT_TYPE, true, 4, 1);
    	Node int2DArrayIntArrayNode = int2DArrayNode.children()[0];
    	verify(int2DArrayIntArrayNode, int[].class.getSimpleName(), "", int[].class, int[].class.getTypeName(), true, 0, 3);
    	Node[] int2DArrayIntArrayChildren = int2DArrayIntArrayNode.children();
    	verify(int2DArrayIntArrayChildren[0], INT_NAME, "", 3, INT_TYPE, false, 0, 0);
    	verify(int2DArrayIntArrayChildren[1], INT_NAME, "", 4, INT_TYPE, false, 1, 0);
    	verify(int2DArrayIntArrayChildren[2], INT_NAME, "", 5, INT_TYPE, false, 2, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullObject", TopLevelClass.class,
    	       null, Superclass.class.getName(), false, 0, 0);
    	Node object = topLevelClassChildren[topLevelClassNdx++];
    	verify(object, "object", TopLevelClass.class,
    	       SuperSuperclass.class, SuperSuperclass.class.getName(), false, 0, 3);
    	Node[] objectChildren = superclassField.children();
    	verify(objectChildren[0], "CONSTANT", SuperInterface.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(objectChildren[1], "CONSTANT", Interface.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	verify(objectChildren[2], "CONSTANT", SuperSuperclass.class, "CONSTANT", STRING_TYPE, false, 0, 0);
    	Node innerClass = topLevelClassChildren[topLevelClassNdx++];
    	verify(innerClass, "innerClass", TopLevelClass.class,
    	       TopLevelClass.InnerClass.class, TopLevelClass.InnerClass.class.getName(), false, 0, 1);
    	verify(innerClass.children()[0], "field", TopLevelClass.InnerClass.class, 10, INT_TYPE, false, 0, 0);
    	Node anonymousInnerClass = topLevelClassChildren[topLevelClassNdx++];
    	verify(anonymousInnerClass, "anonymousInnerClass", TopLevelClass.class,
    	       TopLevelClass.AbstractClass.class, TopLevelClass.AbstractClass.class.getName(), false, 0, 2);
    	Node[] anonymousInnerClassChildren = anonymousInnerClass.children();
    	verify(anonymousInnerClassChildren[0], "CONSTANT", TopLevelClass.AbstractClass.class, "constant", STRING_TYPE, false, 0, 0);
    	verify(anonymousInnerClassChildren[1], "field", TopLevelClass.AbstractClass.class, 11, INT_TYPE, false, 0, 0);
    	Node objectArray = topLevelClassChildren[topLevelClassNdx++];
    	verify(objectArray, "objectArray", TopLevelClass.class,
    	       SuperSuperclass[].class, SuperSuperclass[].class.getTypeName(), true, 0, 2);
    	Node[] objectArrayChildren = objectArray.children();
    	verify(objectArrayChildren[0], SuperSuperclass.class.getSimpleName(), SuperSuperclass.class.getPackage().getName(),
    	       SuperSuperclass.class, SuperSuperclass.class.getName(), false, 0, 3);
    	verify(objectArrayChildren[1], SuperSuperclass.class.getSimpleName(), SuperSuperclass.class.getPackage().getName(),
    	       SuperSuperclass.class, SuperSuperclass.class.getName(), false, 1, 3);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullCollection", TopLevelClass.class,
    	       null, Collection.class.getName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullList", TopLevelClass.class,
    	       null, List.class.getName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "emptyCollection", TopLevelClass.class,
    	       List.class, Collection.class.getName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "collection", TopLevelClass.class,
    	       Set.class, Collection.class.getName(), true, 0, 10);
    	verify(topLevelClassChildren[topLevelClassNdx++], "list", TopLevelClass.class,
    	       List.class, Collection.class.getName(), true, 0, 5);
    	verify(topLevelClassChildren[topLevelClassNdx++], "nullMap", TopLevelClass.class,
    	       null, Map.class.getName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "emptyMap", TopLevelClass.class,
    	       Map.class, Map.class.getName(), true, 0, 0);
    	verify(topLevelClassChildren[topLevelClassNdx++], "map", TopLevelClass.class,
    	       Map.class, Map.class.getName(), true, 0, 10);
    	arrayNode = topLevelClassChildren[topLevelClassNdx++];
    	verify(arrayNode, "multiDimensionalArray", TopLevelClass.class,
    	       Interface[][][].class, Interface[][][].class.getTypeName(), true, 0, 2);
    	arrayChildren = arrayNode.children();
    	verify(arrayChildren[0], Interface.class.getSimpleName() + "[][]", "",
    	       Interface[][].class, Interface[][].class.getTypeName(), true, 0, 2);
    	verify(arrayChildren[1], Interface.class.getSimpleName() + "[][]", "",
    	       Interface[][].class, Interface[][].class.getTypeName(), true, 1, 1);
    	assertThat(topLevelClassNdx, is(topLevelClassChildren.length));
    }

    private void toSourceNodeWithPrimitive(Object primitive) throws Exception {
    	toSourceNode(primitive, primitive, primitive.getClass().getName(), false, 0);
	}

    @Test
    public void toSourceNodeWithPrimitiveArray() throws Exception {
    	Node arrayNode = toSourceNode(new int[] {1, 2}, int[].class, int[].class.getSimpleName(), true, 2);
    	Node[] arrayChildren = arrayNode.children();
    	verify(arrayChildren[0], INT_NAME, "", 1, INT_TYPE, false, 0, 0);
    	verify(arrayChildren[1], INT_NAME, "", 2, INT_TYPE, false, 1, 0);
    }

    @Test
    public void toSourceNodeWithPrimitiveArrayWithNull() throws Exception {
    	Node arrayNode = toSourceNode(new Integer[] {null}, Integer[].class, Integer[].class.getTypeName(), true, 1);
    	Node[] arrayChildren = arrayNode.children();
    	verify(arrayChildren[0], Integer.class.getSimpleName(), Integer.class.getPackage().getName(), null,
    	                Integer.class.getName(), false, 0, 0);
    }

    @Test
    public void toSourceNodeWithString() throws Exception {
		toSourceNodeWithPrimitive("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void toTargetDatafailsWithTargetNodeNull() throws Exception {
    	handler.toTargetData(null);
    }

    @Test
    public void toTargetDataWithEmptyPrimitiveArray() throws Exception {
    	Object sourceObj = new int[0];
    	Node node = handler.toSourceNode(sourceObj, sourceRootNode);
    	Object targetObj = handler.toTargetData(node);
    	assertThat(targetObj, is(sourceObj));
    }

    @Test
    public void toTargetDataWithEnum() throws Exception {
    	Node node = handler.toSourceNode(AnEnum.CONSTANT1, sourceRootNode);
    	Object targetObj = handler.toTargetData(node);
    	assertThat(targetObj, is(AnEnum.CONSTANT1));
    }

	@Test
    public void toTargetDataWithObject() throws Exception {
    	Node node = handler.toSourceNode(new TopLevelClass(), sourceRootNode);
    	Object targetObj = handler.toTargetData(node);
    	assertThat(targetObj.getClass(), equalTo(TopLevelClass.class));
    }

    @Test
    public void toTargetDataWithObjectArray() throws Exception {
    	Node node = handler.toSourceNode(new Object[] {new Object(), 2}, sourceRootNode);
    	Object targetObj = handler.toTargetData(node);
    	assertThat(targetObj, instanceOf(Object[].class));
    	Object[] array = (Object[]) targetObj;
    	assertThat(array[0].getClass(), equalTo(Object.class));
    	assertThat(array[1], is(2));
    }

    @Test
    public void toTargetDataWithPrimitive() throws Exception {
    	Node node = handler.toSourceNode(1, sourceRootNode);
    	Object obj = handler.toTargetData(node);
    	assertThat(obj, is(1));
    }

    @Test
    public void toTargetDataWithPrimitiveArray() throws Exception {
    	Object sourceObj = new int[] {1, 2};
    	Node node = handler.toSourceNode(sourceObj, sourceRootNode);
    	Object targetObj = handler.toTargetData(node);
    	assertThat(targetObj, is(sourceObj));
    }

    private void verify(Node node,
                        String expectedTypeName,
                        boolean expectedIsList,
                        int expectedIndex,
                        int expectedChildCount) {
    	assertThat(expectedTypeName + " node", node, notNullValue());
    	assertThat(expectedTypeName + " type", node.type(), is(expectedTypeName));
    	assertThat(expectedTypeName + " isList", node.isList(), is(expectedIsList));
    	assertThat(expectedTypeName + " index", node.index(), is(expectedIndex));
    	assertThat(expectedTypeName + " child count", node.children().length, is(expectedChildCount));
    }

    private void verify(Node node,
                        String expectedName,
                        Class<?> expectedNamespace,
                        Object expectedValue,
                        String expectedType,
                        boolean expectedIsList,
                        int expectedIndex,
                        int expectedChildCount) {
    	verify(node, expectedName, expectedNamespace.getName(), expectedValue, expectedType, expectedIsList, expectedIndex,
    	       expectedChildCount);
    }

    private void verify(Node node,
                        String expectedName,
                        String expectedNamespace,
                        Object expectedValue,
                        String expectedTypeName,
                        boolean expectedIsList,
                        int expectedIndex,
                        int expectedChildCount) {
    	verify(node, expectedTypeName, expectedIsList, expectedIndex, expectedChildCount);
    	assertThat(expectedTypeName + " name", node.name(), is(expectedName));
    	assertThat(expectedTypeName + " namespace", node.namespace(), is(expectedNamespace));
    	if (expectedValue instanceof Class) {
        	assertThat(expectedTypeName + " value", node.value(), notNullValue());
        	assertThat(expectedTypeName + " value", node.value(), instanceOf((Class<?>)expectedValue));
    	} else {
        	assertThat(expectedTypeName + " value", node.value(), is(expectedValue));
    	}
    }
}
