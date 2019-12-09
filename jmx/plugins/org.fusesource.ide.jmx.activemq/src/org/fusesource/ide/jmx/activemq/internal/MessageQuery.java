/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq.internal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;

/**
 * @author lhein
 *
 */
public class MessageQuery extends QueueBrowseQuery {

    private String id;
    private Message message;

    public MessageQuery(BrokerFacade brokerFacade, SessionPool sessionPool) throws JMSException {
        super(brokerFacade, sessionPool);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() throws JMSException {
        if (message == null) {
            if (id != null) {
                QueueBrowser tempBrowser=getBrowser();
                Enumeration iter = tempBrowser.getEnumeration();
                while (iter.hasMoreElements()) {
                    Message item = (Message) iter.nextElement();
                    if (id.equals(item.getJMSMessageID())) {
                        message = item;
                        break;
                    }
                }
                tempBrowser.close();
            }

        }
        return message;
    }

    public Object getBody() throws JMSException {
        Message message = getMessage();
        if (message instanceof TextMessage) {
            return ((TextMessage) message).getText();
        }
        if (message instanceof ObjectMessage) {
            try {
                return ((ObjectMessage) message).getObject();
            } catch (JMSException e) {
                //message could not be parsed, make the reason available
                return e;
            }
        }
        if (message instanceof MapMessage) {
            return createMapBody((MapMessage) message);
        }
        return null;
    }

    public Map<String, Object> getPropertiesMap() throws JMSException {
        Map<String, Object> answer = new HashMap<String, Object>();
        Message aMessage = getMessage();
        Enumeration iter = aMessage.getPropertyNames();
        while (iter.hasMoreElements()) {
            String name = (String) iter.nextElement();
            Object value = aMessage.getObjectProperty(name);
            if (value != null) {
                answer.put(name, value);
            }
        }
        return answer;
    }

    protected Map<String, Object> createMapBody(MapMessage mapMessage) throws JMSException {
        Map<String, Object> answer = new HashMap<String, Object>();
        Enumeration iter = mapMessage.getMapNames();
        while (iter.hasMoreElements()) {
            String name = (String) iter.nextElement();
            Object value = mapMessage.getObject(name);
            if (value != null) {
                answer.put(name, value);
            }
        }
        return answer;
    }
}
