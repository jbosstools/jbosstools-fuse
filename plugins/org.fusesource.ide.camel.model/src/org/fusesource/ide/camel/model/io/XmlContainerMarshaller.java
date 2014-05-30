/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.camel.model.RouteDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.camel.tooling.util.RouteXml;
import org.fusesource.camel.tooling.util.XmlModel;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.commons.util.IOUtils;


public class XmlContainerMarshaller extends ContainerMarshallerSupport {

	private boolean noRoutesOnLoad;
	
	@Override
	public RouteContainer loadRoutes(File file) {
		try {
			RouteXml helper = createXmlHelper();
			Activator.getLogger().debug("Loading file: " + file);
			XmlModel model = helper.unmarshal(file);
			if (model.getRouteDefinitionList().size()<1) noRoutesOnLoad = true;
			return toContainer(model);
		} catch (Exception e) {
			Activator.getLogger().error("Failed to load Camel mode: " + e, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public RouteContainer loadRoutesFromText(String text) {
		try {
			RouteXml helper = createXmlHelper();
			XmlModel model = helper.unmarshal(text);
			if (model.getRouteDefinitionList().size()<1) noRoutesOnLoad = true;
			return toContainer(model);
		} catch (Exception e) {
			Activator.getLogger().error("Failed to load Camel mode: " + e, e);
			throw new RuntimeException(e);
		}
	}

	protected RouteContainer toContainer(XmlModel model) {
		List<RouteDefinition> routes = model.getRouteDefinitionList();
		RouteContainer answer = new RouteContainer();
		answer.addRoutes(routes);

		answer.setBeans(model.beanMap());
		answer.setCamelContextEndpointUris(model.endpointUriSet());
		answer.setModel(model);
		
		/*
		 * we can't use the camelContext.getEndpointMap() approach as the endpointMap 
		 * is not populated until after start() 
		 */
		return answer;
	}

	@Override
	public void save(IFile ifile, RouteContainer model, IProgressMonitor monitor) throws CoreException {
		RouteXml helper = createXmlHelper();
		List<RouteDefinition> list = model.createRouteDefinitions();

		try {
			String text = IOUtils.loadText(ifile.getContents(), ifile.getCharset());
			String newText = helper.marshalToText(text, list);
			ifile.setContents(new ByteArrayInputStream(newText.getBytes()), true, true, monitor);
		} catch (IOException ex) {
			Activator.getLogger().error("Unable to load text from stream", ex);
		}
	}

	@Override
	public void save(File file, RouteContainer model) {
		List<RouteDefinition> list = model.createRouteDefinitions();

		RouteXml helper = createXmlHelper();
		
		// we check if there is a single route without any content and if this route
		// got added after loading or not
		if (list.size() == 1 && list.get(0).getInputs().size() < 1 && list.get(0).getOutputs().size() < 1 && isNoRoutesOnLoad()) {
			// seems we added that route inside ide automatically as there was no route initially - so we again delete it
			list.clear();
		}

		Activator.getLogger().debug("Saving to file " + file + " routes: " + list);
		helper.marshal(file, list);
	}

	protected RouteXml createXmlHelper() {
		return new RouteXml();
	}

	public String updateText(String xmlText, RouteContainer model) {
		List<RouteDefinition> list = model.createRouteDefinitions();
		RouteXml helper = createXmlHelper();
		
		// we check if there is a single route without any content and if this route
		// got added after loading or not
		if (list.size() == 1 && list.get(0).getInputs().size() < 1 && list.get(0).getOutputs().size() < 1 && isNoRoutesOnLoad()) {
			// seems we added that route inside ide automatically as there was no route initially - so we again delete it
			list.clear();
		}

		return helper.marshalToText(xmlText, list);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#isNoRoutesOnLoad()
	 */
	@Override
	public boolean isNoRoutesOnLoad() {
		return this.noRoutesOnLoad;
	}
}
