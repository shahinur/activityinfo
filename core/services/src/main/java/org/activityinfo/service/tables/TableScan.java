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

    private ResourceId classId;
    private StoreAccessor store;
    private FormClass formClass;

    private Optional<PrimaryKeyMapBuilder> primaryKeyMapBuilder = Optional.absent();
    private Map<String, ColumnScanner> columnMap = Maps.newHashMap();
    private Map<String, ForeignKeyBuilder> foreignKeyMap = Maps.newHashMap();

    private Optional<Integer> rowCount = Optional.absent();

    private PartialEvaluator partialEvaluator;

    public TableScan(StoreAccessor resourceStore, FormClass formClass) {
        this.store = resourceStore;
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
                columnMap.put(columnKey, new FieldScanner(fieldBinding.getReader(), builder));
                return builder;
            } else {
                LOGGER.log(Level.SEVERE, "Column " + fieldPath + " has unsupported type: " + fieldBinding.getType());
                //throw new UnsupportedOperationException("Unsupported type for column " + field.getLabel() + ": " + fieldType);
                return fetchEmptyColumn(ColumnType.STRING);
            }
        }
    }

    public Supplier<ColumnView> fetchPrimaryKeyColumn() {
        String columnKey = "__id";

        ColumnScanner builder = columnMap.get(columnKey);
        if(builder == null) {
            builder = new IdColumnBuilder();
            columnMap.put(columnKey, builder);
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

        InstanceSink[] builders = builderArray();

        int rowCount = 0;

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

        // update row count
        this.rowCount = Optional.of(rowCount);
    }

    private InstanceSink[] builderArray() {
        Iterable<InstanceSink> sinks = Iterables.<InstanceSink>concat(
                primaryKeyMapBuilder.asSet(),
                columnMap.values(),
                foreignKeyMap.values());

        return Iterables.toArray(sinks, InstanceSink.class);
    }
}
