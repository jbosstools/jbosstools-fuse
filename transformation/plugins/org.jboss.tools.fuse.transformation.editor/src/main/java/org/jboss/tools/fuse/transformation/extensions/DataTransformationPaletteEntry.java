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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.DataFormatsDefinition;
import org.apache.camel.spring.CamelContextFactoryBean;
import org.apache.camel.spring.CamelEndpointFactoryBean;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider.CATEGORY_TYPE;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.connectors.ComponentDependency;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

/**
 *
 */
@SuppressWarnings("restriction")
public class DataTransformationPaletteEntry implements ICustomPaletteEntry {


    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
        return new DataMapperEndpointFigureFeature(fp,
                "Data Transformation",
                "Creates a Data Transformation endpoint...");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getImageDecorator(java.lang.Object)
     */
    @Override
    public IImageDecorator getImageDecorator(Object object) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getTypeName()
     */
    @Override
    public String getTypeName() {
        return "DataTransformation";
    }

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#supports(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(Class type) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getRequiredCapabilities(java.lang.Object)
     */
    @Override
    public List<ComponentDependency> getRequiredCapabilities(Object object) {
        List<ComponentDependency> deps = new ArrayList<>();
        ComponentDependency dep = new ComponentDependency();
        dep.setGroupId("org.apache.camel");
        dep.setArtifactId("camel-dozer");
        dep.setVersion(Activator.getDefault().getCamelVersion());
        deps.add(dep);
        return deps;
    }

    class DataMapperEndpointFigureFeature extends CreateEndpointFigureFeature {

        public DataMapperEndpointFigureFeature(IFeatureProvider fp,
                String name, String description) {
            super(fp, name, description, null, getRequiredCapabilities(null));
        }
        
        /*
         * (non-Javadoc)
         * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
         */
        @Override
        public Object[] create(ICreateContext context) {
            // had to override so we get the route BEFORE we create the node, otherwise the focus has changed to the transformation editor
            // before we can get the selected route
            RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
            AbstractNode node = createNode();
            if (selectedRoute != null) {
                selectedRoute.addChild(node);
            } else {
                Activator.getLogger().warning("Warning! Could not find currently selectedNode, so can't associate this node with the route!: " + node);
            }

            // do the add
            PictogramElement pe = addGraphicalRepresentation(context, node);

            getFeatureProvider().link(pe, node);
            
            // activate direct editing after object creation
            getFeatureProvider().getDirectEditingInfo().setActive(true);
            
            // return newly created business object(s)
            return new Object[] { node };
        }

        private void addCamelContextEndpoint(CamelContextFactoryBean context,
                CamelEndpointFactoryBean endpoint) {
            List<CamelEndpointFactoryBean> endpoints = context.getEndpoints();
            if (endpoints == null) {
                endpoints = new LinkedList<>();
            }
            endpoints.add(endpoint);
            setEndpoints(context, endpoints);
        }

        private void addDataFormat(CamelContextFactoryBean context, DataFormatDefinition dataFormat) {
            DataFormatsDefinition dataFormats = context.getDataFormats();
            // create the parent element if it doesn't exist
            if (dataFormats == null) {
                dataFormats = new DataFormatsDefinition();
            }

            // add to the list of formats
            if (dataFormats.getDataFormats() == null) {
                dataFormats.setDataFormats(new LinkedList<DataFormatDefinition>());
            }

            dataFormats.getDataFormats().add(dataFormat);
            context.setDataFormats(dataFormats);
        }

        @Override
        protected AbstractNode createNode() {
            RouteContainer routeContainer = Activator.getDiagramEditor().getModel();
            CamelContextFactoryBean camelContext =
                    routeContainer.getModel().getContextElement();

            // Launch the New Transformation wizard
            NewTransformationWizard wizard = new NewTransformationWizard();
            
            Object element = Activator.getDiagramEditor().getEditorInput();
            if (element instanceof IFileEditorInput) {
                IFileEditorInput input = (IFileEditorInput) element;
                IFile res = input.getFile();
                wizard.setSelectedProject(res.getProject());
                IPath respath = JavaUtil.getJavaPathForResource(res);
                String path = respath.makeRelative().toString();
                wizard.setCamelFilePath(path);
                
                // eventually we want to do all our Camel file updates
                // within the Camel editor's context, but for now
                // we will have the camel config builder make the updates
                wizard.setSaveCamelConfig(false);
            }

            WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            int status = dialog.open();
            if (status != IStatus.OK) {
                return null;
            }

            try {
                // try to refresh the project one more time to make sure everything is
                // not out of sync in Eclipse
                if (wizard.getModel().getProject() != null) {
                    wizard.getModel().getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
                }
            } catch (CoreException e) {
                // ignore
            }
            // Wizard completed successfully; create the necessary config
            addCamelContextEndpoint(camelContext, wizard.getEndpoint().asSpringEndpoint());
            if (wizard.getSourceFormat() != null) {
                addDataFormat(camelContext, wizard.getSourceFormat());
            }
            if (wizard.getTargetFormat() != null) {
                addDataFormat(camelContext, wizard.getTargetFormat());
            }

            // Create the route endpoint
            Endpoint routeEndpoint = new Endpoint("ref:" + wizard.getEndpoint().getId());
            return routeEndpoint;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getPaletteCategory()
     */
    @Override
    public String getPaletteCategory() {
        return CATEGORY_TYPE.TRANSFORMATION.name();
    }

    /**
     * Due to https://issues.apache.org/jira/browse/CAMEL-8498, we cannot set
     * endpoints on CamelContextFactoryBean directly. Use reflection for now
     * until this issue is resolved upstream.
     *
     * @param context
     * @param endpoints
     */
    void setEndpoints(CamelContextFactoryBean context,
            List<CamelEndpointFactoryBean> endpoints) {
        try {
            Field endpointsField = context.getClass().getDeclaredField("endpoints");
            endpointsField.setAccessible(true);
            endpointsField.set(context, endpoints);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}