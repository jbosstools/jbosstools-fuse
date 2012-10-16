package org.fusesource.ide.fabric.views.logs;

import java.util.List;

import org.fusesource.insight.log.LogFilter;

public interface LogContext {

	public void addLogResults(List<LogEventBean> events);

	public abstract LogFilter getLogFilter();

}
