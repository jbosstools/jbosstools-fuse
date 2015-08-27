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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.model.language.ELExpression;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.GroovyExpression;
import org.apache.camel.model.language.HeaderExpression;
import org.apache.camel.model.language.JXPathExpression;
import org.apache.camel.model.language.JavaScriptExpression;
import org.apache.camel.model.language.LanguageExpression;
import org.apache.camel.model.language.MethodCallExpression;
import org.apache.camel.model.language.MvelExpression;
import org.apache.camel.model.language.OgnlExpression;
import org.apache.camel.model.language.PhpExpression;
import org.apache.camel.model.language.ExchangePropertyExpression;
import org.apache.camel.model.language.PythonExpression;
import org.apache.camel.model.language.RefExpression;
import org.apache.camel.model.language.RubyExpression;
import org.apache.camel.model.language.SimpleExpression;
import org.apache.camel.model.language.SpELExpression;
import org.apache.camel.model.language.SqlExpression;
import org.apache.camel.model.language.TokenizerExpression;
import org.apache.camel.model.language.XPathExpression;
import org.apache.camel.model.language.XQueryExpression;
import org.fusesource.ide.foundation.core.util.Strings;



/**
 * A version of {@link ExpressionDefinition} which supports property change
 * listening
 */
public class LanguageExpressionBean extends LanguageExpression implements HasValue {
	private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private ExpressionDefinition original;

	public static LanguageExpressionBean bindToNodeProperty(AbstractNode node, Object propertyName) {
		Object value = node.getPropertyValue(propertyName);
		if (value instanceof LanguageExpressionBean) {
			return (LanguageExpressionBean) value;
		} else {
			LanguageExpressionBean answer = toLanguageExpressionBean(value);
			node.setPropertyValue(propertyName, answer);
			return answer;
		}
	}

	public static LanguageExpressionBean toLanguageExpressionBean(Object value) {
		LanguageExpressionBean languageBean;
		if (value instanceof LanguageExpressionBean) {
			languageBean = (LanguageExpressionBean) value;
		} else if (value instanceof ExpressionDefinition) {
			ExpressionDefinition exp = (ExpressionDefinition) value;
			String language = exp.getLanguage();
			String expression = exp.getExpression();
			if (exp instanceof MethodCallExpression) {
				MethodCallExpression mc = (MethodCallExpression) exp;
				language = "method";
				expression = Strings.getOrElse(mc.getBean(), mc.getRef()) + "." + Strings.getOrElse(mc.getMethod());
			}
			languageBean =  new LanguageExpressionBean(language, expression);
			languageBean.original = exp;
		} else {
			languageBean = new LanguageExpressionBean();
		}
		if (Strings.isBlank(languageBean.getLanguage())) {
			languageBean.setLanguage(CamelModelHelper.getDefaultLanguageName());
		}
		return languageBean;
	}

	public LanguageExpressionBean() {
	}

	public LanguageExpressionBean(String language, String expression) {
		super(language, expression);
	}


