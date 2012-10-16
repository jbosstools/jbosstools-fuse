package org.fusesource.ide.commons.tree;

public class Refreshables {

	/**
	 * Refreshes the object if its refreshable
	 */
	public static void refresh(Object object) {
		if (object instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) object;
			refreshable.refresh();
		}
	}

}
