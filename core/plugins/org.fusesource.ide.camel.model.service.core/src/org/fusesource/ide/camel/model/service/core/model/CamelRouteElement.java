/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class CamelRouteElement extends CamelModelElement implements IFuseDetailsPropertyContributor {
	
	/**
	 * contains all inputs of the route
	 */
	private List<CamelModelElement> inputs = new ArrayList<CamelModelElement>();
	
	/**
	 * contains all outputs of the route
	 */
	private List<CamelModelElement> outputs = new ArrayList<CamelModelElement>();
	
	/**
	 * 
	 */
	public CamelRouteElement(CamelContextElement camelContext, Node underlyingNode) {
		super(camelContext, underlyingNode);
	}
	
	/**
	 * parses the children of this node
	 */
	protected void parseChildren() {
		super.parseChildren();
		inputs.clear();
		outputs.clear();
		for (CamelModelElement c : getChildElements()) {
			if (c.getNodeTypeId().equalsIgnoreCase("from")) {
				inputs.add(c);
			} else {
				outputs.add(c);
			}
		}
	}
	
	/**
	 * @return the inputs
	 */
	public List<CamelModelElement> getInputs() {
		return this.inputs;
	}
	
	/**
	 * @return the outputs
	 */
	public List<CamelModelElement> getOutputs() {
		return this.outputs;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#supportsBreakpoint()
	 */
	@Override
	public boolean supportsBreakpoint() {
		return false;
	}
}
