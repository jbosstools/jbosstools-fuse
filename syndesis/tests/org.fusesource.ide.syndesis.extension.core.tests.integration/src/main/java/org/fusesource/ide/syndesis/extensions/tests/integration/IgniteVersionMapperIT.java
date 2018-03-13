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
package org.fusesource.ide.syndesis.extensions.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Condition;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.syndesis.extensions.core.util.IgniteVersionMapper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lheinema
 */
public class IgniteVersionMapperIT {
	
	private Map<String, String> map;
	
	@Before
	public void initialize() {
		map = new HashMap<>();
		map.put("1.2.0", "1.2.0 (Bla)");
		map.put("1.0-SNAPSHOT", "1.0-SNAPSHOT (not existing version)");
		map.put("1.3-SNAPSHOT", "1.3-SNAPSHOT (Special Version)");		
	}
	
	@Test
	public void testMapperConsolidation() {
		IgniteVersionMapper.consolidateSnapshots(map, new NullProgressMonitor());
		assertThat(map).containsEntry("1.2.0", "1.2.0 (Bla)");
		assertThat(map).doesNotContainKey("1.0-SNAPSHOT");
		assertThat(map).doesNotContainKey("1.3-SNAPSHOT");
		assertThat(map).doesNotContainValue("1.3-SNAPSHOT (Special Version)");
		assertThat(map.keySet()).filteredOn(new Condition<String>() {
			@Override
			public boolean matches(String arg0) {
				return arg0.startsWith("1.3.");
			}
		}).hasSize(1);
	}
}
