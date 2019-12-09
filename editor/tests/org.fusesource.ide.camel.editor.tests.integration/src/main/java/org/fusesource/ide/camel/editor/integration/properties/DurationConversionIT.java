/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.editor.integration.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author lhein
 */
@RunWith(Parameterized.class)
public class DurationConversionIT {
	
	@Parameters
    public static Collection<String> durations() {
        return Arrays.asList(	
        						// valid durations
        						"1h10m30s",
        						"1h10m",
        						"1h",
        						"10m30s",
        						"10m",
        						"30s",
        						"1h30s"        						
        					);
    }
	
	@Parameter
	public String duration;
	
	private ICamelManagerService svc = null;
	
	@Before
	public void setup() {
		this.svc = CamelServiceManagerUtil.getManagerService();
		assertNotNull("Camel Catalog Service was null!", this.svc);
	}
	
	@Test
	public void testInvalidDurationConversion() throws Exception {
		validateDuration(duration);
	}
	
	private void validateDuration(String duration) {
		try {
			this.svc.durationToMillis(duration);
		} catch (IllegalArgumentException ex) {
			fail("Duration " + duration + " failed on conversion...");
		}
	}
}
