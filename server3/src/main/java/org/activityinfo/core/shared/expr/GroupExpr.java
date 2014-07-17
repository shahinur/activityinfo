package org.activityinfo.core.shared.expr;

/**
 * An expression group ()
 */
public class GroupExpr extends ExprNode {

    private ExprNode expr;

    public GroupExpr(ExprNode expr) {
        super();
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "(" + expr.toString() + ")";
    }

    @Override
    public double evalReal() {
        return expr.evalReal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
        GroupExpr other = (GroupExpr) obj;
        return other.expr.equals(expr);
    }
}
