package org.activityinfo.service.tables;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.diagnostic.EvalException;
import org.activityinfo.model.expr.diagnostic.SymbolNotFoundException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnModel;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.model.type.time.TemporalValue;
import org.activityinfo.service.tables.function.ColumnFunctions;

import java.util.List;

/**
 * Constructs a set of rows from a given RowSource model.
 *
 * Several row sources may combine to form a single logical table.
 */
public class RowSetBuilder {


    private TableQueryBatchBuilder batchBuilder;
    private FormTree tree;
    private FormClass rootFormClass;
    private ResourceId formClassId;
    private List<ColumnModel> columns;

    private final ColumnExprVisitor columnVisitor = new ColumnExprVisitor();




    public RowSetBuilder(ResourceId formClassId, TableQueryBatchBuilder batchBuilder) {
        this.formClassId = formClassId;
        this.batchBuilder = batchBuilder;
        this.tree = batchBuilder.getFormTree(formClassId);
        this.rootFormClass = tree.getRootFormClasses().get(formClassId);
    }


    public Supplier<ColumnView> fetch(String expression) {
        ExprNode expr = ExprParser.parse(expression);
        return expr.accept(columnVisitor);
    }

    private Supplier<ColumnView> constant(Object value) {
        return batchBuilder.addConstantColumn(rootFormClass, value);
    }

    private FormTree.Node resolveSymbol(String name) {
        // first try to resolve by id.
        for(FormTree.Node rootField : tree.getRootFields()) {
            if(rootField.getFieldId().asString().equals(name)) {
                return rootField;
            }
        }

        // then try to resolve the field by the code or label
        List<FormTree.Node> matching = Lists.newArrayList();
        for(FormTree.Node rootField : tree.getRootFields()) {
            if(name.equalsIgnoreCase(rootField.getField().getCode())) {
                matching.add(rootField);
            } else if(name.equalsIgnoreCase(rootField.getField().getLabel())) {
                matching.add(rootField);
            }
        }

        if(matching.size() == 1) {
            return matching.get(0);
        } else if(matching.isEmpty()) {
            throw new SymbolNotFoundException(name);
        } else {
            throw new EvalException("Ambiguous symbol [" + name + "] : Could refer to : " +
                Joiner.on(", ").join(matching));
        }
    }

    private class ColumnExprVisitor implements ExprVisitor<Supplier<ColumnView>> {

        @Override
        public Supplier<ColumnView> visitConstant(ConstantExpr node) {
            FieldValue value = node.getValue();
            if(value instanceof TextValue) {
                return constant(((TextValue) value).asString());
            } else if(value instanceof BooleanFieldValue) {
                return constant(((BooleanFieldValue) value).asBoolean());
            } else if(value instanceof Quantity) {
                return constant(((Quantity) value).getValue());
            } else if(value instanceof TemporalValue) {
                return constant(((TemporalValue) value).asInterval().getEndDate());
            } else {
                throw new IllegalArgumentException("value: " + value);
            }
        }

        @Override
        public Supplier<ColumnView> visitSymbol(SymbolExpr symbolExpr) {
            FormTree.Node fieldNode = resolveSymbol(symbolExpr.getName());

            return batchBuilder.addColumn(fieldNode);
        }

        @Override
        public Supplier<ColumnView> visitGroup(GroupExpr expr) {
            return expr.accept(this);
        }

        @Override
        public Supplier<ColumnView> visitCompoundExpr(CompoundExpr compoundExpr) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Supplier<ColumnView> visitFunctionCall(final FunctionCallNode call) {
            final List<Supplier<ColumnView>> arguments = Lists.newArrayList();
            for(ExprNode argument : call.getArguments()) {
                arguments.add(argument.accept(this));
            }
            return new Supplier<ColumnView>() {
                @Override
                public ColumnView get() {
                    List<ColumnView> columns = Lists.newArrayList();
                    for (Supplier<ColumnView> argument : arguments) {
                        columns.add(argument.get());
                    }
                    return ColumnFunctions.create(call.getFunction(), columns);
                }
            };
        }
    }


}
