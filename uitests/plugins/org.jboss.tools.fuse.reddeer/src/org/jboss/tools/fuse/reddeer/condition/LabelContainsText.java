/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.swt.api.Label;

/**
 * @author djelinek
 */
public class LabelContainsText extends AbstractWaitCondition {

	private Label label;
	private String actualText;
	private String expectedText;

	public LabelContainsText(Label label, String expectedText) {
		this.label = label;
		this.expectedText = expectedText;
	}

	@Override
	public boolean test() {
		actualText = label.getText();
		return actualText.contains(expectedText);
	}

	@Override
	public String description() {
		return "Expected '" + expectedText + "' but was '" + actualText + "'";
	}

}
