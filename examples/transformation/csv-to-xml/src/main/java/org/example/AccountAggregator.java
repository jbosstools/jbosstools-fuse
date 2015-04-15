package org.example;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Aggregator implementation which wraps all <account> fragments into a single
 * XML document with a root element of <accounts>.
 */
public class AccountAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            Document doc = newExchange.getIn().getBody(Document.class);
            Node account = doc.removeChild(doc.getDocumentElement());
            doc.appendChild(doc.createElement("accounts"));
            doc.getDocumentElement().appendChild(account);
            newExchange.getIn().setBody(doc);
            return newExchange;
        }
        Document existing = oldExchange.getIn().getBody(Document.class);
        Element newAccount = newExchange.getIn().getBody(Document.class).getDocumentElement();
        existing.getDocumentElement().appendChild(existing.importNode(newAccount, true));
        oldExchange.getIn().setBody(existing);
        return oldExchange;
    }
    
}