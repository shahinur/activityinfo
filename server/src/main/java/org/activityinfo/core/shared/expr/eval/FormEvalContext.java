package org.activityinfo.core.shared.expr.eval;

import com.google.api.client.util.Lists;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.CalculatedFieldType;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.promise.Promise;

import java.util.List;
import java.util.Map;

public class FormEvalContext implements EvalContext {

    /**
     * Maps form ids and codes to a value source, either
     * a static field or calculated field
     */
    private final Map<String, ValueSource> symbolMap = Maps.newHashMap();

    private final Map<String, ValueSource> fieldMap = Maps.newHashMap();

    private final FormClass formClass;
    private final ResourceLocator resourceLocator;

    private Resource formInstance;

    public FormEvalContext(FormClass formClass, ResourceLocator resourceLocator) {
        this.formClass = formClass;
        this.resourceLocator = resourceLocator;

        for (FormField field : formClass.getFields()) {
            ValueSource source = createValueSource(field);
            if (field.hasCode()) {
                symbolMap.put(field.getCode(), source);
            }
            symbolMap.put(field.getId().asString(), source);
            fieldMap.put(field.getId().asString(), source);
        }

        // TODO: cleanup hack: enum values need to be treated as constants, not symbols!

        for (FormField field : formClass.getFields()) {
            if (field.getType() instanceof EnumType) {
                for (EnumValue item : ((EnumType) field.getType()).getValues()) {
                    symbolMap.put(item.getId().asString(), new ConstantValue(new EnumFieldValue(item.getId())));
                }
            } else if (field.getType() instanceof ReferenceType && resourceLocator != null) {
                queryReferences((ReferenceType) field.getType());
            }
        }
    }

    public List<Promise<Void>> putInContextReferenceTypes() {
        if (resourceLocator == null) {
            throw new NullPointerException("Resource locator is null.");
        }

        List<Promise<Void>> promises = Lists.newArrayList();
        for (FormField field : formClass.getFields()) {
            if (field.getType() instanceof ReferenceType) {
                promises.add(queryReferences((ReferenceType) field.getType()));
            }
        }
        return promises;
    }

    private Promise<Void> queryReferences(ReferenceType referenceType) {
        TableModel tableModel = new TableModel(Iterables.getOnlyElement(referenceType.getRange()));
        tableModel.addResourceId("id");
        tableModel.addColumn("label").select().fieldPath(ApplicationProperties.LABEL_PROPERTY);

        return resourceLocator.queryTable(tableModel).then(new Function<TableData, Void>() {
            @Override
            public Void apply(TableData table) {
                ColumnView idView = table.getColumnView("id");
                ColumnView labelView = table.getColumnView("label");
                for (int i = 0; i < idView.numRows(); i++) {
                    String idString = idView.getString(i);
                }
                return null;
            }
        });
    }

    public FormEvalContext(FormClass formClass, Resource resource, ResourceLocator resourceLocator) {
        this(formClass, resourceLocator);
        setInstance(resource);
    }

    public FormEvalContext(FormClass formClass, FormInstance instance, ResourceLocator resourceLocator) {
        this(formClass, resourceLocator);
        setInstance(instance.asResource());
    }

    public void setInstance(Resource resource) {
        this.formInstance = resource;
    }

    public void setInstance(FormInstance instance) {
        setInstance(instance.asResource());
    }

    public ResourceId getId() {
        return formInstance.getId();
    }

    public FieldValue getFieldValue(String fieldName) {
        assert formInstance != null;
        return fieldMap.get(fieldName).getValue(formInstance, this);
    }

    public FieldValue getFieldValue(ResourceId fieldId) {
        return getFieldValue(fieldId.asString());
    }

    private ValueSource createValueSource(FormField field) {

        if (field.getType() instanceof CalculatedFieldType) {
            return new CalculatedField(field);
        } else {
            return new StaticField(field);
        }
    }

    public FieldType resolveFieldType(ResourceId fieldId) {
        return fieldMap.get(fieldId.asString()).resolveType(this);
    }

    @Override
    public FieldValue resolveSymbol(String symbolName) {
        return lookupSymbol(symbolName).getValue(formInstance, this);
    }

    @Override
    public FieldType resolveSymbolType(String name) {
        return lookupSymbol(name).resolveType(this);
    }

    private ValueSource lookupSymbol(String symbolName) {
        ValueSource valueSource = symbolMap.get(symbolName);
        if (valueSource == null) {
            throw new RuntimeException("Unknown symbol '" + symbolName + "'");
        }
        return valueSource;
    }
}
