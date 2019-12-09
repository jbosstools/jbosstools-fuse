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
package org.fusesource.ide.camel.validation.xml;

import org.junit.Test;

public class XMLCamelRoutesValidatorIT extends AbstractXMLCamelRouteValidorTestHelper{

	@Test
	public void testValidateReturnErrorForInvalidNumberParameter() throws Exception {
		testValidateCreatesAValidationMarker("routeWithDuplicatedIds.xml");
	}

	@Test
	public void testValidateOnGlobalEndpoints() throws Exception {
		testValidateCreatesAValidationMarker("routeWithGlobalEndpointWithInvalidNumberParameter.xml");
	}

	@Test
	public void testValidateUnMarshallNodesMissingChild() throws Exception {
		testValidateCreatesAValidationMarker("routeWithUnmarshalMissingChild.xml");
	}

	@Test
	public void testValidateUnMarshallNodesWithoutError() throws Exception {
		testValidate("routeWithUnmarshalValid.xml", 0);
	}

	@Test
	public void testValidateUnMarshallNodesWithRefSettedReturnsError() throws Exception {
		testValidate("routeWithUnmarshalWithRefAndConfigSet.xml", 2);
	}

	@Test
	public void testValidateRefMissingReportsWarning() throws Exception {
		testValidateCreatesAValidationMarker("routeWithUnmarshalWithInvalidRefSet.xml");
	}

}
