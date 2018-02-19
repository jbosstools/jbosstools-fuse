package org.fusesource.ide.server.karaf.core.publish.jmx;

import java.util.Collection;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

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
final class MockedTabularData implements TabularData {
	
	private Collection<?> values;

	public MockedTabularData(Collection<?> values) {
		this.values = values;
	}
	
	@Override
	public Collection<?> values() {
		return values;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public CompositeData remove(Object[] key) {
		return null;
	}

	@Override
	public void putAll(CompositeData[] values) {
	}

	@Override
	public void put(CompositeData value) {
	}

	@Override
	public Set<?> keySet() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public TabularType getTabularType() {
		return null;
	}

	@Override
	public CompositeData get(Object[] key) {
		return null;
	}

	@Override
	public boolean containsValue(CompositeData value) {
		return false;
	}

	@Override
	public boolean containsKey(Object[] key) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public Object[] calculateIndex(CompositeData value) {
		return null;
	}
}
