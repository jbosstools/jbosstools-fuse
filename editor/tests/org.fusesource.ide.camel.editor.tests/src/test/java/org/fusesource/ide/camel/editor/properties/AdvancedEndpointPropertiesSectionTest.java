/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedEndpointPropertiesSectionTest {
	
	@Mock
	private AdvancedEndpointPropertiesSection advancedEndpointPropertiesSection;
	
	@Before
	public void setup(){
		doCallRealMethod().when(advancedEndpointPropertiesSection).computeTabsToCreate(anyList());
	}

	@Test
	public void testComputeTabsToCreate_containsGroupPathEvenForEmptyList() throws Exception {
		List<String> tabsToCreate = advancedEndpointPropertiesSection.computeTabsToCreate(Collections.emptyList());
		
		assertThat(tabsToCreate).containsExactly(FusePropertySection.GROUP_PATH);
	}
	
	@Test
	public void testComputeTabsToCreate_containsGroupFromProperty() throws Exception {
		List<Parameter> props = new ArrayList<>();
		Parameter parameter = new Parameter();
		parameter.setGroup("group1");
		props.add(parameter);
		
		List<String> tabsToCreate = advancedEndpointPropertiesSection.computeTabsToCreate(props);
		
		assertThat(tabsToCreate).containsExactly(FusePropertySection.GROUP_PATH, "group1");
	}
	
	@Test
	public void testComputeTabsToCreate_containsSeveralGroupsFromProperties() throws Exception {
		List<Parameter> props = new ArrayList<>();
		String group1 = "group1";
		props.add(createParameterWithGroup(group1));
		String group2 = "group2";
		props.add(createParameterWithGroup(group2));
		
		List<String> tabsToCreate = advancedEndpointPropertiesSection.computeTabsToCreate(props);
		
		assertThat(tabsToCreate).containsExactly(FusePropertySection.GROUP_PATH, group1, group2);
	}
	
	@Test
	public void testComputeTabsToCreate_containsNoDuplicatedGroupFromProperties() throws Exception {
		List<Parameter> props = new ArrayList<>();
		String group1 = "group1";
		props.add(createParameterWithGroup(group1));
		props.add(createParameterWithGroup(group1));
		
		List<String> tabsToCreate = advancedEndpointPropertiesSection.computeTabsToCreate(props);
		
		assertThat(tabsToCreate).containsExactly(FusePropertySection.GROUP_PATH, group1);
	}

	private Parameter createParameterWithGroup(String group) {
		Parameter parameter = new Parameter();
		parameter.setGroup(group);
		return parameter;
	}

}
