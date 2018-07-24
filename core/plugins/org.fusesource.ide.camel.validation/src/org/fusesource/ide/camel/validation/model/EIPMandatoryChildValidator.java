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
package org.fusesource.ide.camel.validation.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBasicModelElement;
import org.fusesource.ide.camel.validation.l10n.Messages;

public class EIPMandatoryChildValidator implements IValidator {
	
	/**
	 * To get this list up-to-date it is cumbersome, you need to go to ProcessorDefinition.createChildProcessor
	 * and check all elements that are calling it with "true"
	 * "choice" is a special case, it throws NPE if nothing inside, there is no route definition validation in this case.
	 */
	private static Set<String> eipWithMandatoryChildren = new HashSet<>(Arrays.asList(
			AbstractCamelModelElement.CHOICE_NODE_NAME,
			"aggregate",
			"filter",
			"idempotentConsumer",
			"intercept",
			"interceptFrom",
			"interceptSendToEndpoint",
			"loop",
			"multicast",
			"onCompletion",
			"pipeline",
			"sample",
			"split",
			"threads",
			"throttle",
			"resequence"));
	
	@Override
	public IStatus validate(Object value) {
		if(isComponentWithMandatoryChildren(value) && !hasChildren((CamelBasicModelElement)value)) {
			return ValidationStatus.error(Messages.eipWithoutChild);
		}
		return Status.OK_STATUS;
	}

	protected boolean hasChildren(CamelBasicModelElement value) {
		return !value.getChildElements().isEmpty();
	}

	protected boolean isComponentWithMandatoryChildren(Object value) {
		return value instanceof CamelBasicModelElement
				&& eipWithMandatoryChildren.contains(((CamelBasicModelElement) value).getNodeTypeId());
	}

}
