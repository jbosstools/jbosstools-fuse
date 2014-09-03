/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model;

import static org.junit.Assert.assertEquals;

import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.generated.Catch;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.Finally;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.Try;
import org.fusesource.ide.camel.model.generated.When;
import org.junit.Test;


public class AddOrderingTest extends ModelTestSupport {

	@Test
	public void testContentBasedRouter() throws Exception {
		Endpoint ep1 = new Endpoint();
		ep1.setUri("seda:a");
		Endpoint ep2 = new Endpoint();
		ep2.setUri("seda:b");
		Endpoint ep3 = new Endpoint();
		ep3.setUri("seda:c");
		Endpoint ep4 = new Endpoint();
		ep4.setUri("seda:d");

		Choice choice = new Choice();
		ep1.addTargetNode(choice);

		choice.addTargetNode(ep2);
		Otherwise otherwise = new Otherwise();
		choice.addTargetNode(otherwise);
		When when = new When();
		choice.addTargetNode(when);
		choice.addTargetNode(ep3);

		assertOutput(choice, 0, When.class);
		assertOutput(choice, 1, Otherwise.class);
		Endpoint e2 = assertOutput(choice, 2, Endpoint.class);
		assertEquals("e2", "seda:b", e2.getUri());

		Endpoint e3 = assertOutput(choice, 3, Endpoint.class);
		assertEquals("e3", "seda:c", e3.getUri());
	}
	

	@Test
	public void testTryCatch() throws Exception {
		Endpoint ep1 = new Endpoint();
		ep1.setUri("seda:a");
		Endpoint ep2 = new Endpoint();
		ep2.setUri("seda:b");
		Endpoint ep3 = new Endpoint();
		ep3.setUri("seda:c");
		Endpoint ep4 = new Endpoint();
		ep4.setUri("seda:d");

		Try t = new Try();
		ep1.addTargetNode(t);

		Finally fin = new Finally();
		t.addTargetNode(fin);
		Catch c = new Catch();
		t.addTargetNode(c);
		t.addTargetNode(ep2);
		t.addTargetNode(ep3);

		Endpoint e2 = assertOutput(t, 0, Endpoint.class);
		assertEquals("e2", "seda:b", e2.getUri());

		Endpoint e3 = assertOutput(t, 1, Endpoint.class);
		assertEquals("e3", "seda:c", e3.getUri());

		assertOutput(t, 2, Catch.class);
		assertOutput(t, 3, Finally.class);
	}
}
