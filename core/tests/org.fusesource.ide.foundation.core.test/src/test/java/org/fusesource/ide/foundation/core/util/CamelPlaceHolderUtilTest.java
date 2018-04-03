/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.foundation.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CamelPlaceHolderUtilTest {
	
	CamelPlaceHolderUtil camelPlaceHolderUtil = new CamelPlaceHolderUtil();
	
	@Test
	public void testEmptyValueIsNotPlaceHolder() throws Exception {
		assertThat(camelPlaceHolderUtil.isPlaceHolder("")).isFalse();
	}
	
	@Test
	public void testNullValueIsNotPlaceHolder() throws Exception {
		assertThat(camelPlaceHolderUtil.isPlaceHolder("")).isFalse();
	}
	
	@Test
	public void testNormalValueIsNotPlaceHolder() throws Exception {
		assertThat(camelPlaceHolderUtil.isPlaceHolder("aValue")).isFalse();
	}
	
	@Test
	public void testPlaceHolderValueIsPlaceHolder() throws Exception {
		assertThat(camelPlaceHolderUtil.isPlaceHolder("{{a placeholder}}")).isTrue();
	}

}
