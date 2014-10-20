package org.activityinfo.service.tables;

import com.google.common.base.*;
import com.google.common.collect.Lists;
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.diagnostic.AmbiguousSymbolException;
import org.activityinfo.model.expr.diagnostic.SymbolNotFoundException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.formTree.FormTreePrettyPrinter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnModel;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.model.type.time.TemporalValue;
import org.activityinfo.service.tables.function.ColumnFunctions;
import org.activityinfo.service.tables.views.ColumnFilter;

import java.util.List;
import java.util.logging.Logger;

/**
 * Constructs a set of rows from a given RowSource model.
 *
 * Several row sources may combine to form a single logical table.
 */
public class RowSetBuilder {


    private static final Logger LOGGER = Logger.getLogger(RowSetBuilder.class.getName());

    private TableQueryBatchBuilder batchBuilder;
    private FormTree tree;
    private FormClass rootFormClass;
    private ResourceId formClassId;
    private List<ColumnModel> columns;

    private final ColumnExprVisitor columnVisitor = new ColumnExprVisitor();

    /**
     * Represents a field matched by a symbol, along
     * with (optionally) a restriction on which value of the field to choose.
     */
    private static class FieldMatch {
        private FormTree.Node field;

        private Predicate<FormTree.Node> childPredicate;

        private FieldMatch(FormTree.Node field, Predicate<FormTree.Node> childPredicate) {
            this.field = field;
            this.childPredicate = childPredicate;
        }

        public FieldMatch(FormTree.Node field) {
            this.field = field;
            this.childPredicate = Predicates.alwaysTrue();
        }

        @Override
        public String toString() {
            return field.toString();
        }
    }

    private static class FormClassPredicate implements Predicate<FormTree.Node> {

        private final ResourceId formClassId;

        private FormClassPredicate(ResourceId formClassId) {
            this.formClassId = formClassId;
        }

        @Override
        public boolean apply(FormTree.Node input) {
            return input.getDefiningFormClass().getId().equals(formClassId);
        }
    }

    public RowSetBuilder(ResourceId formClassId, TableQueryBatchBuilder batchBuilder) {
        this.formClassId = formClassId;
        this.batchBuilder = batchBuilder;
        this.tree = batchBuilder.getFormTree(formClassId);
        this.rootFormClass = tree.getRootFormClasses().get(formClassId);
    }

    public Supplier<ColumnView> fetch(String expression) {
        ExprNode expr = ExprParser.parse(expression);
        try {
            return expr.accept(columnVisitor);

        } catch(SymbolNotFoundException e) {
            // TODO: I think we should be stricter here but we have unit tests that rely on
            // unmatched symbols mapping to empty columns
            LOGGER.warning("Could not find symbol " + e.getMessage() + " in expression '" + expression + "' " +
                "in root form class " + rootFormClass.getId());
            FormTreePrettyPrinter.print(tree);

            return batchBuilder.addEmptyColumn(rootFormClass);

        }
    }

    public Function<ColumnView, ColumnView> filter(ExprValue filter) {
        if(filter == null) {
            return Functions.identity();
        } else {
            return new ColumnFilter(fetch(filter.getExpression()));
        }
    }

    public Supplier<ColumnView> fetch(ExprValue exprValue) {
        return fetch(exprValue.getExpression());
    }

    public Supplier<ColumnView> fetch(FormField field) {
        return batchBuilder.addColumn(tree.getRootField(field.getId()));
    }


    private Supplier<ColumnView> constant(Object value) {
        return batchBuilder.addConstantColumn(rootFormClass, value);
    }

    private FieldMatch resolveSymbol(String name) {
        return resolveSymbol(tree.getRootFields(), name);
    }

    private FieldMatch resolveSymbol(List<FormTree.Node> fields, String name) {
        return resolveSymbol(fields, Predicates.<FormTree.Node>alwaysTrue(), name);
    }

