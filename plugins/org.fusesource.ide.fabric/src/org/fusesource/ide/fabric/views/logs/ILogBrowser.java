package org.fusesource.ide.fabric.views.logs;

import java.io.IOException;

public interface ILogBrowser {

	void queryLogs(LogContext logs, boolean filterChanged) throws IOException;

}
