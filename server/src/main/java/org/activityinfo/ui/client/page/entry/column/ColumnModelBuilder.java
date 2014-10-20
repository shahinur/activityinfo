package org.activityinfo.ui.client.page.entry.column;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.type.IndicatorNumberFormat;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.ui.client.page.common.columns.EditableLocalDateColumn;
import org.activityinfo.ui.client.page.common.columns.ReadTextColumn;

import java.util.List;

/**
 * Builder class for constructing a ColumnModel for site grids
 */
public class ColumnModelBuilder {

    private List<ColumnConfig> columns = Lists.newArrayList();

    public ColumnModelBuilder addActivityColumn(final UserDatabaseDTO database) {
        ColumnConfig config = new ColumnConfig("activityId", I18N.CONSTANTS.activity(), 100);
        config.setToolTip(I18N.CONSTANTS.activity());
        config.setRenderer(new GridCellRenderer<SiteDTO>() {

            @Override
            public Object render(SiteDTO model,
                                 String property,
                                 ColumnData config,
                                 int rowIndex,
                                 int colIndex,
                                 ListStore<SiteDTO> store,
                                 Grid<SiteDTO> grid) {

                ActivityDTO activity = database.getActivityById(model.getActivityId());
                return (activity == null ? "" : activity.getName());
            }
        });
        columns.add(config);
        return this;
    }

    public ColumnModelBuilder addActivityColumn(final SchemaDTO schema) {
        ColumnConfig config = new ColumnConfig("activityId", I18N.CONSTANTS.activity(), 100);
        config.setToolTip(I18N.CONSTANTS.activity());
        config.setRenderer(new GridCellRenderer<SiteDTO>() {

            @Override
            public Object render(SiteDTO model,
                                 String property,
                                 ColumnData config,
                                 int rowIndex,
                                 int colIndex,
                                 ListStore<SiteDTO> store,
                                 Grid<SiteDTO> grid) {

                ActivityDTO activity = schema.getActivityById(model.getActivityId());
                return (activity == null ? "" : activity.getName());
            }
        });
        columns.add(config);
        return this;
    }

    public ColumnModelBuilder addDatabaseColumn(final SchemaDTO schema) {
        ColumnConfig config = new ColumnConfig("activityId", I18N.CONSTANTS.database(), 100);
        config.setToolTip(I18N.CONSTANTS.database());
        config.setRenderer(new GridCellRenderer<SiteDTO>() {

            @Override
            public Object render(SiteDTO model,
                                 String property,
                                 ColumnData config,
                                 int rowIndex,
                                 int colIndex,
                                 ListStore<SiteDTO> store,
                                 Grid<SiteDTO> grid) {

                ActivityDTO activity = schema.getActivityById(model.getActivityId());
                return (activity == null ? "" : activity.getDatabaseName());
            }
        });
        columns.add(config);
        return this;
    }

    public ColumnModel build() {
        return new ColumnModel(columns);
    }

    protected ColumnModelBuilder maybeAddProjectColumn(ActivityDTO activity) {
        if (!activity.getProjects().isEmpty()) {
            addProjectColumn();
        }
        return this;
    }

    public void addProjectColumn() {
        columns.add(new ReadTextColumn("project", I18N.CONSTANTS.project(), 100));
    }

