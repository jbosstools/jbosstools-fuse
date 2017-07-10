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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.catalog.CamelCatalog;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * this class is used to fix broken catalogs of earlier camel versions
 * 
 * @author lheinema
 */
public class CamelModelPatcher {

	private CamelModelPatcher() {
		// util class
	}

	public static void applyVersionSpecificCatalogFixes(CamelCatalog catalog, CamelModel loadedModel) {
		String camelVersion = catalog.getLoadedVersion();
		if (camelVersion == null) {
			// can't work with a null value here - thats usually caused by a non existing catalog
			return;
		}
		applyMissingOneOfValuesForExpressionsPatch(loadedModel);
		applyMissingWhenChildDefinitionForChoice(loadedModel);
		applyFixesToComponentsSyntax(loadedModel);
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
		// google-drive has a wrong syntax
		Component c = loadedModel.getComponent("google-drive");
		if (c != null && "google-drive:drive:apiName/methodName".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("google-drive:apiName/methodName");
		}
		
		// couchbase has a wrong syntax
		c = loadedModel.getComponent("couchbase");
		if (c != null && "couchbase:url".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("couchbase:protocol:hostname");
		}
		
		// ignite-* has a wrong syntax including invalid []
		c = loadedModel.getComponent("ignite-messaging");
		if (c != null && "ignite-messaging:[topic]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-messaging:topic");
		}
		c = loadedModel.getComponent("ignite-queue");
		if (c != null && "ignite-queue:[name]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-queue:name");
		}
		c = loadedModel.getComponent("ignite-compute");
		if (c != null && "ignite-compute:[endpointId]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-compute:endpointId");
		}
		c = loadedModel.getComponent("ignite-idgen");
		if (c != null && "ignite-idgen:[name]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-idgen:name");
		}
		c = loadedModel.getComponent("ignite-cache");
		if (c != null && "ignite-cache:[cacheName]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-cache:cacheName");
		}
		c = loadedModel.getComponent("ignite-set");
		if (c != null && "ignite-set:[name]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-set:name");
		}
		c = loadedModel.getComponent("ignite-events");
		if (c != null && "ignite-events:[endpointId]".equalsIgnoreCase(c.getSyntax())) {
			c.setSyntax("ignite-events:endpointId");
		}
	}
}
