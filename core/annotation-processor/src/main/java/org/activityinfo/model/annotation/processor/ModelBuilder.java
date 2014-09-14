package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.Form;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

public class ModelBuilder {
    private ProcessingEnvironment processingEnv;
    private RoundEnvironment roundEnv;

    public ModelBuilder(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        this.processingEnv = processingEnv;
        this.roundEnv = roundEnv;
    }

    public FormModel buildFormModel(TypeElement classElement) {

        FormModel form = new FormModel();

        for (Element element : classElement.getEnclosedElements()) {
            if(isFieldAccessor(element)) {
                form.addField(buildFieldModel((ExecutableElement) element));

            } else if(isClassIdConstant(element)) {
                form.setClassId(validateClassId((VariableElement) element));
            }
        }

        return form;
    }

    private VariableElement validateClassId(VariableElement element) {
        boolean valid = element.getModifiers().contains(Modifier.PUBLIC) &&
            element.getModifiers().contains(Modifier.STATIC) &&
            element.getModifiers().contains(Modifier.FINAL);


        if(!valid) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                "Expected CLASS_ID to be public static final field, found: " +
                    element);
        }

        return element;
    }

    private boolean isClassIdConstant(Element element) {
        return element instanceof VariableElement &&
            element.getSimpleName().contentEquals("CLASS_ID");
    }

    private FieldModel buildFieldModel(ExecutableElement getter) {
        FieldModel field = new FieldModel();
        field.setName(parseFieldId(getter));
        field.setTypeModel(typeFromGetter(getter));
        return field;
    }

    private boolean isFieldAccessor(Element e) {
        return
            e instanceof ExecutableElement &&
                ((ExecutableElement)e).getParameters().isEmpty() &&
                e.getModifiers().contains(Modifier.PUBLIC) &&
                !e.getModifiers().contains(Modifier.STATIC) &&
                e.getSimpleName().toString().startsWith("get");
    }

    private String parseFieldId(ExecutableElement getter) {
        String getterName = getter.getSimpleName().toString();
        if(getterName.startsWith("get")) {
            return getterName.substring(3, 4).toLowerCase() +
                getterName.substring(4);
        } else if(getterName.startsWith("is")) {
            return getterName.substring(2, 3).toLowerCase() +
                getterName.substring(3);
        } else {
            throw new IllegalArgumentException("Expected getter name to start with 'get' or " +
                " 'is', found: " + getterName);
        }
    }


    private TypeModel typeFromGetter(ExecutableElement getter) {
        switch(getter.getReturnType().getKind()) {
            case BOOLEAN:
                return new SingletonTypeModel(BooleanType.class);

            case DECLARED:

                DeclaredType returnType = (DeclaredType) getter.getReturnType();
                TypeElement classElement = (TypeElement) returnType.asElement();
                if (classElement.getQualifiedName().contentEquals("java.lang.String")) {
                    return new SingletonTypeModel(TextType.class);

                } else if (classElement.getAnnotation(Form.class) != null) {
                    return new SubFormTypeModel(classElement);
                }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
            "Can't determine field type from return type of field getter", getter);

        throw new UnableToCompleteException();

    }
}
