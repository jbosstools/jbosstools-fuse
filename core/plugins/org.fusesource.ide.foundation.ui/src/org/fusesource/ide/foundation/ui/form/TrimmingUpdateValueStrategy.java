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

package org.fusesource.ide.foundation.ui.form;

import org.eclipse.core.databinding.UpdateValueStrategy;

public class TrimmingUpdateValueStrategy extends UpdateValueStrategy {
	public TrimmingUpdateValueStrategy(int updatePolicy) {
		super(updatePolicy);
	}

	@Override
	public Object convert(Object value) {
		Object answer = super.convert(value);
		if (answer instanceof String) {
			// remove decimal points on long values (see ECLIPSE-198)
			if (value instanceof Long) {
				answer = ((String) answer).replaceAll("\\.", "");
			}
			return answer.toString().trim();
		} 
		return answer;
	}
}