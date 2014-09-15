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
import org.fusesource.ide.fabric8.ui.navigator.Fabric;
import org.fusesource.ide.fabric8.ui.navigator.ProfileNode;

public class ProfileContainersTabSection extends AbstractContainersTabSection {

    @Override
    protected Fabric getFabricForNode(Object node) {
        return ((ProfileNode) node).getFabric();
    }

    @Override
    protected List<?> getPropertySourcesForNode(Object node) {
        return node == null ? Collections.emptyList() : ((ProfileNode) node).getPropertySourceList();
    }

    @Override
    protected CreateJCloudsContainerAction createCloudContainerAction(Object current) {
        final ProfileNode node = (ProfileNode) current;
        return new CreateJCloudsContainerAction(node.getVersionNode(), null, node);
    }

    @Override
    protected CreateSshContainerAction createSshContainerAction(Object current) {
        final ProfileNode node = (ProfileNode) current;
        return new CreateSshContainerAction(node.getVersionNode(), null, node);
    }

    @Override
    protected CreateChildContainerAction createChildContainerAction(Object current) {
        final ProfileNode node = (ProfileNode) current;
        return new CreateChildContainerAction(node);
    }

}
