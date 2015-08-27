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

package org.fusesource.ide.camel.model.util;

import org.apache.camel.model.language.ExpressionDefinition;
import org.fusesource.ide.foundation.core.util.Strings;

public class Expressions {

	public static String getExpressionOrElse(ExpressionDefinition exp) {
		return getExpressionOrElse(exp, "");
	}

	public static String getExpressionOrElse(ExpressionDefinition exp, String defaultValue) {
		if (exp != null) {
			return Strings.getOrElse(exp.getExpression(), defaultValue).trim();
		}
		return defaultValue;
	}

}
