/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * Helper class that holds a reference to the route and enclosing camel context file
 * 
 * @author Renjith M. 
 */
public class CamelCtxNavRouteNode  {
	
	private final CamelRouteElement mRouteSupport;
	private final IFile mFile;

	/**
	 * 
	 * @param routeSupport
	 * @param file
	 */
	public CamelCtxNavRouteNode(CamelRouteElement routeSupport, IFile file) {
		this.mRouteSupport = routeSupport;
		this.mFile = file;
	}
	
	public CamelRouteElement getCamelRoute(){
		return this.mRouteSupport;
	}
	
	public IFile getCamelContextFile(){
		return this.mFile;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.mRouteSupport.toString();
	}
	
	private static List<String> getChildNodesDisplayText(AbstractCamelModelElement routeSupport) {
		List<String> nodeKeys = new ArrayList<String>();
		for (AbstractCamelModelElement childNode : routeSupport.getChildElements()) {
			nodeKeys.add(childNode.getDisplayText());
		}
		return nodeKeys;
	}
	
	/**
	 * An approximate lookup that compares the display text of all the immediate
	 * child nodes and returns the matching node
	 * 
	 * @param model
	 * @return the node that matches the current node in incoming model, null
	 *         otherwise
	 */
	public CamelRouteElement getMatchingRouteFromEditorModel(CamelRouteContainerElement model) {
		if(model!=null){
			List<String> nodeKeys = getChildNodesDisplayText(this.mRouteSupport);
			String nodeDisplayText = this.mRouteSupport.getDisplayText();
			if(nodeDisplayText!=null){
				for(AbstractCamelModelElement node : model.getChildElements()) {
					if(node instanceof CamelRouteElement && nodeDisplayText.equals(node.getDisplayText())) {
						List<AbstractCamelModelElement> editorNodeChildren = node.getChildElements();
						if(nodeKeys.size() == editorNodeChildren.size()) {	
							List<String> editorNodeKeys = getChildNodesDisplayText(node);
							if(editorNodeKeys.containsAll(nodeKeys)) {
								return (CamelRouteElement)node;
							}
						}
					}
				}
			}
		}
		return null;
	}
}
