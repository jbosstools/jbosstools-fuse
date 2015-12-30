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

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.fusesource.ide.foundation.core.util.Strings;


/**
 * @author lhein
 */
public class BasicNodeValidator implements ValidationSupport {

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.validation.ValidationSupport#validate(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
    @Override
    public ValidationResult validate(CamelModelElement node) {
		ValidationResult result = new ValidationResult();
		
		if (node != null && node.getCamelContext() != null) { // TODO: check why camel context can be null?!?
			// we check if all mandatory fields are filled
			for (Parameter pd : node.getUnderlyingMetaModelObject().getParameters()) {
				String property = pd.getName();
				if ((pd.getKind().equalsIgnoreCase("element") && pd.getType().equalsIgnoreCase("array")) || pd.getJavaType().equals("org.apache.camel.model.OtherwiseDefinition")) continue;
				if (pd.getRequired().equalsIgnoreCase("true")) {
					Object val = node.getParameter(property);
					if (val == null || val.toString().trim().length()<1) {
						result.addError("There are mandatory fields which are not filled. Please check the properties view for more details.");
					}
				}
				// check if the ID is unique
				if (property.equalsIgnoreCase("id")) {
					CamelDesignEditor editor = CamelUtils.getDiagramEditor();
					if (editor != null) {
						if (!checkAllUniqueIDs(node, node.getCamelContext().getChildElements(), new ArrayList<String>())) {
							result.addError("The id property is not unique!");
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * checks if the given node's id property is unique in the whole camel context
	 * 
	 * @param nodeUnderValidation
	 * @param nodes
	 * @param processedNodeIDs
	 * @return
	 */
	protected boolean checkAllUniqueIDs(CamelModelElement nodeUnderValidation, List<CamelModelElement> nodes, ArrayList<String> processedNodeIDs) {
		boolean noDoubledIDs = true;
		for (CamelModelElement node : nodes) {
			if (node.getChildElements() != null) {
				noDoubledIDs = checkAllUniqueIDs(nodeUnderValidation, node.getChildElements(), processedNodeIDs);
				if (noDoubledIDs == false) return false;
			}
			if (noDoubledIDs) {
				if (!Strings.isBlank(node.getId())) {
					if (processedNodeIDs.contains(node.getId()) && node.equals(nodeUnderValidation)) {
						return false;
					} else {
						processedNodeIDs.add(node.getId());
					}
				}
			}
		}
		return noDoubledIDs;
	}
}
