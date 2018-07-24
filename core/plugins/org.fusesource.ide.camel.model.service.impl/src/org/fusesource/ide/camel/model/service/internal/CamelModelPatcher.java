/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.impl.ICamelCatalogWrapper;

/**
 * this class is used to fix broken catalogs of earlier camel versions
 * 
 * @author lheinema
 */
public class CamelModelPatcher {

	private static final String CAMEL_VERSION_FIXING_CAMEL_CONTEXT_IN_CATALOG = "2.18.0";

	private CamelModelPatcher() {
		// util class
	}

	public static void applyVersionSpecificCatalogFixes(ICamelCatalogWrapper catalog, CamelModel loadedModel) {
		String camelVersion = catalog.getLoadedVersion();
		if (camelVersion == null) {
			// can't work with a null value here - thats usually caused by a non existing catalog
			return;
		}
		applyMissingOneOfValuesForExpressionsPatch(loadedModel);
		applyMissingWhenChildDefinitionForChoice(loadedModel);
		applyFixesToComponentsSyntax(loadedModel);
		applyMissingCamelContextEip(camelVersion, loadedModel);
		applyZipFileDataformatNameInconsistencyWorkaround(loadedModel);
	}

	private static void applyZipFileDataformatNameInconsistencyWorkaround(CamelModel loadedModel) {
		DataFormat zipfileDataformat = loadedModel.getDataFormat("zipfile");
		if (zipfileDataformat != null) {
			loadedModel.addDataFormat("zipFile", zipfileDataformat);
		}
	}

	private static void applyMissingCamelContextEip(String camelVersion, CamelModel loadedModel) {
		Eip eipLoadedFromInitialCatalog = loadedModel.getEip("camelContext");
		if(CAMEL_VERSION_FIXING_CAMEL_CONTEXT_IN_CATALOG.compareTo(camelVersion) > 0 && eipLoadedFromInitialCatalog == null) {
			Eip camelContextEip = getCamelContextModelForVersion(camelVersion);
			loadedModel.addEip(camelContextEip);
		}
	}

	protected static Eip getCamelContextModelForVersion(String camelVersion) {
		String versionRange = "2.17.x";
		if("2.11".compareTo(camelVersion) > 0) {
			versionRange = "2.10.x";
		} else if("2.12".compareTo(camelVersion) > 0) {
			versionRange = "2.11.x";
		} else if("2.13".compareTo(camelVersion) > 0) {
			versionRange = "2.12.x";
		} else if("2.14".compareTo(camelVersion) > 0) {
			versionRange = "2.13.x";
		} else if("2.15".compareTo(camelVersion) > 0) {
			versionRange = "2.14.x";
		} else if("2.16".compareTo(camelVersion) > 0) {
			versionRange = "2.15.x";
		} else if("2.17".compareTo(camelVersion) > 0) {
			versionRange = "2.16.x";
		}
		InputStream inputStream = CamelModelPatcher.class.getResourceAsStream("camelContext-"+versionRange+".json");
		return Eip.getJSONFactoryInstance(inputStream);
	}

	private static void applyMissingOneOfValuesForExpressionsPatch(CamelModel loadedModel) {
		for (Eip eip : loadedModel.getEips()) {
			if (eip != null) {
				ensureAllParametersWithOneOfContainsAllPossibleValues(eip);
			}
		}
	}

	private static void ensureAllParametersWithOneOfContainsAllPossibleValues(Eip eip) {
		for (Parameter p : eip.getParameters()) {
			if (AbstractCamelModelElement.NODE_KIND_EXPRESSION.equalsIgnoreCase(p.getKind())) {
				ensureOneOfContainsAllPossiblevalues(p);
			}
		}
	}

	private static void ensureOneOfContainsAllPossiblevalues(Parameter p) {
		// expression parameter -> check for specific oneOf values
		List<String> possibleValues = new ArrayList<>();
		possibleValues.addAll(Arrays.asList(p.getOneOf()));
		ensureListContains(possibleValues, "vtdxml");
		ensureListContains(possibleValues, "xpath");
		ensureListContains(possibleValues, "xquery");
		ensureListContains(possibleValues, "xtokenize");
		p.setOneOf(possibleValues.stream().toArray(String[]::new));
	}

