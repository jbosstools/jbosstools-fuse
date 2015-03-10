package org.jboss.mapper.dozer;

import org.jboss.mapper.CustomMapping;
import org.jboss.mapper.MappingType;

/**
 * Dozer implementation of a custom mapping.
 */
public class DozerCustomMapping extends DozerFieldMapping implements CustomMapping {

    private String mappingClass;
    private String mappingOperation;

    /**
     * Create a new DozerCustomMapping.
     * 
     * @param fieldMapping field mapping being customized
     * @param mappingClass name of the class used to customize the mapping
     */
    public DozerCustomMapping(DozerFieldMapping fieldMapping, String mappingClass) {
        this(fieldMapping, mappingClass, null);
    }

    /**
     * Create a new DozerCustomMapping.
     * 
     * @param fieldMapping field mapping being customized
     * @param mappingClass name of the class used to customize the mapping
     * @param mappingOperation operation name to use in the custom mapping class
     */
    public DozerCustomMapping(DozerFieldMapping fieldMapping, String mappingClass,
            String mappingOperation) {

        super(fieldMapping.getSource(), fieldMapping.getTarget(), fieldMapping.getMapping(),
                fieldMapping.getField());
        this.mappingClass = mappingClass;
        this.mappingOperation = mappingOperation;
    }

    @Override
    public String getMappingClass() {
        return mappingClass;
    }

    @Override
    public String getMappingOperation() {
        return mappingOperation;
    }

    @Override
    public MappingType getType() {
        return MappingType.CUSTOM;
    }
}
