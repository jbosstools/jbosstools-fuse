package org.fusesource.ide.fabric.messages;

import org.junit.Test;

public class XmlMessageFilesTest extends MessagesFilesTestSupport {

	@Test
	public void testXmlFile() throws Exception {
		assertFile("sample.xml", true);
	}

}
