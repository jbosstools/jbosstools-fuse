/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.fusesource.ide.camel.model.service.core.util;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.ide.camel.model.service.core.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.catalog.languages.LanguageModel;

/**
 * @author lhein
 */
public class LanguageUtils {

	protected static LanguageModel getLanguageModel() {
		return CamelModelFactory.getModelForVersion(CamelModelFactory.getLatestCamelVersion()).getLanguageModel();
	}
	
	/**
	 * returns an array of all supported language names
	 * 
	 * @return
	 */
	public static String[] languageArray() {
		List<Language> languages = getLanguageModel().getSupportedLanguages();
		List<String> answer = new ArrayList<String>(languages.size());
		for (Language l : languages) {
			answer.add(l.getName());
		}
		return answer.toArray(new String[languages.size()]);
	}

	/**
	 * returns an array of all supported language names and titles
	 * 
	 * @return
	 */
	public static String[][] nameAndLanguageArray() {
		List<Language> languages = getLanguageModel().getSupportedLanguages();
		List<String[]> answer = new ArrayList<String[]>(languages.size());
		for (Language l : languages) {
			answer.add(new String[] { l.getTitle(), l.getName() });
		}
		return answer.toArray(new String[languages.size()][]);
	}
}
