/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author apodhrad
 *
 */
public class SupportedCamelVersions {

	public static final String CAMEL_2_15_1_REDHAT_621084 = "2.15.1.redhat-621084";
	public static final String CAMEL_2_15_1_REDHAT_621117 = "2.15.1.redhat-621117";
	public static final String CAMEL_2_15_1_REDHAT_621159 = "2.15.1.redhat-621159";
	public static final String CAMEL_2_15_1_REDHAT_621169 = "2.15.1.redhat-621169";
	public static final String CAMEL_2_15_1_REDHAT_621186 = "2.15.1.redhat-621186";
	public static final String CAMEL_2_17_0_REDHAT_630187 = "2.17.0.redhat-630187";
	public static final String CAMEL_2_17_0_REDHAT_630224 = "2.17.0.redhat-630224";
	public static final String CAMEL_2_17_0_REDHAT_630254 = "2.17.0.redhat-630254";
	public static final String CAMEL_2_17_0_REDHAT_630262 = "2.17.0.redhat-630262";
	public static final String CAMEL_2_18_1_REDHAT_000012 = "2.18.1.redhat-000012";
	public static final String CAMEL_2_18_1_REDHAT_000015 = "2.18.1.redhat-000015";
	public static final String CAMEL_LATEST = CAMEL_2_18_1_REDHAT_000015;

	public static Collection<String> getCamelVersions() {
		Collection<String> versions = new ArrayList<>();
		versions.add(CAMEL_2_15_1_REDHAT_621084);
		versions.add(CAMEL_2_15_1_REDHAT_621117);
		versions.add(CAMEL_2_15_1_REDHAT_621159);
		versions.add(CAMEL_2_15_1_REDHAT_621169);
		versions.add(CAMEL_2_15_1_REDHAT_621186);
		versions.add(CAMEL_2_17_0_REDHAT_630187);
		versions.add(CAMEL_2_17_0_REDHAT_630224);
		versions.add(CAMEL_2_17_0_REDHAT_630254);
		versions.add(CAMEL_2_18_1_REDHAT_000012);
		versions.add(CAMEL_2_18_1_REDHAT_000015);
		return versions;
	}

}
