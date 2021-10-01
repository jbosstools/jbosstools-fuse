/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * 
 * @author apodhrad
 *
 */
public class SupportedCamelVersions {

	public static final String CAMEL_2_15_1_REDHAT_621084 = "2.15.1.redhat-621084";
	public static final String CAMEL_2_15_1_REDHAT_621090 = "2.15.1.redhat-621090";
	public static final String CAMEL_2_15_1_REDHAT_621107 = "2.15.1.redhat-621107";
	public static final String CAMEL_2_15_1_REDHAT_621117 = "2.15.1.redhat-621117";
	public static final String CAMEL_2_15_1_REDHAT_621159 = "2.15.1.redhat-621159";
	public static final String CAMEL_2_15_1_REDHAT_621169 = "2.15.1.redhat-621169";
	public static final String CAMEL_2_15_1_REDHAT_621177 = "2.15.1.redhat-621177";
	public static final String CAMEL_2_15_1_REDHAT_621186 = "2.15.1.redhat-621186";
	public static final String CAMEL_2_15_1_REDHAT_621211 = "2.15.1.redhat-621211";
	public static final String CAMEL_2_15_1_REDHAT_621216 = "2.15.1.redhat-621216";

	public static final String CAMEL_2_17_0_REDHAT_630187 = "2.17.0.redhat-630187";
	public static final String CAMEL_2_17_0_REDHAT_630224 = "2.17.0.redhat-630224";
	public static final String CAMEL_2_17_0_REDHAT_630254 = "2.17.0.redhat-630254";
	public static final String CAMEL_2_17_0_REDHAT_630262 = "2.17.0.redhat-630262";
	public static final String CAMEL_2_17_0_REDHAT_630283 = "2.17.0.redhat-630283";
	public static final String CAMEL_2_17_0_REDHAT_630310 = "2.17.0.redhat-630310";
	public static final String CAMEL_2_17_0_REDHAT_630329 = "2.17.0.redhat-630329";
	public static final String CAMEL_2_17_0_REDHAT_630343 = "2.17.0.redhat-630343";
	public static final String CAMEL_2_17_0_REDHAT_630347 = "2.17.0.redhat-630347";
	public static final String CAMEL_2_17_0_REDHAT_630356 = "2.17.0.redhat-630356";
	public static final String CAMEL_2_17_0_REDHAT_630371 = "2.17.0.redhat-630371";
	public static final String CAMEL_2_17_0_REDHAT_630377 = "2.17.0.redhat-630377";

	public static final String CAMEL_2_18_1_REDHAT_000012 = "2.18.1.redhat-000012";
	public static final String CAMEL_2_18_1_REDHAT_000015 = "2.18.1.redhat-000015";

	public static final String CAMEL_2_18_1_REDHAT_000021 = "2.18.1.redhat-000021";
	public static final String CAMEL_2_18_1_REDHAT_000024 = "2.18.1.redhat-000024";
	public static final String CAMEL_2_18_1_REDHAT_000025 = "2.18.1.redhat-000025";
	public static final String CAMEL_2_18_1_REDHAT_000026 = "2.18.1.redhat-000026";

	public static final String CAMEL_2_21_0_FUSE_000077_REDHAT_1 = "2.21.0.fuse-000077-redhat-1";
	public static final String CAMEL_2_21_0_FUSE_000112_REDHAT_3 = "2.21.0.fuse-000112-redhat-3";
	public static final String CAMEL_2_21_0_FUSE_710018_REDHAT_00001 = "2.21.0.fuse-710018-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_720050_REDHAT_00001 = "2.21.0.fuse-720050-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_730078_REDHAT_00001 = "2.21.0.fuse-730078-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_731003_REDHAT_00003 = "2.21.0.fuse-731003-redhat-00003";
	public static final String CAMEL_2_21_0_FUSE_740039_REDHAT_00001 = "2.21.0.fuse-740039-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_750033_REDHAT_00001 = "2.21.0.fuse-750033-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_760027_REDHAT_00001 = "2.21.0.fuse-760027-redhat-00001";
	public static final String CAMEL_2_21_0_FUSE_770013_REDHAT_00001 = "2.21.0.fuse-770013-redhat-00001";
	public static final String CAMEL_2_23_2_FUSE_770010_REDHAT_00001 = "2.23.2.fuse-770010-redhat-00001";

	public static final String CAMEL_2_21_0_FUSE_770 = "2.21.0.fuse-770013-redhat-00001";

	public static final String CAMEL_LATEST_FUSE_6_2 = CAMEL_2_15_1_REDHAT_621216;
	public static final String CAMEL_LATEST_FUSE_6_3 = CAMEL_2_17_0_REDHAT_630356;
	public static final String CAMEL_LATEST_FUSE_7 = CAMEL_2_23_2_FUSE_770010_REDHAT_00001;
	public static final String CAMEL_LATEST_FIS = CAMEL_2_18_1_REDHAT_000026;
	
