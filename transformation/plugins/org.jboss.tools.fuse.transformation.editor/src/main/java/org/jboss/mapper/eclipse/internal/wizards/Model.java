/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others. 
 * All rights reserved. This program and the accompanying materials are 
 * made available under the terms of the Eclipse Public License v1.0 which 
 * accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.mapper.eclipse.internal.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.mapper.camel.CamelConfigBuilder;
import org.jboss.mapper.eclipse.internal.util.Util;

/**
 *
 */
public class Model implements PropertyChangeListener {

    private static final String DEFAULT_FILE_PATH = "transformation.xml";

    /**
     *
     */
    public final List<IProject> projects = new ArrayList<>(Arrays.asList(ResourcesPlugin
            .getWorkspace().getRoot().getProjects()));

    /**
     *
     */
    public CamelConfigBuilder camelConfigBuilder;

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private IProject project;
    private String id;
    private String filePath = DEFAULT_FILE_PATH;
    private String sourceFilePath;
    private String targetFilePath;
    private ModelType sourceType;
    private ModelType targetType;
    private String camelFilePath;

    /**
     * @param propertyName
     * @param listener
     */
    public void addPropertyChangeListener(final String propertyName,
            final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * @return the Camel file path
     */
    public String getCamelFilePath() {
        return camelFilePath;
    }

    /**
     * @return the transformation file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the transformation ID that will be seen in the Camel editor
     */
    public String getId() {
        return id;
    }

    /**
     * @return the project in which to create the transformation
     */
    public IProject getProject() {
        return project;
    }

    /**
     * @return the source file path
     */
    public String getSourceFilePath() {
        return sourceFilePath;
    }

    /**
     * @return the source type
     */
    public ModelType getSourceType() {
        return sourceType;
    }

    /**
     * @return the target file path
     */
    public String getTargetFilePath() {
        return targetFilePath;
    }

    /**
     * @return the target type
     */
    public ModelType getTargetType() {
        return targetType;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        changeSupport.firePropertyChange(event.getPropertyName(), event.getOldValue(),
                event.getNewValue());
    }

    /**
     * @param listener
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * @param filePath the Camel file path
     */
    public void setCamelFilePath(final String filePath) {
        changeSupport.firePropertyChange("camelFilePath", this.camelFilePath, this.camelFilePath =
                filePath.trim());
        setProject(project);
    }

    /**
     * @param filePath
     */
    public void setFilePath(final String filePath) {
        changeSupport
                .firePropertyChange("filePath", this.filePath, this.filePath = filePath.trim());
    }

    /**
     * @param id
     */
    public void setId(final String id) {
        changeSupport.firePropertyChange("id", this.id, this.id = id.trim());
    }

    /**
     * @param project
     */
    public void setProject(final IProject project) {
        changeSupport.firePropertyChange("project", this.project, this.project = project);

        if (camelFilePath != null && !camelFilePath.trim().isEmpty()) {
            try {
                IFile test = project.getFile(camelFilePath);
                if (!test.exists()) {
                    test = project.getFile(Util.RESOURCES_PATH + camelFilePath);
                }
                if (test != null && test.exists()) {
                    final File camelFile = new File(test.getLocationURI());
                    camelConfigBuilder = CamelConfigBuilder.loadConfig(camelFile);
                }
            } catch (final Exception e) {
                // swallow
                // e.printStackTrace();
            }
        }
    }

    /**
     * @param sourceFilePath
     */
    public void setSourceFilePath(final String sourceFilePath) {
        changeSupport.firePropertyChange("sourceFilePath", this.sourceFilePath,
                this.sourceFilePath = sourceFilePath.trim());
    }

    /**
     * @param sourceType
     */
    public void setSourceType(final ModelType sourceType) {
        changeSupport.firePropertyChange("sourceType", this.sourceType, this.sourceType =
                sourceType);
    }

    /**
     * @param targetFilePath
     */
    public void setTargetFilePath(final String targetFilePath) {
        changeSupport.firePropertyChange("targetFilePath", this.targetFilePath,
                this.targetFilePath = targetFilePath.trim());
    }

    /**
     * @param targetType
     */
    public void setTargetType(final ModelType targetType) {
        changeSupport.firePropertyChange("targetType", this.targetType, this.targetType =
                targetType);
    }
}
