package org.activityinfo.service.tables;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.service.tables.views.*;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Constructs a set of ColumnViews with a single pass over a set of FormInstances
 */
public class TableScan {

    private ResourceId classId;
    private ResourceStore store;

    private Optional<PrimaryKeyMapBuilder> primaryKeyMapBuilder = Optional.absent();
    private Map<String, ColumnViewBuilder> columnMap = Maps.newHashMap();
    private Map<String, ForeignKeyBuilder> foreignKeyMap = Maps.newHashMap();

    private Optional<Integer> rowCount = Optional.absent();

    public TableScan(ResourceStore resourceStore, ResourceId classId) {
        this.store = resourceStore;
        this.classId = classId;
    }

    public Supplier<ColumnView> fetchColumn(FormTree.Node node, @Nonnull ColumnType columnType) {

        // compose a unique key for this column (we don't want to fetch twice!)
        String columnKey = columnKey(node, columnType);

        // create the column builder if it doesn't exist
        ColumnViewBuilder builder = columnMap.get(columnKey);
        if(builder == null) {
            builder = createBuilder(node, columnType);
            columnMap.put(columnKey, builder);
        }
        return builder;
    }


    public Supplier<ColumnView> fetchPrimaryKeyColumn() {
        String columnKey = "__id";

        IdColumnBuilder builder = (IdColumnBuilder) columnMap.get(columnKey);
        if(builder == null) {
            builder = new IdColumnBuilder();
            columnMap.put(columnKey, builder);
        }
        return builder;
    }


    public Supplier<ColumnView> fetchEmptyColumn(final ColumnType type) {
        return new Supplier<ColumnView>() {
            @Override
            public ColumnView get() {
                return new EmptyColumnView(type, rowCount.get());
            }
        };
    }

    public Supplier<PrimaryKeyMap> fetchPrimaryKey() {
        if(!primaryKeyMapBuilder.isPresent()) {
            primaryKeyMapBuilder = Optional.of(new PrimaryKeyMapBuilder());
        }
        return primaryKeyMapBuilder.get();

    }

    public Supplier<ForeignKeyColumn> fetchForeignKey(String fieldName) {

        // create the key builder if it doesn't exist
        ForeignKeyBuilder builder = foreignKeyMap.get(fieldName);
        if(builder == null) {
            builder = new ForeignKeyBuilder(fieldName);
            foreignKeyMap.put(fieldName, builder);
        }

        return builder;
    }

    private String columnKey(FormTree.Node node, ColumnType columnType) {
        return node.getField().getId().asString() + "_" + columnType.name();
    }

    private ColumnViewBuilder createBuilder(FormTree.Node node, ColumnType columnType) {
        switch(columnType) {
            case STRING:
                return new StringColumnBuilder(checkNotNull(
                        node.getType().getStringReader(
                                node.getField().getId().asString(), FieldType.DEFAULT_COMPONENT)));

            case DATE:
                return new DateColumnBuilder(checkNotNull(
                        node.getType().getDateReader(node.getField().getId().asString(), FieldType.DEFAULT_COMPONENT)));

        }
        throw new UnsupportedOperationException("todo");
    }

    /**
     * Executes the tables scan
     */
    public void execute() {

        ResourceSink[] builders = builderArray();

        int rowCount = 0;

        Iterator<Resource> cursor = store.openCursor(classId);
        while(cursor.hasNext()) {
            Resource resource = cursor.next();
            for(int i=0;i!=builders.length;++i) {
                builders[i].putResource(resource);
            }
            rowCount ++ ;
        }

        // finalize
        for(ColumnViewBuilder builder : columnMap.values()) {
            builder.finalizeView();
        }

        // update row count
        this.rowCount = Optional.of(rowCount);
    }

    private ResourceSink[] builderArray() {
        Iterable<ResourceSink> sinks = Iterables.<ResourceSink>concat(
                primaryKeyMapBuilder.asSet(),
                columnMap.values(),
                foreignKeyMap.values());

        return Iterables.toArray(sinks, ResourceSink.class);
    }
}
