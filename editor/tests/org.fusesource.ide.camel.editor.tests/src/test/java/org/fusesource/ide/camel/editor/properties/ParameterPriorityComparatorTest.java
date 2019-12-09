/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Test;

/**
 * @author Aurelien Pupier
 *
 */
public class ParameterPriorityComparatorTest {
	
	private ParameterPriorityComparator parameterPriorityComparator = new ParameterPriorityComparator();

	@Test
	public void testOrderBasic() throws Exception {
		Parameter o1 = new Parameter();
		o1.setName("a");
		Parameter o2 = new Parameter();
		o2.setName("b");
		assertEquals(-1, parameterPriorityComparator.compare(o1, o2));
	}
	
	@Test
	public void testOrderWithOneRequired() throws Exception {
		Parameter o1 = new Parameter();
		o1.setName("a");
		Parameter o2 = new Parameter();
		o2.setName("b");
		o2.setRequired("true");
		assertEquals(1, parameterPriorityComparator.compare(o1, o2));
	}
	
	@Test
	public void testOrderWithTwoRequired() throws Exception {
		Parameter o1 = new Parameter();
		o1.setName("a");
		o1.setRequired("true");
		Parameter o2 = new Parameter();
		o2.setName("b");
		o2.setRequired("true");
		assertEquals(-1, parameterPriorityComparator.compare(o1, o2));
	}
	
	@Test
	public void testOrderComplex() throws Exception {
		Parameter o1r = new Parameter();
		o1r.setName("aRequired");
		o1r.setRequired("true");
		Parameter o2r = new Parameter();
		o2r.setName("bRequired");
		o2r.setRequired("true");
		Parameter o1 = new Parameter();
		o1.setName("a");
		o1.setRequired("false");
		Parameter o2 = new Parameter();
		o2.setName("b");
		List<Parameter> toSort = Arrays.asList(new Parameter[]{o2,o1,o1r,o2r});
		toSort.sort(parameterPriorityComparator);
		Assertions.assertThat(toSort).containsExactly(o1r,o2r,o1,o2);
	}

}
