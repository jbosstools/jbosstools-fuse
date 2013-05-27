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

import org.eclipse.graphiti.mm.pictograms.impl.DiagramImpl;


public class CamelDiagram extends DiagramImpl {
	private final RiderDesignEditor designEditor;

	public CamelDiagram(RiderDesignEditor designEditor) {
		this.designEditor = designEditor;
	}

//	@Override
//	public Resource eResource() {
//		RouteContainer model = designEditor.getModel();
//		Objects.notNull(model, "model");
//		return model.eResource();
//	}

	public RiderDesignEditor getDesignEditor() {
		return designEditor;
	}



}
