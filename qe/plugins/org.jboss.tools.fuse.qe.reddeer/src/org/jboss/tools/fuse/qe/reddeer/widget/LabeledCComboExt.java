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
package org.jboss.tools.fuse.qe.reddeer.widget;

import org.jboss.reddeer.swt.impl.ccombo.AbstractCCombo;

/**
 * 
 * @author apodhrad
 *
 */
public class LabeledCComboExt extends AbstractCCombo {

	public LabeledCComboExt(String label) {
		this(label, true);
	}

	public LabeledCComboExt(String label, boolean ignoreAsterisk) {
		super(null, 0, new WithLabelMatcherExt(label, ignoreAsterisk));
	}

}
