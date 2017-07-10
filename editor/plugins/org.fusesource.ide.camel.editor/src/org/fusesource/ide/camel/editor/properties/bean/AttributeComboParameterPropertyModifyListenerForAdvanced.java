/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.AbstractComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;

/**
 * @author brianf
 *
 */
public class AttributeComboParameterPropertyModifyListenerForAdvanced extends AbstractComboParameterPropertyModifyListener {

	public AttributeComboParameterPropertyModifyListenerForAdvanced(AbstractCamelModelElement camelModelElement, Parameter parameter) {
		super(camelModelElement, parameter.getName());
	}

	@Override
	protected void updateModel(String newValue) {
		if (camelModelElement instanceof CamelBean) {
			CamelBean bean = (CamelBean) camelModelElement;
			bean.setParameter(parameterName, newValue);
		} else {
			camelModelElement.setParameter(parameterName, newValue);
		}
	}
}