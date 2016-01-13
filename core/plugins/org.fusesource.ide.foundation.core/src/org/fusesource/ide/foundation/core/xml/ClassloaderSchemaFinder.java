package org.fusesource.ide.foundation.core.xml;

import java.net.URL;

/**
 * Locate a schema by checking the classloader of the class provided when creating the XSD Details
 */
public class ClassloaderSchemaFinder implements SchemaFinder {
    @Override
    public URL findSchema(XsdDetails details) {
        return details.getClassLoader().getResource(details.getPath());
    }

}
