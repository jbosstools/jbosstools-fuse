/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.features.create.ext;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.ConnectorsMessages;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.ConnectorEndpoint;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.connectors.Connector;
import org.fusesource.ide.camel.model.connectors.ConnectorDependency;

/**
 * @author lhein
 */
public class CreateConnectorFigureFeature extends CreateFigureFeature<Endpoint> {
    
    private final ConnectorEndpoint endpoint;
    protected final Connector connector;
    
    /**
     * creates a connector create feature
     * 
     * @param fp
     * @param connector
     */
    public CreateConnectorFigureFeature(IFeatureProvider fp, Connector connector) {
        super(fp, getDisplayText(connector.getId()), getDescription(connector.getId()), Endpoint.class);
        this.endpoint = new ConnectorEndpoint(String.format("%s:", connector.getProtocols().get(0).getPrefix())); // we use the first found protocol string
        setExemplar(this.endpoint);
        this.connector = connector;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#getIconName()
     */
    @Override
    protected String getIconName() {
        return String.format("%s.png", this.connector.getId().replaceAll("-", "_"));
    }
        
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#createNode()
     */
    @Override
    protected AbstractNode createNode() {
        return new ConnectorEndpoint(this.endpoint);
    }
        
    /**
     * determines the label to display for that palette entry
     * 
     * @param connectorId
     * @return
     */
    protected static String getDisplayText(String connectorId) {
        String key = connectorId.toLowerCase().replaceAll("-", "_") + "_connector_title";
        try {
            return ConnectorsMessages.class.getDeclaredField(key).get(null).toString();
        } catch (Exception ex) {
            Activator.getLogger().warning("Missing translation for key \"" + key + "\"", ex);
        }
        return connectorId;
    }
    
    /**
     * determines the description to display for this palette entry
     * 
     * @param connectorId
     * @return
     */
    protected static String getDescription(String connectorId) {
        String key = connectorId.toLowerCase().replaceAll("-", "_") + "_connector_description";
        try {
            return ConnectorsMessages.class.getDeclaredField(key).get(null).toString();
        } catch (Exception ex) {
            Activator.getLogger().warning("Missing translation for key \"" + key + "\"", ex);
        }
        return connectorId;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#create(org.eclipse.graphiti.features.context.ICreateContext)
     */
    @Override
    public Object[] create(ICreateContext context) {
        // add maven dependency to pom.xml if needed
        try {
            updateMavenDependencies();
        } catch (CoreException ex) {
            Activator.getLogger().error("Unable to add the connector dependency to the project maven configuration file.", ex);
        }
        // and then let the super class continue the work
        return super.create(context);
    }
    
    /**
     * checks if we need to add a maven dependency for the chosen connector
     * and inserts it into the pom.xml if needed
     */
    protected void updateMavenDependencies() throws CoreException {
        RiderDesignEditor editor = Activator.getDiagramEditor();
        if (editor == null) {
            Activator.getLogger().error("Unable to add connector dependencies because Editor instance can't be determined.");
            return;
        }
        
        IProject project = editor.getCamelContextFile().getProject();
        if (project == null) {
            Activator.getLogger().error("Unable to add connector dependencies because selected project can't be determined.");
            return;
        }
        
        IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append("pom.xml") : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath().append("pom.xml"));
        String pomPath = pomPathValue.toOSString();
        final File pomFile = new File(pomPath);
        final Model model = MavenPlugin.getMaven().readModel(pomFile);

        // then check if connector dependency is already a dep
        ArrayList<ConnectorDependency> missingDeps = new ArrayList<ConnectorDependency>();
        List<Dependency> deps = model.getDependencies();
        for (ConnectorDependency conDep : connector.getDependencies()) {
            boolean found = false;
            for (Dependency pomDep : deps) {
                if (pomDep.getGroupId().equalsIgnoreCase(conDep.getGroupId()) &&
                    pomDep.getArtifactId().equalsIgnoreCase(conDep.getArtifactId())) {
                    // check for correct version
                    if (pomDep.getVersion().equalsIgnoreCase(conDep.getVersion()) == false) {
                        // not the correct version - change it to fit
                        pomDep.setVersion(conDep.getVersion());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingDeps.add(conDep);
            }
        }

        for (ConnectorDependency missDep : missingDeps) {
            Dependency dep = new Dependency();
            dep.setGroupId(missDep.getGroupId());
            dep.setArtifactId(missDep.getArtifactId());
            dep.setVersion(missDep.getVersion());
            model.addDependency(dep);
        }
        
        if (missingDeps.size()>0) {
            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(pomFile));
                MavenPlugin.getMaven().writeModel(model, os);
                IFile pomIFile = project.getProject().getFile("pom.xml");
                if (pomIFile != null){
                    pomIFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
                }
            } catch (Exception ex) {
                Activator.getLogger().error(ex);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Activator.getLogger().error(e);
                }
            }
        }
    }
}
