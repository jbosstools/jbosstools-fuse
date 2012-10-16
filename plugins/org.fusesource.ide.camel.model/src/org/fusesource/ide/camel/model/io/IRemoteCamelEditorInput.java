package org.fusesource.ide.camel.model.io;

import java.io.IOException;

public interface IRemoteCamelEditorInput {
	public String getUriText();
	public String getXml() throws IOException;

}
