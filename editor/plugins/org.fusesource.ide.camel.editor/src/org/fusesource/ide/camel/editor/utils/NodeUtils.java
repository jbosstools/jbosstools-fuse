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

package org.fusesource.ide.camel.editor.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class NodeUtils {

	public static String getPropertyName(final Object id) {
		String propertyName = id.toString();
		int idx = propertyName.indexOf('.');
		if (idx > 0) {
			propertyName = propertyName.substring(idx + 1);
		}
		propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
		return propertyName;
	}

	public static boolean isMandatory(Object bean, String propertyName) {
		// lets look at the setter method and see if its got a @Required
		// annotation
		if (bean instanceof CamelModelElement) {
			CamelModelElement node = (CamelModelElement) bean;
			Eip eip = node.getUnderlyingMetaModelObject();
			Parameter p = eip.getParameter(propertyName);
			if (p != null) {
				return p.getRequired().equals("true");
			}
		}
		return false;
	}
	
	/**
	 * checks if the given parent can host the given child element
	 * 
	 * @param parent	the parent / container element
	 * @param child		the child element
	 * @return			true if the child is valid for the given container, otherwise false
	 */
	public static boolean isValidChild(CamelModelElement parent, CamelModelElement child) {
		if (parent != null && child != null) {
			IProject project = parent.getCamelFile().getResource().getProject();
    		String camelVersion = CamelModelFactory.getCamelVersion(project);
    		CamelModel metaModel = CamelModelFactory.getModelForVersion(camelVersion);
    		
			if (parent.getNodeTypeId().equalsIgnoreCase("choice")) {
				// special case for choice
				return child.getNodeTypeId().equalsIgnoreCase("when") || child.getNodeTypeId().equalsIgnoreCase("otherwise");
			} else {
				Eip containerEip = parent.getUnderlyingMetaModelObject();
	        	if (containerEip == null) {
	        		containerEip = metaModel.getEipModel().getEIPByName(parent.getNodeTypeId());
	        	}
	        	return containerEip != null && containerEip.canHaveChildren() && containerEip.getAllowedChildrenNodeTypes().contains(child.getNodeTypeId());
			}
		}
		return false;
	}
	
	/**
	 * collects all container figures recursively
	 * 
	 * @param fp
	 * @param context
	 * @param pes
	 */
    public static void getAllContainers(IFeatureProvider fp, CamelModelElement context, ArrayList<PictogramElement> pes) {
		for (CamelModelElement cme : context.getChildElements()) {
			if (cme.getUnderlyingMetaModelObject().canHaveChildren()) {
				pes.add(fp.getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(cme));
			}
			if (cme.getChildElements().size() > 0) {
				getAllContainers(fp, cme, pes);
			}
		}
	}

}
