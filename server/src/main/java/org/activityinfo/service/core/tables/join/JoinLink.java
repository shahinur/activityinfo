package org.activityinfo.service.core.tables.join;

import com.google.common.base.Supplier;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.core.tables.views.ForeignKeyColumn;
import org.activityinfo.service.core.tables.views.PrimaryKeyMap;

import java.util.Collection;

/**
 * Holds the foreignKey / primaryKeyMap columns required
 * to evaluate a left join between a LEFT and a RIGHT table.
 */
public class JoinLink {
    private Supplier<ForeignKeyColumn> foreignKey;
    private Supplier<PrimaryKeyMap> primaryKeyMap;

    public JoinLink(Supplier<ForeignKeyColumn> foreignKey, Supplier<PrimaryKeyMap> primaryKeyMap) {
        this.foreignKey = foreignKey;
        this.primaryKeyMap = primaryKeyMap;
    }

    /**
     *
     * @return the number of rows in the left table.
     */
    public int getLeftRowCount() {
        return foreignKey.get().getNumRows();
    }

    /**
     *
     * @return the foreign key(s) for each row in the LEFT table.
     */
    public ForeignKeyColumn getForeignKey() {
        return foreignKey.get();
    }

    /**
     *
     * @return mapping from the RIGHT's primary keys to the corresponding
     * row indices of the RIGHT table
     */
    public PrimaryKeyMap getPrimaryKeyMap() {
        return primaryKeyMap.get();
    }

    /**
     *
     * @return builds an array which maps each row in the left table
     * to the corresponding row in the right table.
     */
    public int[] buildMapping() {
        ForeignKeyColumn fk = foreignKey.get();
        PrimaryKeyMap pk = primaryKeyMap.get();

        int mapping[] = new int[fk.getNumRows()];
        for(int i=0;i!=mapping.length;++i) {
            Collection<ResourceId> foreignKeys = fk.getKeys(i);
            mapping[i] = pk.getUniqueRowIndex(foreignKeys);
        }
        return mapping;
    }
}

