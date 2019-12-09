/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.jboss.tools.fuse.reddeer.utils.FuseShellSSH;

/**
 * Checks whether Fuse server's log contains specified text
 * 
 * @author tsedmik
 */
public class FuseLogContainsText extends AbstractWaitCondition {

	private String text;

	public FuseLogContainsText(String text) {

		this.text = text;
	}

	@Override
	public boolean test() {

		return new FuseShellSSH().execute("log:display").contains(text);
	}

	@Override
	public String description() {

		return "Fuse Server's log contains: \"" + text + "\"";
	}

}