	public static final String CAMEL_LATEST = CAMEL_2_17_0_REDHAT_630377;

	public static Collection<String> getCamelVersions() {
		Collection<String> versions = new ArrayList<>();
		versions.add(CAMEL_2_15_1_REDHAT_621084);
		versions.add(CAMEL_2_15_1_REDHAT_621090);
		versions.add(CAMEL_2_15_1_REDHAT_621107);
		versions.add(CAMEL_2_15_1_REDHAT_621117);
		versions.add(CAMEL_2_15_1_REDHAT_621159);
		versions.add(CAMEL_2_15_1_REDHAT_621169);
		versions.add(CAMEL_2_15_1_REDHAT_621177);
		versions.add(CAMEL_2_15_1_REDHAT_621186);
		versions.add(CAMEL_2_15_1_REDHAT_621211);
		versions.add(CAMEL_2_15_1_REDHAT_621216);
		versions.add(CAMEL_2_17_0_REDHAT_630187);
		versions.add(CAMEL_2_17_0_REDHAT_630224);
		versions.add(CAMEL_2_17_0_REDHAT_630254);
		versions.add(CAMEL_2_17_0_REDHAT_630262);
		versions.add(CAMEL_2_17_0_REDHAT_630283);
		versions.add(CAMEL_2_17_0_REDHAT_630310);
		versions.add(CAMEL_2_17_0_REDHAT_630329);
		versions.add(CAMEL_2_17_0_REDHAT_630343);
		versions.add(CAMEL_2_17_0_REDHAT_630347);
		versions.add(CAMEL_2_17_0_REDHAT_630356);
		versions.add(CAMEL_2_17_0_REDHAT_630371);
		versions.add(CAMEL_2_17_0_REDHAT_630377);
		versions.add(CAMEL_2_18_1_REDHAT_000012);
		versions.add(CAMEL_2_18_1_REDHAT_000015);
		versions.add(CAMEL_2_18_1_REDHAT_000021);
		versions.add(CAMEL_2_18_1_REDHAT_000024);
		versions.add(CAMEL_2_18_1_REDHAT_000025);
		versions.add(CAMEL_2_18_1_REDHAT_000026);
//		versions.add(CAMEL_2_21_0_FUSE_000077_REDHAT_1);
//		versions.add(CAMEL_2_21_0_FUSE_000112_REDHAT_3);
//		versions.add(CAMEL_2_21_0_FUSE_710018_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_720050_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_730078_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_731003_REDHAT_00003);
//		versions.add(CAMEL_2_21_0_FUSE_740039_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_750033_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_760027_REDHAT_00001);
//		versions.add(CAMEL_2_21_0_FUSE_770013_REDHAT_00001);
//		versions.add(CAMEL_2_23_2_FUSE_770010_REDHAT_00001);
		return versions;
	}

