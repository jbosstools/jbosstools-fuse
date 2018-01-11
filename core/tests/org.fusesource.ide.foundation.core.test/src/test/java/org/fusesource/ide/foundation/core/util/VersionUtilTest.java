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
package org.fusesource.ide.foundation.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class VersionUtilTest {

	@Test
	public void testIsStrictlyGreaterThan_returnFalse() throws Exception {
		assertThat(new VersionUtil().isStrictlyGreaterThan("2.20.0", "2.20.1")).isFalse();
	}

	@Test
	public void testIsStrictlyGreaterThan_returnFalseForSameValue() throws Exception {
		assertThat(new VersionUtil().isStrictlyGreaterThan("2.20.0", "2.20.0")).isFalse();
	}

	@Test
	public void testIsStrictlyGreaterThan_returnTrueForStrictlyGreaterVersion() throws Exception {
		assertThat(new VersionUtil().isStrictlyGreaterThan("2.20.1", "2.20.0")).isTrue();
	}

	@Test
	public void testIsGreaterThan_returnFalse() throws Exception {
		assertThat(new VersionUtil().isGreaterThan("2.20.0", "2.20.1")).isFalse();
	}

	@Test
	public void testIsGreaterThan_returnTrueForSameValue() throws Exception {
		assertThat(new VersionUtil().isGreaterThan("2.20.0", "2.20.0")).isTrue();
	}

	@Test
	public void testIsGreaterThan_returnTrueForStrictlyGreaterVersion() throws Exception {
		assertThat(new VersionUtil().isGreaterThan("2.20.1", "2.20.0")).isTrue();
	}
	
	@Test
	public void testIsStrictlyLowerThan2200() throws Exception {
		assertThat(new VersionUtil().isStrictlyLowerThan2200("2.19.9")).isTrue();
	}
	
	@Test
	public void testIsStrictlyLowerThan2200_returnFalseFor2200() throws Exception {
		assertThat(new VersionUtil().isStrictlyLowerThan2200("2.20.0")).isFalse();
	}
	
	@Test
	public void testIsStrictlyLowerThan2200_returnFalseForSuperiorVersion() throws Exception {
		assertThat(new VersionUtil().isStrictlyLowerThan2200("2.20.1")).isFalse();
	}

}
