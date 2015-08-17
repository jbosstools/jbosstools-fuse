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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.catalog.CamelModel;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.eips.Eip;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;


public class StubContainerMarshaller extends ContainerMarshallerSupport {

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#loadRoutes(java.io.File)
	 */
	@Override
	public RouteContainer loadRoutes(File file) {
		Activator.getLogger().debug("Stub marshaller about to open: " + file);
		return createDummyModel();
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#loadRoutesFromText(java.lang.String)
	 */
	@Override
	public RouteContainer loadRoutesFromText(String text) {
		return createDummyModel();
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#save(org.eclipse.core.resources.IFile, org.fusesource.ide.camel.model.RouteContainer, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void save(IFile file, RouteContainer model, IProgressMonitor monitor) {
		Activator.getLogger().debug("TODO: should be saving route " + model + " to "
				+ file + "...");
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#save(java.io.File, org.fusesource.ide.camel.model.RouteContainer)
	 */
	@Override
	public void save(File file, RouteContainer model) {
		Activator.getLogger().debug("TODO: should be saving route " + model + " to "
				+ file + "...");
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#updateText(java.lang.String, org.fusesource.ide.camel.model.RouteContainer)
	 */
	@Override
	public String updateText(String xmlText, RouteContainer model) {
		return xmlText;
	}

	protected RouteContainer createDummyModel() {
		CamelModel model = CamelModelFactory.getModelForVersion(CamelModelFactory.getCamelVersion(null));
		Eip eip = model.getEipModel().getEIPByClass("bean");
		
		
		RouteContainer c = new RouteContainer();

		RouteSupport route1 = new Route();

		Endpoint ep1 = new Endpoint();
		UniversalEIPNode bean1 = new UniversalEIPNode(eip);
		UniversalEIPNode bean2 = new UniversalEIPNode(eip);
		

		ep1.setId("fileIn1");
		ep1.setDescription("Polls files from input folder...");
		ep1.setUri("file:///home/lhein/test/input/");
		ep1.setLayout(new Rectangle(10, 10, 100, 40));

		bean1.setId("procBean1");
		bean1.setDescription("Examines the file...");
		bean1.setShortPropertyValue("method", "examineFile");
		bean1.setShortPropertyValue("ref", "proc1"); 
		bean1.setLayout(new Rectangle(150, 10, 100, 40));

		bean2.setId("procBean2");
		bean2.setDescription("Examines the file...");
		bean1.setShortPropertyValue("method", "examineFile");
		bean1.setShortPropertyValue("ref", "proc2");
		bean2.setLayout(new Rectangle(300, 10, 100, 40));

		ep1.addTargetNode(bean1);
		bean1.addTargetNode(bean2);

		route1.addChild(bean2);
		route1.addChild(ep1);
		route1.addChild(bean1);

		c.addChild(route1);

		Activator.getLogger().debug("Stub marshaller has loaded model: "
				+ c.getDebugInfo());
		return c;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.io.ContainerMarshaler#isNoRoutesOnLoad()
	 */
	@Override
	public boolean isNoRoutesOnLoad() {
		return false;
	}
}
