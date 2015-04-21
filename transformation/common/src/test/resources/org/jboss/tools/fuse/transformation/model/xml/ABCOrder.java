
package test.generateFromInstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="customer-num" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="order-num" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="order-items">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                             &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "header",
    "orderItems"
})
@XmlRootElement(name = "ABCOrder")
public class ABCOrder {

    @XmlElement(required = true)
    protected ABCOrder.Header header;
    @XmlElement(name = "order-items", required = true)
    protected ABCOrder.OrderItems orderItems;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link ABCOrder.Header }
     *     
     */
    public ABCOrder.Header getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABCOrder.Header }
     *     
     */
    public void setHeader(ABCOrder.Header value) {
        this.header = value;
    }

    /**
     * Gets the value of the orderItems property.
     * 
     * @return
     *     possible object is
     *     {@link ABCOrder.OrderItems }
     *     
     */
    public ABCOrder.OrderItems getOrderItems() {
        return orderItems;
    }

    /**
     * Sets the value of the orderItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ABCOrder.OrderItems }
     *     
     */
    public void setOrderItems(ABCOrder.OrderItems value) {
        this.orderItems = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="customer-num" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="order-num" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "status",
        "customerNum",
        "orderNum"
    })
    public static class Header {

        @XmlElement(required = true)
        protected String status;
        @XmlElement(name = "customer-num", required = true)
        protected String customerNum;
        @XmlElement(name = "order-num", required = true)
        protected String orderNum;

        /**
         * Gets the value of the status property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStatus() {
            return status;
        }

        /**
         * Sets the value of the status property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStatus(String value) {
            this.status = value;
        }

        /**
         * Gets the value of the customerNum property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCustomerNum() {
            return customerNum;
        }

        /**
         * Sets the value of the customerNum property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCustomerNum(String value) {
            this.customerNum = value;
        }

        /**
         * Gets the value of the orderNum property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrderNum() {
            return orderNum;
        }

        /**
         * Sets the value of the orderNum property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrderNum(String value) {
            this.orderNum = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="item">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}float"/>
     *                   &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}short"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class OrderItems {

        @XmlElement(required = true)
        protected ABCOrder.OrderItems.Item item;

        /**
         * Gets the value of the item property.
         * 
         * @return
         *     possible object is
         *     {@link ABCOrder.OrderItems.Item }
         *     
         */
        public ABCOrder.OrderItems.Item getItem() {
            return item;
        }

        /**
         * Sets the value of the item property.
         * 
         * @param value
         *     allowed object is
         *     {@link ABCOrder.OrderItems.Item }
         *     
         */
        public void setItem(ABCOrder.OrderItems.Item value) {
            this.item = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="price" type="{http://www.w3.org/2001/XMLSchema}float"/>
         *         &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}short"/>
         *       &lt;/sequence>
         *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "price",
            "quantity"
        })
        public static class Item {

            protected float price;
            protected short quantity;
            @XmlAttribute(name = "id")
            protected String id;

            /**
             * Gets the value of the price property.
             * 
             */
            public float getPrice() {
                return price;
            }

            /**
             * Sets the value of the price property.
             * 
             */
            public void setPrice(float value) {
                this.price = value;
            }

            /**
             * Gets the value of the quantity property.
             * 
             */
            public short getQuantity() {
                return quantity;
            }

            /**
             * Sets the value of the quantity property.
             * 
             */
            public void setQuantity(short value) {
                this.quantity = value;
            }

            /**
             * Gets the value of the id property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getId() {
                return id;
            }

            /**
             * Sets the value of the id property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setId(String value) {
                this.id = value;
            }

        }

    }

}
