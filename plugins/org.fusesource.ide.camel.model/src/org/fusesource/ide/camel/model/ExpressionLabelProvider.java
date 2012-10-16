package org.fusesource.ide.camel.model;

import static org.fusesource.ide.commons.util.Strings.getOrElse;

import org.apache.camel.model.language.ExpressionDefinition;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author lhein
 */
public class ExpressionLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		return super.getImage(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ExpressionDefinition) {
			ExpressionDefinition expression = (ExpressionDefinition) element;
			String language = getOrElse(expression.getLanguage());
			String expr = expression.getExpression();
			return String.format("%s:  %s", language, expr);
		}
		return super.getText(element);
	}
}
