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
