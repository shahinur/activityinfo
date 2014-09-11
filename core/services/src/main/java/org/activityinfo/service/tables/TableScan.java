package org.activityinfo.service.tables;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.activityinfo.model.expr.ExprNode;
import org.activityinfo.model.expr.ExprParser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormEvalContext;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.columns.EmptyColumnView;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.service.tables.views.*;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;

/**
 * Constructs a set of ColumnViews with a single pass over a set of FormInstances
 */
public class TableScan {

    private ResourceId classId;
    private StoreAccessor store;

    private Optional<PrimaryKeyMapBuilder> primaryKeyMapBuilder = Optional.absent();
    private Map<String, ColumnViewBuilder> columnMap = Maps.newHashMap();
    private Map<String, ForeignKeyBuilder> foreignKeyMap = Maps.newHashMap();

    private Optional<Integer> rowCount = Optional.absent();

    private FormEvalContext formEvalContext;

    public TableScan(StoreAccessor resourceStore, FormClass formClass) {
        this.store = resourceStore;
        this.classId = formClass.getId();
        this.formEvalContext = new FormEvalContext(formClass);
    }

    public Supplier<ColumnView> fetchColumn(FormTree.Node node, @Nonnull ColumnType columnType) {

        // compose a unique key for this column (we don't want to fetch twice!)
        String columnKey = columnKey(node, columnType);

        // create the column builder if it doesn't exist
        if(columnMap.containsKey(columnKey)) {
            return columnMap.get(columnKey);
        } else {
            FieldType fieldType = formEvalContext.resolveFieldType(node.getFieldId());
            Optional<ColumnViewBuilder> builder = ViewBuilders.createBuilder(node.getFieldId(), fieldType, columnType);
            if(builder.isPresent()) {
                columnMap.put(columnKey, builder.get());
                return builder.get();
            } else {
                return fetchEmptyColumn(columnType);
            }
        }
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
                return new EmptyColumnView(rowCount.get(), type);
            }
        };
    }

    public Supplier<PrimaryKeyMap> fetchPrimaryKey() {
        if(!primaryKeyMapBuilder.isPresent()) {
            primaryKeyMapBuilder = Optional.of(new PrimaryKeyMapBuilder());
        }
        return primaryKeyMapBuilder.get();

    }

    public Supplier<ColumnView> calculate(String expression) {
        CalcColumnBuilder builder = (CalcColumnBuilder) columnMap.get(expression);
        if (builder == null) {
            try {
                ExprNode expr = ExprParser.parse(expression);
                builder = new CalcColumnBuilder(expr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            columnMap.put(expression, builder);
        }
        return builder;
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


    /**
     * Executes the tables scan
     */
    public void execute() throws Exception {

        FormSink[] builders = builderArray();

        int rowCount = 0;

        Iterator<Resource> cursor = store.openCursor(classId);
        while(cursor.hasNext()) {
            formEvalContext.setInstance(cursor.next());

            for(int i=0;i!=builders.length;++i) {
                builders[i].accept(formEvalContext);
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

    private FormSink[] builderArray() {
        Iterable<FormSink> sinks = Iterables.<FormSink>concat(
                primaryKeyMapBuilder.asSet(),
                columnMap.values(),
                foreignKeyMap.values());

        return Iterables.toArray(sinks, FormSink.class);
    }

}
