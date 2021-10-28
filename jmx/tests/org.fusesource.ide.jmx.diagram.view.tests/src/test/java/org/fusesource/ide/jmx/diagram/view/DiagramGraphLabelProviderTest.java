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
package org.fusesource.ide.jmx.diagram.view;

import org.eclipse.draw2d.Label;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelEndpointMBean;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.jmx.camel.navigator.ProcessorNode;
import org.fusesource.ide.jmx.camel.navigator.RouteNode;
import org.fusesource.ide.jmx.commons.messages.INodeStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class DiagramGraphLabelProviderTest {
	
	@Mock
	private CamelEndpointMBean endpointMBean;
	
	@Mock
	private INodeStatistics stats;
	
	@Test
	public void testGetTextForEntityData() throws Exception {
		DiagramGraphLabelProvider labelProvider = initializeLabelProvider();
		doReturn(1L).when(stats).getCounter();
		
		EntityConnectionData element = createEntityConnectionData();
		assertThat(labelProvider.getText(element)).isEqualTo("Total: 1");
	}
	
	@Test
	public void testGetTextForEntityDataWithNoInteraction() throws Exception {
		DiagramGraphLabelProvider labelProvider = initializeLabelProvider();
		doReturn(0L).when(stats).getCounter();
		
		EntityConnectionData element = createEntityConnectionData();
		assertThat(labelProvider.getText(element)).isEqualTo("");
		labelProvider.getTooltip(element);
	}
	
	@Test
	public void testGetTooltipForEntityData() throws Exception {
		DiagramGraphLabelProvider labelProvider = initializeLabelProvider();
		doReturn(2L).when(stats).getCounter();
		doReturn(2D).when(stats).getMeanElapsedTime();
		doReturn(3L).when(stats).getMaxElapsedTime();
		doReturn(1L).when(stats).getMinElapsedTime();
		
		EntityConnectionData element = createEntityConnectionData();
		assertThat(((Label)labelProvider.getTooltip(element)).getText())
			.isEqualTo("Exchanges total: 2 / mean time: 2 / max time: 3 / min time: 1");
	}
	
	private EntityConnectionData createEntityConnectionData() {
		CamelBasicModelElement node = new CamelBasicModelElement(null, null);
		node.setId("myNodeId");
		EntityConnectionData element = new EntityConnectionData(null, new ProcessorNode(new RouteNode(null, null), null, node));
		return element;
	}

	private DiagramGraphLabelProvider initializeLabelProvider() {
		DiagramGraphLabelProvider labelProvider = spy(new DiagramGraphLabelProvider(new DiagramView()));
		doReturn("myNodeId").when(endpointMBean).getCamelId();
		doReturn(stats).when(labelProvider).getStatsFor("myNodeId");
		return labelProvider;
	}
}
