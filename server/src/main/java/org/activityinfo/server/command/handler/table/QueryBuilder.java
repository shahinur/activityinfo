package org.activityinfo.server.command.handler.table;

import com.google.common.collect.Iterables;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.expr.eval.PartialEvaluator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.*;
import org.activityinfo.service.store.ResourceStore;

public class QueryBuilder implements FormClassVisitor<Void> {

    private final TableModel tableModel;

    public QueryBuilder(ResourceId formClassId) {
        this.tableModel = new TableModel(formClassId);
        tableModel.selectResourceId().as("id");
    }

    public static TableModel build(FormClass rootForm) {
       // FormClass rootForm = Iterables.getOnlyElement(formTree.getRootFormClasses().values());
        QueryBuilder builder = new QueryBuilder(rootForm.getId());
        PartialEvaluator evaluator = new PartialEvaluator(rootForm);
        for(FormField field : rootForm.getFields()) {
            if(field.getType() instanceof ReferenceType) {

            } else {
                FieldReader fieldReader = evaluator.partiallyEvaluate(field);
                fieldReader.getType().accept(field, builder);
            }
        }
        return builder.tableModel;
    }


    @Override
    public Void visitQuantityField(final FormField field, QuantityType type) {
        tableModel.selectField(field.getId());
        return null;
    }

    @Override
    public Void visitTextField(FormField field, TextType type) {
        return null;
    }

    @Override
    public Void visitReferenceField(FormField field, ReferenceType type) {
        // include parent fields
        return null;
    }

    @Override
    public Void visitEnumField(FormField field, EnumType type) {
        return null;
    }

    @Override
    public Void visitBarcodeField(FormField field, BarcodeType type) {
        return null;
    }

    @Override
    public Void visitBooleanField(FormField field, BooleanType type) {
        return null;
    }

    @Override
    public Void visitCalculatedField(FormField field, CalculatedFieldType type) {
        return null;
    }

    @Override
    public Void visitGeoPointField(FormField field, GeoPointType type) {
        return null;
    }

    @Override
    public Void visitImageField(FormField field, ImageType type) {
        return null;
    }

    @Override
    public Void visitLocalDateIntervalField(FormField field, LocalDateIntervalType type) {
        return null;
    }

    @Override
    public Void visitExprField(FormField field, ExprFieldType type) {
        return null;
    }

    @Override
    public Void visitLocalDateField(FormField field, LocalDateType localDateType) {
        tableModel.selectField(field.getId());
        return null;
    }

    @Override
    public Void visitNarrativeField(FormField field, NarrativeType narrativeType) {
        return null;
    }

    @Override
    public Void visitMonthField(FormField field, MonthType monthType) {
        return null;
    }

    @Override
    public Void visitYearField(FormField field, YearType yearType) {
        return null;
    }

    @Override
    public Void visitMissingField(FormField field, MissingFieldType missingFieldType) {
        return null;
    }
}
