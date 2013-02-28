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

package org.fusesource.ide.fabric.views.logs;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.fusesource.ide.commons.Activator;


public class JsonHelper {

	public static String toJSON(ObjectMapper mapper, Object answer) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, answer);
			return writer.toString();
		} catch (IOException e) {
			Activator.getLogger().warning("Failed to marshal the object: " + answer + " to JSON: " + e, e);
			throw new IOException(e.getMessage());
		}
	}

}
