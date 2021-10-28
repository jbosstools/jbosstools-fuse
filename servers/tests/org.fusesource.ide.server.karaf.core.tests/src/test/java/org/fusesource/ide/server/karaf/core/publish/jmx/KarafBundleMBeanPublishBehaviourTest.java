/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KarafBundleMBeanPublishBehaviourTest {
	
	@Mock
	private MBeanServerConnection mbsc;

	@Test
	public void testGetBundleIdWithSpecifyingVersion() throws Exception {
		int idToFind = 5;
		Map<String, Object> map = new HashMap<>();
		map.put("Symbolic Name", "bundleSymbolicName");
		map.put("ID", idToFind);
		map.put("Version", "1.0");
		createTabularData(map);
		KarafBundleMBeanPublishBehaviour karafBundleMBeanPublishBehaviour = new KarafBundleMBeanPublishBehaviour();
		assertThat(karafBundleMBeanPublishBehaviour.getBundleId(mbsc, "bundleSymbolicName", null)).isEqualTo(idToFind);
	}
	
	@Test
	public void testGetBundleIdWithSpecificVersion() throws Exception {
		Map<String, Object> firstVersion = new HashMap<>();
		firstVersion.put("Symbolic Name", "bundleSymbolicName");
		firstVersion.put("ID", 1);
		firstVersion.put("Version", "1.0");
		Map<String, Object> secondVersion= new HashMap<>();
		secondVersion.put("Symbolic Name", "bundleSymbolicName");
		secondVersion.put("ID", 2);
		secondVersion.put("Version", "2.0");
		TabularData tabularData = new MockedTabularData(Arrays.asList(new MockedCompositeData(firstVersion), new MockedCompositeData(secondVersion)));
		doReturn(tabularData).when(mbsc).getAttribute(Mockito.any(), Mockito.anyString());
		KarafBundleMBeanPublishBehaviour karafBundleMBeanPublishBehaviour = new KarafBundleMBeanPublishBehaviour();
		assertThat(karafBundleMBeanPublishBehaviour.getBundleId(mbsc, "bundleSymbolicName", "2.0")).isEqualTo(2);
		assertThat(karafBundleMBeanPublishBehaviour.getBundleId(mbsc, "bundleSymbolicName", "1.0")).isEqualTo(1);
	}
	
	@Test
	public void testGetBundleIdWithNulValueForNameAndNoSymbolicName() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("Name", null);
		map.put("ID", 5);
		map.put("Version", "1.0");
		createTabularData(map);
		KarafBundleMBeanPublishBehaviour karafBundleMBeanPublishBehaviour = new KarafBundleMBeanPublishBehaviour();
		assertThat(karafBundleMBeanPublishBehaviour.getBundleId(mbsc, "bundleSymbolicName", null)).isEqualTo(-1);
	}

	private void createTabularData(Map<String, Object> map) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
		TabularData tabularData = new MockedTabularData(Collections.singletonList(new MockedCompositeData(map)));
		doReturn(tabularData).when(mbsc).getAttribute(Mockito.any(), Mockito.anyString());
	}

}
