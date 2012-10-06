package com.sencha.gwt.formality.client;

import java.util.Date;

public class Person {

  private String name;
  private Date dob;
  private Address mailingAddress = new Address();
  private Address shippingAddress = new Address();

  public Person(String name, Date dob) {
    super();
    this.name = name;
    this.dob = dob;
  }

  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public Date getDob() {
    return this.dob;
  }
  public void setDob(Date dob) {
    this.dob = dob;
  }

  public Address getMailingAddress() {
    return mailingAddress;
  }
  public void setMailingAddress(Address mailingAddress) {
    this.mailingAddress = mailingAddress;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }
  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }
}
