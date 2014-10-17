package org.activityinfo.model.annotation.processor;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.annotation.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
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

    private static final boolean SILENT = true;
    public static final String GENERATED_CLASS_SUFFIX = "Class";


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

        Map<String, ExecutableElement> methods = Maps.newHashMap();
        findGetterMethods(type, methods);

        List<FieldDescriptor> fields = new ArrayList<>();
        for(ExecutableElement getter : methods.values()) {
            FieldDescriptor field = createFieldDescriptor(getter);
            if(field != null) {
                fields.add(field);
            }
        }
        vars.fields = fields;
    }

    private List<String> enumItems(TypeMirror fieldType) {
        Types typeUtils = processingEnv.getTypeUtils();
        Element element = typeUtils.asElement(fieldType);
        List<String> items = Lists.newArrayList();
        for(Element item : element.getEnclosedElements()) {
            if(item.getKind() == ElementKind.ENUM_CONSTANT) {
                items.add(item.toString());
            }
        }
        return items;
    }

    private boolean isEnum(TypeMirror fieldType) {
        if(fieldType.getKind() == TypeKind.DECLARED) {
            Types typeUtils = processingEnv.getTypeUtils();
            Element element = typeUtils.asElement(fieldType);
            Preconditions.checkNotNull(element, fieldType.toString());
            return element.getKind() == ElementKind.ENUM;
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

            String fieldName = FieldDescriptor.fieldNameFromGetter(method);

            if(fieldName != null && !isTransient(method)) {

                if (!methods.containsKey(fieldName)) {
                    methods.put(fieldName, method);
                }
            }
        }
    }

    public FieldDescriptor createFieldDescriptor(ExecutableElement getter) {

        TypeMirror typeMirror = getter.getReturnType();
        if(typeMirror.getKind() == TypeKind.BOOLEAN) {
            return booleanField(getter);

        } else if(typeMirror.getKind() == TypeKind.DOUBLE) {
            return doubleField(getter);

        } else if(typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType type = (DeclaredType) typeMirror;
            TypeElement typeElement = (TypeElement) type.asElement();
            if (typeElement.getQualifiedName().contentEquals("java.lang.String")) {
                return stringField(getter);

            } else if(typeElement.getQualifiedName().contentEquals("java.util.List")) {
                return listField(getter);

            } else if(typeElement.getQualifiedName().contentEquals("org.activityinfo.model.resource.ResourceId")) {
                return referenceField(getter);

            } else if(typeElement.getAnnotation(RecordBean.class) != null) {
                return recordBeanField(getter, type);

            } else if(typeElement.getKind() == ElementKind.ENUM) {
                return enumField(getter);

            } else if(isFieldValueType(typeMirror)) {
                return fieldValueField(getter);

            }
        }

        reportError("Unsupported @Field type: " + getter.getReturnType(), getter);

        return null;
    }


    private FieldDescriptor stringField(ExecutableElement getter) {
        FieldDescriptor field = new FieldDescriptor(getter);
        field.readExpression = "record.isString(" + quote(serializedNameFromGetter(getter)) + ")";
        field.typeExpression = "org.activityinfo.model.type.primitive.TextType.INSTANCE";
        return field;
    }

    private FieldDescriptor booleanField(ExecutableElement getter) {

        FieldDescriptor field = new FieldDescriptor(getter);

        DefaultBooleanValue defaultValue = getter.getAnnotation(DefaultBooleanValue.class);
        if(defaultValue == null) {
            field.readExpression = "record.getBoolean(" + quote(serializedNameFromGetter(getter)) + ")";
        } else {
            field.readExpression = "record.getBoolean(" + quote(serializedNameFromGetter(getter)) + ", " +
                (defaultValue.value() ? "true" : "false") + ")";
        }

        field.typeExpression = "org.activityinfo.model.type.primitive.BooleanType.INSTANCE";
        return field;
    }

    private FieldDescriptor doubleField(ExecutableElement getter) {
        FieldDescriptor field = new FieldDescriptor(getter);
        field.readExpression = "record.getDouble(" + quote(serializedNameFromGetter(getter)) + ")";
        field.typeExpression = "new org.activityinfo.model.type.number.QuantityType()";
        return field;
    }


    private FieldDescriptor recordBeanField(ExecutableElement getter, DeclaredType type) {
        FieldDescriptor field = new FieldDescriptor(getter);
        String typeClass = type + "Class";
        field.readExpression = typeClass + ".toBean(record.getRecord(" + quote(serializedNameFromGetter(getter)) + "))";
        field.serializedExpression = typeClass + ".toRecord(bean." + field.getGetterName() + "())";
        field.typeExpression = "org.activityinfo.model.type.RecordFieldType(" + typeClass + ".CLASS_ID)";
        return field;
    }

    private FieldDescriptor listField(ExecutableElement getter) {
        FieldDescriptor field = new FieldDescriptor(getter);
        field.elementType = listElementType(getter.getReturnType());
        field.readExpression = "record.getRecordList(" + quote(serializedNameFromGetter(getter)) + ")";
        field.typeExpression = "new org.activityinfo.model.type.ListFieldType(" +
            "new org.activityinfo.model.type.RecordFieldType(" +
                field.getElementClassType() + ".CLASS_ID))";
        return field;
    }


    private FieldDescriptor referenceField(ExecutableElement getter) {
        FieldDescriptor field = new FieldDescriptor(getter);
        String typeClass = "org.activityinfo.model.type.ReferenceValue";
        field.readExpression = typeClass + ".deserializeSingle(record.isRecord(" + quote(field.name) + "))";
        field.serializedExpression = typeClass + ".serialize(bean." + field.getGetterName() + "())";
        field.typeExpression = "org.activityinfo.model.type.ReferenceType.single(FormClass.CLASS_ID)";

        return field;
    }


    private FieldDescriptor enumField(ExecutableElement getter) {
        FieldDescriptor field = new FieldDescriptor(getter);
        String enumType = getter.getReturnType().toString();
        field.readExpression = enumType + ".valueOf(record.getString(" + quote(serializedNameFromGetter(getter)) + "))";
        field.serializedExpression = "bean." + field.getGetterName() + "().name()";
        field.typeExpression = "new org.activityinfo.model.type.enumerated.EnumType()";
        return field;
    }

    private boolean isFieldValueType(TypeMirror typeMirror) {
        return processingEnv.getTypeUtils().isAssignable(typeMirror,
            processingEnv.getElementUtils().getTypeElement("org.activityinfo.model.type.FieldValue").asType());
    }

    private FieldDescriptor fieldValueField(ExecutableElement getter) {

        Element type = processingEnv.getTypeUtils().asElement(getter.getReturnType());
        TypeMirror fieldType = getFieldTypeClass(getter.getReturnType());

        if(fieldType == null) {
            reportError(getter.getReturnType() + " is missing @ValueOf annotation, no way to determine FieldType", type);
            return null;
        }


        FieldDescriptor field = new FieldDescriptor(getter);

        String nameExpr = quote(serializedNameFromGetter(getter));
        String fieldValueType = getter.getReturnType().toString();
        field.readExpression = String.format("(record.has(%s) ? %s.fromRecord(record.getRecord(%s)) : null)",
            nameExpr, fieldValueType, nameExpr);

        field.typeExpression = fieldType + ".INSTANCE";
        return field;
    }

    private TypeMirror getFieldTypeClass(TypeMirror propertyType) {
        Types typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();

        TypeElement valueOfClass = elementUtils.getTypeElement(ValueOf.class.getName());
        ExecutableElement valueMember = getValueMethod(valueOfClass);

        Element type = typeUtils.asElement(propertyType);
        for (AnnotationMirror annotation : type.getAnnotationMirrors()) {
            if(typeUtils.isSameType(annotation.getAnnotationType(), valueOfClass.asType())) {

                AnnotationValue annotationValue = annotation.getElementValues().get(valueMember);
                return (TypeMirror) annotationValue.getValue();
            }
        }
        return null;
    }

    private ExecutableElement getValueMethod(TypeElement valueOfClass) {
        for (Element element : valueOfClass.getEnclosedElements()) {
            if(element instanceof ExecutableElement && element.getSimpleName().contentEquals("value")) {
                return (ExecutableElement) element;
            }
        }
        reportError("Cannot find value() method", valueOfClass);
        throw new AbortProcessingException();
    }


    private String quote(String name) {
        return "\"" + name + "\"";
    }

    private boolean isTransient(ExecutableElement method) {
        return method.getAnnotation(Transient.class) != null;
    }

    public String serializedNameFromGetter(ExecutableElement getter) {
        Field field = getter.getAnnotation(Field.class);
        if(field != null && field.name() != null) {
            return field.name();
        }
        return FieldDescriptor.fieldNameFromGetter(getter);
    }

}
