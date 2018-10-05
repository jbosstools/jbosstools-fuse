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
package org.fusesource.ide.camel.model.service.core.util.versionmapper;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class CamelForFuse6ToBomMapper extends OnlineVersionMapper {

	public static final String CAMEL_TO_BOM_MAPPING_PROPERTY = "org.jboss.tools.fuse.camel2bom.url";
	private static final String CAMEL_TO_BOM_MAPPING_DEFAULT_URL = BASE_REPO_CONFIG_URI+"camel2bom.properties";

	static final String FUSE_621_R0_CAMEL_VERSION = "2.15.1.redhat-621084";
	static final String FUSE_621_R1_CAMEL_VERSION = "2.15.1.redhat-621090";
	static final String FUSE_621_R2_CAMEL_VERSION = "2.15.1.redhat-621107";
	static final String FUSE_621_R3_CAMEL_VERSION = "2.15.1.redhat-621117";
	static final String FUSE_621_R4_CAMEL_VERSION = "2.15.1.redhat-621159";
	static final String FUSE_621_R5_CAMEL_VERSION = "2.15.1.redhat-621169";
	static final String FUSE_621_R6_CAMEL_VERSION = "2.15.1.redhat-621177";
	static final String FUSE_621_R7_CAMEL_VERSION = "2.15.1.redhat-621186";
	static final String FUSE_621_R8_CAMEL_VERSION = "2.15.1.redhat-621211";
	public static final String FUSE_621_R9_CAMEL_VERSION = "2.15.1.redhat-621216";

	private static final String FUSE_621_R0_BOM_VERSION = "6.2.1.redhat-084";
	private static final String FUSE_621_R1_BOM_VERSION = "6.2.1.redhat-090";
	private static final String FUSE_621_R2_BOM_VERSION = "6.2.1.redhat-107";
	private static final String FUSE_621_R3_BOM_VERSION = "6.2.1.redhat-117";
	private static final String FUSE_621_R4_BOM_VERSION = "6.2.1.redhat-159";
	private static final String FUSE_621_R5_BOM_VERSION = "6.2.1.redhat-169";
	private static final String FUSE_621_R6_BOM_VERSION = "6.2.1.redhat-177";
	private static final String FUSE_621_R7_BOM_VERSION = "6.2.1.redhat-186";
	private static final String FUSE_621_R8_BOM_VERSION = "6.2.1.redhat-211";
	private static final String FUSE_621_R9_BOM_VERSION = "6.2.1.redhat-216";

	static final String FUSE_63_R0_CAMEL_VERSION = "2.17.0.redhat-630187";
	static final String FUSE_63_R1_CAMEL_VERSION = "2.17.0.redhat-630224";
	static final String FUSE_63_R2_CAMEL_VERSION = "2.17.0.redhat-630254";
	static final String FUSE_63_R3_CAMEL_VERSION = "2.17.0.redhat-630262";
	public static final String FUSE_63_R4_CAMEL_VERSION = "2.17.0.redhat-630283";
	public static final String FUSE_63_R5_CAMEL_VERSION = "2.17.0.redhat-630310";
	public static final String FUSE_63_R6_CAMEL_VERSION = "2.17.0.redhat-630329";
	public static final String FUSE_63_R7_CAMEL_VERSION = "2.17.0.redhat-630343";
	public static final String FUSE_63_R8_CAMEL_VERSION = "2.17.0.redhat-630347";
	public static final String FUSE_63_R9_CAMEL_VERSION = "2.17.0.redhat-630356";

	private static final String FUSE_63_R0_BOM_VERSION = "6.3.0.redhat-187";
	private static final String FUSE_63_R1_BOM_VERSION = "6.3.0.redhat-224";
	private static final String FUSE_63_R2_BOM_VERSION = "6.3.0.redhat-254";
	private static final String FUSE_63_R3_BOM_VERSION = "6.3.0.redhat-262";
	public static final String FUSE_63_R4_BOM_VERSION = "6.3.0.redhat-283";
	public static final String FUSE_63_R5_BOM_VERSION = "6.3.0.redhat-310";
	private static final String FUSE_63_R6_BOM_VERSION = "6.3.0.redhat-329";
	private static final String FUSE_63_R7_BOM_VERSION = "6.3.0.redhat-343";
	private static final String FUSE_63_R8_BOM_VERSION = "6.3.0.redhat-347";
	private static final String FUSE_63_R9_BOM_VERSION = "6.3.0.redhat-356";

	public CamelForFuse6ToBomMapper() {
		super(CAMEL_TO_BOM_MAPPING_PROPERTY, CAMEL_TO_BOM_MAPPING_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(FUSE_621_R0_CAMEL_VERSION, FUSE_621_R0_BOM_VERSION);
		mapping.put(FUSE_621_R1_CAMEL_VERSION, FUSE_621_R1_BOM_VERSION);
		mapping.put(FUSE_621_R2_CAMEL_VERSION, FUSE_621_R2_BOM_VERSION);
		mapping.put(FUSE_621_R3_CAMEL_VERSION, FUSE_621_R3_BOM_VERSION);
		mapping.put(FUSE_621_R4_CAMEL_VERSION, FUSE_621_R4_BOM_VERSION);
		mapping.put(FUSE_621_R5_CAMEL_VERSION, FUSE_621_R5_BOM_VERSION);
		mapping.put(FUSE_621_R6_CAMEL_VERSION, FUSE_621_R6_BOM_VERSION);
		mapping.put(FUSE_621_R7_CAMEL_VERSION, FUSE_621_R7_BOM_VERSION);
		mapping.put(FUSE_621_R8_CAMEL_VERSION, FUSE_621_R8_BOM_VERSION);
		mapping.put(FUSE_621_R9_CAMEL_VERSION, FUSE_621_R9_BOM_VERSION);
		mapping.put(FUSE_63_R0_CAMEL_VERSION, FUSE_63_R0_BOM_VERSION);
		mapping.put(FUSE_63_R1_CAMEL_VERSION, FUSE_63_R1_BOM_VERSION);
		mapping.put(FUSE_63_R2_CAMEL_VERSION, FUSE_63_R2_BOM_VERSION);
		mapping.put(FUSE_63_R3_CAMEL_VERSION, FUSE_63_R3_BOM_VERSION);
		mapping.put(FUSE_63_R4_CAMEL_VERSION, FUSE_63_R4_BOM_VERSION);
		mapping.put(FUSE_63_R5_CAMEL_VERSION, FUSE_63_R5_BOM_VERSION);
		mapping.put(FUSE_63_R6_CAMEL_VERSION, FUSE_63_R6_BOM_VERSION);
		mapping.put(FUSE_63_R7_CAMEL_VERSION, FUSE_63_R7_BOM_VERSION);
		mapping.put(FUSE_63_R8_CAMEL_VERSION, FUSE_63_R8_BOM_VERSION);
		mapping.put(FUSE_63_R9_CAMEL_VERSION, FUSE_63_R9_BOM_VERSION);
		return mapping;
	}

}
