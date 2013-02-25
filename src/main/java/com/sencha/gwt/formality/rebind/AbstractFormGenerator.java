package com.sencha.gwt.formality.rebind;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.rg.CssResourceGenerator;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates a Form implementation, based on the structure of the interface and any annotations
 * used to enrich this description. This class is designed to be extended to modify or replace
 * default functionality, to work with any GWT library that plays well with {@link Editor} and
 * {@link IsWidget}.
 * 
 * A future implementation might even read these behaviors from other annotations, as
 * {@link CssResource} is wired to {@link CssResourceGenerator}.
 * 
 *
 */
public abstract class AbstractFormGenerator extends Generator {
  protected static final String ROOT_FIELD_NAME = "_root";

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    TypeOracle oracle = context.getTypeOracle();
  
    JClassType editorType = oracle.findType(Editor.class.getName());
    JClassType isEditorType = oracle.findType(IsEditor.class.getName());
    JClassType toGenerate = oracle.findType(typeName);
  
    String packageName = toGenerate.getPackage().getName();
    String simpleSourceName = toGenerate.getName().replace('.', '_') + "Impl";
  
    // Attempt to create a new file - if null, it already exists, and should be used
    PrintWriter pw = context.tryCreate(logger, packageName, simpleSourceName);
    if (pw == null) {
      return packageName + "." + simpleSourceName;
    }
  
    // Prepare to make a new class in given package with the specified name
    ClassSourceFileComposerFactory factory =
        new ClassSourceFileComposerFactory(packageName, simpleSourceName);
  
    // Set the superclass, interfaces, and any imports
    factory.addImplementedInterface(typeName);
    factory.addImport(GWT.class.getName());
    // Create a SourceWrite to write out code
    SourceWriter sw = factory.createSourceWriter(context, pw);
  
    // Declare a container, that will be returned by asWidget()
    // root field to hold the fields
    writeRootField(logger, context, toGenerate, sw);
  
    // Build the fields, methods needed to generate the requested fields and make them available
    for (JMethod m : toGenerate.getOverridableMethods()) {
      assert m.getParameters().length == 0;
      JClassType editor = m.getReturnType().isClassOrInterface();
      if (editor != null && (editor.isAssignableTo(editorType) || editor.isAssignableTo(isEditorType))) {
        // field
        sw.println("private %1$s %2$s;", editor.getParameterizedQualifiedSourceName(), m.getName());//field
  
        // method
        sw.println("%1$s {", m.getReadableDeclaration(false, true, true, true, true));
        sw.indentln("return %1$s;", m.getName());
        sw.println("}");
      }
    }
  
    // expose the root field
    sw.println("public %1$s asWidget() {return _root;}", Widget.class.getName());
  
  
    // constructor to assemble the fields
    sw.println("public %1$s() {", simpleSourceName);
    sw.indent();
    writeDefaultConstructorContents(logger, context, toGenerate, sw);
    sw.outdent();
    sw.println("}");
  
    // Commit and return the class
    sw.commit(logger);
    return factory.getCreatedClassName();
  }
  
  /**
   * Responsible for writing out the root of the layout. This method must create at least one
   * field called _root, which will then be returned from asWidget(). If it creates others as
   * well, those can be used in other steps like {@link #writeRootInstantiation(TreeLogger, GeneratorContext, JClassType, SourceWriter)}
   * to properly set them up.
   * 
   * @param toGenerate 
   * @param context 
   * @param logger 
   * @param sw
   */
  protected abstract void writeRootField(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw);

  /**
   * Responsible for initializing the root widget. This will be a field, declared already in
   * {@link #writeRootField(TreeLogger, GeneratorContext, JClassType, SourceWriter)}, and will
   * be named _root. 
   * 
   * @param logger
   * @param context
   * @param toGenerate
   * @param sw
   */
  protected abstract void writeRootInstantiation(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw);

  /**
   * Responsible for writing the contents of the default constructor, which should do the basic
   * wiring of creating widgets and attaching widgets to the root, etc.
   * 
   * Instead of overriding this, consider {@link #writeRootInstantiation(TreeLogger, GeneratorContext, JClassType, SourceWriter)}
   * or {@link #writeInitForMethodsInType(TreeLogger, TypeOracle, JClassType, SourceWriter, Set)} for
   * modifying structure, except the order that the editors are assembled. Can also be useful to 
   * write out and wire up extra pieces like an EditorDriver.
   * 
   * @param logger
   * @param context
   * @param toGenerate
   * @param sw
   */
  protected void writeDefaultConstructorContents(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw) {
    // Instantiate the root container
    writeRootInstantiation(logger, context, toGenerate, sw);
  
    // Look for methods again, this time in order or declaration (starting at subtype)
    // to append the form in order it was declared
    Set<JMethod> visited = new HashSet<JMethod>();
    writeInitForMethodsInType(logger, context, toGenerate, toGenerate, sw, visited);
    for (JClassType extended : toGenerate.getFlattenedSupertypeHierarchy()) {
      writeInitForMethodsInType(logger, context, toGenerate, extended, sw, visited);
    }
  }

  /**
   * Responsible for initializing the field(s) needed for the given method m. By default, asWidget
   * will be skipped.
   * 
   * Designed to be called from within the constructor, to initialize the editor before any
   * method could potentially be called, and before the structure is built up for asWidget.
   * 
   * @param logger
   * @param context
   * @param toGenerate
   * @param sw
   * @param m
   * @param editor
   */
  protected void writeInitializeEditor(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw, JMethod m, JClassType editor) {
    sw.println("this.%2$s = GWT.create(%1$s.class);", editor.getQualifiedSourceName(), m.getName());
  }

  /**
   * Responsible for building the widget structure required for the given method m. By default, 
   * asWidget will be skipped, and this will not be invoked if the return type of m does not
   * implement IsWidget.
   * 
   * @param logger
   * @param context
   * @param toGenerate
   * @param sw
   * @param m
   */
  protected abstract void writeAppendEditorToRoot(TreeLogger logger, GeneratorContext context, JClassType toGenerate, SourceWriter sw, JMethod m);

  /**
   * Initialize details required for each field that is needed. This is called from within 
   * {@link #writeDefaultConstructorContents(TreeLogger, GeneratorContext, JClassType, SourceWriter)},
   * in order starting at the interface passed in 
   * 
   * @param logger
   * @param context
   * @param type
   * @param sw
   * @param visited  methods already visited to be sure we don't init/add things multiple times
   */
  protected void writeInitForMethodsInType(TreeLogger logger, GeneratorContext context, JClassType toGenerate, JClassType type, SourceWriter sw, Set<JMethod> visited) {
    TypeOracle oracle = context.getTypeOracle();
    JClassType editorType = oracle.findType(Editor.class.getName());
    JClassType isWidgetType = oracle.findType(IsWidget.class.getName());
    for (JMethod m : type.getMethods()) {
      if (visited.contains(m)) {
        continue;
      }

      visited.add(m);

      if (m.getName().equals("asWidget")) {
        // Nothing to do, carry on
        continue;
      }

      logger.log(Type.DEBUG, "Appending " + m.getName());

      JClassType editor = m.getReturnType().isClassOrInterface();
      assert editor != null && editor.isAssignableTo(editorType);//not certain we need to take this much care

      // Instantiate the field
      writeInitializeEditor(logger, context, toGenerate, sw, m, editor);


      // Check to see if it implements IsWidget, if not, probably just an editor adapter
      if (editor.isAssignableTo(isWidgetType)) {

        writeAppendEditorToRoot(logger, context, toGenerate, sw, m);
      }
    }
  }
}