package org.xyz;

import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;

@FixedLengthRecord(length=54, paddingChar=' ')
public class XyzCustomer {

	// example: BikesBikesBikes should become BikesBikes
    @DataField(pos = 1, length=15)
    private String company;	

	// example: NA or EU
    @DataField(pos = 16, length=2)
    private String region;
    
    // example: true or false
    @DataField(pos = 18, length=5)
    private boolean active;
    
    // example: George
    @DataField(pos = 24, length=6)
    private String firstName;
    
    // example: Jungle
    @DataField(pos = 30, length=10)
    private String lastName;
    
    // example: 100 N Park Ave
    @DataField(pos = 40, length=16)
    private String streetAddress;
    
    // example: Phoenix
    @DataField(pos = 56, length=10)
    private String city;

    // example: AZ
    @DataField(pos = 66, length=2)
    private String state;
    
    // example: 85017
    @DataField(pos = 68, length=5)
    private String zip;
    
    // example: 602-555-1100
    @DataField(pos = 73, length=12)
    private String phone;

    public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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