    private FieldMatch resolveSymbol(List<FormTree.Node> fields, Predicate<FormTree.Node> predicate, String name) {
        // first try to resolve by id.
        for(FormTree.Node rootField : fields) {
            if(rootField.getFieldId().asString().equals(name)) {
                return new FieldMatch(rootField);
            }
        }

        // then try to resolve the field by the code or label
        List<FieldMatch> matching = Lists.newArrayList();
        matchSymbol(fields, predicate, name, matching);

        if(matching.size() == 1) {
            return matching.get(0);

        } else if(matching.isEmpty()) {
            throw new SymbolNotFoundException(name);

        } else {
            throw new AmbiguousSymbolException(name, "Could refer to : " +
                Joiner.on(", ").join(matching));
        }
    }

    private void matchSymbol(List<FormTree.Node> fields, Predicate<FormTree.Node> predicate, String symbolName, List<FieldMatch> matching) {
        boolean matched = false;
        for(FormTree.Node fieldNode : fields) {
            if (predicate.apply(fieldNode)) {
                FieldMatch match = matches(symbolName, fieldNode);
                if(match != null) {
                    matching.add(match);
                    matched = true;
                }
            }
        }
        // if we do not have a direct match, consider descendants
        if(!matched) {
            for(FormTree.Node field : fields) {
                matchSymbol(field.getChildren(), predicate, symbolName, matching);
            }
        }
    }

    private FieldMatch matches(String symbolName, FormTree.Node fieldNode) {
        if(symbolName.equalsIgnoreCase(fieldNode.getField().getCode()) ||
           symbolName.equalsIgnoreCase(fieldNode.getField().getLabel())) {
            return new FieldMatch(fieldNode);
        }
        if(symbolName.equals(fieldNode.getFieldId().asString())) {
            return new FieldMatch(fieldNode);
        }
        for(ResourceId superProperty : fieldNode.getField().getSuperProperties()) {
            if(symbolName.equals(superProperty.asString())) {
                return new FieldMatch(fieldNode);
            }
        }
        if(fieldNode.getType() instanceof ReferenceType) {
            ReferenceType fieldType = (ReferenceType) fieldNode.getType();
            for(ResourceId formClassId : fieldType.getRange()) {
                if(formClassId.asString().equals(symbolName)) {
                    return new FieldMatch(fieldNode, new FormClassPredicate(formClassId));
                }
            }
        }
        return null;
    }

    private FieldMatch resolveCompoundExpr(List<FormTree.Node> fields, CompoundExpr expr) {
        if(expr.getValue() instanceof SymbolExpr) {
            FieldMatch parentField = resolveSymbol(fields, ((SymbolExpr) expr.getValue()).getName());
            return resolveSymbol(parentField.field.getChildren(), parentField.childPredicate, expr.getField().getName());

        } else if(expr.getValue() instanceof CompoundExpr) {
            return resolveCompoundExpr(fields, (CompoundExpr) expr.getValue());

        } else {
            throw new UnsupportedOperationException("Unexpected value of compound expr: " + expr.getValue());
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
            if(symbolExpr.getName().equals(ColumnModel.ID_SYMBOL)) {
                return batchBuilder.getIdColumn(rootFormClass);
            } else if(symbolExpr.getName().equals(ColumnModel.CLASS_SYMBOL)) {
                return batchBuilder.addConstantColumn(rootFormClass, rootFormClass.getId().asString());
            }
            FieldMatch fieldMatch = resolveSymbol(symbolExpr.getName());
            return batchBuilder.addColumn(fieldMatch.field);
        }

        @Override
        public Supplier<ColumnView> visitGroup(GroupExpr group) {
            return group.getExpr().accept(this);
        }

        @Override
        public Supplier<ColumnView> visitCompoundExpr(CompoundExpr compoundExpr) {
            FieldMatch match = resolveCompoundExpr(tree.getRootFields(), compoundExpr);
            LOGGER.info("Resolved expr '" + compoundExpr + "' to " + match.field.debugPath());
            return batchBuilder.addColumn(match.field);
        }

        @Override
        public Supplier<ColumnView> visitFunctionCall(final FunctionCallNode call) {
            if(ColumnFunctions.isSupported(call.getFunction())) {
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
            } else {
                return batchBuilder.addExpr(rootFormClass, call.asExpression());
            }
        }
    }


}
