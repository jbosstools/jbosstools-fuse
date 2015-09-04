
package xyzorder;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="properties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="custId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="origin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="approvalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="lineItems">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="lineItem" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="itemId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                       &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *                                       &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
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
    "properties"
})
@XmlRootElement(name = "XYZOrder")
public class XYZOrder {

    @XmlElement(required = true)
    protected XYZOrder.Properties properties;

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link XYZOrder.Properties }
     *     
     */
    public XYZOrder.Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link XYZOrder.Properties }
     *     
     */
    public void setProperties(XYZOrder.Properties value) {
        this.properties = value;
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
     *         &lt;element name="custId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="orderId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="origin" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="approvalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="lineItems">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="lineItem" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="itemId" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                             &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}short"/>
     *                             &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}float"/>
     *                           &lt;/sequence>
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
        "custId",
        "priority",
        "orderId",
        "origin",
        "approvalCode",
        "lineItems"
    })
    public static class Properties {

        @XmlElement(required = true)
        protected String custId;
        @XmlElement(required = true)
        protected String priority;
        @XmlElement(required = true)
        protected String orderId;
        @XmlElement(required = true)
        protected String origin;
        @XmlElement(required = true)
        protected String approvalCode;
        @XmlElement(required = true)
        protected XYZOrder.Properties.LineItems lineItems;

        /**
         * Gets the value of the custId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCustId() {
            return custId;
        }

        /**
         * Sets the value of the custId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCustId(String value) {
            this.custId = value;
        }

        /**
         * Gets the value of the priority property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPriority() {
            return priority;
        }

        /**
         * Sets the value of the priority property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPriority(String value) {
            this.priority = value;
        }

        /**
         * Gets the value of the orderId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrderId() {
            return orderId;
        }

        /**
         * Sets the value of the orderId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrderId(String value) {
            this.orderId = value;
        }

        /**
         * Gets the value of the origin property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrigin() {
            return origin;
        }

        /**
         * Sets the value of the origin property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrigin(String value) {
            this.origin = value;
        }

        /**
         * Gets the value of the approvalCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getApprovalCode() {
            return approvalCode;
        }

        /**
         * Sets the value of the approvalCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setApprovalCode(String value) {
            this.approvalCode = value;
        }

        /**
         * Gets the value of the lineItems property.
         * 
         * @return
         *     possible object is
         *     {@link XYZOrder.Properties.LineItems }
         *     
         */
        public XYZOrder.Properties.LineItems getLineItems() {
            return lineItems;
        }

        /**
         * Sets the value of the lineItems property.
         * 
         * @param value
         *     allowed object is
         *     {@link XYZOrder.Properties.LineItems }
         *     
         */
        public void setLineItems(XYZOrder.Properties.LineItems value) {
            this.lineItems = value;
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
         *         &lt;element name="lineItem" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="itemId" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                   &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}short"/>
         *                   &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}float"/>
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
            "lineItem"
        })
        public static class LineItems {

            protected List<XYZOrder.Properties.LineItems.LineItem> lineItem;

            /**
             * Gets the value of the lineItem property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the lineItem property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getLineItem().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link XYZOrder.Properties.LineItems.LineItem }
             * 
             * 
             */
            public List<XYZOrder.Properties.LineItems.LineItem> getLineItem() {
                if (lineItem == null) {
                    lineItem = new ArrayList<XYZOrder.Properties.LineItems.LineItem>();
                }
                return this.lineItem;
            }

            // TODO: This method has been added by hand. This should not be necessary 
            // once we get some additional fixes into the model generation code.
            public void setLineItem(List<XYZOrder.Properties.LineItems.LineItem> item) {
            	this.lineItem = item;
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
             *         &lt;element name="itemId" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}short"/>
             *         &lt;element name="cost" type="{http://www.w3.org/2001/XMLSchema}float"/>
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
                "itemId",
                "amount",
                "cost"
            })
            public static class LineItem {

                @XmlElement(required = true)
                protected String itemId;
                protected short amount;
                protected float cost;

                /**
                 * Gets the value of the itemId property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getItemId() {
                    return itemId;
                }

                /**
                 * Sets the value of the itemId property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setItemId(String value) {
                    this.itemId = value;
                }

                /**
                 * Gets the value of the amount property.
                 * 
                 */
                public short getAmount() {
                    return amount;
                }

                /**
                 * Sets the value of the amount property.
                 * 
                 */
                public void setAmount(short value) {
                    this.amount = value;
                }

                /**
                 * Gets the value of the cost property.
                 * 
                 */
                public float getCost() {
                    return cost;
                }

                /**
                 * Sets the value of the cost property.
                 * 
                 */
                public void setCost(float value) {
                    this.cost = value;
                }

            }

        }

    }

}
