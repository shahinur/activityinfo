package org.activityinfo.store.cloudsql;

import com.google.common.collect.Iterables;
import org.activityinfo.model.resource.ResourceId;

import java.util.Set;

public class ChildQueryBuilder {

    private Set<ResourceId> ownerIds;
    private Set<ResourceId> classIds;


    public static ChildQueryBuilder ownedBy(Set<ResourceId> ownerIds) {
        ChildQueryBuilder builder = new ChildQueryBuilder();
        builder.ownerIds = ownerIds;
        return builder;
    }

    public ChildQueryBuilder ofClass(Set<ResourceId> classIds) {
        this.classIds = classIds;
        return this;
    }

    public String sql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, class_id, owner_id, label, version, sub_tree_version FROM resource WHERE ");
        appendCriteria(sql, "owner_id", ownerIds);
        sql.append(" AND ");
        appendCriteria(sql, "class_id", classIds);
        return sql.toString();
    }

    public void appendCriteria(StringBuilder sql, String columnName, Set<ResourceId> ids) {
        sql.append(columnName);
        if(ids.size() == 1) {
            sql.append(" = ");
            appendQuotedId(sql, Iterables.getOnlyElement(ids));
        } else {
            sql.append(" in (");
            boolean needsComma = false;
            for(ResourceId id : ids) {
                if(needsComma) {
                    sql.append(", ");
                }
                appendQuotedId(sql, id);
                needsComma = true;
            }
            sql.append(')');
        }
    }

    private void appendQuotedId(StringBuilder sql, ResourceId id) {
        sql.append('\'');
        String string = id.asString();
        for(int i=0;i!=string.length();++i) {
            int cp = string.codePointAt(i);
            if(!Character.isLetterOrDigit(cp) &&
                    cp != '_' && cp != ':') {
                throw new IllegalArgumentException("Invalid character '" + new String(Character.toChars(cp)) + "' in " +
                                                   "resource id " + id );
            }
            sql.appendCodePoint(cp);
        }
        sql.append('\'');
    }
}
