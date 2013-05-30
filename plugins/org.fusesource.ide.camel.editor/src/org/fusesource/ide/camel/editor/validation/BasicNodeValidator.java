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

package org.fusesource.ide.camel.editor.validation;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.AbstractNode;


/**
 * @author lhein
 */
public class BasicNodeValidator {

	/**
	 * does the validation
	 * 
	 * @param node
	 * @return
	 */
	protected ValidationResult validate(AbstractNode node) {
		ValidationResult result = new ValidationResult();

		// we check if all mandatory fields are filled
		for (IPropertyDescriptor pd : node.getPropertyDescriptors()) {
			String property = NodeUtils.getPropertyName(pd.getId());
			if (NodeUtils.isMandatory(node, property)) {
				Object val = node.getPropertyValue(pd.getId());
				if (val == null || val.toString().trim().length()<1) {
					result.addError("There are mandatory fields which are not filled. Please check the properties view for more details.");
				}
			}
		}
		
		return result;
	}
}
