/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.junit.Test;

import junit.framework.TestCase;

public class XmlMarshalTest extends TestCase {
	
	CamelIOHandler marshaller = new CamelIOHandler();
	
	protected File sourceDir = new File("src");
	protected File outputDir = new File("target/testData");
	
	@Test
	public void testLoadAndSaveOfSimpleModel() throws Exception {
		assertModelRoundTrip("sample.xml", 1);
	}
	
	protected CamelFile assertModelRoundTrip(String name, int outputCount) {
		outputDir.mkdirs();

		File inFile = new File(sourceDir, name);
		CamelFile model1 = marshaller.loadCamelModel(inFile, new NullProgressMonitor());
		
		File outFile = new File(outputDir, name);
		marshaller.saveCamelModel(model1, outFile, new NullProgressMonitor());
		
		CamelFile model2 = marshaller.loadCamelModel(outFile, new NullProgressMonitor());

		String model1String = model1.getDocumentAsXML();
		String model2String = model2.getDocumentAsXML();
		
		assertEquals("Should have the same content", model1String, model2String);
		
		return model2;
	}

	protected <T> void assertContains(Collection<T> collection, T... items) {
		for (T item : items) {
			assertTrue("collection should contain " + item, collection.contains(item));
		}
	}
}
