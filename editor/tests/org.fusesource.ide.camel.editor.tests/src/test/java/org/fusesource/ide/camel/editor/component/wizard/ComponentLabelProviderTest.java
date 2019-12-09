/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.component.wizard;

import org.fusesource.ide.camel.editor.component.wizard.ComponentLabelProvider;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentLabelProviderTest {

	@Test
	public void testGetText_returnTitleIfSet() throws Exception {
		final Component component = new Component();
		component.setTitle("myTitle");
		component.setScheme("schemeTitle");
		assertThat(new ComponentLabelProvider().getText(component)).isEqualTo("myTitle");
	}

	@Test
	public void testGetText_returnWithDescription() throws Exception {
		final Component component = new Component();
		component.setTitle("myTitle");
		component.setScheme("schemeTitle");
		component.setDescription("my description");
		assertThat(new ComponentLabelProvider().getText(component)).isEqualTo("myTitle - my description");
	}

	@Test
	public void testGetText_returnHumanizedSchemTitleIfNoTitleSet() throws Exception {
		final Component component = new Component();
		component.setScheme("schemeTitle");
		assertThat(new ComponentLabelProvider().getText(component)).isEqualTo("Scheme Title");
	}

	@Test
	public void testGetText_dontExplodeOnNull() throws Exception {
		assertThat(new ComponentLabelProvider().getText(null)).isEqualTo("");
	}

}
