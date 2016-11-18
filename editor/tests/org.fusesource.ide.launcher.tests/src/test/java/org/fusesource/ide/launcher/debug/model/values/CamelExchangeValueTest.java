/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model.values;

import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CamelExchangeValueTest {

	@Test
	public void testCreationWithMessageWithoutTimestamp() throws Exception {
		BacklogTracerEventMessage exchange = new BacklogTracerEventMessage();
		exchange.setExchangeId("TestExchangeId");
		CamelExchangeValue camelExchangeValue = new CamelExchangeValue(null, exchange, String.class);

		assertThat(camelExchangeValue.hasVariables()).isTrue();
		assertThat(camelExchangeValue.getVariables()).hasSize(5);
	}

}
