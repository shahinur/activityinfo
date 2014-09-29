package org.activityinfo.model.annotation.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * A FormField defined by an accessor method on the @Form interface.
 * An instance of this class is made available to the Velocity template engine for
 * each property. The public methods of this class define JavaBeans-style properties
 * that are accessible from templates. For example {@link #getType()} means we can
 * write {@code $f.type} for a Velocity variable {@code $f} that is a {@code Field}.
 */
public class FieldDescriptor {

    /**
     * The Java property name
     */
    String name;

    /**
     * The fully qualified type name of the Java property
     */
    String type;

    TypeMirror typeMirror;

    /**
     * The getter method, for example, {@code isVisible} or {@code getColor}
     */
    ExecutableElement getter;

    /**
     * The expression that evaluates to an instance of FieldType
     */
    String typeExpression;

    /**
     * The expression that will read the field's value from a record.
     */
    String readExpression;

    /**
     * The expression that will be set to the record.
     */
    String serializedExpression;

    List<String> enumItems;

    /**
     * For list types, the fully-qualified class name of the element.
     */
    String elementType;

    public FieldDescriptor(ExecutableElement getter) {
        this.getter = getter;
        this.name = fieldNameFromGetter(getter);
        this.serializedExpression = "bean." + getter.getSimpleName() + "()";
    }

    public static String fieldNameFromGetter(ExecutableElement getter) {
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

    public String getReadExpression() {
        return readExpression;
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

    public String getTypeExpression() {
        return typeExpression;
    }

    public String getElementType() {
        return elementType;
    }

    public boolean isList() {
        return elementType != null;
    }

    public List<String> getEnumItems() {
        return enumItems;
    }

    public String getElementClassType() {
        return elementType + "Class";
    }

    public String getSerializedExpression() {
        return serializedExpression;
    }
}
