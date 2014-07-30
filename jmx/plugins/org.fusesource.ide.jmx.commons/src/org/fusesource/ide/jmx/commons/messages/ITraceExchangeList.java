package org.fusesource.ide.jmx.commons.messages;

import java.util.List;

public interface ITraceExchangeList extends NodeStatisticsContainer {

	public abstract List<IExchange> getExchangeList();

}