    public ColumnModelBuilder maybeAddLockColumn(final ActivityDTO activity) {
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            ColumnConfig columnLocked = new ColumnConfig("x", "", 28);
            columnLocked.setRenderer(new LockedColumnRenderer(activity.getLockedPeriodSet()));
            columnLocked.setSortable(false);
            columnLocked.setMenuDisabled(true);
            columns.add(columnLocked);
        }
        return this;
    }

    public ColumnConfig createIndicatorColumn(IndicatorDTO indicator) {

        ColumnConfig indicatorColumn = new ColumnConfig(indicator.getPropertyName(), indicatorHeader(indicator), 100);
        indicatorColumn.setToolTip(indicator.getName());

        if (indicator.getType() == QuantityType.TYPE_CLASS) {
            indicatorColumn.setAlignment(Style.HorizontalAlignment.RIGHT);
            indicatorColumn.setNumberFormat(IndicatorNumberFormat.INSTANCE);
            // For SUM indicators, don't show ZEROs in the Grid
            // (it looks better if we don't)
            if (indicator.getAggregation() == IndicatorDTO.AGGREGATE_SUM) {
                indicatorColumn.setRenderer(new GridCellRenderer() {
                    @Override
                    public Object render(ModelData model,
                                         String property,
                                         ColumnData config,
                                         int rowIndex,
                                         int colIndex,
                                         ListStore listStore,
                                         Grid grid) {
                        Object value = model.get(property);
                        if (value instanceof Double && (Double) value != 0) {
                            return IndicatorNumberFormat.INSTANCE.format((Double) value);
                        } else {
                            return "";
                        }
                    }
                });
            } else if (indicator.getAggregation() == IndicatorDTO.AGGREGATE_SITE_COUNT) {
                indicatorColumn.setRenderer(new GridCellRenderer() {
                    @Override
                    public Object render(ModelData model,
                                         String property,
                                         ColumnData config,
                                         int rowIndex,
                                         int colIndex,
                                         ListStore listStore,
                                         Grid grid) {

                        return "1"; // the value of a site count indicator a single site is always 1
                    }
                });
            }
            return indicatorColumn;

        } else if (indicator.getType() == TextType.TYPE_CLASS ||
                   indicator.getType() == NarrativeType.TYPE_CLASS ||
                   indicator.getType() == BarcodeType.TYPE_CLASS) {

            indicatorColumn.setRenderer(new GridCellRenderer() {
                @Override
                public Object render(ModelData model,
                                     String property,
                                     ColumnData config,
                                     int rowIndex,
                                     int colIndex,
                                     ListStore listStore,
                                     Grid grid) {
                    return model.get(property);
                }
            });
            return indicatorColumn;

        } else {
            return null;
        }
    }

    private String indicatorHeader(IndicatorDTO indicator) {
        if(!Strings.isNullOrEmpty(indicator.getListHeader())) {
            return SafeHtmlUtils.htmlEscape(indicator.getListHeader());
        } else if(!Strings.isNullOrEmpty(indicator.getCode())) {
            return SafeHtmlUtils.htmlEscape(indicator.getCode());
        } else {
            return SafeHtmlUtils.htmlEscape(indicator.getName());
        }
    }

    public ColumnModelBuilder addIndicatorColumn(IndicatorDTO indicator, String header) {
        columns.add(createIndicatorColumn(indicator));
        return this;
    }

    public ColumnModelBuilder maybeAddTwoLineLocationColumn(ActivityDTO activity) {
        if (!activity.getLocationType().isAdminLevel() &&
            !activity.getLocationType().isNationwide()) {
            ReadTextColumn column = new ReadTextColumn("locationName", activity.getLocationType().getName(), 100);
            column.setRenderer(new LocationColumnRenderer());
            columns.add(column);
        }
        return this;
    }

    public ColumnModelBuilder maybeAddSingleLineLocationColumn(ActivityDTO activity) {
        if (!activity.getLocationType().isAdminLevel() &&
            !activity.getLocationType().isNationwide()) {
            ReadTextColumn column = new ReadTextColumn("locationName", activity.getLocationType().getName(), 100);
            columns.add(column);
        }
        return this;
    }

    public ColumnModelBuilder addLocationColumn() {
        ReadTextColumn column = new ReadTextColumn("locationName", I18N.CONSTANTS.location(), 100);
        columns.add(column);
        return this;
    }

    public ColumnModelBuilder addAdminLevelColumns(ActivityDTO activity) {
        return addAdminLevelColumns(activity.getAdminLevels());
    }

    public ColumnModelBuilder addAdminLevelColumns(List<AdminLevelDTO> adminLevels) {
        for (AdminLevelDTO level : adminLevels) {
            columns.add(new ColumnConfig(AdminLevelDTO.getPropertyName(level.getId()), level.getName(), 100));
        }
        return this;
    }

    public ColumnModelBuilder addAdminLevelColumns(UserDatabaseDTO database) {
        return addAdminLevelColumns(database.getCountry().getAdminLevels());

    }

    public ColumnModelBuilder maybeAddPartnerColumn(ActivityDTO activity) {
        if(activity.getPartnerRange().size() > 1) {
            addPartnerColumn();
        }
        return this;
    }

    public ColumnModelBuilder maybeAddPartnerColumn(UserDatabaseDTO db) {
        if(db.getPartners().size() > 1) {
            addPartnerColumn();
        }
        return this;
    }

    public ColumnModelBuilder addPartnerColumn() {
        columns.add(new ColumnConfig("partnerName", I18N.CONSTANTS.partner(), 100));
        return this;
    }

    public ColumnModelBuilder maybeAddDateColumn(ActivityDTO activity) {
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            columns.add(new EditableLocalDateColumn("date2", I18N.CONSTANTS.date(), 100));
        }
        return this;
    }

    public ColumnModelBuilder addMapColumn() {
        ColumnConfig mapColumn = new ColumnConfig("x", "", 25);
        mapColumn.setRenderer(new GridCellRenderer<ModelData>() {
            @Override
            public Object render(ModelData model,
                                 String property,
                                 ColumnData config,
                                 int rowIndex,
                                 int colIndex,
                                 ListStore listStore,
                                 Grid grid) {
                if (model instanceof SiteDTO) {
                    SiteDTO siteModel = (SiteDTO) model;
                    if (siteModel.hasCoords()) {
                        return "<div class='mapped'>&nbsp;&nbsp;</div>";
                    } else {
                        return "<div class='unmapped'>&nbsp;&nbsp;</div>";
                    }
                }
                return " ";
            }
        });
        columns.add(mapColumn);
        return this;
    }

    public ColumnModelBuilder addFields(ActivityDTO activity) {
        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            for (IsFormField field : activity.getFields()) {
                if (field instanceof IndicatorDTO) {
                    ColumnConfig indicatorColumn = createIndicatorColumn((IndicatorDTO) field);
                    if(indicatorColumn != null) {
                        columns.add(indicatorColumn);
                    }
                } else if(field instanceof AttributeGroupDTO) {
                    AttributeGroupDTO group = (AttributeGroupDTO) field;
                    if(!group.isMultipleAllowed()) {
                        columns.add(createEnumColumn(group));
                    }
                }
            }
        }
        return this;
    }

    private ColumnConfig createEnumColumn(final AttributeGroupDTO field) {
        ColumnConfig enumColumn = new ColumnConfig("name", field.getLabel(), 100);
        enumColumn.setToolTip(field.getLabel());
        enumColumn.setRenderer(new GridCellRenderer<SiteDTO>() {
            @Override
            public Object render(SiteDTO model, String property, ColumnData config, int rowIndex, int colIndex,
                                 ListStore<SiteDTO> store, Grid<SiteDTO> grid) {

                for (AttributeDTO attribute : field.getAttributes()) {
                    if (model.getAttributeValue(attribute.getId())) {
                        return SafeHtmlUtils.htmlEscape(attribute.getName());
                    }
                }
                return null;
            }
        });
        return enumColumn;
    }

    public ColumnModelBuilder addTreeNameColumn() {
        ColumnConfig name = new ColumnConfig("name", I18N.CONSTANTS.location(), 200);
        name.setRenderer(new TreeGridCellRenderer<ModelData>() {

            @Override
            public Object render(ModelData model,
                                 String property,
                                 ColumnData config,
                                 int rowIndex,
                                 int colIndex,
                                 ListStore<ModelData> store,
                                 Grid<ModelData> grid) {

                return super.render(model, propertyName(model), config, rowIndex, colIndex, store, grid);
            }

            private String propertyName(ModelData model) {
                if (model instanceof SiteDTO) {
                    return "locationName";
                } else {
                    return "name";
                }
            }

        });
        columns.add(name);

        return this;
    }

    public ColumnModelBuilder maybeAddProjectColumn(UserDatabaseDTO database) {
        if (database.getProjects().size() > 1) {
            addProjectColumn();
        }
        return this;
    }
}
