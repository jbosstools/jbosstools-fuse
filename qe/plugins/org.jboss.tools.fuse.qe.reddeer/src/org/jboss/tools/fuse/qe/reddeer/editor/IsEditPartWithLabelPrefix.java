/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.editor;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.core.IsInstanceOf;
import org.jboss.reddeer.gef.finder.FigureFinder;

/**
 * Matches edit part which contains {@link org.eclipse.draw2d.Label} or {@link org.eclipse.draw2d.text.TextFlow} with a
 * given prefix.
 * 
 * @author apodhrad
 * 
 */
public class IsEditPartWithLabelPrefix extends BaseMatcher<EditPart> {

	private String prefix;

	/**
	 * Constructs a matcher with a given prefix.
	 * 
	 * @param prefix
	 *            Prefix
	 */
	public IsEditPartWithLabelPrefix(String prefix) {
		this.prefix = prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hamcrest.Matcher#matches(java.lang.Object)
	 */
	@Override
	public boolean matches(Object obj) {
		if (obj instanceof GraphicalEditPart) {
			GraphicalEditPart gep = (GraphicalEditPart) obj;
			if (gep.isSelectable()) {
				List<IFigure> labels = new FigureFinder().find(gep.getFigure(), new IsInstanceOf(Label.class));
				for (IFigure figure : labels) {
					String label = ((Label) figure).getText();
					if (label.trim().startsWith(this.prefix.trim())) {
						return true;
					}
				}
				List<IFigure> textFlows = new FigureFinder().find(gep.getFigure(), new IsInstanceOf(TextFlow.class));
				for (IFigure figure : textFlows) {
					String text = ((TextFlow) figure).getText();
					if (text.trim().startsWith(this.prefix.trim())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("is EditPart with label '" + prefix + "'");
	}
}
