/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model;

import org.apache.camel.ExchangePattern;
import org.apache.camel.model.DescriptionDefinition;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.ToDefinition;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.preferences.PreferencesConstants;


public class CamelModelHelper {

	public static String getDefaultLanguageName() {
		String answer = Activator.getDefault().getPreferenceStore()
				.getString(PreferencesConstants.EDITOR_DEFAULT_LANGUAGE);
		if (Strings.isBlank(answer)) {
			return "simple";
		} else {
			return answer;
		}
	}

	public static boolean isPropertyListOFSetHeaders(final Object id) {
		return Objects.equal(UniversalEIPNode.getPropertyKey("wireTap", "headers"), id);
	}

	public static String getUri(FromDefinition input) {
		String key = input.getUri();
		if (Strings.isBlank(key)) {
			String ref = input.getRef();
			if (!Strings.isBlank(ref)) {
				return "ref:" + ref;
			}
		}
		return key;
	}

	public static String getUri(ToDefinition input) {
		String key = input.getUri();
		if (Strings.isBlank(key)) {
			String ref = input.getRef();
			if (!Strings.isBlank(ref)) {
				return "ref:" + ref;
			}
		}
		return key;
	}

	public static void setUri(FromDefinition node, Endpoint endpoint) {
		String value = endpoint.getUri();
		if (value != null && value.startsWith("ref:")) {
			node.setRef(value.substring(4));
		} else {
			node.setUri(value);
		}
	}

	public static void setUri(ToDefinition node, Endpoint endpoint) {
		String value = endpoint.getUri();
		if (value != null && value.startsWith("ref:")) {
			node.setRef(value.substring(4));
		} else {
			node.setUri(value);
		}
	}

	public static String getExchangePattern(ToDefinition input) {
		String pattern = input.getPattern() != null ? input.getPattern().name() : null;
		if (Strings.isBlank(pattern)) {
			return null;
		}
		return pattern;
	}
	
	public static void setExchangePattern(ToDefinition node, Endpoint endpoint) {
		String value = endpoint.getPattern();
		if (value != null && value.trim().length()>0) {
			node.setPattern(ExchangePattern.asEnum(value));
		} else {
			node.setPattern(null);
		}
	}
	
	public static void setId(FromDefinition node, Endpoint endpoint) {
		String value = endpoint.getId();
		if (value != null && value.trim().length()>0) {
			node.setId(value);
		}
	}

	public static void setDescription(FromDefinition node, Endpoint endpoint) {
		String value = endpoint.getId();
		if (value != null && value.trim().length()>0) {
			DescriptionDefinition dd = new DescriptionDefinition();
			dd.setText(endpoint.getDescription());
			node.setDescription(dd);
		}
	}

	public static boolean isMockEndpointURI(String value) {
		return value.startsWith("mock:");
	}

	public static boolean isTimerEndpointURI(String value) {
		return value.startsWith("timer:") || value.startsWith("quartz:");
	}


}
