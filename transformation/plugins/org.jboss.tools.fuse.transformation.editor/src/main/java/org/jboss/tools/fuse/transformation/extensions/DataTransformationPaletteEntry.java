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
package org.jboss.tools.fuse.transformation.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.jboss.tools.fuse.transformation.editor.Activator;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;
import org.w3c.dom.Node;

/**
 *
 */
public class DataTransformationPaletteEntry implements ICustomPaletteEntry {

    private static final String PROTOCOL = "dozer";

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry
     * #newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
        return new DataMapperEndpointFigureFeature(fp,
                "Data Transformation",
                "Creates a Data Transformation endpoint...");
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getProtocol()
     */
    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#providesProtocol(java.lang.String)
     */
    @Override
    public boolean providesProtocol(String protocol) {
        return PROTOCOL.equalsIgnoreCase(protocol);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getRequiredDependencies()
     */
    @Override
    public List<Dependency> getRequiredDependencies() {
        List<Dependency> deps = new ArrayList<>();
        Dependency dep = new Dependency();
        dep.setGroupId("org.apache.camel");
        dep.setArtifactId("camel-dozer");
        dep.setVersion(CamelUtils.getCurrentProjectCamelVersion());
        deps.add(dep);
        return deps;
    }

    class DataMapperEndpointFigureFeature extends CreateEndpointFigureFeature {

        public DataMapperEndpointFigureFeature(IFeatureProvider fp,
                String name, String description) {
            super(fp, name, description, null, getRequiredDependencies());
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
         */
        @Override
        public Object[] create(ICreateContext context) {
        	if (getRequiredDependencies() != null && getRequiredDependencies().isEmpty() == false) {
    			// add maven dependency to pom.xml if needed
    	        try {
    	            updateMavenDependencies(getRequiredDependencies());
    	        } catch (CoreException ex) {
    	            Activator.log(Status.ERROR, "Unable to add the component dependency to the project maven configuration file.\n" + ex.getMessage());
    	        }
    		}
    		return super.create(context);
        }

        /* (non-Javadoc)
         * @see org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature#createNode(org.fusesource.ide.camel.model.service.core.model.CamelModelElement, boolean)
         */
        @Override
        protected CamelModelElement createNode(CamelModelElement parent, boolean createDOMNode) {
            // Launch the New Transformation wizard
            NewTransformationWizard wizard = new NewTransformationWizard();
            wizard.setNeedsProgressMonitor(true);

            IResource res = parent.getCamelFile().getResource();
            wizard.setSelectedProject(res.getProject());
            IPath respath = JavaUtil.getJavaPathForResource(res);
            String path = respath.makeRelative().toString();
            wizard.setCamelFilePath(path);

            // eventually we want to do all our Camel file updates
            // within the Camel editor's context, but for now
            // we will have the camel config builder make the updates
            wizard.setSaveCamelConfig(false);

            WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            int status = dialog.open();
            CamelModelElement ep = (status == IStatus.OK) ? wizard.getRouteEndpoint() : null;
            if (ep != null && getEip() != null) {
            	CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
            	if (editor.getModel() != null) { 
            		Node newNode = null;
            		if (createDOMNode) {
            			newNode = editor.getModel().createElement(getEip().getName(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
            		}
            		ep.setParent(parent);
            		ep.setUnderlyingMetaModelObject(getEip());
            		if (createDOMNode) {
            			ep.setXmlNode(newNode);
            			ep.updateXMLNode();
            		}
            	}
            }
            return ep;
        }
    }
}