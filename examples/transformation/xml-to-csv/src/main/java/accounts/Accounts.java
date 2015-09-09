
package accounts;

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
 *         &lt;element name="org.xyz.Account" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="company">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="geo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="contact">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="streetAddr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "orgXyzAccount"
})
@XmlRootElement(name = "accounts")
public class Accounts {

    @XmlElement(name = "org.xyz.Account")
    protected Accounts.OrgXyzAccount orgXyzAccount;

    /**
     * Gets the value of the orgXyzAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Accounts.OrgXyzAccount }
     *     
     */
    public Accounts.OrgXyzAccount getOrgXyzAccount() {
        return orgXyzAccount;
    }

    /**
     * Sets the value of the orgXyzAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Accounts.OrgXyzAccount }
     *     
     */
    public void setOrgXyzAccount(Accounts.OrgXyzAccount value) {
        this.orgXyzAccount = value;
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
     *         &lt;element name="company">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="geo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="contact">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="streetAddr" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "company",
        "contact"
    })
    public static class OrgXyzAccount {

        @XmlElement(required = true)
        protected Accounts.OrgXyzAccount.Company company;
        @XmlElement(required = true)
        protected Accounts.OrgXyzAccount.Contact contact;

        /**
         * Gets the value of the company property.
         * 
         * @return
         *     possible object is
         *     {@link Accounts.OrgXyzAccount.Company }
         *     
         */
        public Accounts.OrgXyzAccount.Company getCompany() {
            return company;
        }

        /**
         * Sets the value of the company property.
         * 
         * @param value
         *     allowed object is
         *     {@link Accounts.OrgXyzAccount.Company }
         *     
         */
        public void setCompany(Accounts.OrgXyzAccount.Company value) {
            this.company = value;
        }

        /**
         * Gets the value of the contact property.
         * 
         * @return
         *     possible object is
         *     {@link Accounts.OrgXyzAccount.Contact }
         *     
         */
        public Accounts.OrgXyzAccount.Contact getContact() {
            return contact;
        }

        /**
         * Sets the value of the contact property.
         * 
         * @param value
         *     allowed object is
         *     {@link Accounts.OrgXyzAccount.Contact }
         *     
         */
        public void setContact(Accounts.OrgXyzAccount.Contact value) {
            this.contact = value;
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
         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="geo" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "name",
            "geo",
            "active"
        })
        public static class Company {

            @XmlElement(required = true)
            protected String name;
            @XmlElement(required = true)
            protected String geo;
            @XmlElement(required = true)
            protected String active;

            /**
             * Gets the value of the name property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the geo property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGeo() {
                return geo;
            }

            /**
             * Sets the value of the geo property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGeo(String value) {
                this.geo = value;
            }

            /**
             * Gets the value of the active property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getActive() {
                return active;
            }

            /**
             * Sets the value of the active property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setActive(String value) {
                this.active = value;
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
         *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="lastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="streetAddr" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "firstName",
            "lastName",
            "streetAddr",
            "city",
            "state",
            "zip",
            "phone"
        })
        public static class Contact {

            @XmlElement(required = true)
            protected String firstName;
            @XmlElement(required = true)
            protected String lastName;
            @XmlElement(required = true)
            protected String streetAddr;
            @XmlElement(required = true)
            protected String city;
            @XmlElement(required = true)
            protected String state;
            protected int zip;
            @XmlElement(required = true)
            protected String phone;

            /**
             * Gets the value of the firstName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFirstName() {
                return firstName;
            }

            /**
             * Sets the value of the firstName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFirstName(String value) {
                this.firstName = value;
            }

            /**
             * Gets the value of the lastName property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLastName() {
                return lastName;
            }

            /**
             * Sets the value of the lastName property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLastName(String value) {
                this.lastName = value;
            }

            /**
             * Gets the value of the streetAddr property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getStreetAddr() {
                return streetAddr;
            }

            /**
             * Sets the value of the streetAddr property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setStreetAddr(String value) {
                this.streetAddr = value;
            }

            /**
             * Gets the value of the city property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCity() {
                return city;
            }

            /**
             * Sets the value of the city property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCity(String value) {
                this.city = value;
            }

            /**
             * Gets the value of the state property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getState() {
                return state;
            }

            /**
             * Sets the value of the state property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setState(String value) {
                this.state = value;
            }

            /**
             * Gets the value of the zip property.
             * 
             */
            public int getZip() {
                return zip;
            }

            /**
             * Sets the value of the zip property.
             * 
             */
            public void setZip(int value) {
                this.zip = value;
            }

            /**
             * Gets the value of the phone property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPhone() {
                return phone;
            }

            /**
             * Sets the value of the phone property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPhone(String value) {
                this.phone = value;
            }

        }

    }

}
