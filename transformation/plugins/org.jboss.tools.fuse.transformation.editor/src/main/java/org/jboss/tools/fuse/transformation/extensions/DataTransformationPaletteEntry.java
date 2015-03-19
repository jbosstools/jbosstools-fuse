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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider.CATEGORY_TYPE;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.connectors.ComponentDependency;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

/**
 * @author bfitzpat
 */
@SuppressWarnings("restriction")
public class DataTransformationPaletteEntry implements ICustomPaletteEntry {


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
        return new DataMapperEndpointFigureFeature(fp, 
                "Data Transformation", 
                "Creates a Data Transformation endpoint...");
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getImageDecorator(java.lang.Object)
     */
    @Override
    public IImageDecorator getImageDecorator(Object object) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getTypeName()
     */
    @Override
    public String getTypeName() {
        return "DataTransformation";
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#supports(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(Class type) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getRequiredCapabilities(java.lang.Object)
     */
    @Override
    public List<ComponentDependency> getRequiredCapabilities(Object object) {
        List<ComponentDependency> deps = new ArrayList<ComponentDependency>();
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

        @Override
        public Object[] create(ICreateContext context) {
            RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
            RouteContainer routeContainer = Activator.getDiagramEditor().getModel();
            CamelContextFactoryBean camelContext =
                    routeContainer.getModel().getContextElement();
            
            // Launch the New Transformation wizard
            NewTransformationWizard wizard = new NewTransformationWizard(); 
            WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            int status = dialog.open(); 
            if (status != IStatus.OK) {
                return new Object[] {};
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
            Endpoint routeEndpoint = new Endpoint("ref:xml2json");
            if (selectedRoute != null) {
                selectedRoute.addChild(routeEndpoint);
            } else {
                Activator.getLogger().warning(
                        "Warning! Could not find currently selectedNode, so can't associate this node with the route!: "
                                + routeEndpoint);
            }

            // do the add
            PictogramElement pe = addGraphicalRepresentation(context, routeEndpoint);
            getFeatureProvider().link(pe, routeEndpoint);

            // activate direct editing after object creation
            getFeatureProvider().getDirectEditingInfo().setActive(true);

            // return newly created business object(s)
            return new Object[] {routeEndpoint};
        }

        private void addCamelContextEndpoint(CamelContextFactoryBean context,
                CamelEndpointFactoryBean endpoint) {
            List<CamelEndpointFactoryBean> endpoints = context.getEndpoints();
            if (endpoints == null) {
                endpoints = new LinkedList<CamelEndpointFactoryBean>();
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
    }

    @Override
    public String getPaletteCategory() {
        return CATEGORY_TYPE.TRANSFORMATION.name();
    }

    /**
     * Due to https://issues.apache.org/jira/browse/CAMEL-8498, we cannot set
     * endpoints on CamelContextFactoryBean directly. Use reflection for now
     * until this issue is resolved upstream.
     */
    private void setEndpoints(CamelContextFactoryBean context,
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