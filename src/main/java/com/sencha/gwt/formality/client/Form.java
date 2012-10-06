package com.sencha.gwt.formality.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 *
 * @param <T>
 */
public interface Form<T> extends IsWidget, Editor<T> {

  /**
   * 
   *
   */
  @Documented
  @Target(ElementType.METHOD)
  public @interface Label {
    String value();
  }

}
