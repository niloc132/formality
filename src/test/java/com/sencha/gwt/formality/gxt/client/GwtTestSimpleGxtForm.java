package com.sencha.gwt.formality.gxt.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IndexedPanel;
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

  public interface CityAndState extends Form<Address> {
    TextField city();
    TextField state();
  }

  public interface AddressForm extends CityAndState, Form<Address> {
    TextField line1();
    TextField line2();

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

  public void testWidgetMakeup() {
    PersonForm form = GWT.create(PersonForm.class);

    IndexedPanel.ForIsWidget root = (IndexedPanel.ForIsWidget) form.asWidget();

    assertEquals(form.name(), ((HasOneWidget) root.getWidget(0)).getWidget());
    assertEquals(form.dob(), ((HasOneWidget) root.getWidget(1)).getWidget());
    assertEquals(form.mailingAddress().asWidget(), ((HasOneWidget) root.getWidget(2)).getWidget());
    assertEquals(form.shippingAddress().asWidget(), ((HasOneWidget) root.getWidget(3)).getWidget());

    AddressForm address = form.shippingAddress();
    root = (IndexedPanel.ForIsWidget) address.asWidget();

    assertEquals(address.line1(), ((HasOneWidget) root.getWidget(0)).getWidget());
    assertEquals(address.line2(), ((HasOneWidget) root.getWidget(1)).getWidget());
    assertEquals(address.city(), ((HasOneWidget) root.getWidget(2)).getWidget());
    assertEquals(address.state(), ((HasOneWidget) root.getWidget(3)).getWidget());
  }
}
