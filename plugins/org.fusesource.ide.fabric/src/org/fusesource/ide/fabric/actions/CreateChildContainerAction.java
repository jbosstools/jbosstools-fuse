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

package org.fusesource.ide.fabric.actions;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.fusesource.ide.commons.ui.actions.ActionSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.Fabric;
import org.fusesource.ide.fabric.navigator.ProfileNode;


public class CreateChildContainerAction extends ActionSupport {
    private ContainerNode containerNode;
    private Fabric fabric;
    private ProfileNode profileNode;

    public CreateChildContainerAction(ContainerNode containerNode) {
        super(Messages.createChildAgentMenuLabel, Messages.createChildAgentToolTip, FabricPlugin.getDefault().getImageDescriptor("add_obj.gif"));
        this.containerNode = containerNode;
        setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("new_child_container.png"));
    }

    public CreateChildContainerAction(Fabric fabric) {
        this((ContainerNode) null);
        this.fabric = fabric;
    }

    public CreateChildContainerAction(ProfileNode profileNode) {
        this(profileNode.getFabric());
        this.profileNode = profileNode;
    }

    @Override
    public void run() {
        ContainerNode node = containerNode;
        if (node == null) {
            // we are invoked from another node so lets find it...
            if (fabric != null) {
                List<ContainerNode> roots = fabric.getRootContainers();
                if (roots.size() > 0) {
                    node = roots.get(0);
                }
            }
        }
        if (node == null) {
            FabricPlugin.getLogger().warning("No Roto container node available for fabric: " + fabric);
        } else {
            CreateChildContainerDialog dialog = new CreateChildContainerDialog(node, node.getFabric().getNewAgentName());
            if (profileNode != null) {
                CreateChildContainerForm childContainerForm = dialog.getChildContainerForm();
                if (childContainerForm != null) {
                    childContainerForm.setInitialProfileSelections(profileNode.getProfile());
                }
            }
            dialog.open();
        }
    }

    public static void addIfSingleRootContainer(IMenuManager menu, Fabric fabric) {
        List<ContainerNode> roots = fabric.getRootContainers();
        if (roots.size() == 1) {
            ContainerNode root = roots.get(0);
            menu.add(new CreateChildContainerAction(root));
        }
    }


    public ContainerNode getContainerNode() {
        return containerNode;
    }

    public void setContainerNode(ContainerNode containerNode) {
        this.containerNode = containerNode;
    }

    public ProfileNode getProfileNode() {
        return profileNode;
    }

    public void setProfileNode(ProfileNode profileNode) {
        this.profileNode = profileNode;
    }

    public void setFabric(Fabric fabric) {
        this.fabric = fabric;
    }

    /**
     * Sets the enabled flag based on whether there's a single root element
     */
    public void updateEnabled() {
        boolean hasRoot = containerNode != null;
        if (containerNode == null && fabric != null) {
            List<ContainerNode> roots = fabric.getRootContainers();
            hasRoot = roots.size() == 1;
        }
        setEnabled(hasRoot);
    }
}