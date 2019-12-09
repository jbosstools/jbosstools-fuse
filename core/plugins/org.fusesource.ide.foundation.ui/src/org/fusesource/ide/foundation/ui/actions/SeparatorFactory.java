/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.actions;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.action.Separator;

/**
 * Creates {@link Separator} objects with unique IDs for a view
 */
public class SeparatorFactory {
	private final String prefix;
	private final AtomicInteger counter = new AtomicInteger();

	public SeparatorFactory(String ownerViewId) {
		this.prefix = ownerViewId + ".separator.";
	}

	@Override
	public String toString() {
		return "SeparatorFactory(" + prefix + "n)";
	}



	public Separator createSeparator() {
		Separator answer = new Separator();
		answer.setId(prefix + counter.incrementAndGet());
		return answer;
	}

}
