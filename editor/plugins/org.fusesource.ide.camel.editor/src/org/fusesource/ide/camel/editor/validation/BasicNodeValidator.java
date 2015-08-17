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

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.commons.util.Strings;


/**
 * @author lhein
 */
public class BasicNodeValidator implements ValidationSupport {

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.validation.ValidationSupport#validate(org.fusesource.ide.camel.model.AbstractNode)
	 */
    @Override
    public ValidationResult validate(AbstractNode node) {
		ValidationResult result = new ValidationResult();

		// we check if all mandatory fields are filled
		for (IPropertyDescriptor pd : node.getPropertyDescriptors()) {
			if( pd == null ) {
				continue;
			}
			String property = NodeUtils.getPropertyName(pd.getId());
			if (NodeUtils.isMandatory(node, property)) {
				Object val = node.getPropertyValue(pd.getId());
				if (val == null || val.toString().trim().length()<1) {
					result.addError("There are mandatory fields which are not filled. Please check the properties view for more details.");
				}
			}
			// check if the ID is unique
			if (property.equalsIgnoreCase("id")) {
				RiderDesignEditor editor = Activator.getDiagramEditor();
				if (editor != null) {
					if (!checkAllUniqueIDs(node, editor.getModel().getChildren(), new ArrayList<String>())) {
						result.addError("The id property is not unique!");
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
	protected boolean checkAllUniqueIDs(AbstractNode nodeUnderValidation, List<AbstractNode> nodes, ArrayList<String> processedNodeIDs) {
		boolean noDoubledIDs = true;
		for (AbstractNode node : nodes) {
			if (node.getChildren() != null) {
				noDoubledIDs = checkAllUniqueIDs(nodeUnderValidation, node.getChildren(), processedNodeIDs);
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
