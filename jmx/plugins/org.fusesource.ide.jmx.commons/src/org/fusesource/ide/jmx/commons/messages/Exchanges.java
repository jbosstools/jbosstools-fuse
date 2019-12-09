/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.jmx.commons.Activator;
import org.fusesource.ide.jmx.commons.messages.support.NamespaceFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class Exchanges {

	protected static final Pattern startBodyElementRegex = Pattern.compile("<body type=\"[^\"]*\">");

	public static JAXBContext newJaxbContext() throws JAXBException {
		return JAXBContext.newInstance(ExchangeList.class, Exchange.class, Message.class, Header.class, Body.class);
	}

	public static Exchange unmarshalXmlString(String message) throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller();
		Object object = unmarshaller.unmarshal(new StringReader(message));
		return asExchange(object);
	}

	public static Exchange unmarshalNoNamespaceXmlString(String badMessage) throws JAXBException, SAXException {
		// lets XML encode the payload as a fix for camel 2.7.x
		String message = wrapBadXmlMessage(badMessage);
		
		Unmarshaller unmarshaller = createUnmarshaller();
		XMLReader reader = XMLReaderFactory.createXMLReader();
		NamespaceFilter inFilter = new NamespaceFilter("http://fabric.fusesource.org/schema/messages", true);
		inFilter.setParent(reader);
		InputSource is = new InputSource(new StringReader(message));
		SAXSource source = new SAXSource(inFilter, is);
		Object object = unmarshaller.unmarshal(source);
		return asExchange(object);
	}

	/**
	 * Lets wrap the XML in a CDATA statement if it starts with a "<" as there is an XML encoding bug in Camel 2.7.x
	 */
	public static String wrapBadXmlMessage(String message) {
		int end = message.lastIndexOf("</body>");
		if (end > 0) {
			Matcher m = startBodyElementRegex.matcher(message);
			if (m.find()) {
				int start = m.end();
				String middle = message.substring(start, end);
				if (middle.startsWith("<")) {
					String prefix = message.substring(0, start);
					String postfix = message.substring(end);
					String answer = prefix + "<![CDATA[" + middle + "]]>" + postfix;
					return answer;
				}
			}
		}
		return message;
	}

	public static ExchangeList unmarshalExchangesXmlString(String message) throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller();
		Object object = unmarshaller.unmarshal(new StringReader(message));
		return asExchangeList(object);
	}

	public static void marshal(Object message, File file) throws JAXBException {
		Marshaller marshaller = createMarshaller(message);
		marshaller.marshal(message, file);
	}

	public static String marshal(Object message) throws JAXBException {
		Marshaller marshaller = createMarshaller(message);
		StringWriter writer = new StringWriter();
		marshaller.marshal(message, writer);
		return writer.toString();
	}

	public static IMessage toMessage(Object object) {
		IMessage message = null;
		if (object instanceof IExchange) {
			IExchange exchange = (IExchange) object;
			message = exchange.getIn();
		} else if (object instanceof IMessage) {
			message = (IMessage) object;
		}
		return message;
	}

	public static void preMarshalHook(Object object) {
		if (object instanceof PreMarshalHook) {
			PreMarshalHook hook = (PreMarshalHook) object;
			hook.preMarshal();
		}
	}


	public static IMessage loadMessage(Object description, InputStream in) throws JAXBException {
		Object object = loadObject(description, in);
		return asMessage(object);
	}

	public static ExchangeList loadExchanges(Object description, InputStream in) throws JAXBException {
		Object object = loadObject(description, in);
		return asExchangeList(object);
	}

	protected static Object loadObject(Object description, InputStream in) throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller();
		return unmarshaller.unmarshal(in);
	}

	public static RiderLogFacade getLogger() {
		return Activator.getLogger();
	}

	/*
	public static IMessage loadMessage(URL url) throws JAXBException, IOException {
		// TODO - we should be able to know from the kind of the file, whether
		// its a message or not...

		// try to load the given XML file; if its not XML assume its just text
		// and include it as a body in a message instead
		Unmarshaller unmarshaller = createUnmarshaller();
		Object object = null;
		try {
			object = unmarshaller.unmarshal(url);
		} catch (Exception e) {
			getLogger().info(
					"Could not parse " + url + " as a message file so using it as a payload. " + e, e);
		}
		if (object instanceof IMessage) {
			return (IMessage) object;
		} else {
			Message message = new Message();

			// load the file as a String for now...
			String body = IOUtil.loadText(url.openStream(), "utf-8");
			message.setBody(body);
			return message;
		}
	}
	*/

	public static IMessage loadMessage(File file) throws JAXBException {
		// TODO - we should be able to know from the kind of the file, whether
		// its a message or not...

		// try to load the given XML file; if its not XML assume its just text
		// and include it as a body in a message instead
		Unmarshaller unmarshaller = createUnmarshaller();
		Object object = null;
		try {
			object = unmarshaller.unmarshal(file);
		} catch (Exception e) {
			getLogger().info(
					"Could not parse " + file + " as a message file so using it as a payload. " + e, e);
		}
		if (object instanceof IMessage) {
			return (IMessage) object;
		}
		return null;
	    // TODO
	    /*
		} else {
			Message message = new Message();

			// load the file as a String for now...
			String body = IOUtil.loadTextFile(file, "utf-8");
			message.setBody(body);
			return message;
			
		}
		*/
	}
	
	public static Exchange asExchange(Object object) {
		if (object instanceof Exchange) {
			return (Exchange) object;
		} else if (object instanceof Message) {
			return new Exchange((Message) object);
		} else if (object instanceof ExchangeList) {
			ExchangeList exchangeList = (ExchangeList) object;
			List<Exchange> list = exchangeList.getExchanges();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	public static ExchangeList asExchangeList(Object object) {
		if (object instanceof ExchangeList) {
			return (ExchangeList) object;
		} else {
			ExchangeList answer = new ExchangeList();
			Exchange exchange = asExchange(object);
			if (exchange != null) {
				answer.getExchanges().add(exchange);
				return answer;
			}
			return null;
		}
	}
	
	public static IMessage asMessage(Object object) {
		if (object instanceof IMessage) {
			return (IMessage) object;
		} else {
			Exchange exchange = asExchange(object);
			if (exchange != null) {
				return exchange.getIn();
			}
		}
		return null;
	}


	protected static Unmarshaller createUnmarshaller() throws JAXBException {
		JAXBContext context = newJaxbContext();
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return unmarshaller;
	}

	protected static Marshaller createMarshaller(Object object) throws JAXBException, PropertyException {
		JAXBContext context = newJaxbContext();
		Marshaller marshaller = context.createMarshaller();
		preMarshalHook(object);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return marshaller;
	}

	public static Object getBody(IExchange exchange) {
		if (exchange != null) {
			IMessage message = exchange.getIn();
			if (message != null) {
				return message.getBody();
			}
		}
		return null;
	}

	/**
	 * Returns the headers on the given exchange or an empty map if there are none
	 */
	public static Map<String, Object> getHeaders(IExchange exchange) {
		if (exchange != null) {
			IMessage message = exchange.getIn();
			if (message != null) {
				Map<String, Object> headers = message.getHeaders();
				if (headers != null) {
					return headers;
					
				}
			}
		}
		return Collections.emptyMap();
	}


}