	/**
	 * Attach a non-null PropertyChangeListener to this object.
	 * 
	 * @param l
	 *            a non-null PropertyChangeListener instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		listeners.addPropertyChangeListener(l);
	}

	/**
	 * Remove a PropertyChangeListener from this component.
	 * 
	 * @param l
	 *            a PropertyChangeListener instance
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(l);
		}
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(prop, old, newValue);
		}
	}

	@Override
	public void setExpression(String expression) {
		Object old = getExpression();
		if (expression != null && "".equals(expression)) {
			// empty expression should be regarded as null
			expression = null;
		}
		super.setExpression(expression);
		firePropertyChange("expression", old, expression);
	}

	@Override
	public void setLanguage(String language) {
		Object old = getLanguage();
		super.setLanguage(language);
		firePropertyChange("language", old, getLanguage());
	}

	@Override
	public boolean hasValue() {
		// there is only a value if end user has assigned an expression
		return getExpression() != null;
	}

	public ExpressionDefinition getOriginal() {
		return original;
	}

	public void setOriginal(ExpressionDefinition original) {
		this.original = original;
	}


	public ExpressionDefinition toXmlExpression() {
		// converts the language to the underlying expression
		String language = Strings.getOrElse(getLanguage());
		String expression = Strings.getOrElse(getExpression());
		/** TODO
		if ("beanshell".equals(language)) {
			return getOrCreateOriginalExpression(BeanShellExpression.class, expression);
		}
		 */
		if ("constant".equals(language)) {
			return getOrCreateOriginalExpression(ConstantExpression.class, expression);
		}
		if ("el".equals(language)) {
			return getOrCreateOriginalExpression(ELExpression.class, expression);
		}
		if ("groovy".equals(language)) {
			return getOrCreateOriginalExpression(GroovyExpression.class, expression);
		}
		if ("header".equals(language)) {
			return getOrCreateOriginalExpression(HeaderExpression.class, expression);
		}
		if ("javaScript".equals(language)) {
			return getOrCreateOriginalExpression(JavaScriptExpression.class, expression);
		}
		if ("jxpath".equals(language)) {
			return getOrCreateOriginalExpression(JXPathExpression.class, expression);
		}
		if ("method".equals(language)) {
			return getOrCreateMethodCall(expression);
		}
		if ("mvel".equals(language)) {
			return getOrCreateOriginalExpression(MvelExpression.class, expression);
		}
		if ("ognl".equals(language)) {
			return getOrCreateOriginalExpression(OgnlExpression.class, expression);
		}
		if ("php".equals(language)) {
			return getOrCreateOriginalExpression(PhpExpression.class, expression);
		}
		if ("exchangeProperty".equals(language)) {
			return getOrCreateOriginalExpression(ExchangePropertyExpression.class, expression);
		}
		if ("python".equals(language)) {
			return getOrCreateOriginalExpression(PythonExpression.class, expression);
		}
		if ("ref".equals(language)) {
			return getOrCreateOriginalExpression(RefExpression.class, expression);
		}
		if ("ruby".equals(language)) {
			return getOrCreateOriginalExpression(RubyExpression.class, expression);
		}
		if ("simple".equals(language)) {
			return getOrCreateOriginalExpression(SimpleExpression.class, expression);
		}
		if ("spel".equals(language)) {
			return getOrCreateOriginalExpression(SpELExpression.class, expression);
		}
		if ("sql".equals(language)) {
			return getOrCreateOriginalExpression(SqlExpression.class, expression);
		}
		if ("tokenize".equals(language)) {
			return getOrCreateOriginalExpression(TokenizerExpression.class, expression);
		}
		if ("xpath".equals(language)) {
			return getOrCreateOriginalExpression(XPathExpression.class, expression);
		}
		if ("xquery".equals(language)) {
			return getOrCreateOriginalExpression(XQueryExpression.class, expression);
		}
		return this;
	}

	private <T extends ExpressionDefinition> T getOrCreateOriginalExpression(Class<T> aType, String expression) {
		ExpressionDefinition answer = getOrCreateOriginalExpression(aType);
		answer.setExpression(expression);
		return aType.cast(answer);
	}

	private <T extends ExpressionDefinition> T getOrCreateOriginalExpression(Class<T> aType) {
		if (!aType.isInstance(original)) {
			this.original = org.apache.camel.util.ObjectHelper.newInstance(aType);
		}
		return aType.cast(original);
	}

	private MethodCallExpression getOrCreateMethodCall(String expression) {
		MethodCallExpression answer = getOrCreateOriginalExpression(MethodCallExpression.class);
		// lets split the method call on dot
		int idx = expression.lastIndexOf('.');
		if (idx > 0) {
			answer.setRef(expression.substring(0, idx));
			answer.setMethod(expression.substring(idx + 1));

		} else {
			answer.setBean(expression);
		}
		return answer;
	}

}
