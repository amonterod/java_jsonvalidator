package com.jsonvalidator;

import org.json.JSONObject;

class Address {
    private String street;
    private String city;
    private String country;

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    
}

class User {

    private long id;
    private String name;
    private String gender;
    private String email;
    private Address address;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    

}


public class JsonHandler {

    public JSONObject createJsonUser(User us) {
        JSONObject json = new JSONObject();

        json.accumulate("id", us.getId());
        json.accumulate("name", us.getName());
        json.accumulate("gender", us.getGender());
        json.accumulate("email", us.getEmail());

        JSONObject addr = new JSONObject();
        addr.accumulate("street", us.getAddress().getStreet());
        addr.accumulate("city", us.getAddress().getCity());
        addr.accumulate("country", us.getAddress().getCountry());

        json.accumulate("address", addr);

        return json;
    }

    public static void main(String[] args) {
        Address addr = new Address();
        addr.setStreet("Calle Alcala");
        addr.setCity("Madrid");
        addr.setCountry("Spain");

        User us = new User();
        us.setId(1);
        us.setName("John");
        us.setGender("Male");
        us.setEmail("john@gmail.com");
        us.setAddress(addr);

        System.out.println(new JsonHandler().createJsonUser(us).toString(4));

    }
}