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
package org.fusesource.ide.camel.model.service.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.apache.maven.model.Dependency;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CamelCatalogUtilsTest {

	@Parameters(name = "{0} {1} {2}")
	public static Collection<Object[]> data() {
		String latestBomFis20Bom = CamelCatalogUtils.CAMEL_VERSION_2_FUSE_6_FIS_BOM_MAPPING.values().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
		CamelCatalogUtils.CAMEL_VERSION_2_FUSE_7_BOM_MAPPING.put("specificCamelVersionForTest", "specificBomVersionForTest");
		return Arrays.asList(new Object[][] {
			{ "2.15.1.redhat-621186", FuseBomFilter.BOM_FUSE_6, "6.2.1.redhat-186" },
			{ CamelCatalogUtils.FUSE_63_R5_CAMEL_VERSION, FuseBomFilter.BOM_FUSE_6, CamelCatalogUtils.FUSE_63_R5_BOM_VERSION },
			{ CamelCatalogUtils.FUSE_63_R4_CAMEL_VERSION, FuseBomFilter.BOM_FUSE_6, CamelCatalogUtils.FUSE_63_R4_BOM_VERSION },
			{ CamelCatalogUtils.FIS_20_R3_CAMEL_VERSION, FuseBomFilter.BOM_FUSE_6_FIS, "2.2.170.redhat-000019" },
			{ CamelCatalogUtils.FIS_20_R3_CAMEL_VERSION, FuseBomFilter.BOM_FUSE_6_FIS, "2.2.170.redhat-000019" },
			{ "2.19.0", FuseBomFilter.BOM_FUSE_6_FIS, latestBomFis20Bom },
			{ "2.19.0", FuseBomFilter.BOM_FUSE_6, CamelCatalogUtils.CAMEL_VERSION_2_FUSE_6_BOM_MAPPING.values().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null) },
			{ "2.20.1", FuseBomFilter.BOM_FUSE_6_FIS, latestBomFis20Bom},
			{ "specificCamelVersionForTest", FuseBomFilter.BOM_FUSE_7, "specificBomVersionForTest"}
		});
	}

    @Parameter
    public String camelVersion;
    @Parameter(1)
    public Dependency bomUsed;
    @Parameter(2)
	public String expectedBomVersion;
    
	@Test
	public void testBomVersionRetrieval() throws Exception {
		assertThat(CamelCatalogUtils.getBomVersionForCamelVersion(camelVersion, null, new NullProgressMonitor(), bomUsed)).isEqualTo(expectedBomVersion);
	}

}
