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
