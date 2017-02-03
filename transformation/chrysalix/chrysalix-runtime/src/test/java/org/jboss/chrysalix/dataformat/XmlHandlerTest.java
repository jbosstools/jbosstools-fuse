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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import org.jboss.chrysalix.Engine;
import org.jboss.chrysalix.InMemoryRepository;
import org.jboss.chrysalix.Node;
import org.jboss.chrysalix.Repository;
import org.jboss.chrysalix.dataformat.XmlHandler;
import org.junit.Test;

public class XmlHandlerTest {

    private static final String RESOURCES_FOLDER = "src/test/resources/" + XmlHandlerTest.class.getPackage().getName().replace('.', '/') + '/';
    private static final String SOURCE_XML = RESOURCES_FOLDER + "source.xml";
    private static final String TARGET_XML = RESOURCES_FOLDER + "target.xml";
    private static final String EXPECTED_XML = RESOURCES_FOLDER + "expectedTarget.xml";
    private static final String MAPPINGS = RESOURCES_FOLDER + "mappings.txt";

    @Test
    public void mapXml() throws Exception {
        Engine engine = new Engine(RESOURCES_FOLDER);
        XmlHandler handler = new XmlHandler();
        Repository repository = new InMemoryRepository();
        Node sourceFileNode = engine.toNode(SOURCE_XML, handler, repository);
        Node targetFileNode = engine.toNode(TARGET_XML, handler, repository);
        engine.map(sourceFileNode, targetFileNode, MAPPINGS);
        engine.toFile(targetFileNode, handler);
        List<String> targetLines = Files.readAllLines(new File(TARGET_XML).toPath());
        List<String> expectedLines = Files.readAllLines(new File(EXPECTED_XML).toPath());
        assertThat("Lines", targetLines.size(), is(expectedLines.size()));
        int line = 1;
        for (Iterator<String> targetIter = targetLines.iterator(), expectedIter = expectedLines.iterator(); targetIter.hasNext();) {
            String targetLine = targetIter.next();
            String expectedLine = expectedIter.next();
            assertThat("Line " + line++, targetLine, is(expectedLine));
        }
    }
}
