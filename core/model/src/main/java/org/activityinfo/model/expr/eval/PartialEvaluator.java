package org.activityinfo.model.expr.eval;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.diagnostic.CircularReferenceException;
import org.activityinfo.model.expr.diagnostic.ExprException;
import org.activityinfo.model.expr.diagnostic.ExprSyntaxException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.primitive.TextValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Partially evaluates a field-level expression, expanding any calculated indicators
 * on which the expression depends.
 */
public class PartialEvaluator {

    private final FormSymbolTable symbolTable;
    private final Map<String, FieldReader> readers = Maps.newHashMap();
    private final LinkedList<FormField> stack = new LinkedList<>();
    private final PartiallyEvaluatingVisitor visitor = new PartiallyEvaluatingVisitor();

    public PartialEvaluator(FormClass formClass) {
        this.symbolTable = new FormSymbolTable(formClass);
    }


    public FieldReader partiallyEvaluate(ExprNode node) {
        try {
            return node.accept(visitor);

        } catch(ExprException e) {
            return new ConstantReader(new ErrorValue(e), ErrorType.INSTANCE);
        }
    }


    public FieldReader partiallyEvaluate(FormField field) {
        try {
            return visitor.fieldReader(field);

        } catch(ExprException e) {
            return new ConstantReader(new ErrorValue(e), ErrorType.INSTANCE);
        }
    }


    private static class ConstantReader implements FieldReader {

        private final FieldValue value;
        private final FieldType type;

        private ConstantReader(FieldValue value, FieldType type) {
            this.value = value;
            this.type = type;
        }

        @Override
        public FieldValue readField(Record record) {
            return value;
        }

        @Override
        public FieldType getType() {
            return type;
        }
    }

    private class PartiallyEvaluatingVisitor implements ExprVisitor<FieldReader> {


        @Override
        public FieldReader visitConstant(ConstantExpr node) {
            final FieldValue constantValue = node.getValue();
            final FieldType type = node.getType();
            return new FieldReader() {
                @Override
                public FieldValue readField(Record record) {
                    return constantValue;
                }

                @Override
                public FieldType getType() {
                    return type;
                }
            };
        }

        @Override
        public FieldReader visitSymbol(SymbolExpr symbol) {
            return fieldReader(symbolTable.resolveSymbol(symbol));
        }

        public FieldReader fieldReader(FormField field) {
            // have we already created a FieldReader for this field name?
            FieldReader reader = readers.get(field.getName());
            if (reader != null) {
                return reader;
            }
            if (field.getType() instanceof CalculatedFieldType) {
                // expand this expression
                reader = expandCalculatedField(field);
            } else {
                reader = recordReader(field);
            }

            // cache partial evaluation
            readers.put(field.getName(), reader);

            return reader;
        }

        private FieldReader expandCalculatedField(FormField field) {
            // detect cycles
            if (stack.contains(field)) {
                throw new CircularReferenceException(stack);
            }
            stack.push(field);
            try {
                CalculatedFieldType calculatedType = (CalculatedFieldType) field.getType();
                ExprNode calculatedNode = ExprParser.parse(calculatedType.getExpression());
                return calculatedNode.accept(this);

            } finally {
                stack.pop();
            }
        }

        private FieldReader recordReader(FormField field) {
            final String fieldName = field.getName();
            if (field.getType() instanceof TextType) {
                return new FieldReader() {
                    @Override
                    public FieldValue readField(Record record) {
                        return TextValue.valueOf(record.isString(fieldName));
                    }

                    @Override
                    public FieldType getType() {
                        return TextType.INSTANCE;
                    }
                };
            } else if (field.getType() instanceof BooleanType) {
                return new FieldReader() {
                    @Override
                    public FieldValue readField(Record record) {
                        Object value = record.get(fieldName);
                        if (value instanceof Boolean) {
                            return BooleanFieldValue.valueOf(value == Boolean.TRUE);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public FieldType getType() {
                        return BooleanType.INSTANCE;
                    }
                };
            } else if (field.getType().getTypeClass() instanceof RecordFieldTypeClass) {
                final FieldType type = field.getType();
                final RecordFieldTypeClass typeClass = (RecordFieldTypeClass) type.getTypeClass();
                return new FieldReader() {
                    @Override
                    public FieldValue readField(Record record) {
                        return Types.read(record, fieldName, typeClass);
                    }

                    @Override
                    public FieldType getType() {
                        return type;
                    }
                };
            } else {
                throw new UnsupportedOperationException("Cannot create field reader for field type " + field.getType());
            }
        }

        @Override
        public FieldReader visitGroup(GroupExpr expr) {
            return expr.accept(this);
        }

        @Override
        public FieldReader visitCompoundExpr(CompoundExpr compoundExpr) {
            throw new ExprSyntaxException("Compound expressions not supported in field-level expressions.");
        }

        @Override
        public FieldReader visitFunctionCall(final FunctionCallNode functionCallNode) {

            // Partially evaluate arguments
            final List<FieldReader> arguments = Lists.newArrayList();
            final List<FieldType> argumentTypes = Lists.newArrayList();

            for (ExprNode argumentExpr : functionCallNode.getArguments()) {
                FieldReader argumentReader = argumentExpr.accept(this);
                arguments.add(argumentReader);
                argumentTypes.add(argumentReader.getType());
            }

            // Resolve type of the function's value
            final FieldType functionType = functionCallNode.getFunction().resolveResultType(argumentTypes);

            return new FieldReader() {
                @Override
                public FieldValue readField(Record record) {
                    List<FieldValue> argumentValues = Lists.newArrayList();
                    for (FieldReader argument : arguments) {
                        argumentValues.add(argument.readField(record));
                    }
                    return functionCallNode.getFunction().apply(argumentValues);
                }

                @Override
                public FieldType getType() {
                    return functionType;
                }
            };
        }
    }
}
