/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.fusesource.ide.camel.model.service.core.util;

import java.util.Collection;

import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author lhein
 */
public class LanguageUtils {

	/**
	 * returns an array of all supported language names
	 * @param camelFile 
	 * 
	 * @return
	 */
	public static String[] languageArray(CamelFile camelFile) {
		Collection<Language> languages = camelFile.getCamelModel().getLanguages();
		return languages.stream().map(Language::getName).toArray(String[]::new);
	}

	/**
	 * returns an array of all supported language names and titles
	 * 
	 * @return the name and languages array available with the latest version of Camel embedded.
	 */
	public static String[][] nameAndLanguageArray() {
		Collection<Language> languages = CamelCatalogCacheManager.getInstance().getDefaultCamelModel(CamelCatalogUtils.DEFAULT_CAMEL_VERSION).getLanguages();
		return languages.stream()
				.map(lang -> new String[] { lang.getTitle(), lang.getName() })
				.toArray(String[][]::new);
	}
}
