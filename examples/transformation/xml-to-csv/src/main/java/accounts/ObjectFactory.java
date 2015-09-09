
package accounts;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the accounts package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: accounts
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Accounts }
     * 
     */
    public Accounts createAccounts() {
        return new Accounts();
    }

    /**
     * Create an instance of {@link Accounts.OrgXyzAccount }
     * 
     */
    public Accounts.OrgXyzAccount createAccountsOrgXyzAccount() {
        return new Accounts.OrgXyzAccount();
    }

    /**
     * Create an instance of {@link Accounts.OrgXyzAccount.Company }
     * 
     */
    public Accounts.OrgXyzAccount.Company createAccountsOrgXyzAccountCompany() {
        return new Accounts.OrgXyzAccount.Company();
    }

    /**
     * Create an instance of {@link Accounts.OrgXyzAccount.Contact }
     * 
     */
    public Accounts.OrgXyzAccount.Contact createAccountsOrgXyzAccountContact() {
        return new Accounts.OrgXyzAccount.Contact();
    }

}
