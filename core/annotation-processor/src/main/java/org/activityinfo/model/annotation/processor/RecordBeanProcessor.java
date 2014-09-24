package org.activityinfo.model.annotation.processor;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.activityinfo.model.annotation.RecordBean;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Javac annotation processor (compiler plugin) to generate model accessor implementations
 * and builders for Application models.
 */
@SupportedAnnotationTypes("org.activityinfo.model.annotation.RecordBean")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RecordBeanProcessor extends AbstractProcessor {

    private static final boolean SILENT = false;
    public static final String GENERATED_CLASS_SUFFIX = "Serde";


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Collection<? extends Element> annotatedElements =
            roundEnv.getElementsAnnotatedWith(RecordBean.class);
        Collection<? extends TypeElement> types = ElementFilter.typesIn(annotatedElements);

        for (TypeElement type : types) {
            try {
                processType(type);
            } catch (AbortProcessingException e) {
                // We can't complete generation for this model, but continue with the others

            } catch (Exception e) {
                // Don't propagate this exception, which will confusingly crash the compiler.
                // Instead, report a compiler error with the stack trace.
                String trace = Throwables.getStackTraceAsString(e);
                reportError("@" + RecordBean.class.getSimpleName() + " processor threw an exception: " + trace, type);
            }
        }
        return true; // no further processing of this annotation type
    }

    /**
     * Issue a compilation error. This method does not throw an exception, since we want to
     * continue processing and perhaps report other errors. It is a good idea to introduce a
     * test case in CompilationErrorsTest for any new call to reportError(...) to ensure that we
     * continue correctly after an error.
     */
    private void reportError(String msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }


    private void note(String msg) {
        if (!SILENT) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    private void reportWarning(String msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg, e);
    }

    /**
     * Issue a compilation error and abandon the processing of this class. This does not prevent
     * the processing of other classes.
     */
    private void abortWithError(String msg, Element e) {
        reportError(msg, e);
        throw new AbortProcessingException();
    }

    private void processType(TypeElement type) {
        RecordBean recordBean = type.getAnnotation(RecordBean.class);
        if (recordBean == null) {
            // This shouldn't happen unless the compilation environment is buggy,
            // but it has happened in the past and can crash the compiler.
            abortWithError("annotation processor for @" + RecordBean.class.getName() +
                  " was invoked with a type that "
                + "does not have that annotation; this is probably a compiler bug", type);
        }
        if (type.getKind() != ElementKind.CLASS) {
            abortWithError("@" + RecordBean.class.getName() + " only applies to classes", type);
        }

        SerdeTemplateVars vars = new SerdeTemplateVars();
        vars.pkg = TypeSimplifier.packageNameOf(type);
        vars.beanClass = classNameOf(type);
        vars.simpleClassName = TypeSimplifier.simpleNameOf(vars.beanClass);
        vars.serdeClass = TypeSimplifier.simpleNameOf(generatedSubclassName(type));
        vars.formClassId = recordBean.classId();

        defineVarsForType(type, vars);

        String text = vars.toText();
        System.out.println(text);
        writeSourceFile(generatedSubclassName(type), text, type);
    }

    private String generatedClassName(TypeElement type, String suffix) {
        String name = type.getSimpleName().toString();
        while (type.getEnclosingElement() instanceof TypeElement) {
            type = (TypeElement) type.getEnclosingElement();
            name = type.getSimpleName() + "_" + name;
        }
        String pkg = TypeSimplifier.packageNameOf(type);
        String dot = pkg.isEmpty() ? "" : ".";
        return pkg + dot + name + suffix;
    }

    private String generatedSubclassName(TypeElement type) {
        return generatedClassName(type, GENERATED_CLASS_SUFFIX);
    }


    // Return the name of the class, including any enclosing classes but not the package.
    private static String classNameOf(TypeElement type) {
        String name = type.getQualifiedName().toString();
        String pkgName = TypeSimplifier.packageNameOf(type);
        if (!pkgName.isEmpty()) {
            return name.substring(pkgName.length() + 1);
        } else {
            return name;
        }
    }

    private void writeSourceFile(String className, String text, TypeElement originatingType) {
        try {
            note(text);
            JavaFileObject sourceFile =
                processingEnv.getFiler().createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "Could not write generated class " + className + ": " + e);
        }
    }

    private void defineVarsForType(TypeElement type, SerdeTemplateVars vars) {
        Types typeUtils = processingEnv.getTypeUtils();

        Map<String, ExecutableElement> methods = Maps.newHashMap();
        findGetterMethods(type, methods);

        String pkg = TypeSimplifier.packageNameOf(type);

        List<Field> fields = new ArrayList<>();
        List<ListField> listFields = new ArrayList<>();

        for(ExecutableElement getter : methods.values()) {
            TypeMirror fieldType = getter.getReturnType();
            if(isList(fieldType)) {
                ListField field = new ListField();
                field.name = parseFieldName(getter);
                field.getter = getter;
                field.elementType = listElementType(fieldType);
                listFields.add(field);

            } else {
                Field field = new Field();
                field.name = parseFieldName(getter);
                field.getter = getter;
                field.type = qualifiedNameOf(fieldType);
                field.typeMirror = fieldType;
                fields.add(field);
            }
        }
        vars.fields = fields;
        vars.listFields = listFields;
    }

    private String serdeClassType(TypeMirror typeMirror) {
        TypeElement type = (TypeElement) processingEnv.getTypeUtils().asElement(typeMirror);
        return generatedClassName(type, GENERATED_CLASS_SUFFIX);
    }


    private boolean isList(TypeMirror fieldType) {
        Types typeUtils = processingEnv.getTypeUtils();
        Element element = typeUtils.asElement(fieldType);
        if(element instanceof TypeElement) {
            TypeElement type = (TypeElement) element;
            return type.getQualifiedName().contentEquals("java.util.List");
        } else {
            return false;
        }
    }

    private String listElementType(TypeMirror listType) {
        // Get the type argument of List<E> where E is the element type
        DeclaredType declaredType = (DeclaredType) listType;
        TypeMirror elementType = declaredType.getTypeArguments().get(0);

        return qualifiedNameOf(elementType);
    }

    /**
     *
     * @param typeMirror
     * @return the qualified name of the
     */
    private String qualifiedNameOf(TypeMirror typeMirror) {
        return typeMirror.toString();
//        TypeElement elementType = (TypeElement) processingEnv.getTypeUtils().asElement(typeMirror);
//        assert elementType != null : "asElement(" + typeMirror + ") is null";
//        return elementType.getQualifiedName().toString();
    }

    private TypeMirror getTypeMirror(Class<?> c) {
        return processingEnv.getElementUtils().getTypeElement(c.getName()).asType();
    }

    private void findGetterMethods(TypeElement type, Map<String, ExecutableElement> methods) {
        note("Looking at methods in " + type);
        Types typeUtils = processingEnv.getTypeUtils();
        for (TypeMirror superInterface : type.getInterfaces()) {
            findGetterMethods((TypeElement) typeUtils.asElement(superInterface), methods);
        }

        // Add each method of this class
        List<ExecutableElement> theseMethods = ElementFilter.methodsIn(type.getEnclosedElements());
        for (ExecutableElement method : theseMethods) {

            String fieldName = parseFieldName(method);
            if(fieldName == null) {
                // continue processing to find all errors
                continue;
            }
            if(!methods.containsKey(fieldName)) {
                methods.put(fieldName, method);
            }
        }
    }


    public String parseFieldName(ExecutableElement getter) {
        String getterName = getter.getSimpleName().toString();
        String prefix;
        if (getterName.startsWith("get")) {
            prefix = "get";
        } else if (getterName.startsWith("is")) {
            prefix = "is";
        } else {
            return null;
        }

        if(!getter.getParameters().isEmpty()) {
            return null;
        }

        return
            getterName.substring(prefix.length(), prefix.length() + 1).toLowerCase() +
            getterName.substring(prefix.length() + 1);
    }
}
