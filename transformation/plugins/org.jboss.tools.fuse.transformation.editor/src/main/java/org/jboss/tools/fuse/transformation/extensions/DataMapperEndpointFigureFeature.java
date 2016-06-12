/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.fuse.transformation.extensions;

import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;
import org.w3c.dom.Node;

public class DataMapperEndpointFigureFeature extends CreateEndpointFigureFeature {

	private List<Dependency> requiredDependencies;

    public DataMapperEndpointFigureFeature(IFeatureProvider fp, String name, String description, List<Dependency> deps) {
        super(fp, name, description, null, deps);
        this.requiredDependencies = deps;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
     */
    @Override
    public Object[] create(ICreateContext context) {
    	if (requiredDependencies != null && requiredDependencies.isEmpty() == false) {
			// add maven dependency to pom.xml if needed
	        try {
	            updateMavenDependencies(requiredDependencies);
	        } catch (CoreException ex) {
	            Activator.log(Status.ERROR, "Unable to add the component dependency to the project maven configuration file.\n" + ex.getMessage()); //$NON-NLS-1$
	        }
		}
		return super.create(context);
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

        WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
        int status = dialog.open();
        AbstractCamelModelElement ep = (status == IStatus.OK) ? wizard.getRouteEndpoint() : null;
        if (ep != null && getEip() != null) {
        	CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
        	if (editor.getModel() != null) {
        		ep.setParent(parent);
        		ep.setUnderlyingMetaModelObject(getEip());
        		if (createDOMNode) {
        			Node newNode = null;
        			newNode = editor.getModel().createElement(getEip().getName(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
        			ep.setXmlNode(newNode);
        			ep.updateXMLNode();
        		}
        	}
        }
        return ep;
    }
}
