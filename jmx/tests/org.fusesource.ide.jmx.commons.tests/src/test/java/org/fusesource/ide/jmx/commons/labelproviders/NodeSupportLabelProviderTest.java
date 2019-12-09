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
package org.fusesource.ide.jmx.commons.labelproviders;


import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.jboss.tools.jmx.core.tree.Node;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class NodeSupportLabelProviderTest {

	@Test
	public void testGetText() throws Exception {
		NodeSupport normalConnectedNodeSupport = new NodeSupport((Node)null) {
			@Override
			public String toString() {
				return "sampleValueForTest";
			}
			
			@Override
			public boolean isConnectionAvailable() {
				return true;
			}
		};
		String text = new NodeSupportLabelProvider().getText(normalConnectedNodeSupport);
		assertThat(text).isEqualTo("sampleValueForTest");
	}
	
	@Test
	public void testGetTextReturnNullIfConnectionNotAvailable() throws Exception {
		NodeSupport normalConnectedNodeSupport = Mockito.spy(new NodeSupport((Node)null) {
			@Override
			public String toString() {
				return "sampleValueForTest";
			}
			
			@Override
			public boolean isConnectionAvailable() {
				return false;
			}
		});
		String text = new NodeSupportLabelProvider().getText(normalConnectedNodeSupport);
		assertThat(text).isNull();
	}
	
}
