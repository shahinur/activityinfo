package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.DefaultBooleanValue;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * A FormField defined by an accessor method on the @Form interface.
 * An instance of this class is made available to the Velocity template engine for
 * each property. The public methods of this class define JavaBeans-style properties
 * that are accessible from templates. For example {@link #getType()} means we can
 * write {@code $f.type} for a Velocity variable {@code $f} that is a {@code Field}.
 */
public class ScalarField {

    String name;
    String alias;
    String type;
    TypeMirror typeMirror;
    ExecutableElement getter;
    String fieldType;


    public String getReadExpression() {
        if(typeMirror.getKind() == TypeKind.BOOLEAN) {
            return getBooleanReadExpression();

        } else if(typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType type = (DeclaredType) typeMirror;
            TypeElement typeElement = (TypeElement) type.asElement();
            if(typeElement.getQualifiedName().contentEquals("java.lang.String")) {
                return "record.getString(" + quote(name) + ")";
            }
        }
        throw new UnsupportedOperationException(typeMirror.toString());
    }

    private String getBooleanReadExpression() {
        DefaultBooleanValue defaultValue = getter.getAnnotation(DefaultBooleanValue.class);
        if(defaultValue == null) {
            return "record.getBoolean(" + quote(name) + ")";
        } else {
            return "record.getBoolean(" + quote(name) + ", " +
                (defaultValue.value() ? "true" : "false") + ")";
        }
    }


    public String getSetterName() {
        return "set" + name.substring(0,1).toUpperCase() + name.substring(1);
    }


    public String getGetterName() {
        return getter.getSimpleName().toString();
    }

    public String getConstantName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0, 1).toUpperCase());
        for(int i=1;i<name.length();++i) {
            if(Character.isUpperCase(name.charAt(i)) &&
                !Character.isUpperCase(name.charAt(i-1))) {

                sb.append("_");
            }
            sb.append(name.substring(i, i+1).toUpperCase());
        }
        return sb.toString();
    }

    private String quote(String name) {
        return "\"" + name + "\"";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public ExecutableElement getGetter() {
        return getter;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getTypeExpression() {
        if(typeMirror.getKind() == TypeKind.BOOLEAN) {
            return "org.activityinfo.model.type.primitive.BooleanType.INSTANCE";

        } else if(typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType type = (DeclaredType) typeMirror;
            TypeElement typeElement = (TypeElement) type.asElement();
            if(typeElement.getQualifiedName().contentEquals("java.lang.String")) {
                return "org.activityinfo.model.type.primitive.TextType.INSTANCE";
            }
        }
        throw new UnsupportedOperationException(typeMirror.toString());
    }
}
