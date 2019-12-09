/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.provider;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.fusesource.ide.camel.editor.internal.CamelModelNotificationService;

/**
 * @author lhein
 */
public class DiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String ID = "org.fusesource.ide.camel.editor.dtp.id";

	private CamelModelNotificationService camelModelNotificationService;
	
	private IToolBehaviorProvider[] toolBehaviorProviders;

	public DiagramTypeProvider() {
		super();
		setFeatureProvider(new CamelFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders =
					new IToolBehaviorProvider[] { new ToolBehaviourProvider(this) };
		}
		return toolBehaviorProviders;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#getNotificationService()
	 */
	@Override
	public INotificationService getNotificationService() {
		if (this.camelModelNotificationService == null) {
			this.camelModelNotificationService = new CamelModelNotificationService(this);
		}
		return this.camelModelNotificationService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#isAutoUpdateAtStartup()
	 */
	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#isAutoUpdateAtRuntimeWhenEditorIsSaved()
	 */
	@Override
	public boolean isAutoUpdateAtRuntimeWhenEditorIsSaved() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#isAutoUpdateAtReset()
	 */
	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#isAutoUpdateAtRuntime()
	 */
	@Override
	public boolean isAutoUpdateAtRuntime() {
		return true;
	}
}
