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

import org.apache.camel.catalog.CamelCatalog;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * this class is used to fix broken catalogs of earlier camel versions
 * 
 * @author lheinema
 */
public class CamelModelPatcher {

	private static final ComparableVersion v217Ref = new ComparableVersion("2.17.0");

	private CamelModelPatcher() {
		// util class
	}

	public static void applyVersionSpecificCatalogFixes(CamelCatalog catalog, CamelModel loadedModel) {
		String camelVersion = catalog.getLoadedVersion();
		if (camelVersion == null) {
			// can't work with a null value here - thats usually caused by a non existing catalog
			return;
		}
		ComparableVersion loadedVersion = new ComparableVersion(camelVersion);
		if (loadedVersion.compareTo(v217Ref) < 0) {
			// we've got a pre-2.17 version with a whacky catalog here - apply
			// some fixes
			applyMissingOneOfValuesForExpressionsPatch(loadedModel);
			applyMissingWhenChildDefinitionForChoice(loadedModel);
		}
	}

	private static void applyMissingOneOfValuesForExpressionsPatch(CamelModel loadedModel) {
		for (Eip eip : loadedModel.getEips()) {
			if (eip != null) {
				for (Parameter p : eip.getParameters()) {
					if (AbstractCamelModelElement.NODE_KIND_EXPRESSION.equalsIgnoreCase(p.getKind())) {
						// expression parameter -> check for specific oneOf values
						ArrayList<String> possibleValues = new ArrayList<>();
						possibleValues.addAll(Arrays.asList(p.getOneOf()));
						if (!possibleValues.contains("vtdxml")) {
							possibleValues.add("vtdxml");
						}
						if (!possibleValues.contains("xpath")) {
							possibleValues.add("xpath");
						}
						if (!possibleValues.contains("xquery")) {
							possibleValues.add("xquery");
						}
						if (!possibleValues.contains("xtokenize")) {
							possibleValues.add("xtokenize");
						}
						p.setOneOf(possibleValues.toArray(new String[possibleValues.size()]));
					}
				}
			}
		}
	}

	private static void applyMissingWhenChildDefinitionForChoice(CamelModel loadedModel) {
		Eip choiceEip = loadedModel.getEip(AbstractCamelModelElement.CHOICE_NODE_NAME);
		if (choiceEip != null) {
			for (Parameter p : choiceEip.getParameters()) {
				if ("array".equalsIgnoreCase(p.getType())
						&& AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())) {
					if (p.getOneOf() == null)
						p.setOneOf(new String[0]);
					ArrayList<String> possibleChildren = new ArrayList<>();
					possibleChildren.addAll(Arrays.asList(p.getOneOf()));
					if (!possibleChildren.contains(AbstractCamelModelElement.WHEN_NODE_NAME)) {
						possibleChildren.add(AbstractCamelModelElement.WHEN_NODE_NAME);
						p.setOneOf(possibleChildren.toArray(new String[possibleChildren.size()]));
					}
				}
			}
		}
	}
}
