/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.properties;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;

/**
 * Filter for "Contents" tab section.
 */
public class ContentsSectionFilter implements IFilter {

	private static final ArrayList<String> noContentsTab = new ArrayList<String>();

	// we want to skip the contents tab in properties view for the following node types
	static {
		noContentsTab.add("org.fusesource.ide.jmx.camel.navigator.RoutesNode");
		noContentsTab.add("org.fusesource.ide.jmx.camel.navigator.RouteNode");
		noContentsTab.add("org.fusesource.ide.jmx.camel.navigator.ProcessorNode");
		noContentsTab.add("org.fusesource.ide.jmx.karaf.navigator.osgi.BundlesNode");
		noContentsTab.add("org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node");
		noContentsTab.add("org.fusesource.ide.jmx.fabric8.navigator.ContainersNode");
		noContentsTab.add("org.fusesource.ide.jmx.fabric8.navigator.ProfileNode");
		noContentsTab.add("org.fusesource.ide.jmx.fabric8.navigator.ContainerNode");
		noContentsTab.add("org.fusesource.ide.jmx.fabric8.navigator.VersionNode");
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