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
package org.fusesource.ide.projecttemplates.wizards.pages.model;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.foundation.core.util.Strings;

public class TemplateItemIdentity {

	private String id;
	private String name;
	private String description;
	private List<String> keywords = new ArrayList<>();

	public TemplateItemIdentity(String id, String name, String description, String keywords) {
		this.id = id;
		this.name = name;
		this.description = description;
		initKeywords(keywords);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	
	private void initKeywords(String keywordsString) {
		if(keywordsString != null){
			String[] words = keywordsString.split(" ");
			for (String word : words) {
				word = word.trim();
				if (!Strings.isBlank(word) && !keywords.contains(word)) {
					keywords.add(word);
				}
			}
		}
	}
	
}