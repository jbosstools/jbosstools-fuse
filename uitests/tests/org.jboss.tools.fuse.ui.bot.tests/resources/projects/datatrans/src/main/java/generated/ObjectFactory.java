
package generated;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ABCOrder }
     * 
     */
    public ABCOrder createABCOrder() {
        return new ABCOrder();
    }

    /**
     * Create an instance of {@link ABCOrder.OrderItems }
     * 
     */
    public ABCOrder.OrderItems createABCOrderOrderItems() {
        return new ABCOrder.OrderItems();
    }

    /**
     * Create an instance of {@link ABCOrder.Header }
     * 
     */
    public ABCOrder.Header createABCOrderHeader() {
        return new ABCOrder.Header();
    }

    /**
     * Create an instance of {@link ABCOrder.OrderItems.Item }
     * 
     */
    public ABCOrder.OrderItems.Item createABCOrderOrderItemsItem() {
        return new ABCOrder.OrderItems.Item();
    }

}
