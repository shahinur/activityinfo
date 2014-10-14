package org.activityinfo.model.expr.eval;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.model.expr.*;
import org.activityinfo.model.expr.diagnostic.CircularReferenceException;
import org.activityinfo.model.expr.diagnostic.ExprException;
import org.activityinfo.model.expr.diagnostic.ExprSyntaxException;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.primitive.TextValue;

import java.util.*;

/**
 * Partially evaluates a field-level expression, expanding any calculated indicators
 * on which the expression depends.
 */
public class PartialEvaluator {

    private final FormSymbolTable symbolTable;
    private final Map<String, FieldReader> readers = Maps.newHashMap();
    private final LinkedList<FormField> stack = new LinkedList<>();
    private final PartiallyEvaluatingVisitor visitor = new PartiallyEvaluatingVisitor();
    private final Map<ResourceId, PartialEvaluator> subFormEvaluators;
    private final Map<ResourceId, FormClass> subFormMap;

    public PartialEvaluator(FormClass formClass, Map<ResourceId, FormClass> subFormMap) {
        this(formClass, subFormMap, new HashMap<ResourceId, PartialEvaluator>());
    }

    public PartialEvaluator(FormClass formClass) {
        this(formClass, new ApplicationClassProvider().asMap());
    }

    private PartialEvaluator(FormClass formClass,
                             Map<ResourceId, FormClass> subFormMap,
                             Map<ResourceId, PartialEvaluator> subFormEvaluators) {
        this.subFormMap = subFormMap;
        this.symbolTable = new FormSymbolTable(formClass);
        this.subFormEvaluators = subFormEvaluators;
    }


    public FieldReader partiallyEvaluate(ExprNode node) {
        try {
            return node.accept(visitor);

        } catch(ExprException e) {
            return new ConstantFieldReader(new ErrorValue(e), ErrorType.INSTANCE);
        }
    }

    public FieldBinding bind(FieldPath fieldPath) {
        // Get the root field
        Iterator<ResourceId> path = fieldPath.iterator();
        FormField field = getField(path.next());
        FieldReader reader = partiallyEvaluate(field);

        PartialEvaluator evaluator = this;

        while(path.hasNext()) {
            Preconditions.checkState(reader.getType() instanceof RecordFieldType,
                    "cannot read sub field from " + field.getType());

            evaluator = subFormEvaluator(reader.getType());
            field = evaluator.getField(path.next());
            reader = new SubFieldReader(reader, evaluator.partiallyEvaluate(field));
        }
        return new FieldBinding(field, reader);
    }

    private PartialEvaluator subFormEvaluator(FieldType type) {
        RecordFieldType fieldType = (RecordFieldType) type;
        ResourceId formClassId = fieldType.getClassId();
        PartialEvaluator evaluator = subFormEvaluators.get(formClassId);
        if(evaluator == null) {
            FormClass formClass = subFormMap.get(formClassId);
            if(formClass == null) {
                throw new IllegalArgumentException(formClassId.asString());
            }
            evaluator = new PartialEvaluator(formClass, subFormMap, subFormEvaluators);
            subFormEvaluators.put(formClassId, evaluator);
        }
        return evaluator;
    }

    public FieldReader partiallyEvaluate(FormField field) {
        try {
            return visitor.fieldReader(field);

        } catch(ExprException e) {
            return new ConstantFieldReader(new ErrorValue(e), ErrorType.INSTANCE);
        }
    }

    public FormField getField(ResourceId fieldId) {
        return symbolTable.resolveFieldById(fieldId.asString());
    }

    public FormField getField(String fieldName) {
        return symbolTable.resolveFieldById(fieldName);
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
            stack.add(field);
            try {
                CalculatedFieldType calculatedType = (CalculatedFieldType) field.getType();
                ExprNode calculatedNode = ExprParser.parse(calculatedType.getExpression());
                return calculatedNode.accept(this);

            } finally {
                stack.removeLast();
            }
        }

        private FieldReader recordReader(final FormField field) {
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
            } else if (field.getType().getTypeClass() == RecordFieldType.TYPE_CLASS) {

                final RecordFieldType type = (RecordFieldType) field.getType();
                return new FieldReader() {
                    @Override
                    public FieldValue readField(Record record) {
                        Record value = record.isRecord(fieldName);
                        if(value != null && value.getClassId().equals(type.getClassId())) {
                            return value;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public FieldType getType() {
                        return field.getType();
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
            return expr.getExpr().accept(this);
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
