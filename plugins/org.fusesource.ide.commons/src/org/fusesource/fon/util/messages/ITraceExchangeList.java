package org.fusesource.fon.util.messages;

import java.util.List;

public interface ITraceExchangeList extends NodeStatisticsContainer {

	public abstract List<IExchange> getExchangeList();

}
