/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.fuse.transformation.extensions;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;
import org.w3c.dom.Node;

public class DataMapperEndpointFigureFeature extends CreateEndpointFigureFeature {

    public DataMapperEndpointFigureFeature(IFeatureProvider fp, String name, String description, List<Dependency> deps) {
        super(fp, name, description, null, deps);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature#createNode(org.fusesource.ide.camel.model.service.core.model.CamelModelElement, boolean)
     */
    @Override
    protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
        // Launch the New Transformation wizard
        NewTransformationWizard wizard = new NewTransformationWizard();
        wizard.init(null, null);
        wizard.setNeedsProgressMonitor(true);

        // eventually we want to do all our Camel file updates
        // within the Camel editor's context, but for now
        // we will have the camel config builder make the updates
        wizard.setSaveCamelConfig(false);

        WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
        dialog.setBlockOnOpen(true);
        int status = dialog.open();
        AbstractCamelModelElement ep = (status == IStatus.OK) ? wizard.getRouteEndpoint() : null;
        if (ep != null && getEip() != null) {
        	CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
        	if (editor.getModel() != null) {
        		ep.setParent(parent);
        		ep.setUnderlyingMetaModelObject(getEip());
        		if (createDOMNode) {
        			Node newNode = editor.getModel().createElement(getEip().getName(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
        			ep.setXmlNode(newNode);
        			ep.updateXMLNode();
        		}
        	}
        }
        return ep;
    }
}
