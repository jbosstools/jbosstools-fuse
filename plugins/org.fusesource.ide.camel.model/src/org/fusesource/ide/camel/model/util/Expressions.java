package org.fusesource.ide.camel.model.util;

import org.apache.camel.model.language.ExpressionDefinition;
import org.fusesource.ide.commons.util.Strings;


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
