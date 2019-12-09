/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.projecttemplates.actions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SwitchCamelVersionActionTest {
	
	@Parameters(name = "from {0} to {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 { "2.19.3", "2.20.0", true},
                 { "2.20.0", "2.20.1", false},
                 { "2.19.3", "2.18.1", false},
                 { "2.20.1", "2.19.3", true},
           });
    }
    
    @Parameter
    public String oldVersion;

    @Parameter(1)
    public String newversion;
    
    @Parameter(2)
    public boolean shouldWarn;

	@Test
	public void testShouldWarnAboutDozerAPIBreak() throws Exception {
		assertThat(new SwitchCamelVersionAction().shouldWarnAboutDozerAPIBreak(oldVersion, newversion)).isEqualTo(shouldWarn);
	}

}
