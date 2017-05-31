/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.qe.reddeer.component;

import java.util.ArrayList;
import java.util.List;

public class CamelComponents {

	public static List<CamelComponent> getAll() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.addAll(getEndpoints());
		list.addAll(getRouting());
		list.addAll(getControlFlow());
		list.addAll(getTransformation());
		list.addAll(getMiscellaneous());
		return list;
	}

	public static List<CamelComponent> getEndpoints() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.add(new ActiveMQ());
		list.add(new Atom());
		list.add(new Bean());
		list.add(new ControlBus());
		list.add(new CXF());
		list.add(new CXFRS());
		list.add(new Direct());
		list.add(new DirectVM());
		list.add(new EJB());
		list.add(new File());
		list.add(new FTP());
		list.add(new FTPS());
		list.add(new IMAP());
		list.add(new IMAPS());
		list.add(new JDBC());
		list.add(new JGroups());
		list.add(new JMS());
		list.add(new Language());
		list.add(new Linkedin());
		list.add(new Log());
		list.add(new Mina2());
		list.add(new MQTT());
		list.add(new MVEL());
		list.add(new Netty());
		list.add(new NettyHTTP());
		list.add(new Netty4());
		list.add(new Netty4HTTP());
		list.add(new POP3());
		list.add(new POP3S());
		list.add(new Process());
		list.add(new Quartz());
		list.add(new Quartz2());
		list.add(new Restlet());
		list.add(new RSS());
		list.add(new Salesforce());
		list.add(new SAPNetWeaver());
		list.add(new Scheduler());
		list.add(new SEDA());
		list.add(new Servlet());
		list.add(new SFTP());
		list.add(new SMTP());
		list.add(new SMTPS());
		list.add(new SQL());
		list.add(new Timer());
		list.add(new VM());
		list.add(new XQuery());
		list.add(new XSLT());
		return list;
	}

	public static List<CamelComponent> getRouting() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.add(new Aggregate());
		list.add(new Choice());
		list.add(new Filter());
		list.add(new IdempotentConsumer());
		list.add(new LoadBalance());
		list.add(new Multicast());
		list.add(new Otherwise());
		list.add(new Pipeline());
		list.add(new RecipientList());
		list.add(new Resequence());
		list.add(new Route());
		list.add(new RoutingSlip());
		list.add(new Sort());
		list.add(new Split());
		list.add(new When());
		list.add(new WireTap());
		return list;
	}

	public static List<CamelComponent> getControlFlow() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.add(new Delay());
		list.add(new DoCatch());
		list.add(new DoFinally());
		list.add(new Intercept());
		list.add(new InterceptFrom());
		list.add(new InterceptSendToEndpoint());
		list.add(new Loop());
		list.add(new OnCompletion());
		list.add(new OnException());
		list.add(new Rollback());
		list.add(new Throttle());
		list.add(new ThrowException());
		list.add(new Transacted());
		list.add(new DoTry());
		return list;
	}

	public static List<CamelComponent> getTransformation() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.add(new ConvertBodyTo());
		list.add(new Enrich());
		list.add(new InOnly());
		list.add(new InOut());
		list.add(new Marshal());
		list.add(new PollEnrich());
		list.add(new RemoveHeader());
		list.add(new RemoveHeaders());
		list.add(new RemoveProperties());
		list.add(new RemoveProperty());
		list.add(new SetBody());
		list.add(new SetExchangePattern());
		list.add(new SetFaultBody());
		list.add(new SetHeader());
		list.add(new SetOutHeader());
		list.add(new SetProperty());
		list.add(new Transform());
		list.add(new Unmarshal());
		return list;
	}

	public static List<CamelComponent> getMiscellaneous() {
		List<CamelComponent> list = new ArrayList<CamelComponent>();
		list.add(new AOP());
		list.add(new Policy());
		list.add(new Sample());
		list.add(new Stop());
		list.add(new Threads());
		list.add(new Validate());
		return list;
	}

}
