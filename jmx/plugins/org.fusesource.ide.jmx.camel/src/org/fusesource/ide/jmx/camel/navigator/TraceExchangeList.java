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

package org.fusesource.ide.jmx.camel.navigator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.jmx.commons.messages.Exchange;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.INodeStatistics;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeList;
import org.fusesource.ide.jmx.commons.messages.NodeStatistics;
import org.xml.sax.SAXException;


public class TraceExchangeList implements ITraceExchangeList {

	private Map<String, ExchangeStepList> stepListMap = new HashMap<String, TraceExchangeList.ExchangeStepList>();
	private Map<String, NodeStatistics> nodeStatMap = new HashMap<String, NodeStatistics>();
	private List<ExchangeStepList> stepLists = new ArrayList<ExchangeStepList>();
	private List<IExchange> exchangeList = new ArrayList<IExchange>();
	private Map<IExchange, Long> uuidSet = new LinkedHashMap<IExchange, Long>();
	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public List<IExchange> getExchangeList() {
		return exchangeList;
	}

	public void addBackLogTraceMessages(List<BacklogTracerEventMessage> traceMessages) throws JAXBException, SAXException {
		for (BacklogTracerEventMessage traceMessage : traceMessages) {
			String exchangeId = traceMessage.getExchangeId();
			ExchangeStepList stepList = stepListMap.get(exchangeId);
			if (stepList == null) {
				stepList = new ExchangeStepList(exchangeId);
				stepLists.add(stepList);
				stepListMap.put(exchangeId, stepList);
			}
			IExchange exchange = stepList.addExchange(traceMessage);
			String toNode = traceMessage.getToNode();
			if (exchange != null && toNode != null) {
				INodeStatistics nodeStats = getNodeStats(toNode);
				nodeStats.addExchange(exchange);
			}
		}
		refreshExchangeList(stepLists);
	}
	
	public void addFabricTraceMessages(List<BacklogTracerEventMessage> traceMessages) throws JAXBException, SAXException {
		for (BacklogTracerEventMessage traceMessage : traceMessages) {
			String exchangeId = traceMessage.getExchangeId();
			ExchangeStepList stepList = stepListMap.get(exchangeId);
			if (stepList == null) {
				stepList = new ExchangeStepList(exchangeId);
				stepLists.add(stepList);
				stepListMap.put(exchangeId, stepList);
			}
			IExchange exchange = stepList.addExchange(traceMessage);
			String toNode = traceMessage.getToNode();
			if (exchange != null && toNode != null) {
				INodeStatistics nodeStats = getNodeStats(toNode);
				nodeStats.addExchange(exchange);
			}
		}
		refreshExchangeList(stepLists);
	}
	
	private void refreshExchangeList(List<ExchangeStepList> stepLists) {
		List<IExchange> temp = new ArrayList<IExchange>();
		for (ExchangeStepList stepList : stepLists) {
			temp.addAll(stepList.getExchangeList());
		}
		
		// sort so they are in natural sort order
		Collections.sort(temp);
		
		// then update the exchange index, if missing
		for (IExchange ie : temp) {
			if (ie instanceof Exchange) {
				Exchange exchange = (Exchange) ie;
				if (exchange.getExchangeIndex() == null) {
					ie.getIn().setExchangeIndex(counter.incrementAndGet());
				}
			}
		}

		exchangeList.clear();
		exchangeList.addAll(temp);
	}

	@Override
	public INodeStatistics getNodeStats(String toNode) {
		NodeStatistics nodeStats = nodeStatMap.get(toNode);
		if (nodeStats == null) {
			nodeStats = new NodeStatistics();
			nodeStatMap.put(toNode, nodeStats);
		}
		return nodeStats;
	}

	public class ExchangeStepList {
		private final String exchangeId;
		private List<IExchange> exchangeList = new ArrayList<IExchange>();
		private Long firstExchangeTimeMs;
		private long lastTime;

		public ExchangeStepList(String exchangeId) {
			this.exchangeId = exchangeId;
		}

		public List<IExchange> getExchangeList() {
			return exchangeList;
		}

		public IExchange addExchange(BacklogTracerEventMessage traceMessage) throws JAXBException, SAXException {
			IExchange answer = createExchange(traceMessage);
			if (answer != null) {
//				answer.getIn().setExchangeIndex(exchangeList.size() + 1);
				exchangeList.add(answer);
			}
			// remember the uuid of this iexchange
			uuidSet.put(answer, traceMessage.getUid());
			return answer;
		}
		
		public IExchange createExchange(BacklogTracerEventMessage traceMessage) throws JAXBException, SAXException {

			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(org.fusesource.ide.jmx.commons.backlogtracermessage.Message.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(traceMessage.getMessage(), writer);
			String xml = writer.toString();

			Exchange exchange = null;
			if (!Strings.isBlank(xml)) {
				exchange = Exchanges.unmarshalNoNamespaceXmlString(xml);
			}
			if (exchange == null) {
				exchange = new Exchange();
			}
			IMessage in = exchange.getIn();
			in.setToNode(traceMessage.getToNode());
			in.setUuid(traceMessage.getUid());
			Date timestamp = traceMessage.getTimestamp();
			if (timestamp != null) {
				in.setTimestamp(timestamp);
				long time = timestamp.getTime();
				long relative = 0;
				boolean first = false;
				if (firstExchangeTimeMs == null) {
					firstExchangeTimeMs = time;
					first = true;
				} else {
					relative = time - firstExchangeTimeMs;
				}
				long elapsed = relative - lastTime;
				lastTime = relative;
				in.setRelativeTime(relative);
				if (!first) {
					in.setElapsedTime(elapsed);
				}
			}
			exchange.setId(traceMessage.getExchangeId());
			
			return exchange;
		}
	}
}
