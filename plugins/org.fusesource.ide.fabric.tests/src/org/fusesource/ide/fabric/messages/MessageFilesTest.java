package org.fusesource.ide.fabric.messages;

import org.junit.Test;

public class MessageFilesTest extends MessagesFilesTestSupport {

	@Test
	public void testTextFile() throws Exception {
		assertFile("sample.txt", true);
	}

	@Test
	public void testBinaryFile() throws Exception {
		assertFile("sample.gif", false);
	}

}
