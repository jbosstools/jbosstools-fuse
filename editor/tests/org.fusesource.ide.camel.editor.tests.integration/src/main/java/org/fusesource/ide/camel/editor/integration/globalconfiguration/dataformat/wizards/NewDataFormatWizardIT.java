/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.integration.globalconfiguration.dataformat.wizards;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.provider.DataFormatContributor;
import org.fusesource.ide.camel.editor.globalconfiguration.dataformat.wizards.NewDataFormatWizard;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.tests.integration.core.io.FuseProject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Aurelien Pupier
 *
 */
@RunWith(Parameterized.class)
public class NewDataFormatWizardIT {

	private static final int CURRENTLY_SHIPPED_MODEL_BUNDLES = 5;
	
	@Rule
	public FuseProject fuseproject = new FuseProject(NewDataFormatWizardIT.class.getName());

	@Parameter
	public String camelVersion;

	@Parameter(value = 1)
	public String dataFormatName;

	@Parameter(value = 2)
	public DataFormat dataFormat;

	@Parameters(name = "{0} - {1}")
	public static Collection<Object[]> data() {
		List<String> supportedCamelVersions = CamelModelFactory.getSupportedCamelVersions();
		Assertions.assertThat(supportedCamelVersions).hasSize(CURRENTLY_SHIPPED_MODEL_BUNDLES);
		Collection<Object[]> res = new HashSet<>();
		for (String camelVersion : supportedCamelVersions) {
			CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);
			List<DataFormat> supportedDataFormats = camelModel.getDataformatModel().getSupportedDataFormats();
			Stream<Object[]> stream = supportedDataFormats.stream().map(dataFormat -> new Object[] { camelVersion, dataFormat.getName(), dataFormat });
			res.addAll(stream.collect(Collectors.toCollection(HashSet::new)));
		}
		return res;
	}

	@Test
	public void testCreation() throws CoreException, IOException {
		CamelModel camelModel = CamelModelFactory.getModelForVersion(camelVersion);

		CamelFile camelFile = fuseproject.createEmptyCamelFile();

		NewDataFormatWizard newDataFormatWizard = new NewDataFormatWizard(camelFile, camelModel.getDataformatModel());
		final String id = dataFormat.getName() + "-id";
		Element dataFormatNode = newDataFormatWizard.createDataFormatNode(dataFormat, id);
		new CamelGlobalConfigEditor(null).addDataFormat(camelFile, dataFormatNode);

		// Check that element just created has been correctly initialized
		check(id, camelFile);
		// Check that Model is valid after reloading from the filesystem
		final CamelIOHandler camelIOHandler = new CamelIOHandler();
		camelIOHandler.setDocument(camelFile.getDocument());
		camelIOHandler.saveCamelModel(camelFile, camelFile.getResource().getLocation().toFile(), new NullProgressMonitor());
		CamelFile reloadedCamelFile = camelIOHandler.loadCamelModel(camelFile.getResource(), new NullProgressMonitor());
		check(id, reloadedCamelFile);
	}

	/**
	 * @param id
	 * @param camelFile
	 */
	private void check(final String id, CamelFile camelFile) {
		Assertions.assertThat(camelFile.getRouteContainer() instanceof CamelContextElement).isTrue();
		Assertions.assertThat(((CamelContextElement)camelFile.getRouteContainer()).getDataformats().keySet()).containsExactly(id);
		AbstractCamelModelElement dataFormatCME = ((CamelContextElement)camelFile.getRouteContainer()).getDataformats().get(id);
		checkSpecialParameterLoaded(id, dataFormatCME);
		assertThat(new DataFormatContributor().canHandle(dataFormatCME)).isTrue();
	}

	/**
	 * @param id
	 * @param dataFormatReloaded
	 */
	private void checkSpecialParameterLoaded(final String id, AbstractCamelModelElement dataFormatReloaded) {
		final Object typeParameter = dataFormatReloaded.getParameter("type");
		if ("bindy-csv-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("Csv");
		} else if ("bindy-kpv-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("KeyValue");
		} else if ("bindy-fixed-id".equals(id)) {
			Assertions.assertThat(typeParameter).isEqualTo("Fixed");
		}

		final Object libraryParameter = dataFormatReloaded.getParameter("library");
		if ("json-jackson-id".equals(id)) {
			Assertions.assertThat(libraryParameter).isEqualTo("Jackson");
		} else if ("json-xstream-id".equals(id)) {
			// Default value in general, don't know how to check it currently
			// because it is removed from the model attributes list then
			// Assertions.assertThat(libraryParameter).isEqualTo("XStream");
		} else if ("json-gson-id".equals(id)) {
			Assertions.assertThat(libraryParameter).isEqualTo("Gson");
		}
	}
}
