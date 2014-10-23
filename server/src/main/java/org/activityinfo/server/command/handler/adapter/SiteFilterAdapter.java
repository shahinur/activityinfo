package org.activityinfo.server.command.handler.adapter;

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
import org.activityinfo.model.table.ColumnModel;
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

    public ExprValue buildExpression(Filter filter) {
        SetBuilder intersection = new SetBuilder(AndFunction.INSTANCE);

        if(filter.isRestricted(DimensionType.Partner)) {
            intersection.add(partnerFilter(filter.getRestrictions(DimensionType.Partner)));

        } else if(filter.isRestricted(DimensionType.Project)) {
            intersection.add(projectFilter(filter.getRestrictions(DimensionType.Project)));

        } else if(filter.isRestricted(DimensionType.AdminLevel)) {
            intersection.add(adminFilter(filter.getRestrictions(DimensionType.AdminLevel)));

        } else if(filter.isRestricted(DimensionType.Site)) {
            intersection.add(siteFilter(filter.getRestrictions(DimensionType.Site)));
        }
        return intersection.buildFieldValue();
    }

    private ExprNode siteFilter(Set<Integer> ids) {
        return filter(new SymbolExpr(ColumnModel.ID_SYMBOL), SITE_DOMAIN, ids);
    }

    private ExprNode filter(SymbolExpr fieldExpr, char domain, Set<Integer> ids) {
        SetBuilder union = new SetBuilder(OrFunction.INSTANCE);
        for(Integer id : ids) {
            union.add(equals(fieldExpr, CuidAdapter.resourceId(domain, id)));
        }
        return union.build();
    }

    private ExprNode partnerFilter(Set<Integer> partnerIds) {
        return filter(field(PARTNER_FIELD), PARTNER_DOMAIN, partnerIds);
    }

    private SymbolExpr field(int fieldIndex) {
        ResourceId fieldId = CuidAdapter.field(formTree.getRootClassId(), fieldIndex);
        return new SymbolExpr(fieldId.asString());
    }

    private ExprNode projectFilter(Set<Integer> partnerIds) {
        return filter(field(PROJECT_FIELD), PROJECT_DOMAIN, partnerIds);
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
