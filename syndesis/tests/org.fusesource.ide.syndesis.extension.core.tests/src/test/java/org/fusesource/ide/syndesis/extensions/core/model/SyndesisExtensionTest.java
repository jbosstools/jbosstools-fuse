/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author lhein
 */
public class SyndesisExtensionTest {

	private SyndesisExtension extension;

	@Before
	public void onStart() throws IOException {
		File jsonFile = new File("testdata/extdef1.json");
		this.extension = SyndesisExtension.getJSONFactoryInstance(new FileInputStream(jsonFile));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getExtensionId()}.
	 */
	@Test
	public void testGetExtensionId() {
		assertTrue("extensionId value is not matching", extension.getExtensionId().equals("my:test"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getVersion()}.
	 */
	@Test
	public void testGetVersion() {
		assertTrue("version value is not matching", extension.getVersion().equals("1.0.0"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getName()}.
	 */
	@Test
	public void testGetName() {
		assertTrue("name value is not matching", extension.getName().equals("Test Extension"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertTrue("description value is not matching", extension.getDescription().equals("Testing"));
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertTrue("tags are missing", extension.getTags() != null && !extension.getTags().isEmpty() && extension.getTags().size() == 2);
		assertTrue("missing tag value", extension.getTags().contains("test"));
		assertTrue("missing tag value", extension.getTags().contains("extension"));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertTrue("dependencies are missing", extension.getDependencies() != null && !extension.getDependencies().isEmpty() && extension.getDependencies().size() == 2);
		assertTrue("missing dependency value", extension.getDependencies().contains("mvn:g/a/v"));
		assertTrue("missing dependency value", extension.getDependencies().contains("..."));
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getActions()}.
	 */
	@Test
	public void testGetActions() {
		assertTrue("actions are missing", extension.getActions() != null && !extension.getActions().isEmpty() && extension.getActions().size() == 1);
	
		SyndesisAction action = extension.getActions().get(0);
		testSyndesisAction(action);
	}

	private void testSyndesisAction(SyndesisAction action) {
		assertTrue("invalid null action", action != null);

		assertTrue("Action ID not matching", action.getId().equals("actionOne"));

		assertTrue("Action Name not matching", action.getName().equals("First Action"));

		assertTrue("Action Type not matching", action.getActionType().equals("extension"));

		assertTrue("Action Description not matching", action.getDescription().equals("Test Action"));

		assertTrue("Action Name not matching", action.getName().equals("First Action"));

		assertTrue("Action tags are missing", action.getTags() != null && !action.getTags().isEmpty() && action.getTags().size() == 3);
		assertTrue("missing action tag value", action.getTags().contains("A"));
		assertTrue("missing action tag value", action.getTags().contains("B"));
		assertTrue("missing action tag value", action.getTags().contains("C"));

		SyndesisActionDescriptor descriptor = action.getDescriptor();
		testSyndesisActionDescriptor(descriptor);
	}

	private void testSyndesisActionDescriptor(SyndesisActionDescriptor descriptor) {
		assertTrue("invalid null descriptor", descriptor != null);

		assertTrue("Action descriptor kind not matching", descriptor.getKind().equals("step"));

		assertTrue("Action descriptor entrypoint not matching", descriptor.getEntryPoint().equals("direct:my/test/actionOne"));

		ActionDataShape inputShape = descriptor.getInputDataShape();
		testSyndesisActionDescriptorInputDataShape(inputShape);

		ActionDataShape outputShape = descriptor.getOutputDataShape();
		testSyndesisActionDescriptorOutputDataShape(outputShape);
	}
	
	private void testSyndesisActionDescriptorInputDataShape(ActionDataShape inputShape) {
		assertTrue("invalid null Input Datashape", inputShape != null);
		
		assertTrue("Action Input Datashape kind not matching", inputShape.getKind().equals("any"));
	}
	
	private void testSyndesisActionDescriptorOutputDataShape(ActionDataShape outputShape) {
		assertTrue("invalid null Output Datashape", outputShape != null);
		
		assertTrue("Action Output Datashape kind not matching", outputShape.getKind().equals("any"));
	}
}
