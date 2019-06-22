package com.barosanu.model;

import javax.mail.Store;
import java.util.Properties;

public class EmailAccount {

    private String password;
    private Properties properties;
    private String address;
    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public EmailAccount(String address, String password) {
        this.address = address;
        this.password = password;
        EmailUtils.addPropertiesToAccout(this);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getAddress() {
        return address;
    }

    public Properties getProperties() {
        return properties;
    }
}
