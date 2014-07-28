package org.activityinfo.core.shared.expr;

public class ConstantExpr extends ExprNode<Double> {

    private double value;

    public ConstantExpr(double value) {
        super();
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public Double evalReal() {
        return value;
    }

    @Override
    public String toString() {
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
        ConstantExpr other = (ConstantExpr) obj;
        if (Double.doubleToLongBits(value) != Double
                .doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }
}
