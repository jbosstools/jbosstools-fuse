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
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.m2e.core.MavenPlugin;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.connectors.ComponentDependency;


/**
 * @author lhein
 */
public class CreateFigureFeature<E> extends AbstractCreateFeature implements PaletteCategoryItemProvider {

	private Class<E> clazz;
	private AbstractNode exemplar;

	public CreateFigureFeature(IFeatureProvider fp, String name, String description, Class<E> clazz) {
		super(fp, name, description);
		this.clazz = clazz;
	}


	@Override
	public CATEGORY_TYPE getCategoryType() {
		return CATEGORY_TYPE.getCategoryType(getCategoryName());
	}


	@Override
	public String getCategoryName() {
		AbstractNode node = getExemplar();
		if (node != null) {
			return node.getCategoryName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateImageId()
	 */
	@Override
	public String getCreateImageId() {
		String iconName = getIconName();
		if (iconName != null) iconName = ImageProvider.getKeyForSmallIcon(iconName);
		return iconName;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractCreateFeature#getCreateLargeImageId()
	 */
	@Override
	public String getCreateLargeImageId() {
		String iconName = getIconName();
		if (iconName != null) iconName = String.format("%s_large", iconName);
		return iconName;
	}

	/**
	 * retrieves the icon name for the given class via reflection
	 * 
	 * @return	the icon name or null
	 */
	protected String getIconName() {
		AbstractNode node = getExemplar();
		if (node != null) {
			return node.getIconName();
		}
		return null;
	}

	/**
     * @return the clazz
     */
    public Class<E> getClazz() {
        return this.clazz;
    }
	
	/**
	 * Returns the singleton exemplar node we can use to access things like icons and category names etc
	 */
	protected AbstractNode getExemplar() {
		if (exemplar == null) {
			try {
				exemplar = (AbstractNode) clazz.newInstance();
			} catch (Exception e) {
				Activator.getLogger().warning("Failed to create instance of " + clazz + ". " + e, e);
			}
		}
		return exemplar;
	}

	protected void setExemplar(AbstractNode exemplar) {
		this.exemplar = exemplar;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ICreate#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		AbstractNode node = createNode();

		RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
		Diagram diagram = getDiagram();

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


	protected AbstractNode createNode() {
		AbstractNode node = null;

		try {
			node = (AbstractNode)this.clazz.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return node;
	}
	
    /**
     * checks if we need to add a maven dependency for the chosen component
     * and inserts it into the pom.xml if needed
     */
    public void updateMavenDependencies(List<ComponentDependency> compDeps) throws CoreException {
        RiderDesignEditor editor = Activator.getDiagramEditor();
        if (editor == null) {
            Activator.getLogger().error("Unable to add component dependencies because Editor instance can't be determined.");
            return;
        }
        
        IProject project = editor.getCamelContextFile().getProject();
        if (project == null) {
            Activator.getLogger().error("Unable to add component dependencies because selected project can't be determined.");
            return;
        }
        
        IPath pomPathValue = project.getProject().getRawLocation() != null ? project.getProject().getRawLocation().append("pom.xml") : ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath().append("pom.xml"));
        String pomPath = pomPathValue.toOSString();
        final File pomFile = new File(pomPath);
        final Model model = MavenPlugin.getMaven().readModel(pomFile);

        // then check if component dependency is already a dep
        ArrayList<ComponentDependency> missingDeps = new ArrayList<ComponentDependency>();
        List<Dependency> deps = model.getDependencies();
        for (ComponentDependency conDep : compDeps) {
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

        for (ComponentDependency missDep : missingDeps) {
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
