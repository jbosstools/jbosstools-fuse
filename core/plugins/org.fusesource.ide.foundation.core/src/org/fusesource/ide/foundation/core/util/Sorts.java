/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.core.util;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Sorts {
	
	public static String[] toSortedStringArray(Collection<String> collection) { 
		Set<String> keys = new TreeSet<String>(collection);
		return keys.toArray(new String[keys.size()]);

	}

}
