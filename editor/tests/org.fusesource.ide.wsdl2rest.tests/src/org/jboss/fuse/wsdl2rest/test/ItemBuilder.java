package org.jboss.fuse.wsdl2rest.test;

import java.util.Date;

public class ItemBuilder {

    private Item result = new Item();
    
    public ItemBuilder copy(Item obj) {
        result.setId(obj.getId());
        result.setName(obj.getName());
        result.setDateOfBirth(obj.getDateOfBirth());
        return this;
    }
    
    public ItemBuilder id(Integer id) {
        result.setId(id);
        return this;
    }
    
    public ItemBuilder name(String name) {
        result.setName(name);
        return this;
    }
    
    public ItemBuilder dateOfBirth(Date dob) {
        result.setDateOfBirth(dob);
        return this;
    }
    
    public Item build() {
        return result;
    }
}