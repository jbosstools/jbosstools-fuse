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
package org.fusesource.ide.syndesis.extensions.core.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author lhein
 */
@RunWith(Parameterized.class)
public class SyndesisExtensionJSONStructuralLoadingTest {

	@Parameters(name="{0}")
	public static Collection<String> data() {
		return Arrays.asList(	"https://raw.githubusercontent.com/syndesisio/syndesis-extensions/master/syndesis-connector-irc/src/main/resources/META-INF/syndesis/syndesis-extension-definition.json",
								"https://raw.githubusercontent.com/syndesisio/syndesis-extensions/master/syndesis-connector-telegram/src/main/resources/META-INF/syndesis/syndesis-extension-definition.json",
								"https://raw.githubusercontent.com/syndesisio/syndesis-extensions/master/syndesis-connector-timer/src/main/resources/META-INF/syndesis/syndesis-extension-definition.json");
	}
	
	@Parameter
	public String jsonUrl;
	
	@Test
	public void testJSONLoading() throws IOException {
		URI uri = URI.create(jsonUrl);
		try (InputStream is = uri.toURL().openStream()) {
			SyndesisExtension extension = SyndesisExtension.getJSONFactoryInstance(is);
			assertThat(extension).as("URL " + jsonUrl + " is an invalid source file. Structure might have changed upstream!").isNotNull();
		}		
	}
}
