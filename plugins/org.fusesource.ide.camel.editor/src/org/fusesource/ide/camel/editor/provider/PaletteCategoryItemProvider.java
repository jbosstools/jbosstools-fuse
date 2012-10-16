package org.fusesource.ide.camel.editor.provider;

import org.fusesource.ide.camel.editor.Activator;

/**
 * @author lhein
 */
public interface PaletteCategoryItemProvider {
	static enum CATEGORY_TYPE {
		ENDPOINTS, ROUTING, CONTROL_FLOW, TRANSFORMATION, MISCELLANEOUS, NONE;

		public static CATEGORY_TYPE getCategoryType(String name) {
			if (name != null) {
				String enumName = name.toUpperCase().replace(' ', '_');
				CATEGORY_TYPE answer = PaletteCategoryItemProvider.CATEGORY_TYPE.valueOf(enumName);
				if (answer != null) {
					return answer;
				} else {
					Activator.getLogger().warning("Could not find CATEGORY_TYPE for " + enumName + " from caetgory name: " + name);
				}
			}
			return CATEGORY_TYPE.NONE;
		}
	};

	/**
	 * returns the category type
	 * 
	 * @return
	 */
	CATEGORY_TYPE getCategoryType();

	public abstract String getCategoryName();
}
