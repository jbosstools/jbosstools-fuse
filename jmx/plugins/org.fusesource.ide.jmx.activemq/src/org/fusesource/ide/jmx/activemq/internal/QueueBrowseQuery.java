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

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

/**
 * @author lhein
 *
 */
public class QueueBrowseQuery extends DestinationFacade {
    private SessionPool sessionPool;
    private String selector;
    private Session session;
    private Queue queue;
    private QueueBrowser browser;

    public QueueBrowseQuery(BrokerFacade brokerFacade, SessionPool sessionPool) throws JMSException {
        super(brokerFacade);
        this.sessionPool = sessionPool;
        this.session = sessionPool.borrowSession();
        setJMSDestinationType("query");
    }

    public void destroy() throws Exception {
        if (browser != null) {
            browser.close();
        }
        sessionPool.returnSession(session);
        session = null;
    }

    public QueueBrowser getBrowser() throws JMSException {
        if (browser == null) {
            browser = createBrowser();
        }
        return browser;
    }

    public void setBrowser(QueueBrowser browser) {
        this.browser = browser;
    }

    public Queue getQueue() throws JMSException {
        if (queue == null) {
            queue = session.createQueue(getValidDestination());
        }
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public Session getSession() {
        return session;
    }

    @Override
	public boolean isQueue() {
        return true;
    }

    protected QueueBrowser createBrowser() throws JMSException {
        return getSession().createBrowser(getQueue(), getSelector());
    }
}
