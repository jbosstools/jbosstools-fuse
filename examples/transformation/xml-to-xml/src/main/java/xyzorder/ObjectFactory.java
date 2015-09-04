
package xyzorder;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the xyzorder package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: xyzorder
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XYZOrder }
     * 
     */
    public XYZOrder createXYZOrder() {
        return new XYZOrder();
    }

    /**
     * Create an instance of {@link XYZOrder.Properties }
     * 
     */
    public XYZOrder.Properties createXYZOrderProperties() {
        return new XYZOrder.Properties();
    }

    /**
     * Create an instance of {@link XYZOrder.Properties.LineItems }
     * 
     */
    public XYZOrder.Properties.LineItems createXYZOrderPropertiesLineItems() {
        return new XYZOrder.Properties.LineItems();
    }

    /**
     * Create an instance of {@link XYZOrder.Properties.LineItems.LineItem }
     * 
     */
    public XYZOrder.Properties.LineItems.LineItem createXYZOrderPropertiesLineItemsLineItem() {
        return new XYZOrder.Properties.LineItems.LineItem();
    }

}
