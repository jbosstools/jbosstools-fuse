/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model.variables;


/**
 * @author lhein
 */
public interface IVariableConstants {
	public static final String VARIABLE_NAME_UID = "UID";
	public static final String VARIABLE_NAME_TIMESTAMP = "Timestamp";
	public static final String VARIABLE_NAME_ROUTEID = "RouteId";
	public static final String VARIABLE_NAME_NODEID = "NodeId";
	public static final String VARIABLE_NAME_EXCHANGEID = "ExchangeId";
	public static final String VARIABLE_NAME_MESSAGE = "Message";
	public static final String VARIABLE_NAME_EXCHANGE = "Exchange";
	public static final String VARIABLE_NAME_MESSAGEID = "MessageId";
	public static final String VARIABLE_NAME_MESSAGEHEADERS = "MessageHeaders";
	public static final String VARIABLE_NAME_MESSAGEBODY = "MessageBody";
	public static final String VARIABLE_NAME_ENDPOINT = "Endpoint";
	public static final String VARIABLE_NAME_LOGLEVEL = "LogLevel";
	public static final String VARIABLE_NAME_DEBUGCOUNTER = "DebugCounter";
	public static final String VARIABLE_NAME_BODYMAXCHARS = "BodyMaxChars";
	public static final String VARIABLE_NAME_BODYINCLUDEFILES = "BodyIncludeFiles";
	public static final String VARIABLE_NAME_BODYINCLUDESTREAMS = "BodyIncludeStreams";	
	public static final String VARIABLE_NAME_DEBUGGER = "CamelDebugger";
	
	public static final String VARIABLE_NAME_PROCESSOR = "Processor";
	public static final String VARIABLE_NAME_PROCESSOR_ID = "ProcessorId";
	public static final String VARIABLE_NAME_PROCESSOR_ROUTE_ID	= "RouteId";
	public static final String VARIABLE_NAME_PROCESSOR_CAMEL_ID = "CamelId";
	public static final String VARIABLE_NAME_PROCESSOR_EXCHANGES_COMPLETED = "CompletedExchanges";
	public static final String VARIABLE_NAME_PROCESSOR_EXCHANGES_FAILED = "FailedExchanges";
	public static final String VARIABLE_NAME_PROCESSOR_EXCHANGES_TOTAL = "TotalExchanges";
	public static final String VARIABLE_NAME_PROCESSOR_REDELIVERIES = "Redeliveries";
	public static final String VARIABLE_NAME_PROCESSOR_EXTERNAL_REDELIVERIES = "ExternalRedeliveries";
	public static final String VARIABLE_NAME_PROCESSOR_FAILURES_HANDLED = "HandledFailures";
	public static final String VARIABLE_NAME_PROCESSOR_LAST_PROCESSING_TIME = "LastProcessingTime";
	public static final String VARIABLE_NAME_PROCESSOR_MIN_PROCESSING_TIME = "MinProcessingTime";
	public static final String VARIABLE_NAME_PROCESSOR_MAX_PROCESSING_TIME = "MaxProcessingTime";
	public static final String VARIABLE_NAME_PROCESSOR_AVG_PROCESSING_TIME = "AverageProcessingTime";
	public static final String VARIABLE_NAME_PROCESSOR_TOTAL_PROCESSING_TIME = "TotalProcessingTime";
}
