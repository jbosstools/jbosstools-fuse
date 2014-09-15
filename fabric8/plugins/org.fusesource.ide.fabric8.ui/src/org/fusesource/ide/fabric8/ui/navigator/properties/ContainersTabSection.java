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
package org.fusesource.ide.fabric8.ui.navigator.properties;

import java.util.Collections;
import java.util.List;

import org.fusesource.ide.fabric8.ui.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric8.ui.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric8.ui.actions.jclouds.CreateJCloudsContainerAction;
import org.fusesource.ide.fabric8.ui.navigator.ContainersNode;
import org.fusesource.ide.fabric8.ui.navigator.Fabric;

/**
 * ContainersTabSection
 */
public class ContainersTabSection extends AbstractContainersTabSection {

    protected Fabric getFabricForNode(Object node) {
        return ((ContainersNode) node).getFabric();
    }

    protected List<?> getPropertySourcesForNode(Object node) {
        return node == null ? Collections.emptyList() : ((ContainersNode) node).getPropertySourceList();
    }

    protected CreateJCloudsContainerAction createCloudContainerAction(Object current) {
        return new CreateJCloudsContainerAction(getFabric());
    }

    protected CreateSshContainerAction createSshContainerAction(Object current) {
        return new CreateSshContainerAction(getFabric());
    }

    protected CreateChildContainerAction createChildContainerAction(Object current) {
        return new CreateChildContainerAction(getFabric());
    }

}
