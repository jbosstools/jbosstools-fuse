/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish.jmx;

import java.util.Collection;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.InvalidKeyException;

final class MockedCompositeData implements CompositeData {
	
	private Map<String, ?> getValues;

	public MockedCompositeData(Map<String, ?> getValues) {
		this.getValues = getValues;
	}
	
	@Override
	public Collection<?> values() {
		return null;
	}

	@Override
	public CompositeType getCompositeType() {
		return null;
	}

	@Override
	public Object[] getAll(String[] keys) {
		return null;
	}

	@Override
	public Object get(String key) {
		if (getValues.containsKey(key)) {
			return getValues.get(key);
		} else {
			throw new InvalidKeyException();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public boolean containsKey(String key) {
		return false;
	}
}
