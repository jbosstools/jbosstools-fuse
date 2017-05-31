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
package org.jboss.tools.fuse.qe.reddeer.condition;

import org.jboss.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.reddeer.swt.api.Text;

/**
 * 
 * @author apodhrad
 *
 */
public class ContainsText extends AbstractWaitCondition {

	private Text text;
	private String actualText;
	private String expectedText;

	public ContainsText(Text text, String expectedText) {
		this.text = text;
		this.expectedText = expectedText;
	}

	@Override
	public boolean test() {
		actualText = text.getText();
		return actualText.contains(expectedText);
	}

	@Override
	public String description() {
		return "Expected '" + expectedText + "' but was '" + actualText + "'";
	}

}
