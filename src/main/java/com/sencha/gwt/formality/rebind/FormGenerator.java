package com.sencha.gwt.formality.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.user.rebind.SourceWriter;
import com.sencha.gwt.formality.client.Form.Label;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * GXT-based implementation of the AbstractFormGenerator that builds FieldLabels inside a FlowLayoutContaqiner for the
 * requested fields.
 *
 */
public class FormGenerator extends AbstractFormGenerator {

  @Override
  protected void writeRootField(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw) {
    sw.println("private %1$s %2$s;", FlowLayoutContainer.class.getName(), ROOT_FIELD_NAME);
  }

  @Override
  protected void writeRootInstantiation(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw) {
    sw.println("this.%2$s = GWT.create(%1$s.class);", FlowLayoutContainer.class.getName(), ROOT_FIELD_NAME);
  }

  @Override
  protected void writeAppendEditorToRoot(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw, JMethod m) {
    // look for the label annotation, if it exists
    Label l = m.getAnnotation(Label.class);
    String labelExpr = l == null ? null : l.value();
  
    // Optionally wrap the field
    final String wrappedExpr;
    if (labelExpr != null) {
      wrappedExpr = String.format("new %1$s(this.%2$s.asWidget(), \"%3$s\")", FieldLabel.class.getName(), m.getName(), escape(labelExpr));
    } else {
      wrappedExpr = m.getName();
    }
  
    // Then append it to the container
    sw.println("%2$s.add(%1$s);", wrappedExpr, ROOT_FIELD_NAME);
  }

}
