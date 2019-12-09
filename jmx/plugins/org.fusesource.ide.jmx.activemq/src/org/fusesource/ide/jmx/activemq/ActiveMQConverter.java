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

package org.fusesource.ide.jmx.activemq;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.fusesource.ide.jmx.commons.messages.Exchange;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.Message;


public class ActiveMQConverter {

	private Set<String> nestedProperties = new HashSet<String>(Arrays.asList(
			"BooleanProperties", "ByteProperties", "ShortProperties",
			"IntProperties", "LongProperties", "FloatProperties",
			"DoubleProperties", "StringProperties"));
	private Set<String> bodyKeys = new HashSet<>(Arrays.asList("Text",
			"Object", "Bytes", "Map"));
	private Set<String> ignoredKeys = new HashSet<>(Arrays.asList("PropertiesText"));

	public IExchange toExchange(Object object) {
		if (object instanceof CompositeData) {
			CompositeData data = (CompositeData) object;
			return toExchange(data);
		}
		return null;
	}

	public IExchange toExchange(CompositeData data) {
		Message message = new Message();
		Map<String, Object> headers = message.getHeaders();
		Set<String> keySet = data.getCompositeType().keySet();
		for (String key : keySet) {
			Object value = data.get(key);
			// ActiveMQJMXPlugin.getLogger().debug("Key: " + key + " value " + value);
			if (ignoredKeys.contains(key)) {
				continue;
			}
			boolean nestedProperty = nestedProperties.contains(key);
			if (nestedProperty && value instanceof TabularData) {
				TabularData td = (TabularData) value;
				Map<String, Object> map = toMap(td);
				putAllNonNull(headers, map);
			} else if (nestedProperty && value instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) value;
				putAllNonNull(headers, map);
			} else if (bodyKeys.contains(key)) {
				message.setBody(value);
			} else if (value != null) {
				headers.put(key, value);
			}
		}
		return new Exchange(message);
	}

	protected void putAllNonNull(Map<String, Object> headers,
			Map<String, Object> map) {
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			Object value = entry.getValue();
			if (value != null) {
				headers.put(entry.getKey(), value);
			}
		}
	}

	private Map<String, Object> toMap(TabularData td) {
		HashMap<String, Object> answer = new HashMap<>();
		Collection<?> rows = td.values();
		for (Object row : rows) {
			if (row instanceof CompositeData) {
				CompositeData cd = (CompositeData) row;
				// lets assume its just a key & value
				Object key = cd.get("key");
				if (key != null) {
					Object value = cd.get("value");
					//ActiveMQJMXPlugin.getLogger().debug("Got key " + key + " value " + value);
					answer.put(key.toString(), value);
				}
			}
		}
		return answer;
	}
}
