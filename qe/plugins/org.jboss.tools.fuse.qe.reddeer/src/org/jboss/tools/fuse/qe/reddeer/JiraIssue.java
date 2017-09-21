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
package org.jboss.tools.fuse.qe.reddeer;

/**
 * 
 * @author apodhrad
 *
 */
public class JiraIssue extends AssertionError {

	private static final long serialVersionUID = 1L;

	public JiraIssue(String issue) {
		super("Please see https://issues.jboss.org/browse/" + issue);
	}

	public JiraIssue(String issue, Throwable cause) {
		super("Please see https://issues.jboss.org/browse/" + issue, cause);
	}

}
