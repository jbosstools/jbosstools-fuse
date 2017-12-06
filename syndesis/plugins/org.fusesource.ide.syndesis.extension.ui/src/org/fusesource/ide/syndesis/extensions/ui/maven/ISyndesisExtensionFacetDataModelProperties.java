/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.maven;

import org.eclipse.core.runtime.QualifiedName;
import org.fusesource.ide.projecttemplates.util.camel.ICamelFacetDataModelProperties;
import org.fusesource.ide.syndesis.extensions.ui.SyndesisExtensionProjectNature;

/**
 * @author lheinema
 */
public interface ISyndesisExtensionFacetDataModelProperties extends ICamelFacetDataModelProperties {
	/*
	 * Our primary keys for setting sar information in the wizard
	 */
	public static final String SYNDESIS_EXTENSION_CONTENT_FOLDER = "ISyndesisExtensionFacetDataModelProperties.Content_Folder"; //$NON-NLS-1$
	public static final String SYNDESIS_EXTENSION_PROJECT_VERSION = "ISyndesisExtensionFacetDataModelProperties.Project.Version"; //$NON-NLS-1$
	public static final String SPRING_BOOT_PROJECT_VERSION = "ISyndesisExtensionFacetDataModelProperties.SpringBoot.Project.Version"; //$NON-NLS-1$
	public static final String SYNDESIS_EXTENSION_PROJECT_METADATA = "ISyndesisExtensionFacetDataModelProperties.Project.MetaData"; //$NON-NLS-1$
	public static final QualifiedName QNAME_SYNDESIS_EXTENSION_VERSION = new QualifiedName("syndesis.extension", SYNDESIS_EXTENSION_PROJECT_VERSION); //$NON-NLS-1$
	public static final QualifiedName QNAME_SPRING_BOOT_VERSION = new QualifiedName("spring.boot", SPRING_BOOT_PROJECT_VERSION); //$NON-NLS-1$
	
	/**
	 * 
	 */
	public static final String FACET_JST_SYNDESIS_EXTENSION = "jst.syndesis.extension";
	
	/*
	 * Other constants
	 */
	public final static String SYNDESIS_PROJECT_NATURE = SyndesisExtensionProjectNature.NATURE_ID;
}
