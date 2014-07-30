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

package org.fusesource.ide.camel.editor.editor;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.io.ContainerMarshaler;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;


public class RiderDesignEditorData {
	public ContainerMarshaler marshaller = new XmlContainerMarshaller();
	public boolean loaded = false;
	public List<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();

	public RouteContainer model;
	public RouteSupport selectedRoute;
	public boolean diagramChanged;
	public boolean textChanged;
	public boolean lazyLoading;
	public boolean shownValidationError;
	public boolean loadModelOnSetInput;
	public AbstractNode selectedNode;
	public int selectedRouteIndex;

	public void recreateModel() {
		model = model.recreateModel();
	}

	public int indexOfRoute(RouteSupport route) {
		if (model != null) {
			return model.getChildren().indexOf(route);
		}
		return 0;
	}

}