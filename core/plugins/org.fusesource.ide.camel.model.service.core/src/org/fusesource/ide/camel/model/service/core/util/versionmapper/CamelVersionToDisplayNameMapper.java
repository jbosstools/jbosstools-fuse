/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util.versionmapper;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class CamelVersionToDisplayNameMapper extends OnlineVersionMapper {
	
	public CamelVersionToDisplayNameMapper() {
		super("org.jboss.fuse.displayname.url", BASE_REPO_CONFIG_URI+"camelVersionToDisplayName.properties");
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R0_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R0_CAMEL_VERSION + " (Fuse 6.2.1 R0)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R1_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R1_CAMEL_VERSION + " (Fuse 6.2.1 R1)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R2_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R2_CAMEL_VERSION + " (Fuse 6.2.1 R2)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R3_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R3_CAMEL_VERSION + " (Fuse 6.2.1 R3)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R4_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R4_CAMEL_VERSION + " (Fuse 6.2.1 R4)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R5_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R5_CAMEL_VERSION + " (Fuse 6.2.1 R5)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R6_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R6_CAMEL_VERSION + " (Fuse 6.2.1 R6)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R7_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R7_CAMEL_VERSION + " (Fuse 6.2.1 R7)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R8_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R8_CAMEL_VERSION + " (Fuse 6.2.1 R8)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_621_R9_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_621_R9_CAMEL_VERSION + " (Fuse 6.2.1 R9)");
		
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R0_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R0_CAMEL_VERSION + " (Fuse 6.3.0 R0)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R1_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R1_CAMEL_VERSION + " (Fuse 6.3.0 R1)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R2_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R2_CAMEL_VERSION + " (Fuse 6.3.0 R2)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R3_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R3_CAMEL_VERSION + " (Fuse 6.3.0 R3)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R4_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R4_CAMEL_VERSION + " (Fuse 6.3.0 R4)");
		mapping.put(CamelForFuse6ToBomMapper.FUSE_63_R5_CAMEL_VERSION, CamelForFuse6ToBomMapper.FUSE_63_R5_CAMEL_VERSION + " (Fuse 6.3.0 R5)");
		
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R1_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R1_CAMEL_VERSION + " (FIS 2.0 R1)");
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R2_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R2_CAMEL_VERSION + " (FIS 2.0 R2)");
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R3_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R3_CAMEL_VERSION + " (FIS 2.0 R3)");
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R4_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R4_CAMEL_VERSION + " (FIS 2.0 R4)");
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R5_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R5_CAMEL_VERSION + " (FIS 2.0 R5)");
		mapping.put(CamelForFIS20ToBomMapper.FIS_20_R6_CAMEL_VERSION, CamelForFIS20ToBomMapper.FIS_20_R6_CAMEL_VERSION + " (FIS 2.0 R6)");
		
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_700_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_700_CAMEL_VERSION + " (Fuse 7.0.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_701_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_701_CAMEL_VERSION + " (Fuse 7.0.1 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION + " (Fuse 7.1.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_720_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_720_CAMEL_VERSION + " (Fuse 7.2.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_730_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_730_CAMEL_VERSION + " (Fuse 7.3.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_731_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_731_CAMEL_VERSION + " (Fuse 7.3.1 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_740_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_740_CAMEL_VERSION + " (Fuse 7.4.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_750_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_750_CAMEL_VERSION + " (Fuse 7.5.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_760_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_760_CAMEL_VERSION + " (Fuse 7.6.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_770_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_770_CAMEL_VERSION + " (Fuse 7.7.0 GA)");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_780_CAMEL_VERSION, CamelForFuseOnOpenShiftToBomMapper.FUSE_780_CAMEL_VERSION + " (Fuse 7.8.0 GA)");
		
		return mapping;
	}
}
