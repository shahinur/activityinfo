package org.activityinfo.ui.client.page.config.design.importer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.source.SourceTable;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.BatchCommand;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.result.BatchResult;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.model.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SchemaImporter {

    public interface ProgressListener {
        void submittingBatch(int batchNumber, int batchCount);
    }

    public static class Warning {
        private String message;
        private boolean fatal;

        public Warning(String message, boolean fatal) {
            super();
            this.message = message;
            this.fatal = fatal;
        }

        public String getMessage() {
            return message;
        }

        public boolean isFatal() {
            return fatal;
        }

        @Override
        public String toString() {
            return (fatal ? "ERROR" : "WARN") + ": " + message;
        }

    }

    private Dispatcher dispatcher;
    private UserDatabaseDTO db;

    private ProgressListener listener;

    private AsyncCallback<Void> callback;

    private Map<String, ActivityDTO> activityMap = Maps.newHashMap();
    private Map<String, Integer> locationTypeMap = Maps.newHashMap();

    private List<ActivityDTO> newActivities = Lists.newArrayList();
    private List<IndicatorDTO> newIndicators = Lists.newArrayList();
    private List<AttributeGroupDTO> newAttributeGroups = Lists.newArrayList();
    private List<AttributeDTO> newAttributes = Lists.newArrayList();


    private static boolean REQUIRED = true;
    private static boolean OPTIONAL = false;

    private class Column {
        private int index;
        private String name;
        private int maxLength;

        public Column(int index, String name, int maxLength) {
            super();
            this.index = index;
            this.name = name;
            this.maxLength = maxLength;
        }

        public String get(SourceRow row) {
            if (index < 0) {
                return null;
            }
            String value = row.getColumnValue(index);
            if (value.length() > maxLength) {
                warn("Truncating value '" + value + "' for column '" + name + "': max length is " + maxLength);
                value = value.substring(0, maxLength);
            }
            return value;
        }
    }

    // columns
    private Column activityCategory;
    private Column activityName;

    private SourceTable source;
    private Column formFieldType;
    private Column fieldName;
    private Column fieldCategory;
    private Column fieldDescription;
    private Column fieldUnits;
    private Column fieldMandatory;
    private Column multipleAllowed;
    private Column attributeValue;
    private Column locationType;
    private Column reportingFrequency;

    private int batchNumber;
    private int batchCount;

    private List<Warning> warnings = Lists.newArrayList();

    private LocationTypeDTO defaultLocationType;
    private boolean fatalError;

    public SchemaImporter(Dispatcher dispatcher, UserDatabaseDTO db) {
        this.dispatcher = dispatcher;
        this.db = db;

        for (ActivityDTO activity : db.getActivities()) {
            activityMap.put(activity.getName() + activity.getCategory(), activity);
        }
        for (LocationTypeDTO locationType : db.getCountry().getLocationTypes()) {
            locationTypeMap.put(locationType.getName().toLowerCase(), locationType.getId());
        }
        defaultLocationType = db.getCountry().getLocationTypes().iterator().next();
    }

    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }


    public boolean parseColumns(SourceTable source) {
        this.source = source;
        this.source.parseAllRows();
        findColumns();
        return !fatalError;
    }

    public boolean processRows() {
        processRows(source);
        return !fatalError;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    private void processRows(SourceTable source) {
        for (SourceRow row : source.getRows()) {
            ActivityDTO activity = getActivity(row);
            String fieldType = formFieldType.get(row);
            if ("Indicator".equals(fieldType)) {
                IndicatorDTO indicator = new IndicatorDTO();
                indicator.setName(fieldName.get(row));
                indicator.setCategory(fieldCategory.get(row));
                indicator.setDescription(fieldDescription.get(row));
                indicator.setUnits(fieldUnits.get(row));
                indicator.set("activityId", activity);
                if (isTruthy(fieldMandatory.get(row))) {
                    indicator.setMandatory(true);
                }
                newIndicators.add(indicator);
            } else if ("AttributeGroup".equals(fieldType)) {
                String name = fieldName.get(row);
                AttributeGroupDTO group = activity.getAttributeGroupByName(name);
                if (group == null) {
                    group = new AttributeGroupDTO();
                    group.setName(name);
                    group.set("activityId", activity);

                    if (isTruthy(multipleAllowed.get(row))) {
                        group.setMultipleAllowed(true);
                    }
                    if (isTruthy(fieldMandatory.get(row))) {
                        group.setMandatory(true);
                    }
                    activity.getAttributeGroups().add(group);
                    newAttributeGroups.add(group);
                }
                String attribName = attributeValue.get(row);
                AttributeDTO attrib = findAttrib(group, attribName);
                if (attrib == null) {
                    attrib = new AttributeDTO();
                    attrib.setName(attribName);
                    attrib.set("attributeGroupId", group);
                    newAttributes.add(attrib);
                }
            }
        }
    }


    private AttributeDTO findAttrib(AttributeGroupDTO group, String attribName) {
        for (AttributeDTO attrib : group.getAttributes()) {
            if (attrib.getName().equals(attribName)) {
                return attrib;
            }
        }
        return null;
    }

    private boolean isTruthy(String columnValue) {
        if (columnValue == null) {
            return false;
        }
        String loweredValue = columnValue.toLowerCase().trim();
        return loweredValue.equals("1") ||
               loweredValue.startsWith("t") || // true
               loweredValue.startsWith("y");   // yes

    }

    private ActivityDTO getActivity(SourceRow row) {
        String name = activityName.get(row);
        String category = activityCategory.get(row);

        ActivityDTO activity = activityMap.get(name + category);
        if (activity == null) {
            activity = new ActivityDTO();
            activity.set("databaseId", db.getId());
            activity.setName(name);
            activity.setCategory(category);
            activity.setLocationTypeId(findLocationType(activity, row));

            String frequency = Strings.nullToEmpty(reportingFrequency.get(row));
            if (frequency.toLowerCase().contains("month")) {
                activity.setReportingFrequency(ActivityDTO.REPORT_MONTHLY);
            }

            activityMap.put(name + category, activity);
            newActivities.add(activity);
        }

        return activity;

    }

    private int findLocationType(ActivityDTO activity, SourceRow row) {
        String name = locationType.get(row);
        if (Strings.isNullOrEmpty(name)) {
            warn("No location type given for Activity " + activity.getName());
            return defaultLocationType.getId();
        }
        Integer typeId = locationTypeMap.get(name.toLowerCase());
        if (typeId == null) {
            warn("Invalid location type '%s' given for Activity " + activity.getName());
            return defaultLocationType.getId();
        }
        return typeId;
    }

    private void warn(String message) {
        warnings.add(new Warning(message, false));
    }

    private void error(String message) {
        warnings.add(new Warning(message, true));
        fatalError = true;
    }

    private int findColumn(String name) {
        for (SourceColumn col : source.getColumns()) {
            if (col.getHeader().equalsIgnoreCase(name)) {
                return col.getIndex();
            }
        }
        return -1;
    }

    private Column findColumn(String name, boolean required) {
        return findColumn(name, required, Integer.MAX_VALUE);
    }

    private Column findColumn(String name, boolean required, int maxLength) {
        int col = findColumn(name);
        if (col == -1) {
            if (required) {
                error("Required column not found: " + name);
            } else {
                warn("Optional column not found: " + name);
            }
        }
        return new Column(col, col == -1 ? name : source.getColumnHeader(col), maxLength);
    }


    private void findColumns() {
        activityCategory = findColumn("ActivityCategory", OPTIONAL, 255);
        activityName = findColumn("ActivityName", REQUIRED, 45);
        locationType = findColumn("LocationType", OPTIONAL);
        formFieldType = findColumn("FormFieldType", REQUIRED);
        fieldName = findColumn("Name", REQUIRED);
        fieldCategory = findColumn("Category", OPTIONAL, 50);
        fieldDescription = findColumn("Description", OPTIONAL);
        fieldUnits = findColumn("Units", REQUIRED, 15);
        fieldMandatory = findColumn("Mandatory", OPTIONAL);
        multipleAllowed = findColumn("multipleAllowed", OPTIONAL);
        attributeValue = findColumn("AttributeValue", REQUIRED, 50);
        reportingFrequency = findColumn("ReprotingFrequency", OPTIONAL);
    }

    public void persist(AsyncCallback<Void> callback) {
        this.callback = callback;

        List<List<? extends EntityDTO>> batches = Lists.newArrayList();
        batches.add(newActivities);
        batches.add(newIndicators);
        batches.add(newAttributeGroups);
        batches.add(newAttributes);

        batchCount = batches.size();
        batchNumber = 1;

        persistBatch(batches.iterator());
    }

    private void persistBatch(final Iterator<List<? extends EntityDTO>> batchIterator) {
        BatchCommand batchCommand = new BatchCommand();
        final List<? extends EntityDTO> batch = batchIterator.next();
        for (EntityDTO entity : batch) {
            batchCommand.add(create(entity));
        }
        listener.submittingBatch(batchNumber++, batchCount);

        dispatcher.execute(batchCommand, new AsyncCallback<BatchResult>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(BatchResult result) {
                for (int i = 0; i != result.getResults().size(); ++i) {
                    CreateResult createResult = result.getResult(i);
                    batch.get(i).set("id", createResult.getNewId());
                }
                if (batchIterator.hasNext()) {
                    persistBatch(batchIterator);
                } else {
                    callback.onSuccess(null);
                }
            }
        });
    }

    private Command<CreateResult> create(EntityDTO dto) {
        Map<String, Object> map = Maps.newHashMap();
        for (String propertyName : dto.getPropertyNames()) {
            Object value = dto.get(propertyName);
            if (value instanceof EntityDTO) {
                map.put(propertyName, ((EntityDTO) value).getId());
            } else {
                map.put(propertyName, value);
            }
        }
        return new CreateEntity(dto.getEntityName(), map);
    }

    public void clearWarnings() {
        warnings.clear();
        fatalError = false;
    }
}
