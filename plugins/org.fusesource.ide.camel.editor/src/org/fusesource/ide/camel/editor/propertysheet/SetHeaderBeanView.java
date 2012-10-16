package org.fusesource.ide.camel.editor.propertysheet;

import org.apache.camel.model.SetHeaderDefinition;
import org.fusesource.ide.camel.model.LanguageExpressionBean;
import org.fusesource.ide.commons.util.BeanSupport;


public class SetHeaderBeanView extends BeanSupport {
	private final SetHeaderDefinition definition;
	private LanguageExpressionBean languageBean;
	private String headerName;
	private String expression;
	private String language;

	public SetHeaderBeanView(SetHeaderDefinition definition) {
		this.definition = definition;
		languageBean = LanguageExpressionBean.toLanguageExpressionBean(definition.getExpression());
		// lets make sure its definitely a correct kind of expression
		definition.setExpression(languageBean);

		this.headerName = definition.getHeaderName();
		this.expression = languageBean.getExpression();
		this.language = languageBean.getLanguage();
	}

	@Override
	public String toString() {
		return "SetHeader(" + getHeaderName() + " = " + getLanguage() + ": " + getExpression();
	}

	public void update() {
		definition.setHeaderName(headerName);
		languageBean.setExpression(expression);
		languageBean.setLanguage(language);

	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}



}
