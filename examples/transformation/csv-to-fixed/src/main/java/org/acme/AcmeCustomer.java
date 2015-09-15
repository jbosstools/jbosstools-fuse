package org.acme;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;


@CsvRecord(separator = ",")
public class AcmeCustomer {

    @DataField(pos = 1)
    private String companyName;
    @DataField(pos = 2)
    private String region;
    @DataField(pos = 3)
    private boolean active;
    @DataField(pos = 4)
    private String firstName;
    @DataField(pos = 5)
    private String lastName;
    @DataField(pos = 6)
    private String streetAddr;
    @DataField(pos = 7)
    private String city;
    @DataField(pos = 8)
    private String state;
    @DataField(pos = 9)
    private String zip;
    @DataField(pos = 10)
    private String phone;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetAddr() {
        return streetAddr;
    }

    public void setStreetAddr(String streetAddr) {
        this.streetAddr = streetAddr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
