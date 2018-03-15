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
package org.fusesource.ide.projecttemplates.wizards.pages.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.projecttemplates.adopters.util.CamelDSLType;
import org.fusesource.ide.projecttemplates.impl.simple.EmptyProjectTemplateForFuse7;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItem;
import org.fusesource.ide.projecttemplates.wizards.pages.model.TemplateItemIdentity;
import org.junit.Test;

public class TemplateLabelProviderTest {

	@Test
	public void testGetTextContainsDSlTypeIndication() throws Exception {
		TemplateItem templateItem = new TemplateItem(new TemplateItemIdentity("id", "a name", "a description", null), 10, null, new EmptyProjectTemplateForFuse7(), CamelDSLType.BLUEPRINT);
		
		assertThat(new TemplateLabelProvider().getText(templateItem)).isEqualTo("a name - Blueprint DSL");
	}

}
