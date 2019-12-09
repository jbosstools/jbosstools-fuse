/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.dozer;

import java.util.List;

import org.jboss.tools.fuse.transformation.core.dozer.config.Field;
import org.jboss.tools.fuse.transformation.core.dozer.config.FieldDefinition;
import org.jboss.tools.fuse.transformation.core.dozer.config.Mapping;
import org.jboss.tools.fuse.transformation.core.model.Model;

public abstract class BaseDozerMapping {

    private Mapping mapping;
    private Field field;

    protected BaseDozerMapping(Mapping mapping, Field field) {
        this.mapping = mapping;
        this.field = field;
    }

    /**
     * Deletes this field mapping from the Dozer configuration.
     */
    public void delete() {
        mapping.getFieldOrFieldExclude().remove(field);
    }

    /**
     * Returns the Dozer mapping config model underneath this object.
     * 
     * @return Dozer Mapping config
     */
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * Returns the Dozer field config model underneath this object.
     * 
     * @return Dozer Field config
     */
    public Field getField() {
        return field;
    }
    
    public List<Integer> getSourceIndex() {
        return DozerUtil.getFieldIndexes(field.getA().getContent());
    }
    
    public List<Integer> getTargetIndex() {
        return DozerUtil.getFieldIndexes(field.getB().getContent());
    }
    
    public void setSourceIndex(List<Integer> indexes) {
        throw new UnsupportedOperationException(
                "setSourceIndex not supported for mappings of type " + getClass().getSimpleName()); //$NON-NLS-1$
    }
    
    public void setTargetIndex(List<Integer> indexes) {
        throw new UnsupportedOperationException(
                "setTargetIndex not supported for mappings of type " + getClass().getSimpleName()); //$NON-NLS-1$
    }
    
    public void setFieldIndex(FieldDefinition field, Model model, String rootType, List<Integer> indexes) {
        field.setContent(DozerUtil.getFieldName(model, rootType, indexes));
    }

    public void setSourceDateFormat(String format) {
        field.getA().setDateFormat(format);
    }
    
    public String getSourceDateFormat() {
        return field.getA().getDateFormat();
    }

    public void setTargetDateFormat(String format) {
        field.getB().setDateFormat(format);
    }
    
    public String getTargetDateFormat() {
        return field.getB().getDateFormat();
    }
}
