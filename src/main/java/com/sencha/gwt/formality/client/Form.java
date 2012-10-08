package com.sencha.gwt.formality.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Allows a developer to simply declare a set of editors/widgets in order, and expect them
 * to be folded into a simple widget which will display all of them. Some layout
 * details can be customized through general annotations, and more can be added and supported
 * by extending the generator.
 * 
 * 
 * 
 * Methods in sub-interfaces will appear earlier than those in extended interfaces with other
 * editors.
 *
 * @param <T> type of the model object to be edited
 */
public interface Form<T> extends IsWidget, Editor<T> {

  /**
   * Declares a label for the form component. It is assumed then that the generator has a process
   * in place to turn this into readable content, and that lack of this annotation should mean that
   * no label should be drawn (as opposed to an empty string, which suggests space for a label, but
   * no label text).
   * 
   * 
   * Note that this has no effect on non-widgets.
   *
   * @todo consider I18N support
   */
  @Documented
  @Target(ElementType.METHOD)
  public @interface Label {
    String value();
  }

}
