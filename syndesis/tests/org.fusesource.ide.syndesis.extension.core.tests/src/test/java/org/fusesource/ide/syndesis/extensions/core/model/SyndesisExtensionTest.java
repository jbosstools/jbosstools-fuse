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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

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
		assertEquals("extensionId value is not matching", "my:test", extension.getExtensionId());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getVersion()}.
	 */
	@Test
	public void testGetVersion() {
		assertEquals("version value is not matching", "1.0.0", extension.getVersion());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("name value is not matching", "Test Extension", extension.getName());
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		assertEquals("description value is not matching", "Testing", extension.getDescription());
	}
	
	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getTags()}.
	 */
	@Test
	public void testGetTags() {
		assertThat(extension.getTags()).containsOnly("test", "extension");
	}

	/**
	 * Test method for {@link org.fusesource.ide.syndesis.extensions.core.model.SyndesisExtension#getDependencies()}.
	 */
	@Test
	public void testGetDependencies() {
		assertThat(extension.getDependencies()).containsOnly("...", "g/a/v");
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
		assertEquals("Action ID not matching", "actionOne", action.getId());

		assertEquals("Action Name not matching", "First Action", action.getName());

		assertEquals("Action Type not matching", "extension", action.getActionType());

		assertEquals("Action Description not matching", "Test Action", action.getDescription());

		assertEquals("Action Name not matching", "First Action", action.getName());

		assertThat(action.getTags()).containsOnly("A", "B", "C");

		SyndesisActionDescriptor descriptor = action.getDescriptor();
		testSyndesisActionDescriptor(descriptor);
	}

	private void testSyndesisActionDescriptor(SyndesisActionDescriptor descriptor) {
		assertEquals("Action descriptor kind not matching", "step", descriptor.getKind());

		assertEquals("Action descriptor entrypoint not matching", "direct:my/test/actionOne", descriptor.getEntryPoint());

		ActionDataShape inputShape = descriptor.getInputDataShape();
		testSyndesisActionDescriptorInputDataShape(inputShape);

		ActionDataShape outputShape = descriptor.getOutputDataShape();
		testSyndesisActionDescriptorOutputDataShape(outputShape);
	}
	
	private void testSyndesisActionDescriptorInputDataShape(ActionDataShape inputShape) {
		assertEquals("Action Input Datashape kind not matching", "any", inputShape.getKind());
	}
	
	private void testSyndesisActionDescriptorOutputDataShape(ActionDataShape outputShape) {
		assertEquals("Action Output Datashape kind not matching", "any", outputShape.getKind());
	}
}
