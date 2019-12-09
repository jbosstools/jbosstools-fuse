/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.tests.util;

import java.util.ArrayList;
import java.util.Collection;

public class ParametizedTestUtil {
	
	/**
	 * converts an object array into a collection of object arrays
	 * 
	 * @param items
	 * @return
	 */
	public static Collection<Object[]> asCollection(Object[] items) {
		ArrayList<Object[]> ret = new ArrayList<Object[]>();
		for( int i = 0; i < items.length; i++ ) {
			ret.add(new Object[]{items[i]});
		}
		return ret;
	}
}
