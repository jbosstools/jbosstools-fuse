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
package org.fusesource.ide.syndesis.extension.core.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.fusesource.ide.syndesis.extensions.core.util.SyndesisExtensionsUtil;
import org.junit.Test;

/**
 * @author lheinema
 */
public class SyndesisExtensionUtilVersionLoaderIT {
		
	@Test
	public void testURLVersionLoaderFromURL() {
		SyndesisExtensionsUtil.IgniteVersionInfoModel model = SyndesisExtensionsUtil.getIgniteVersionModel();
		
		assertThat(model).isNotNull();
		assertThat(model.getCamelVersion()).isNotEmpty();
		assertThat(model.getSpringBootVersion()).isNotEmpty();
		assertThat(model.getSyndesisVersion()).isNotEmpty();
	}
}
