/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.ui.internal.propertysheet;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;

/**
 * Filter for "Contents" tab section.
 */
public class ContentsSectionFilter implements IFilter {

	private static final ArrayList<String> noContentsTab = new ArrayList<String>();
	
	// we want to skip the contents tab in properties view for the following node types
	static {
		noContentsTab.add("org.fusesource.ide.fabric.camel.navigator.RoutesNode");
		noContentsTab.add("org.fusesource.ide.fabric.navigator.osgi.BundlesNode");
		noContentsTab.add("org.fusesource.ide.fabric.camel.navigator.RouteNode");
		noContentsTab.add("org.fusesource.ide.fabric.camel.navigator.ProcessorNode");
		noContentsTab.add("org.fusesource.ide.fabric.navigator.Fabric");
		noContentsTab.add("org.fusesource.ide.fabric.navigator.ContainersNode");
		noContentsTab.add("org.fusesource.ide.fabric.navigator.ProfileNode");
		noContentsTab.add("org.fusesource.ide.fabric.navigator.ContainerNode");
	}
	
    @Override
    public boolean select(Object toTest) {
    	if (noContentsTab.contains(toTest.getClass().getName())) return false;
    	
        if (toTest instanceof RefreshableCollectionNode) {
            //List<?> propertySources = ((RefreshableCollectionNode) toTest).getPropertySourceList();
            return true; //propertySources != null && propertySources.size() > 0;
        }
        return false;
    }

}
