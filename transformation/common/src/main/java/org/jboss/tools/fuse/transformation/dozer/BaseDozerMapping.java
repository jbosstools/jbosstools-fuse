package org.jboss.tools.fuse.transformation.dozer;

import org.jboss.tools.fuse.transformation.dozer.config.Field;
import org.jboss.tools.fuse.transformation.dozer.config.Mapping;

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
}
