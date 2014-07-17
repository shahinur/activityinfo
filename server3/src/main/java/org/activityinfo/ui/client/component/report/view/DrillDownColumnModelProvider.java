package org.activityinfo.ui.client.component.report.view;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSchema;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.ui.client.page.entry.column.ColumnModelBuilder;
import org.activityinfo.ui.client.page.entry.column.ColumnModelProvider;
import org.activityinfo.ui.client.page.entry.grouping.GroupingModel;

import java.util.Set;

/**
 * Creates a column model based on the active filter and row and column dimensions
 */
public class DrillDownColumnModelProvider implements ColumnModelProvider {

    private final Dispatcher dispatcher;

    public DrillDownColumnModelProvider(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void fetchColumnModels(final Filter filter,
                                  GroupingModel grouping,
                                  final AsyncCallback<ColumnModel> callback) {
        dispatcher.execute(new GetSchema(), new AsyncCallback<SchemaDTO>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SchemaDTO schema) {
                callback.onSuccess(buildModel(schema, filter));
            }
        });
    }

    private ColumnModel buildModel(SchemaDTO schema, Filter filter) {
        ColumnModelBuilder builder = new ColumnModelBuilder().addMapColumn()
                                                             .addDatabaseColumn(schema)
                                                             .addActivityColumn(schema)
                                                             .addLocationColumn()
                                                             .addPartnerColumn();

        Set<Integer> indicatorIds = filter.getRestrictions(DimensionType.Indicator);
        for (UserDatabaseDTO db : schema.getDatabases()) {
            for (ActivityDTO activity : db.getActivities()) {
                for (IndicatorDTO indicator : activity.getIndicators()) {
                    if (indicatorIds.contains(indicator.getId())) {
                        String header;
                        if (indicatorIds.size() == 1) {
                            header = I18N.CONSTANTS.value();
                        } else if (!Strings.isNullOrEmpty(indicator.getListHeader())) {
                            header = indicator.getListHeader();
                        } else {
                            header = indicator.getName();
                        }
                        builder.addIndicatorColumn(indicator, header);
                    }
                }
            }
        }
        return builder.build();
    }
}
