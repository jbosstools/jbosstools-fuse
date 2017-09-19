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
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;

final class BeanRefAndClassCrossValidator extends MultiValidator {
	
	private ISWTObservableValue classObservable;
	private ISWTObservableValue beanrefObservable;
	
	public BeanRefAndClassCrossValidator(ISWTObservableValue classObservable, ISWTObservableValue beanrefObservable) {
		this.classObservable = classObservable;
		this.beanrefObservable = beanrefObservable;
	}
	
	@Override
	protected IStatus validate() {
		String className = (String)classObservable.getValue();
		String beanRef = (String)beanrefObservable.getValue();
		if((className == null || className.isEmpty()) && (beanRef == null || beanRef.isEmpty())) {
			return ValidationStatus.error("Must specify either an explicit class name in the project or a reference to a global bean that exposes one.");
		} 
		if(className != null && !className.isEmpty() && beanRef != null && !beanRef.isEmpty()) {
			return ValidationStatus.error("Must specify either an explicit class name in the project or a reference to a global bean that exposes one, not both.");
		}
		return Status.OK_STATUS;
	}
}