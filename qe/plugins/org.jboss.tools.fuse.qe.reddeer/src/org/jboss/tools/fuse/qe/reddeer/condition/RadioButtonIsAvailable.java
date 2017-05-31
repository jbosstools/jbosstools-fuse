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
import org.jboss.reddeer.swt.impl.button.RadioButton;

/**
 * Wait condition which try toogle a radio button with defined label
 * 
 * @author djelinek
 *
 */
public class RadioButtonIsAvailable extends AbstractWaitCondition {

	private String label;

	public RadioButtonIsAvailable(String label) {
		this.label = label;
	}

	@Override
	public boolean test() {
		try {
			new RadioButton(label).toggle(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
