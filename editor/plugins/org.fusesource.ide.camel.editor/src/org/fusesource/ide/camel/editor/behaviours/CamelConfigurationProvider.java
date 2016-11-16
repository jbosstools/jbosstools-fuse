/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.behaviours;

import org.eclipse.gef.EditPolicy;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.internal.config.ConfigurationProvider;
import org.eclipse.graphiti.ui.internal.policy.DefaultEditPolicyFactory;
import org.eclipse.graphiti.ui.internal.policy.IEditPolicyFactory;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;

//TODO: remove after Graphiti issue fixed https://bugs.eclipse.org/bugs/show_bug.cgi?id=499720
final class CamelConfigurationProvider extends ConfigurationProvider {
	private boolean _isDisposed;
	private IEditPolicyFactory _editPolicyFactory;

	CamelConfigurationProvider(DiagramBehavior diagramBehavior, IDiagramTypeProvider diagramTypeProvider) {
		super(diagramBehavior, diagramTypeProvider);
	}

	@Override
	public IEditPolicyFactory getEditPolicyFactory() {
			if (_editPolicyFactory == null && !_isDisposed) {
				_editPolicyFactory = new DefaultEditPolicyFactory(this){
					
					/**
					 * Bad Graphiti naming, the "delete" is handling all operations.
					 * */
					public EditPolicy createConnectionDeleteEditPolicy(IConfigurationProvider configurationProvider) {
						return new CamelDefaultConnectionEditPolicy(configurationProvider);
					}
					
				};
			}
			return _editPolicyFactory;
	}

	@Override
	public void dispose() {
		super.dispose();
		_editPolicyFactory = null;
		_isDisposed = true;
	}
}