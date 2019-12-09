/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.util.CamelFilesFinder;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CamelRouteElement extends AbstractCamelModelElement implements IFuseDetailsPropertyContributor {
	
	/**
	 * contains all inputs of the route
	 */
	private List<AbstractCamelModelElement> inputs = new ArrayList<>();
	
	/**
	 * contains all outputs of the route
	 */
	private List<AbstractCamelModelElement> outputs = new ArrayList<>();
	
	public CamelRouteElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode);
	}
	
	public CamelRouteElement(CamelContextElement camelContext, Node underlyingNode) {
		super(camelContext, underlyingNode);
	}
	
	/**
	 * parses the children of this node
	 */
	@Override
	protected void parseChildren() {
		super.parseChildren();
		inputs.clear();
		outputs.clear();
		for (AbstractCamelModelElement c : getChildElements()) {
			if (AbstractCamelModelElement.ENDPOINT_TYPE_FROM.equalsIgnoreCase(c.getNodeTypeId())) {
				inputs.add(c);
			} else {
				outputs.add(c);
			}
		}
	}
	
	/**
	 * @return the inputs
	 */
	public List<AbstractCamelModelElement> getInputs() {
		return this.inputs;
	}
	
	/**
	 * @return the outputs
	 */
	public List<AbstractCamelModelElement> getOutputs() {
		return this.outputs;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#supportsBreakpoint()
	 */
	@Override
	public boolean supportsBreakpoint() {
		return false;
	}
	
	@Override
	public String getNewID() {
		Set<CamelFile> resolvedCamelFiles = findCamelFilesInSameProject();
		int i = 1;
		String answer = String.format("_%s%d", getNodeTypeId(), i++);
		while (!isNewIDAvailable(resolvedCamelFiles, answer)) {
			answer = String.format("_%s%d", getNodeTypeId(), i++);
		}
		return answer;
	}

	Set<CamelFile> findCamelFilesInSameProject() {
		// grab ab camel files
		Set<IFile> allCamelFilesInProject = new CamelFilesFinder().findFiles(getCamelFile().getResource().getProject());
		Set<CamelFile> cfSet = new HashSet<>();
		Iterator<IFile> cfIt = allCamelFilesInProject.iterator();
		while (cfIt.hasNext()) {
			IFile file = cfIt.next();
			// try to grab from opened editors
			CamelFile cf = CamelFilesFinder.getFileFromEditor(file);
			if (cf != null) {
				// use the model of the open editor
				cfSet.add(cf);
			} else {
				// use what is available in the file
				cfSet.add(new CamelIOHandler().loadCamelModel(file, new NullProgressMonitor()));
			}
		}
		return cfSet;
	}
	
	private boolean isNewIDAvailable(Set<CamelFile> resolvedCamelFiles, String answer) {
		for (CamelFile camelFile : resolvedCamelFiles) {
			if(!camelFile.isNewIDAvailable(answer)){
				return false;
			}
		}
		return true;
	}
}
