package org.jboss.mapper;

import java.io.OutputStream;
import java.util.List;

import org.jboss.mapper.model.Model;

/**
 * Abstraction over specific mapping framework implementations. Tooling should
 * use this contract vs. interacting directly with the underlying mapping
 * framework.
 */
public interface MapperConfiguration {
    /**
     * Remove all mappings in the mapper configuration.
     */
    void removeAllMappings();

    /**
     * Remove a specific mapping from the mapper configuration.
     * 
     * @param mapping mapping to remove
     */
    void removeMapping(MappingOperation<?,?> mapping);

    /**
     * Get all mappings that include the specified model as a source.
     * 
     * @param source source model
     * @return list of mappings
     */
    List<MappingOperation<?,?>> getMappingsForSource(Model source);

    /**
     * Get all mappings that include the specified model as a target.
     * 
     * @param target target model
     * @return list of mappings
     */
    List<MappingOperation<?,?>> getMappingsForTarget(Model target);

    /**
     * Get a list of all mappings in the mapper configuration.
     * 
     * @return list of mappings
     */
    List<MappingOperation<?,?>> getMappings();

    /**
     * Get the list of literal values used as the source for mappings.
     * 
     * @return list of literals
     */
    List<Literal> getLiterals();

    /**
     * Map a source field to a target field.
     * 
     * @param source model for the source field
     * @param target model for the target field
     * @return mapping created
     */
    FieldMapping map(Model source, Model target);

    /**
     * Map a literal value to a target field.
     * 
     * @param literal literal value
     * @param target target field
     * @return mapping created
     */
    LiteralMapping map(Literal literal, Model target);

    /**
     * Write the mapping configuration to the specified output stream.
     * 
     * @param output stream to write to
     * @throws Exception marshaling or writing the config failed
     */
    void saveConfig(OutputStream output) throws Exception;

    /**
     * Add a class mapping between the fromClass and toClass. Honestly, this
     * method is sorta Dozer-specific and probably shouldn't be in this
     * interface.
     * 
     * @param fromClass source class name
     * @param toClass target class name
     */
    void addClassMapping(String fromClass, String toClass);

    /**
     * Returns the source model for the mapping.
     * 
     * @return source model
     */
    Model getSourceModel();

    /**
     * Returns the target model for the mapping.
     * 
     * @return target model
     */
    Model getTargetModel();

    /**
     * Use a custom mapping class for an existing FieldMapping.
     * 
     * @param mapping mapping to customize
     * @param mappingClass class to use for customizing the mapping
     * @return the new CustomMapping
     */
    CustomMapping customizeMapping(FieldMapping mapping, String mappingClass);

    /**
     * Use a custom mapping class for an existing FieldMapping.
     * 
     * @param mapping mapping to customize
     * @param mappingClass class to use for customizing the mapping
     * @param mappingOperation operation in the mappingClass to use
     * @return CustomMapping
     */
    CustomMapping customizeMapping(FieldMapping mapping, String mappingClass,
            String mappingOperation);
}
