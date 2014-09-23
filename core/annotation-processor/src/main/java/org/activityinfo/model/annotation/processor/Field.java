package org.activityinfo.model.annotation.processor;

import org.activityinfo.model.annotation.DefaultBooleanValue;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;

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
public class Field {

    String name;
    String type;
    TypeMirror typeMirror;
    ExecutableElement getter;
    FieldType fieldType;


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
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }


    public String getGetterName() {
        return getter.getSimpleName().toString();
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

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getTypeExpression() {
        if(typeMirror.getKind() == TypeKind.BOOLEAN) {
            return BooleanType.class.getName() + ".INSTANCE";

        } else if(typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType type = (DeclaredType) typeMirror;
            TypeElement typeElement = (TypeElement) type.asElement();
            if(typeElement.getQualifiedName().contentEquals("java.lang.String")) {
                return TextType.class.getName() + ".INSTANCE";
            }
        }
        throw new UnsupportedOperationException(typeMirror.toString());
    }
}
