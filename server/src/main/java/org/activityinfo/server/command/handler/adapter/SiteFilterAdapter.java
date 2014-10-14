package org.activityinfo.server.command.handler.adapter;

import com.google.common.collect.Iterables;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.model.expr.ConstantExpr;
import org.activityinfo.model.expr.ExprNode;
import org.activityinfo.model.expr.FunctionCallNode;
import org.activityinfo.model.expr.SymbolExpr;
import org.activityinfo.model.expr.functions.AndFunction;
import org.activityinfo.model.expr.functions.EqualFunction;
import org.activityinfo.model.expr.functions.ExprFunction;
import org.activityinfo.model.expr.functions.OrFunction;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.type.expr.ExprValue;
import org.activityinfo.service.store.StoreReader;

import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;


public class SiteFilterAdapter {

    private FormTree formTree;
    private StoreReader storeReader;
    private ResourceId rootClassId;
    private int databaseId = -1;

    public SiteFilterAdapter(StoreReader storeReader, FormTree formTree) {
        this.storeReader = storeReader;
        this.rootClassId = formTree.getRootClassId();
        this.formTree = formTree;
    }

    public ExprValue buildExpression(Filter query) {
        SetBuilder intersection = new SetBuilder(AndFunction.INSTANCE);

        if(query.isRestricted(DimensionType.Partner)) {
            intersection.add(partnerFilter(query.getRestrictions(DimensionType.Partner)));

        } else if(query.isRestricted(DimensionType.Project)) {
            intersection.add(projectFilter(query.getRestrictions(DimensionType.Project)));

        } else if(query.isRestricted(DimensionType.AdminLevel)) {
            intersection.add(adminFilter(query.getRestrictions(DimensionType.AdminLevel)));
        }
        return intersection.buildFieldValue();
    }

    private ExprNode partnerFilter(Set<Integer> partnerIds) {
        int databaseId = findDatabaseId();
        SymbolExpr field = new SymbolExpr(field(formTree.getRootClassId(), PARTNER_FIELD));

        SetBuilder union = new SetBuilder(OrFunction.INSTANCE);
        for(Integer partnerId : partnerIds) {
            union.add(equals(field, partnerInstanceId(databaseId, partnerId)));
        }
        return union.build();
    }

    private ExprNode projectFilter(Set<Integer> partnerIds) {
        int databaseId = findDatabaseId();
        SymbolExpr field = new SymbolExpr(field(formTree.getRootClassId(), PARTNER_FIELD));

        SetBuilder union = new SetBuilder(OrFunction.INSTANCE);
        for(Integer partnerId : partnerIds) {
            union.add(equals(field, partnerInstanceId(databaseId, partnerId)));
        }
        return union.build();
    }

    private ExprNode adminFilter(Set<Integer> entityIds) {

        SetBuilder union = new SetBuilder(OrFunction.INSTANCE);
        for(int adminEntityId : entityIds) {
            ResourceId entityId = CuidAdapter.entity(adminEntityId);
            ResourceNode adminEntity = storeReader.getResourceNode(entityId);
            ResourceId levelClassId = adminEntity.getClassId();
            union.addEquals(new SymbolExpr(levelClassId.asString()), entityId);
        }
        return union.build();
    }

    private FunctionCallNode equals(SymbolExpr field, ResourceId resourceId) {
        return new FunctionCallNode(EqualFunction.INSTANCE, field, new ConstantExpr(resourceId.asString()));
    }

    private int findDatabaseId() {
        if(databaseId < 0) {

            // Get the database id from the partner class, in which the database id is encoded
            // For example: P1 is the partner form for databaseId=1

            FormTree.Node partnerField = formTree.getRootField(field(formTree.getRootClassId(), PARTNER_FIELD));
            ResourceId partnerClass = Iterables.getOnlyElement(partnerField.getRange());
            databaseId = CuidAdapter.getLegacyId(partnerClass);
        }
        return databaseId;
    }


    private static class SetBuilder {
        private final ExprFunction operator;
        private ExprNode set = null;

        private SetBuilder(ExprFunction operator) {
            this.operator = operator;
        }

        public void add(ExprNode expr) {
            if(set == null) {
                set = expr;
            } else {
                set = new FunctionCallNode(operator, set, expr);
            }
        }
        public void addEquals(SymbolExpr symbolExpr, ResourceId entityId) {
            add(new FunctionCallNode(EqualFunction.INSTANCE, symbolExpr, new ConstantExpr(entityId.asString())));
        }

        public ExprNode build() {
            if(set == null) {
                return new ConstantExpr(true);
            } else {
                return set;
            }
        }

        public ExprValue buildFieldValue() {
            if(set == null) {
                return null;
            } else {
                return new ExprValue(set.asExpression());
            }
        }
    }


}
