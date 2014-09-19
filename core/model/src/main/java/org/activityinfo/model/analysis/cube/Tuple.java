package org.activityinfo.model.analysis.cube;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class Tuple {

    private final double value;
    private final String[][] members;

    Tuple(double value, String[][] members) {
        this.value = value;
        this.members = members;
    }

    public double getValue() {
        return value;
    }

    public Set<String> getMembers(int dimensionIndex) {
        return ImmutableSet.copyOf(this.members[dimensionIndex]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for(int i=0;i!=members.length;++i) {
            if(members[i] != null && members.length > 0) {
                if(i > 0) {
                    sb.append(", ");
                }
                if(members[i].length == 1) {
                    sb.append(members[i][0]);
                } else {
                    sb.append("(");
                    for(int j=0;j<members[i].length;++j) {
                        if(j > 0) {
                            sb.append(", ");
                        }
                        sb.append(members[i][j]);
                    }
                    sb.append(")");
                }
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