	public static HashMap<String, String> getCamelVersionsWithLabels() {
		HashMap<String, String> versions = new HashMap<String, String>();
		versions.put(CAMEL_2_15_1_REDHAT_621084, CAMEL_2_15_1_REDHAT_621084 + " (Fuse 6.2.1 GA)");
		versions.put(CAMEL_2_15_1_REDHAT_621090, CAMEL_2_15_1_REDHAT_621090 + " (Fuse 6.2.1 R1)");
		versions.put(CAMEL_2_15_1_REDHAT_621107, CAMEL_2_15_1_REDHAT_621107 + " (Fuse 6.2.1 R2)");
		versions.put(CAMEL_2_15_1_REDHAT_621117, CAMEL_2_15_1_REDHAT_621117 + " (Fuse 6.2.1 R3)");
		versions.put(CAMEL_2_15_1_REDHAT_621159, CAMEL_2_15_1_REDHAT_621159 + " (Fuse 6.2.1 R4)");
		versions.put(CAMEL_2_15_1_REDHAT_621169, CAMEL_2_15_1_REDHAT_621169 + " (Fuse 6.2.1 R5)");
		versions.put(CAMEL_2_15_1_REDHAT_621177, CAMEL_2_15_1_REDHAT_621177 + " (Fuse 6.2.1 R6)");
		versions.put(CAMEL_2_15_1_REDHAT_621186, CAMEL_2_15_1_REDHAT_621186 + " (Fuse 6.2.1 R7)");
		versions.put(CAMEL_2_15_1_REDHAT_621211, CAMEL_2_15_1_REDHAT_621211 + " (Fuse 6.2.1 R8)");
		versions.put(CAMEL_2_15_1_REDHAT_621216, CAMEL_2_15_1_REDHAT_621216 + " (Fuse 6.2.1 R9)");

		versions.put(CAMEL_2_17_0_REDHAT_630187, CAMEL_2_17_0_REDHAT_630187 + " (Fuse 6.3.0 GA)");
		versions.put(CAMEL_2_17_0_REDHAT_630224, CAMEL_2_17_0_REDHAT_630224 + " (Fuse 6.3.0 R1)");
		versions.put(CAMEL_2_17_0_REDHAT_630254, CAMEL_2_17_0_REDHAT_630254 + " (Fuse 6.3.0 R2)");
		versions.put(CAMEL_2_17_0_REDHAT_630262, CAMEL_2_17_0_REDHAT_630262 + " (Fuse 6.3.0 R3)");
		versions.put(CAMEL_2_17_0_REDHAT_630283, CAMEL_2_17_0_REDHAT_630283 + " (Fuse 6.3.0 R4)");
		versions.put(CAMEL_2_17_0_REDHAT_630310, CAMEL_2_17_0_REDHAT_630310 + " (Fuse 6.3.0 R5)");
		versions.put(CAMEL_2_17_0_REDHAT_630329, CAMEL_2_17_0_REDHAT_630329 + " (Fuse 6.3.0 R6)");
		versions.put(CAMEL_2_17_0_REDHAT_630343, CAMEL_2_17_0_REDHAT_630343 + " (Fuse 6.3.0 R7)");
		versions.put(CAMEL_2_17_0_REDHAT_630347, CAMEL_2_17_0_REDHAT_630347 + " (Fuse 6.3.0 R8)");
		versions.put(CAMEL_2_17_0_REDHAT_630356, CAMEL_2_17_0_REDHAT_630356 + " (Fuse 6.3.0 R9)");
		versions.put(CAMEL_2_17_0_REDHAT_630371, CAMEL_2_17_0_REDHAT_630371 + " (Fuse 6.3.0 R10)");
		versions.put(CAMEL_2_17_0_REDHAT_630377, CAMEL_2_17_0_REDHAT_630377 + " (Fuse 6.3.0 R11)");

		versions.put(CAMEL_2_18_1_REDHAT_000012, CAMEL_2_18_1_REDHAT_000012 + " (FIS 2.0 R1)");
		versions.put(CAMEL_2_18_1_REDHAT_000015, CAMEL_2_18_1_REDHAT_000015 + " (FIS 2.0 R2)");
		versions.put(CAMEL_2_18_1_REDHAT_000021, CAMEL_2_18_1_REDHAT_000021 + " (FIS 2.0 R3)");
		versions.put(CAMEL_2_18_1_REDHAT_000024, CAMEL_2_18_1_REDHAT_000024 + " (FIS 2.0 R4)");
		versions.put(CAMEL_2_18_1_REDHAT_000025, CAMEL_2_18_1_REDHAT_000025 + " (FIS 2.0 R5)");
		versions.put(CAMEL_2_18_1_REDHAT_000026, CAMEL_2_18_1_REDHAT_000026 + " (FIS 2.0 R6)");

		versions.put(CAMEL_2_21_0_FUSE_000077_REDHAT_1, CAMEL_2_21_0_FUSE_000077_REDHAT_1 + " (Fuse 7.0.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_000112_REDHAT_3, CAMEL_2_21_0_FUSE_000112_REDHAT_3 + " (Fuse 7.0.1 GA)");
		versions.put(CAMEL_2_21_0_FUSE_710018_REDHAT_00001, CAMEL_2_21_0_FUSE_710018_REDHAT_00001 + " (Fuse 7.1.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_720050_REDHAT_00001, CAMEL_2_21_0_FUSE_720050_REDHAT_00001 + " (Fuse 7.2.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_730078_REDHAT_00001, CAMEL_2_21_0_FUSE_730078_REDHAT_00001 + " (Fuse 7.3.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_731003_REDHAT_00003, CAMEL_2_21_0_FUSE_731003_REDHAT_00003 + " (Fuse 7.3.1 GA)");
		versions.put(CAMEL_2_21_0_FUSE_740039_REDHAT_00001, CAMEL_2_21_0_FUSE_740039_REDHAT_00001 + " (Fuse 7.4.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_750033_REDHAT_00001, CAMEL_2_21_0_FUSE_750033_REDHAT_00001 + " (Fuse 7.5.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_760027_REDHAT_00001, CAMEL_2_21_0_FUSE_760027_REDHAT_00001 + " (Fuse 7.6.0 GA)");
		versions.put(CAMEL_2_21_0_FUSE_770013_REDHAT_00001, CAMEL_2_21_0_FUSE_770013_REDHAT_00001 + " (Fuse 7.7.0 GA)");
		versions.put(CAMEL_2_23_2_FUSE_770010_REDHAT_00001, CAMEL_2_23_2_FUSE_770010_REDHAT_00001 + " (Fuse 7.7.0 GA)");

		return versions;
	}

}
