/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.chrysalix.dataformat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

enum AnEnum {
	CONSTANT1(1), CONSTANT2(2);

	int field;

	AnEnum(int field) {
		this.field = field;
	}
}

enum Blah {
	BLAH;
}

interface Interface extends SuperInterface {

	String CONSTANT = "CONSTANT";
}

class Superclass extends SuperSuperclass {

	static final String CONSTANT = "CONSTANT";
	static String staticField = "static";

	SuperSuperclass field = new SuperSuperclass();
}

interface SuperInterface {

	String CONSTANT = "CONSTANT";
}

class SuperSuperclass implements Interface {

	static final String CONSTANT = "CONSTANT";
}

@SuppressWarnings("unused")
class TopLevelClass extends Superclass implements Interface {

	private static final String CONSTANT = "CONSTANT";
	private static String staticField = "static";

	private byte bytePrimitive;
	private boolean booleanPrimitive;
	private char charPrimitive;
	private int numberPrimitive;
	private Double nullWrapper;
	private Byte byteWrapper = 0;
	private Boolean booleanWrapper = false;
	private Character charWrapper = '\0';
	private Number numberWrapper = (short)0;
	private String string;
	private AnEnum enumeration = AnEnum.CONSTANT1;
	private int[] nullArray;
	private int[] emptyArray = {};
	private int[] primitiveArray = {1, 2, 3, 4};
	private Object[] multiDimensionalPrimitiveArray = { "",
													  	0,
													  	null,
													  	new Object[] { new int[] {1, 2},
														  			   new String[] {""} },
													  	new int[][] { new int[] {3, 4, 5} } };
	private Superclass nullObject;
	private SuperSuperclass object = new SuperSuperclass();
	private InnerClass innerClass = new InnerClass();
	public AbstractClass anonymousInnerClass = new AbstractClass() {};
	private SuperSuperclass[] objectArray = {new SuperSuperclass(), new SuperSuperclass()};
	private Collection<Superclass> nullCollection;
	private List<Superclass> nullList;
	private Collection<Superclass> emptyCollection = new ArrayList<>();
	private Collection<Superclass> collection = new HashSet<>();
	private Collection<String> list = new ArrayList<>();
	private Map<Integer, Interface> nullMap;
	private Map<Integer, Interface> emptyMap = new HashMap<>();
	private Map<InnerClass, Interface> map = new HashMap<>();
	private Interface[][][] multiDimensionalArray = { { {new Superclass(), new Superclass()},
													    {new Superclass()} },
													  { {new Superclass(), new Superclass(), new Superclass()} } };

	TopLevelClass() {
		for (int ndx = 0; ndx < 10; ndx++) {
			collection.add(new Superclass());
		}
		for (int ndx = 0; ndx < 5; ndx++) {
			list.add("string" + ndx);
		}
		for (int ndx = 0; ndx < 10; ndx++) {
			map.put(new InnerClass(), new Superclass());
		}
	}

	private void method() {}

	public abstract class AbstractClass {

		private static final String CONSTANT = "constant";

		int field = 11;
	}

	public class InnerClass {

		int field = 10;
	}
}
