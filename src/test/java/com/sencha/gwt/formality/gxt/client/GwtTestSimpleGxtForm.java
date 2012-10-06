package com.sencha.gwt.formality.gxt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.junit.client.GWTTestCase;
import com.sencha.gwt.formality.client.Address;
import com.sencha.gwt.formality.client.Form;
import com.sencha.gwt.formality.client.Person;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.TextField;

public class GwtTestSimpleGxtForm extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.sencha.gwt.formality.gxt.FormalityWithGXT";
  }

  public interface Driver extends SimpleBeanEditorDriver<Person, PersonForm> {}

  public interface PersonForm extends Form<Person> {
    TextField name();

    DateField dob();

    AddressForm mailingAddress();
    AddressForm shippingAddress();
  }

  public interface AddressForm extends Form<Address> {
    TextField line1();
    TextField line2();

    TextField city();
    TextField state();
  }

  public void testBindPerson() {
    Person p = new Person("Colin Alworth", new DateWrapper(1985, 4, 26).asDate());

    p.getMailingAddress().setLine1("1234 N Main St");
    p.getMailingAddress().setCity("Somewhere");
    p.getMailingAddress().setState("AB");

    PersonForm form = GWT.create(PersonForm.class);
    Driver d = GWT.create(Driver.class);
    d.initialize(form);

    d.edit(p);


  }
}
