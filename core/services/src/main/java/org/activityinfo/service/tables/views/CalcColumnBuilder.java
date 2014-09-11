package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.activityinfo.model.expr.ExprNode;
import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.DoubleArrayColumnView;

import java.util.List;

public class CalcColumnBuilder implements ColumnViewBuilder {

    private ExprNode expr;

    private final List<Integer> values = Lists.newArrayList();
    private Optional<ColumnView> result = Optional.absent();

    public CalcColumnBuilder(ExprNode expr) {
        this.expr = expr;
    }


    @Override
    public void accept(FormEvalContext instance) {
        expr.evaluate(instance);
    }

    @Override
    public void finalizeView() {

        double[] dv = new double[values.size()];
        for(int i=0;i<values.size();++i) {
            dv[i] = values.get(i);
        }

        result = Optional.of((ColumnView)new DoubleArrayColumnView(dv));
    }

    @Override
    public ColumnView get() {
        return result.get();
    }
}
