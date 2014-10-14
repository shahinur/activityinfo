package org.activityinfo.server.command.handler.adapter;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.AdminEntityDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.formTree.FormTreePrettyPrinter;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.ColumnView;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.RecordFieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalClass;
import org.activityinfo.service.store.StoreReader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SiteFormQuery {
    private static final Logger LOGGER = Logger.getLogger(SiteFormQuery.class.getName());
    private final GetSites query;
    private final StoreReader reader;
    private final ResourceId formClassId;
    private final FormTree formTree;

    public SiteFormQuery(GetSites query, StoreReader reader, ResourceId formClassId) {
        this.reader = reader;
        this.query = query;
        this.formClassId = formClassId;
        this.formTree = reader.getFormTree(formClassId);
    }

    private TableModel buildQuery() {

        FormTree tree = reader.getFormTree(formClassId);

        FormTreePrettyPrinter.print(tree);

        TableModel tableModel = new TableModel(formClassId);
        tableModel.selectResourceId().as("id");
        tableModel.selectExpr(filterExpr());

        Set<ResourceId> referencedClassIds = Sets.newHashSet();

        // Add root fields first
        for(FormTree.Node field : tree.getRootFields()) {
            if(basicType(field.getType())) {
                tableModel.selectField(field.getFieldId());

            } else if(field.getType() instanceof RecordFieldType) {
                RecordFieldType fieldType = (RecordFieldType) field.getType();
                if(fieldType.getClassId().equals(LocalDateIntervalClass.CLASS_ID)) {
                    tableModel.selectField(
                            new FieldPath(field.getFieldId(), LocalDateIntervalClass.END_DATE_FIELD_ID)).as("date2");
                }
            } else if(field.getType() instanceof ReferenceType) {
                findReferenceLabels(field, referencedClassIds);
            }
        }

        // Add labels for all labeled references and their parents
        for(ResourceId referencedClassId : referencedClassIds) {
            tableModel.selectField(new FieldPath(referencedClassId, ApplicationProperties.LABEL_PROPERTY))
                    .as(referenceColumnId(referencedClassId));
        }

        System.out.println(tableModel);

        return tableModel;
    }

    private String filterExpr() {
        return new SiteFilterAdapter(reader, formTree)
                .buildExpression(query.getFilter()).asExpression();
    }

    private String referenceColumnId(ResourceId classId) {
        // Use legacy property names if applicable, mostly for unit tests
        switch(classId.getDomain()) {
            case CuidAdapter.ADMIN_ENTITY_DOMAIN:
                return AdminEntityDTO.getPropertyName(CuidAdapter.getLegacyId(classId));
            case CuidAdapter.PARTNER_FORM_CLASS_DOMAIN:
                return "partnerName";
            case CuidAdapter.PROJECT_CLASS_DOMAIN:
                return "projectName";
            case CuidAdapter.LOCATION_DOMAIN:
                return "locationName";
        }

        return classId.asString();
    }


    public SiteResult executeQuery(int offset, int limit, Optional<SortInfo> sortInfo) {

        LOGGER.log(Level.INFO, "Querying " + formClassId + ", offset = " + offset + ", limit");


        TableModel tableModel = buildQuery();
        TableData tableData = reader.getTable(tableModel);
        int numRows = tableData.getNumRows();

        // Calculate the beginning and ending row indices
        List<SiteDTO> sites = Lists.newArrayList();
        int start = Math.max(0, offset);
        if(limit < 0) {
            limit = Integer.MAX_VALUE;
        }

        if(limit > 0) {

            // Compute the order of the row indices if we're sorting
            int[] order = computeOrder(tableData, sortInfo);

            // Prepare for copying by putting ids / views into arrays
            int numColumns = tableData.getColumns().size();
            String columnIds[] = new String[numColumns];
            ColumnView views[] = new ColumnView[numColumns];
            int j = 0;
            for (Map.Entry<String, ColumnView> entry : tableData.getColumns().entrySet()) {
                columnIds[j] = entry.getKey();
                views[j] = entry.getValue();
                j++;
            }

            // Copy the data into the SiteDTO objects
            for (int i = start; i < numRows; ++i) {
                SiteDTO dto = new SiteDTO();
                dto.setFormClassId(formClassId);

                int ii;
                if (order == null) {
                    ii = i;
                } else {
                    ii = order[i];
                }
                for (j = 0; j < numColumns; ++j) {
                    dto.set(columnIds[j], views[j].get(ii));
                }
                sites.add(dto);

                if(sites.size() >= limit) {
                    break;
                }
            }
        }
        return new SiteResult(sites, offset, numRows);
    }

    private void findReferenceLabels(FormTree.Node field, Set<ResourceId> formClasses) {
        for(FormTree.Node child : field.getChildren()) {
            if(child.getField().isSubPropertyOf(ApplicationProperties.LABEL_PROPERTY)) {
                formClasses.add(child.getDefiningFormClass().getId());

            } else if(child.getField().getType() instanceof ReferenceType) {
                findReferenceLabels(child, formClasses);
            }
        }
    }

    private boolean basicType(FieldType type) {
        return type instanceof QuantityType ||
                type instanceof TextType ||
                type instanceof EnumType ||
                type instanceof BarcodeType;
    }

    private int[] computeOrder(TableData tableData, Optional<SortInfo> sort) {
        if(!sort.isPresent()) {
            return null;
        }

        ColumnView columnView = tableData.getColumnView(sort.get().getSortField());
        if (columnView == null) {
            LOGGER.log(Level.WARNING, "Unknown sort key [" + sort.get().getSortField() + "] " +
                    "columns: " + Joiner.on(", ").join(tableData.getColumns().keySet()));
            return null;
        }

        int[] order = new int[tableData.getNumRows()];
        for (int i = 0; i != order.length; ++i) {
            order[i] = i;
        }

        Ordering ordering = Ordering.natural().nullsFirst();
        if(sort.get().getSortDir() == Style.SortDir.DESC) {
            ordering = ordering.reverse();
        }

        for (int i = 0; i < order.length; i++) {
            for (int j = i; j > 0
                    && ordering.compare(columnView.get(order[j - 1]), columnView.get(order[j])) > 0; j--) {
                final int b = j - 1;
                final int t = order[j];
                order[j] = order[b];
                order[b] = t;
            }
        }
        return order;
    }

}
