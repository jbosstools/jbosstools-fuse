package org.fusesource.ide.foundation.core.xml;

import java.net.URL;

import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;
import org.osgi.framework.Bundle;

/**
 * Represents a schema finder that will search all currently active
 * bundles in the eclipse environment. 
 * 
 * This seems to be used to locate camel-spring.xsd, which is currently stored inside 
 *    plugins/org.fusesource.ide.jmx.activemq/libs/activemq-osgi-5.11.0.redhat-620133.jar
 *    
 * while camel-blueprint.xsd is inside org.eclipse.emf.common
 * 
 * Something tells me this can be done better, though. 
 *
 */
public class BundleSchemaFinder implements SchemaFinder {
	@Override
	public URL findSchema(XsdDetails xsd) {
		String path = xsd.getPath();
		URL answer = null;
		Bundle[] bundles = FoundationCoreActivator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles) {
			answer = bundle.getResource(path);
			if (answer != null) {
				break;
			}
		}
		//Activator.getLogger().debug("for path: " + path + " xsd " + xsd + " found: " + answer);
		return answer;
	}
}
