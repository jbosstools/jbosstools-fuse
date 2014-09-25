/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.edit;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;

public class DelegatingItemPropertyDescriptor implements IItemPropertyDescriptor {

	 protected IItemPropertyDescriptor delegateItemPropertyDescriptor;
	 
	 protected EStructuralFeature delegateFeature;

	 public DelegatingItemPropertyDescriptor(IItemPropertyDescriptor itemPropertyDescriptor, EStructuralFeature delegateFeature) {
		super();
		this.delegateItemPropertyDescriptor = itemPropertyDescriptor;
		this.delegateFeature = delegateFeature;
	}

	@Override
	public Object getPropertyValue(Object object) {
		return delegateItemPropertyDescriptor.getPropertyValue(getDelegateObject(object));
	}

	@Override
	public boolean isPropertySet(Object object) {
		return delegateItemPropertyDescriptor.isPropertySet(getDelegateObject(object));
	}

	@Override
	public boolean canSetProperty(Object object) {
		return delegateItemPropertyDescriptor.canSetProperty(getDelegateObject(object));
	}

	@Override
	public void resetPropertyValue(Object object) {
		delegateItemPropertyDescriptor.resetPropertyValue(getDelegateObject(object));
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		delegateItemPropertyDescriptor.setPropertyValue(getDelegateObject(object), value);
	}

	@Override
	public String getCategory(Object object) {
		return delegateItemPropertyDescriptor.getCategory(getDelegateObject(object));
	}

	@Override
	public String getDescription(Object object) {
		return delegateItemPropertyDescriptor.getDescription(getDelegateObject(object));
	}

	@Override
	public String getDisplayName(Object object) {
		return delegateItemPropertyDescriptor.getDisplayName(getDelegateObject(object));
	}

	@Override
	public String[] getFilterFlags(Object object) {
		return delegateItemPropertyDescriptor.getFilterFlags(getDelegateObject(object));
	}

	@Override
	public Object getHelpContextIds(Object object) {
		return delegateItemPropertyDescriptor.getHelpContextIds(getDelegateObject(object));
	}

	@Override
	public String getId(Object object) {
		return delegateItemPropertyDescriptor.getId(getDelegateObject(object));
	}

	@Override
	public IItemLabelProvider getLabelProvider(Object object) {
		return delegateItemPropertyDescriptor.getLabelProvider(getDelegateObject(object));
	}

	@Override
	public boolean isCompatibleWith(Object object, Object anotherObject,
			IItemPropertyDescriptor anotherPropertyDescriptor) {
		return delegateItemPropertyDescriptor.isCompatibleWith(getDelegateObject(object), anotherObject, anotherPropertyDescriptor);
	}

	@Override
	public Object getFeature(Object object) {
		return delegateItemPropertyDescriptor.getFeature(getDelegateObject(object));
	}

	@Override
	public boolean isMany(Object object) {
		return delegateItemPropertyDescriptor.isMany(getDelegateObject(object));
	}

	@Override
	public Collection<?> getChoiceOfValues(Object object) {
		return delegateItemPropertyDescriptor.getChoiceOfValues(getDelegateObject(object));
	}

	@Override
	public boolean isMultiLine(Object object) {
		return delegateItemPropertyDescriptor.isMultiLine(getDelegateObject(object));
	}

	@Override
	public boolean isSortChoices(Object object) {
		return delegateItemPropertyDescriptor.isSortChoices(getDelegateObject(object));
	}
	
	protected Object getDelegateObject(Object object) {
		return ((EObject)object).eGet(delegateFeature);
	}

}
