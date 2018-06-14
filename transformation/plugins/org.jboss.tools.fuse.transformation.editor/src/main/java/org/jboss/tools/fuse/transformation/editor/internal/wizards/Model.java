/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 */
public class Model implements PropertyChangeListener {

    private static final String DEFAULT_FILE_PATH = "transformation.xml"; //$NON-NLS-1$

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private String id;
    private String filePath = DEFAULT_FILE_PATH;
    private String sourceFilePath;
    private String targetFilePath;
    private ModelType sourceType;
    private ModelType targetType;
    private String camelFilePath;
    private String sourceTypeStr;
    private String targetTypeStr;
    private String sourceDataFormatid;
    private String targetDataFormatid;
    private String sourceClassName;
    private String targetClassName;

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
     * @param filePath
     */
    public void setFilePath(final String filePath) {
        changeSupport
                .firePropertyChange("filePath", this.filePath, this.filePath = filePath.trim()); //$NON-NLS-1$
    }

    /**
     * @param id
     */
    public void setId(final String id) {
        changeSupport.firePropertyChange("id", this.id, this.id = id.trim()); //$NON-NLS-1$
    }

    /**
     * @param sourceFilePath
     */
    public void setSourceFilePath(final String sourceFilePath) {
        changeSupport.firePropertyChange("sourceFilePath", this.sourceFilePath, //$NON-NLS-1$
                this.sourceFilePath = sourceFilePath.trim());
    }

    /**
     * @param sourceType
     */
    public void setSourceType(final ModelType sourceType) {
        changeSupport.firePropertyChange("sourceType", this.sourceType, this.sourceType = //$NON-NLS-1$
                sourceType);
    }

    /**
     * @param targetFilePath
     */
    public void setTargetFilePath(final String targetFilePath) {
        changeSupport.firePropertyChange("targetFilePath", this.targetFilePath, //$NON-NLS-1$
                this.targetFilePath = targetFilePath.trim());
    }

    /**
     * @param targetType
     */
    public void setTargetType(final ModelType targetType) {
        changeSupport.firePropertyChange("targetType", this.targetType, this.targetType = //$NON-NLS-1$
                targetType);
    }

    public String getSourceTypeStr() {
        return sourceTypeStr;
    }

    public void setSourceTypeStr(String sourceTypeStr) {
        changeSupport.firePropertyChange("sourceTypeStr", this.sourceTypeStr, this.sourceTypeStr = //$NON-NLS-1$
                sourceTypeStr);
    }

    public String getTargetTypeStr() {
        return targetTypeStr;
    }

    public void setTargetTypeStr(String targetTypeStr) {
        changeSupport.firePropertyChange("targetTypeStr", this.targetTypeStr, this.targetTypeStr = //$NON-NLS-1$
                targetTypeStr);
    }

    public String getSourceDataFormatid() {
        return sourceDataFormatid;
    }

    public void setSourceDataFormatid(String sourceDataFormatid) {
        changeSupport.firePropertyChange("sourceDataFormatid", this.sourceDataFormatid, this.sourceDataFormatid = //$NON-NLS-1$
                sourceDataFormatid.trim());
    }

    public String getTargetDataFormatid() {
        return targetDataFormatid;
    }

    public void setTargetDataFormatid(String targetDataFormatid) {
        changeSupport.firePropertyChange("targetDataFormatid", this.targetDataFormatid, this.targetDataFormatid = //$NON-NLS-1$
                targetDataFormatid.trim());
    }

    public String getSourceClassName() {
        return sourceClassName;
    }

    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }
}
