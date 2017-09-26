/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.validation.xml;

import org.junit.Test;

public class XMLCamelRoutesValidatorCheckIdIT extends AbstractXMLCamelRouteValidorTestHelper {
	
	@Test
	public void testValidateDuplicateIdsReportsError() throws Exception {
		testValidateCreatesAValidationMarker("routeWithDuplicatedIds.xml");
	}
	
	@Test
	public void testValidateWithCamelContextAndRouteWithSameIdReportsError() throws Exception {
		testValidateCreatesAValidationMarker("routeWithCamelContextAndRouteDuplicatedId.xml");
	}
	
	@Test
	public void testValidateWithSameComponentIdAndComponentDefintionIdReportsError() throws Exception {
		testValidateCreatesAValidationMarker("routeWithSameComponentIdAndComponentDefintionId.xml");
	}

}
