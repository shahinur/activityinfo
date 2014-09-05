package org.activityinfo.store.cloudsql;

import com.google.common.collect.Iterables;
import org.activityinfo.model.resource.ResourceId;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class QueryBuilder {

    private StringBuilder where = new StringBuilder();

    public static QueryBuilder where() {
        return new QueryBuilder();
    }

    public QueryBuilder ownedBy(Set<ResourceId> ownerIds) {
        appendCriteria("owner_id", ownerIds);
        return this;
    }

    public QueryBuilder ownedBy(ResourceId resourceId) {
        return ownedBy(Collections.singleton(resourceId));
    }

    public QueryBuilder ofClass(Set<ResourceId> classIds) {
        appendCriteria("class_id", classIds);
        return this;
    }

    public QueryBuilder ofClass(ResourceId classId) {
        return ofClass(Collections.singleton(classId));
    }

    public QueryBuilder withId(Set<ResourceId> resourceIds) {
        appendCriteria("id", resourceIds);
        return this;
    }

    public String sql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, class_id, owner_id, label, version, sub_tree_version FROM resource WHERE ");
        sql.append(where);
        return sql.toString();
    }

    public void appendCriteria(String columnName, Set<ResourceId> ids) {
        if(where.length() > 0) {
            where.append(" AND ");
        }
        where.append(columnName);
        if(ids.size() == 1) {
            where.append(" = ");
            appendQuotedId(Iterables.getOnlyElement(ids));
        } else {
            where.append(" in (");
            boolean needsComma = false;
            for(ResourceId id : ids) {
                if(needsComma) {
                    where.append(", ");
                }
                appendQuotedId(id);
                needsComma = true;
            }
            where.append(')');
        }
    }

    private void appendQuotedId(ResourceId id) {
        where.append('\'');
        String string = id.asString();
        for(int i=0;i!=string.length();++i) {
            int cp = string.codePointAt(i);
            if(!Character.isLetterOrDigit(cp) &&
                    cp != '_' && cp != ':') {
                throw new IllegalArgumentException("Invalid character '" + new String(Character.toChars(cp)) + "' in " +
                                                   "resource id " + id );
            }
            where.appendCodePoint(cp);
        }
        where.append('\'');
    }

    @Override
    public String toString() {
        return where.toString();
    }

}
