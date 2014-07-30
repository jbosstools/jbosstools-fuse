/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.features.create;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.provider.PaletteCategoryItemProvider;


/**
 * @author lhein
 */
public final class CreateFeatureFactory {
	
	/**
	 * factory method for generating features for specified model classes
	 * 
	 * @param clazz
	 * @param fp
	 * @param name
	 * @param description
	 * @return
	 */
	public static <E> PaletteCategoryItemProvider create(Class<E> clazz, IFeatureProvider fp, String name, String description) {
		return new CreateFigureFeature<E>(fp, name, description, clazz);
	}
}
