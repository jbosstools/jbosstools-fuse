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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
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
	public void testValidateWithSameComponentIdAndComponentDefinitionIdAndNotSpecifyinValueForFuseNewerThan220_ReportsNoError() throws Exception {
		testValidate("routeWithSameComponentIdAndComponentDefinitionId.xml", 0);
	}
	
	@Test
	public void testValidateWithSameComponentIdAndComponentDefinitionIdAndNotSpecifyinValueForFuseOlderThan220_ReportsError() throws Exception {
		changeProjectCamelVersionTo("2.19.4");
		
		testValidateCreatesAValidationMarker("routeWithSameComponentIdAndComponentDefinitionId.xml");
	}

	private void changeProjectCamelVersionTo(String camelVersion) throws IOException, CoreException {
		IFile pomIfile = fuseProject.getProject().getFile("pom.xml");
		Path targetFile = pomIfile.getLocation().toFile().toPath();

		try (Stream<String> lines = Files.lines(targetFile)) {
			List<String> replaced = lines
					.map(line-> {
						return line.replaceAll(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY, camelVersion);
					})
					.collect(Collectors.toList());
			Files.write(targetFile, replaced);
		}
		
		pomIfile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
	}
	
	@Test
	public void testValidateWithSameComponentIdAndComponentDefinitionIdAndRegisterEndpointsIdsSetToTrue_ReportsError() throws Exception {
		testValidateCreatesAValidationMarker("routeWithSameComponentIdAndComponentDefinitionIdAndRegisterEndpointsIdsSetToTrue.xml");
	}
	
	@Test
	public void testValidateWithSameComponentIdAndComponentDefinitionIdAndRegisterEndpointsIdsSetToFalse_ReportsNoError() throws Exception {
		testValidate("routeWithSameComponentIdAndComponentDefinitionIdAndRegisterEndpointsIdsSetToFalse.xml", 0);
	}

}
