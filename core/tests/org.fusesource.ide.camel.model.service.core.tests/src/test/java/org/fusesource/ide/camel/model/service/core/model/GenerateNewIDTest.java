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
package org.fusesource.ide.camel.model.service.core.model;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenerateNewIDTest {
	
	@Mock
	private Eip underlyingMetaModel;
	@Mock
	private IProject project;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CamelFile camelFile, camelFile2;

	@Test
	public void getNewId_ReturnsFirstValueWhenUnique() throws Exception {
		doReturn("noteType").when(underlyingMetaModel).getName();
		CamelBasicModelElement camelBasicModelElement = new CamelBasicModelElement(new CamelContextElement(null, null), null);
		camelBasicModelElement.setUnderlyingMetaModelObject(underlyingMetaModel);
		
		assertThat(camelBasicModelElement.getNewID()).isEqualTo("_noteType1");
	}
	
	@Test
	public void getNewId_ReturnsSecondValueWhenSeveralAlreadyExisting() throws Exception {
		doReturn("noteType").when(underlyingMetaModel).getName();
		CamelContextElement parent = new CamelContextElement(null, null);
		CamelBasicModelElement camelBasicModelElement = new CamelBasicModelElement(parent, null);
		camelBasicModelElement.setUnderlyingMetaModelObject(underlyingMetaModel);
		parent.addChildElement(camelBasicModelElement);
		camelBasicModelElement.setId(camelBasicModelElement.getNewID());
		CamelBasicModelElement camelBasicModelElement2 = new CamelBasicModelElement(parent, null);
		camelBasicModelElement2.setUnderlyingMetaModelObject(underlyingMetaModel);
		
		assertThat(camelBasicModelElement2.getNewID()).isEqualTo("_noteType2");
	}
	
	@Test
	public void getNewId_ReturnsSecondValueOnRouteWhenSeveralCamelFileInSameProject() throws Exception {
		Set<CamelFile> camelFiles = new HashSet<>();
		camelFiles.add(camelFile);
		when(camelFile.isNewIDAvailable(Mockito.anyString())).thenCallRealMethod();

		doReturn("route").when(underlyingMetaModel).getName();
		CamelContextElement parent = new CamelContextElement(camelFile, null);
		doReturn(parent).when(camelFile).getRouteContainer();
		CamelRouteElement camelRouteModelElement = spy(new CamelRouteElement(parent, null));
		doReturn(camelFiles).when(camelRouteModelElement).findCamelFilesInSameProject();
		camelRouteModelElement.setUnderlyingMetaModelObject(underlyingMetaModel);
		String newID = camelRouteModelElement.getNewID();
		assertThat(newID).isEqualTo("_route1");
		camelRouteModelElement.setId(newID);
		parent.addChildElement(camelRouteModelElement);
		
		/*Artificially add the second camelfile in the project*/
		camelFiles.add(camelFile2);
		when(camelFile2.isNewIDAvailable(Mockito.anyString())).thenCallRealMethod();
		/*Configure the second CamelFile*/
		CamelContextElement parent2 = new CamelContextElement(camelFile2, null);
		doReturn(parent2).when(camelFile2).getRouteContainer();
		CamelRouteElement camelRouteModelElement2 = spy(new CamelRouteElement(parent2, null));
		doReturn(camelFiles).when(camelRouteModelElement2).findCamelFilesInSameProject();
		camelRouteModelElement2.setUnderlyingMetaModelObject(underlyingMetaModel);
		parent2.addChildElement(camelRouteModelElement2);
		
		assertThat(camelRouteModelElement2.getNewID()).isEqualTo("_route2");
	}
	
	@Test
	public void getNewId_supportFileWithoutContext() throws Exception {
		Set<CamelFile> camelFiles = new HashSet<>();
		camelFiles.add(camelFile);
		when(camelFile.isNewIDAvailable(Mockito.anyString())).thenCallRealMethod();

		doReturn("route").when(underlyingMetaModel).getName();
		doReturn(null).when(camelFile).getRouteContainer();
		
		/*Artificially add the second camelfile in the project*/
		camelFiles.add(camelFile2);
		when(camelFile2.isNewIDAvailable(Mockito.anyString())).thenCallRealMethod();
		/*Configure the second CamelFile*/
		CamelContextElement parent2 = new CamelContextElement(camelFile2, null);
		doReturn(parent2).when(camelFile2).getRouteContainer();
		CamelRouteElement camelRouteModelElement2 = spy(new CamelRouteElement(parent2, null));
		doReturn(camelFiles).when(camelRouteModelElement2).findCamelFilesInSameProject();
		camelRouteModelElement2.setUnderlyingMetaModelObject(underlyingMetaModel);
		parent2.addChildElement(camelRouteModelElement2);
		
		assertThat(camelRouteModelElement2.getNewID()).isEqualTo("_route1");
	}
	
	
}
