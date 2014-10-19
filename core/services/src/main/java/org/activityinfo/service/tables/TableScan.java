package org.activityinfo.service.tables;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.activityinfo.model.expr.eval.FieldBinding;
import org.activityinfo.model.expr.eval.PartialEvaluator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.views.ConstantColumnView;
import org.activityinfo.model.table.views.EmptyColumnView;
import org.activityinfo.service.tables.views.*;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Constructs a set of ColumnViews with a single pass over a set of FormInstances
 */
public class TableScan {

    private static final Logger LOGGER = Logger.getLogger(TableScan.class.getName());
    public static final String PK_COLUMN_KEY = "__id";

    private ResourceId classId;
    private StoreAccessor store;
    private ColumnCache cache;
    private FormClass formClass;

    private Optional<PrimaryKeyMapBuilder> primaryKeyMapBuilder = Optional.absent();
    private Map<String, ColumnScanner> columnMap = Maps.newHashMap();
    private Map<String, ForeignKeyBuilder> foreignKeyMap = Maps.newHashMap();

    private Optional<Integer> rowCount = Optional.absent();

    private PartialEvaluator partialEvaluator;

    public TableScan(StoreAccessor resourceStore, ColumnCache cache, FormClass formClass) {
        this.store = resourceStore;
        this.cache = cache;
        this.formClass = formClass;
        this.classId = formClass.getId();
        this.partialEvaluator = new PartialEvaluator(formClass);
    }

    public Supplier<ColumnView> fetchColumn(FieldPath fieldPath) {

        // compose a unique key for this column (we don't want to fetch twice!)
        String columnKey = columnKey(fieldPath);

        // create the column builder if it doesn't exist
        if(columnMap.containsKey(columnKey)) {
            return columnMap.get(columnKey);
        } else {
            FieldBinding fieldBinding = partialEvaluator.bind(fieldPath);
            ColumnViewBuilder builder = ViewBuilderFactory.get(fieldBinding.getField(), fieldBinding.getType());
            if(builder != null) {
                FieldScanner fieldScanner = new FieldScanner(fieldBinding.getReader(), builder);
                columnMap.put(columnKey, fieldScanner);
                return fieldScanner;

            } else {
                LOGGER.log(Level.SEVERE, "Column " + fieldPath + " has unsupported type: " + fieldBinding.getType());
                //throw new UnsupportedOperationException("Unsupported type for column " + field.getLabel() + ": " + fieldType);
                return fetchEmptyColumn(ColumnType.STRING);
            }
        }
    }

    public Supplier<ColumnView> fetchPrimaryKeyColumn() {
        ColumnScanner builder = columnMap.get(PK_COLUMN_KEY);
        if(builder == null) {
            builder = new IdColumnBuilder();
            columnMap.put(PK_COLUMN_KEY, builder);
        }
        return builder;
    }

    public Supplier<ColumnView> fetchColumn(ResourceId fieldId) {
        return fetchColumn(new FieldPath(fieldId));
    }

    public Supplier<ColumnView> fetchEmptyColumn(final ColumnType type) {
        return new Supplier<ColumnView>() {
            @Override
            public ColumnView get() {
                return new EmptyColumnView(rowCount.get(), type);
            }
        };
    }

    public Supplier<ColumnView> fetchConstantColumn(final Object constantValue) {
        return new Supplier<ColumnView>() {
            @Override
            public ColumnView get() {
                return ConstantColumnView.create(rowCount.get(), constantValue);
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
            FormField field = partialEvaluator.getField(fieldName);
            builder = new ForeignKeyBuilder(partialEvaluator.partiallyEvaluate(field));
            foreignKeyMap.put(fieldName, builder);
        }

        return builder;
    }

    private String columnKey(FieldPath fieldId) {
        return fieldId.toString();
    }


    /**
     * Executes the tables scan
     */
    public void execute() throws Exception {

        // First try to retrieve as much as we can from the cache
        resolveCached();

        // Is there any work left to do after resolving from cache?
        InstanceSink[] builders = builderArray();
        if(builders.length == 0) {
            return;
        }

        int rowCount = 0;

        // Run the query
        Iterator<Resource> cursor = store.openCursor(classId);
        while(cursor.hasNext()) {
            Resource resource = cursor.next();

            for(int i=0;i!=builders.length;++i) {
                builders[i].accept(resource.getId(), resource.getValue());
            }
            rowCount ++ ;
        }

        // finalize
        for(ColumnScanner builder : columnMap.values()) {
            builder.finalizeView();
        }

        // put to cache
        cache.put(formClass.getId(), columnMap);

        // update row count
        this.rowCount = Optional.of(rowCount);
    }

    private void resolveCached() {
        Map<String, ColumnView> cachedViews = cache.getIfPresent(formClass.getId(), columnMap.keySet());

        LOGGER.log(Level.INFO, "Loaded " + cachedViews.size() + " columns from cache");


        if(!cachedViews.isEmpty()) {
            for (Map.Entry<String, ColumnView> cachedEntry : cachedViews.entrySet()) {

                LOGGER.log(Level.INFO, "Loaded " + cachedEntry.getKey() + " from cache");

                ColumnView cachedView = cachedEntry.getValue();
                columnMap.get(cachedEntry.getKey()).useCached(cachedView);
                columnMap.remove(cachedEntry.getKey());
            }
            this.rowCount = Optional.of(cachedViews.values().iterator().next().numRows());
        }
    }

    private InstanceSink[] builderArray() {
        Iterable<InstanceSink> sinks = Iterables.<InstanceSink>concat(
                primaryKeyMapBuilder.asSet(),
                columnMap.values(),
                foreignKeyMap.values());

        return Iterables.toArray(sinks, InstanceSink.class);
    }
}
