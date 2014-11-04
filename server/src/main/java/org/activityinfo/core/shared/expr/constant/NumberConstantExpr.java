package org.activityinfo.core.shared.expr.constant;

import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.model.type.number.Quantity;

public class NumberConstantExpr extends ExprNode<Double> implements IsConstantExpr  {

    private double value;

    public NumberConstantExpr(double value) {
        super();
        this.value = value;
    }

    public Quantity getValue() {
        return new Quantity(value);
    }

    @Override
    public Double evalReal() {
        return value;
    }

    @Override
    public String toString() {
        return asExpression();
    }

    @Override
    public String asExpression() {
        return Double.toString(value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumberConstantExpr other = (NumberConstantExpr) obj;
        if (Double.doubleToLongBits(value) != Double
                .doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }
}
