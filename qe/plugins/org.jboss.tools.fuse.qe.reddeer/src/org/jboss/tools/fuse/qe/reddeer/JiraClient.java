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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

public class JiraClient {

	public static final String JBOSS_JIRA = "https://issues.jboss.org/rest/api/latest";

	private String url;

	public JiraClient() {
		this(JBOSS_JIRA);
	}

	public JiraClient(String url) {
		this.url = url;
	}

	public String getIssue(String issueId) {
		try {
			return new HttpClient(url + "/issue/" + issueId + "?fields=status").get();
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public boolean isIssueClosed(String issueId) {
		String issue = getIssue(issueId);
		if (issue != null) {
			Pattern pattern = Pattern.compile("\"name\":\"Closed\"");
			return pattern.matcher(issue).find();
		}
		return false;
	}
}
