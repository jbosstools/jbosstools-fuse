/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved.  See the COPYRIGHT.txt file distributed with this work
 * for information regarding copyright ownership.  Some portions may be
 * licensed to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import org.jboss.chrysalix.InMemoryRepository;
import org.jboss.chrysalix.Node;
import org.junit.Test;

public class XmlHandlerTest {

    private static final String RESOURCES_FOLDER = "src/test/resources/" + XmlHandlerTest.class.getPackage().getName().replace('.', '/');
    private static final String SOURCE = "source.xml";
    private static final String TARGET = "target.xml";
    private static final String SOURCE_PATH = RESOURCES_FOLDER + '/' + SOURCE;
    private static final String TARGET_PATH = RESOURCES_FOLDER + '/' + TARGET;

    @Test
    public void toSourceNode() throws Exception {
    	XmlHandler handler = new XmlHandler();
    	Node root = new InMemoryRepository().newRootNode("root");
    	handler.toSourceNode(SOURCE_PATH, root);
    	assertThat(root.children().length, is(1));
    	Node source = root.children()[0];
    	assertThat(source, notNullValue());
    	assertThat(source.name(), is("source.xml"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void toSourceNodeWhenFileNotExists() throws Exception {
    	XmlHandler handler = new XmlHandler();
    	handler.toSourceNode(TARGET_PATH, new InMemoryRepository().newRootNode("root"));
    }

    @Test
    public void toTargetData() throws Exception {
    	XmlHandler handler = new XmlHandler();
    	Node root = new InMemoryRepository().newRootNode("root");
    	handler.toSourceNode(SOURCE_PATH, root);
    	assertThat(root.children().length, is(1));
    	Node source = root.children()[0];
    	Object data = handler.toTargetData(source);
    	assertThat(data, notNullValue());
    	assertThat(data.getClass().isArray(), is(true));
        assertThat(data.getClass().getComponentType(), equalTo(String.class));
		String[] lines = (String[])data;
        List<String> expectedLines = Files.readAllLines(new File(SOURCE_PATH).toPath());
        assertThat(lines.length, is(expectedLines.size()));
        int line = 1;
        for (Iterator<String> expectedIter = expectedLines.iterator(); expectedIter.hasNext();) {
            String targetLine = lines[line - 1];
            String expectedLine = expectedIter.next();
            assertThat("Line " + line++, targetLine, is(expectedLine));
        }
    }

    @Test
    public void toTargetNode() throws Exception {
    	XmlHandler handler = new XmlHandler();
    	Node source = handler.toTargetNode(TARGET_PATH, new InMemoryRepository().newRootNode("root"));
    	assertThat(source, notNullValue());
    	assertThat(source.name(), is("target.xml"));
    }
}
