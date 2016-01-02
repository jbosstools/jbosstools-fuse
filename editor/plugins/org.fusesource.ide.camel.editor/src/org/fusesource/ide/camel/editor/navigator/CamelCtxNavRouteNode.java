/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;

/**
 * Helper class that holds a reference to the route and enclosing camel context file
 */
public class CamelCtxNavRouteNode implements IPropertySource {
	
	private final RouteSupport mRouteSupport;
	private final IFile mFile;
	public CamelCtxNavRouteNode(RouteSupport routeSupport,IFile file) {
		this.mRouteSupport = routeSupport;
		this.mFile = file;
	}
	
	public RouteSupport getCamelRoute(){
		return mRouteSupport;
	}
	public IFile getCamelContextFile(){
		return mFile;
	}
	
	@Override
	public String toString() {
		return mRouteSupport.toString();
	}
	
	private static List<String> getChildNodesDisplayText(AbstractNode routeSupport){
		List<String> nodeKeys = new ArrayList<String>();
		for(AbstractNode childNode:routeSupport.getChildren()){
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
	public RouteSupport getMatchingRouteFromEditorModel(RouteContainer model) {
		if(model!=null){
			List<String> nodeKeys = getChildNodesDisplayText(this.mRouteSupport);
			String nodeDisplayText = this.mRouteSupport.getDisplayText();
			if(nodeDisplayText!=null){
				for(AbstractNode node:model.getChildren()){
					if(node instanceof RouteSupport && nodeDisplayText.equals(node.getDisplayText())){
						List<AbstractNode> editorNodeChildren = node.getChildren();
						if(nodeKeys.size()==editorNodeChildren.size()){	
							List<String> editorNodeKeys = getChildNodesDisplayText(node);
							if(editorNodeKeys.containsAll(nodeKeys)){
								return (RouteSupport)node;
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public Object getEditableValue() {			
		return mRouteSupport.getEditableValue();
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {			
		return mRouteSupport.getPropertyDescriptors();
	}

	@Override
	public Object getPropertyValue(Object id) {			
		return mRouteSupport.getPropertyValue(id);
	}

	@Override
	public boolean isPropertySet(Object id) {			
		return mRouteSupport.isPropertySet(id);
	}

	@Override
	public void resetPropertyValue(Object id) {
		mRouteSupport.resetPropertyValue(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		mRouteSupport.setPropertyValue(id, value);
	}
	
	
}
