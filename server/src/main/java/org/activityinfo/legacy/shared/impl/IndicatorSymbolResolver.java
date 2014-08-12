package org.activityinfo.legacy.shared.impl;

import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.core.shared.expr.*;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.SiteDTO;

import java.util.List;
import java.util.Map;

class IndicatorSymbolResolver implements PlaceholderExprResolver {

    private SiteDTO site;
    private int activityId;

    private Map<String, Supplier<Double>> symbolMap = Maps.newHashMap();
    private List<Runnable> calculatedValueUpdaters = Lists.newArrayList();

    public IndicatorSymbolResolver(int activityId, SqlResultSet results) {
        this.activityId = activityId;
        for (final SqlResultSetRow indicatorRow : results.getRows()) {
            if(indicatorRow.getInt("ActivityId") == activityId) {
                String expression = isCalculated(indicatorRow);
                if(expression == null) {
                    addStaticValue(indicatorRow);
                } else {
                    addCalculatedIndicator(indicatorRow, expression);
                }
            }
        }
    }

    public int getActivityId() {
        return activityId;
    }

    public void setSite(SiteDTO site) {
        this.site = site;
    }

    private void addStaticValue(SqlResultSetRow indicatorRow) {
        add(indicatorRow, new StaticValue(indicatorRow.getInt("IndicatorId")));
    }

    private void addCalculatedIndicator(SqlResultSetRow indicatorRow, String expression) {
        CalculatedValue value = new CalculatedValue(expression);
        add(indicatorRow, value);
        calculatedValueUpdaters.add(
                new CalculatedValueUpdater(
                        indicatorRow.getInt("IndicatorId"), value));
    }

    public void populateCalculatedIndicators() {
        for(Runnable updaters : calculatedValueUpdaters) {
            updaters.run();
        }
    }

    private void add(SqlResultSetRow indicatorRow, Supplier<Double> value) {
        int indicatorId = indicatorRow.getInt("IndicatorId");
        symbolMap.put(CuidAdapter.indicatorField(indicatorId).asString(), value);

        String code = indicatorRow.get("nameInExpression");
        if (!Strings.isNullOrEmpty(code)) {
            symbolMap.put(code, value);
        }
    }

    private String isCalculated(SqlResultSetRow indicatorRow) {
        String expression = indicatorRow.getString("Expression");
        boolean calculateAutomatically = indicatorRow.getBoolean("calculatedAutomatically");
        if(calculateAutomatically && !Strings.isNullOrEmpty(expression)) {
            return expression;
        } else {
            return null;
        }
    }


    @Override
    public void resolve(PlaceholderExpr placeholderExpr) {
        String symbol = placeholderExpr.getPlaceholderObj().getPlaceholder();
        Supplier<Double> value = symbolMap.get(symbol);

        if(value == null) {
            throw new UnsupportedOperationException("Placeholder is not supported: " + placeholderExpr);

        } else {
            placeholderExpr.setValue(value.get());
        }
    }

    private class CalculatedValue implements Supplier<Double> {

        private final String expression;
        private boolean calculating = false;

        private CalculatedValue(String expression) {
            this.expression = expression;
        }

        @Override
        public Double get() {

            if(calculating) {
                throw new RuntimeException("Circular reference to " + expression);
            }
            calculating = true;
            try {
                ExprParser parser = new ExprParser(new ExprLexer(expression), IndicatorSymbolResolver.this);
                ExprNode exprNode = parser.parse();
                return exprNode.evalReal();
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
                return null;
            } finally {
                calculating = false;
            }
        }
    }

    private class CalculatedValueUpdater implements Runnable {

        private int integerId;
        private Supplier<Double> value;

        private CalculatedValueUpdater(int integerId, Supplier<Double> value) {
            this.integerId = integerId;
            this.value = value;
        }

        @Override
        public void run() {
            site.setIndicatorValue(integerId, value.get());
        }
    }

    private class StaticValue implements Supplier<Double> {

        private final int indicatorId;

        private StaticValue(int indicatorId) {
            this.indicatorId = indicatorId;
        }

        @Override
        public Double get() {
            Double value = site.getIndicatorDoubleValue(indicatorId);
            if(value == null) {
                return 0d; // workaround until we have default values
            } else {
                return value;
            }
        }
    }

}
