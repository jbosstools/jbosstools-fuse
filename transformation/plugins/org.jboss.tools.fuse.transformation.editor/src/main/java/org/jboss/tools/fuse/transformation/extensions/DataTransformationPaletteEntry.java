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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.jboss.tools.fuse.transformation.editor.internal.util.JavaUtil;
import org.jboss.tools.fuse.transformation.editor.wizards.NewTransformationWizard;

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
        dep.setVersion(Activator.getDefault().getCamelVersion());
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
            // had to override so we get the route BEFORE we create the node, otherwise the focus has
            // changed to the transformation editor before we can get the selected route
            RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
            AbstractNode node = createNode();

            if (node == null) {
                // user canceled the wizard
                return new Object[0];
            }

            if (selectedRoute != null) {
                selectedRoute.addChild(node);
            } else {
                Activator.getLogger().warning("Warning! Could not find currently selectedNode,"
                        + " so can't associate this node with the route!: " + node);
            }

            // do the add
            PictogramElement pe = addGraphicalRepresentation(context, node);

            getFeatureProvider().link(pe, node);

            // activate direct editing after object creation
            getFeatureProvider().getDirectEditingInfo().setActive(true);

            // return newly created business object(s)
            return new Object[] { node };
        }

        @Override
        protected AbstractNode createNode() {
            // Launch the New Transformation wizard
            NewTransformationWizard wizard = new NewTransformationWizard();
            wizard.setNeedsProgressMonitor(true);

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
            return (status == IStatus.OK) ? wizard.getRouteEndpoint() : null;
        }
    }
}