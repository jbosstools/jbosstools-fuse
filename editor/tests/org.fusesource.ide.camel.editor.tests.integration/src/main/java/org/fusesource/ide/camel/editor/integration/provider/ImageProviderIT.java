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
package org.fusesource.ide.camel.editor.integration.provider;

import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
public class ImageProviderIT {

	@Test
	public void testSmallIconForProvidedElement() throws Exception {
		assertThat(ImageProvider.getKeyForSmallIcon(false, "cxf")).isEqualTo(ImageProvider.PREFIX + "cxf" + ImageProvider.POSTFIX_SMALL);
	}

	@Test
	public void testSmallIconForNotProvidedElement() throws Exception {
		assertThat(ImageProvider.getKeyForSmallIcon(false, "youhou")).isEqualTo(ImageProvider.PREFIX + "generic" + ImageProvider.POSTFIX_SMALL);
	}

	@Test
	public void testSmallIconForNotProvidedEndpoint() throws Exception {
		assertThat(ImageProvider.getKeyForSmallIcon(true, "youhou")).isEqualTo(ImageProvider.PREFIX + "endpoint" + ImageProvider.POSTFIX_SMALL);
	}

	@Test
	public void testLargeIconForProvidedElement() throws Exception {
		assertThat(ImageProvider.getKeyForDiagramIcon(false, "cxf")).isEqualTo(ImageProvider.PREFIX + "cxf" + ImageProvider.POSTFIX_LARGE);
	}

	@Test
	public void testLargeIconForNotProvidedElement() throws Exception {
		assertThat(ImageProvider.getKeyForDiagramIcon(false, "youhou")).isEqualTo(ImageProvider.PREFIX + "generic" + ImageProvider.POSTFIX_LARGE);
	}

	@Test
	public void testLargeIconForNotProvidedEndpoint() throws Exception {
		assertThat(ImageProvider.getKeyForDiagramIcon(true, "youhou")).isEqualTo(ImageProvider.PREFIX + "endpoint" + ImageProvider.POSTFIX_LARGE);
	}

}