	private static void ensureListContains(List<String> possibleValues, String possibleExpressionLanguage) {
		if (!possibleValues.contains(possibleExpressionLanguage)) {
			possibleValues.add(possibleExpressionLanguage);
		}
	}

	private static void applyMissingWhenChildDefinitionForChoice(CamelModel loadedModel) {
		Eip choiceEip = loadedModel.getEip(AbstractCamelModelElement.CHOICE_NODE_NAME);
		if (choiceEip != null) {
			for (Parameter p : choiceEip.getParameters()) {
				applyMissingWhenChildDefinitionForChoice(p);
			}
		}
	}

	private static void applyMissingWhenChildDefinitionForChoice(Parameter p) {
		if ("array".equalsIgnoreCase(p.getType())
				&& AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())) {
			if (p.getOneOf() == null) {
				p.setOneOf(new String[0]);
			}
			List<String> possibleChildren = new ArrayList<>();
			possibleChildren.addAll(Arrays.asList(p.getOneOf()));
			if (!possibleChildren.contains(AbstractCamelModelElement.WHEN_NODE_NAME)) {
				possibleChildren.add(AbstractCamelModelElement.WHEN_NODE_NAME);
				p.setOneOf(possibleChildren.stream().toArray(String[]::new));
			}
		}
	}
	
	private static void applyFixesToComponentsSyntax(CamelModel loadedModel) {
		applyFixToComponent(loadedModel, "google-drive", "google-drive:drive:apiName/methodName", "google-drive:apiName/methodName");
		applyFixToComponent(loadedModel, "couchbase", "couchbase:url", "couchbase:protocol:hostname");
		applyFixToComponent(loadedModel, "ignite-messaging", "ignite-messaging:[topic]", "ignite-messaging:topic");
		applyFixToComponent(loadedModel, "ignite-queue", "ignite-queue:[name]", "ignite-queue:name");
		applyFixToComponent(loadedModel, "ignite-compute", "ignite-compute:[endpointId]", "ignite-compute:endpointId");
		applyFixToComponent(loadedModel, "ignite-idgen", "ignite-idgen:[name]", "ignite-idgen:name");
		applyFixToComponent(loadedModel, "ignite-cache", "ignite-cache:[cacheName]", "ignite-cache:cacheName");
		applyFixToComponent(loadedModel, "ignite-set", "ignite-set:[name]", "ignite-set:name");
		applyFixToComponent(loadedModel, "ignite-events", "ignite-events:[endpointId]", "ignite-events:endpointId");
		applyFixToComponent(loadedModel, "atomix-set", "atomix-set:setName", "atomix-set:resourceName");
		applyFixToComponent(loadedModel, "atomix-value", "atomix-value:valueName", "atomix-value:resourceName");
		applyFixToComponent(loadedModel, "atomix-queue", "atomix-queue:queueName", "atomix-queue:resourceName");
		applyFixToComponent(loadedModel, "atomix-map", "atomix-map:mapName", "atomix-map:resourceName");
		applyFixToComponent(loadedModel, "atomix-multimap", "atomix-multimap:multiMapName", "atomix-multimap:resourceName");
		applyFixToComponent(loadedModel, "atomix-messaging", "atomix-messaging:group", "atomix-messaging:resourceName");
		applyFixToComponent(loadedModel, "iec60870-client", "iec60870-client:endpointUri", "iec60870-client:uriPath");
		applyFixToComponent(loadedModel, "iec60870-server", "iec60870-server:endpointUri", "iec60870-server:uriPath");
		applyFixToComponent(loadedModel, "wordpress", "wordpress:operation", "wordpress:operationDetail");
		applyFixToComponent(loadedModel, "micrometer", "micrometer:metricsType:meterName", "micrometer:metricsType:metricsName");
	}

	private static void applyFixToComponent(CamelModel loadedModel, String componentScheme, String invalidSyntaxBugFromOlderVersion, String correctSyntax) {
		Component c = loadedModel.getComponent(componentScheme);
		if (c != null && invalidSyntaxBugFromOlderVersion.equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax(correctSyntax);
		}
	}
}
