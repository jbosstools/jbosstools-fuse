/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.chrysalix;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 */
public class EngineTest {

	@Test(expected=IllegalArgumentException.class)
	public void mapFailsWithMappingsFilePathEmpty() throws Exception {
		new Engine().map(Mockito.mock(Node.class), Mockito.mock(Node.class), "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void mapFailsWithMappingsFilePathNotExists() throws Exception {
		new Engine().map(Mockito.mock(Node.class), Mockito.mock(Node.class), "blah");
	}

	@Test(expected=IllegalArgumentException.class)
	public void mapFailsWithMappingsFilePathNull() throws Exception {
		new Engine().map(Mockito.mock(Node.class), Mockito.mock(Node.class), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void mapFailsWithSourceNodeNull() throws Exception {
		new Engine().map(null, null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void mapFailsWithTargetNodeNull() throws Exception {
		new Engine().map(Mockito.mock(Node.class), null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void registerTransformerFailsWithNull() {
		new Engine().registerTransformer(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void splitPathFailsWithNull() {
		Engine.splitPath(null);
	}

	@Test
	public void splitPathWithAbsolutePath() {
		String[] segment = Engine.splitPath("/asdf");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(2));
		assertThat(segment[0], is(""));
		assertThat(segment[1], is("asdf"));
	}

	@Test
	public void splitPathWithBracketedNamespace() {
		String[] segment = Engine.splitPath("{ns}:asdf");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(1));
		assertThat(segment[0], is("{ns}:asdf"));
	}

	@Test
	public void splitPathWithBracketedNamespaceWithSlashes() {
		String[] segment = Engine.splitPath("{http://www.w3.org/2001/XMLSchema}:asdf");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(1));
		assertThat(segment[0], is("{http://www.w3.org/2001/XMLSchema}:asdf"));
	}

	@Test
	public void splitPathWithEmptyPath() {
		String[] segment = Engine.splitPath("");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(1));
		assertThat(segment[0], is(""));
	}

	@Test
	public void splitPathWithMultiSegmentPath() {
		String[] segment = Engine.splitPath("asdf/qwer");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(2));
		assertThat(segment[0], is("asdf"));
		assertThat(segment[1], is("qwer"));
	}

	@Test
	public void splitPathWithNamespace() {
		String[] segment = Engine.splitPath("ns:asdf");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(1));
		assertThat(segment[0], is("ns:asdf"));
	}

	@Test
	public void splitPathWithRelativePath() {
		String[] segment = Engine.splitPath("@asdf[0]");
		assertThat(segment, notNullValue());
		assertThat(segment.length, is(1));
		assertThat(segment[0], is("@asdf[0]"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void toSourceNodeFailsWithDataNull() throws Exception {
		new Engine().toSourceNode(null, null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toSourceNodeFailsWithHandlerNull() throws Exception {
		new Engine().toSourceNode(new Object(), null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toSourceNodeFailsWithRepositoryNull() throws Exception {
		new Engine().toSourceNode(new Object(), Mockito.mock(DataFormatHandler.class), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toTargetDataFailsWithHandlerNull() throws Exception {
		new Engine().toTargetData(Mockito.mock(Node.class), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toTargetDataFailsWithTargetNodeNull() throws Exception {
		new Engine().toTargetData(null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toTargetNodeFailsWithDataNull() throws Exception {
		new Engine().toTargetNode(null, null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toTargetNodeFailsWithHandlerNull() throws Exception {
		new Engine().toTargetNode(new Object(), null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void toTargetNodeFailsWithRepositoryNull() throws Exception {
		new Engine().toTargetNode(new Object(), Mockito.mock(DataFormatHandler.class), null);
	}
}
