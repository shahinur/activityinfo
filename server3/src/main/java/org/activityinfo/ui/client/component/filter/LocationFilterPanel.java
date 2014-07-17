package org.activityinfo.ui.client.component.filter;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.common.collect.Sets;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.PivotSites;
import org.activityinfo.legacy.shared.command.PivotSites.PivotResult;
import org.activityinfo.legacy.shared.command.PivotSites.ValueType;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.model.LocationDTO;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;
import org.activityinfo.legacy.shared.reports.content.EntityCategory;
import org.activityinfo.legacy.shared.reports.model.Dimension;
import org.activityinfo.ui.client.component.filter.FilterToolBar.ApplyFilterEvent;
import org.activityinfo.ui.client.component.filter.FilterToolBar.ApplyFilterHandler;
import org.activityinfo.ui.client.component.filter.FilterToolBar.RemoveFilterEvent;
import org.activityinfo.ui.client.component.filter.FilterToolBar.RemoveFilterHandler;
import org.activityinfo.ui.client.style.legacy.icon.IconImageBundle;

import java.util.ArrayList;
import java.util.List;

public class LocationFilterPanel extends ContentPanel implements FilterPanel {

    private Dispatcher service;
    private FilterToolBar filterToolBar;
    private Filter baseFilter = new Filter();
    private Filter value = new Filter();

    private ListStore<LocationDTO> store;
    private CheckBoxListView<LocationDTO> listView;

    @Inject
    public LocationFilterPanel(Dispatcher service) {
        this.service = service;
        initializeComponent();

        createFilterToolBar();
        createList();
    }

    private void initializeComponent() {
        setHeadingText(I18N.CONSTANTS.filterByLocation());
        setIcon(IconImageBundle.ICONS.filter());

        setLayout(new FitLayout());
        setScrollMode(Style.Scroll.NONE);
        setHeadingText(I18N.CONSTANTS.filterByLocation());
        setIcon(IconImageBundle.ICONS.filter());
    }

    private void createFilterToolBar() {
        filterToolBar = new FilterToolBar();
        filterToolBar.addApplyFilterHandler(new ApplyFilterHandler() {

            @Override
            public void onApplyFilter(ApplyFilterEvent deleteEvent) {
                applyFilter();
            }
        });
        filterToolBar.addRemoveFilterHandler(new RemoveFilterHandler() {

            @Override
            public void onRemoveFilter(RemoveFilterEvent deleteEvent) {
                clearFilter();
                ValueChangeEvent.fire(LocationFilterPanel.this, value);
            }
        });
        setTopComponent(filterToolBar);
    }

    protected void applyFilter() {
        value = new Filter();
        if (isRendered()) {
            List<Integer> selectedIds = getSelectedIds();
            if (selectedIds.size() > 0) {
                value.addRestriction(DimensionType.Location, getSelectedIds());
            }
        }

        ValueChangeEvent.fire(this, value);
        filterToolBar.setApplyFilterEnabled(false);
        filterToolBar.setRemoveFilterEnabled(true);
    }

    private List<Integer> getSelectedIds() {
        List<Integer> list = new ArrayList<Integer>();

        for (LocationDTO model : listView.getChecked()) {
            list.add(model.getId());
        }
        return list;
    }

    private void createList() {
        store = new ListStore<LocationDTO>();
        listView = new CheckBoxListView<LocationDTO>();
        listView.setStore(store);
        listView.setDisplayProperty("name");
        listView.addListener(Events.Select, new Listener<ListViewEvent<LocationTypeDTO>>() {

            @Override
            public void handleEvent(ListViewEvent<LocationTypeDTO> be) {
                filterToolBar.setApplyFilterEnabled(true);
            }
        });
        add(listView);
    }

    protected void clearFilter() {
        for (LocationDTO location : listView.getStore().getModels()) {
            listView.setChecked(location, false);
        }
        value = new Filter();
        filterToolBar.setApplyFilterEnabled(false);
        filterToolBar.setRemoveFilterEnabled(false);
    }

    @Override
    public Filter getValue() {
        return value;
    }

    @Override
    public void setValue(Filter value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Filter value, boolean fireEvents) {
        this.value = new Filter();
        this.value.addRestriction(DimensionType.Location, value.getRestrictions(DimensionType.Location));
        applyInternalValue();
        if (fireEvents) {
            ValueChangeEvent.fire(this, this.value);
        }
    }

    private void applyInternalValue() {
        for (LocationDTO model : listView.getStore().getModels()) {
            listView.setChecked(model, value.getRestrictions(DimensionType.Location).contains(model.getId()));
        }
        filterToolBar.setApplyFilterEnabled(false);
        filterToolBar.setRemoveFilterEnabled(value.isRestricted(DimensionType.Location));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Filter> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void applyBaseFilter(Filter rawFilter) {
        final Filter filter = new Filter(rawFilter);
        filter.clearRestrictions(DimensionType.Location);

        // avoid fetching a list of ALL locations if no indicators have been selected
        if (!filter.isRestricted(DimensionType.Indicator)) {
            store.removeAll();
            return;
        }

        if (baseFilter == null || !baseFilter.equals(filter)) {
            PivotSites pivotSites = new PivotSites();
            pivotSites.setDimensions(Sets.<Dimension>newHashSet(new Dimension(DimensionType.Location)));
            pivotSites.setFilter(filter);
            pivotSites.setValueType(ValueType.TOTAL_SITES);
            service.execute(pivotSites, new AsyncCallback<PivotResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onSuccess(PivotResult result) {
                    store.removeAll();
                    List<Integer> ids = getSelectedIds();

                    for (Bucket bucket : result.getBuckets()) {
                        LocationDTO dto = new LocationDTO();
                        dto.setId(((EntityCategory) bucket.getCategory(new Dimension(DimensionType.Location))).getId());
                        dto.setName(((EntityCategory) bucket.getCategory(new Dimension(DimensionType.Location)))
                                .getLabel());
                        store.add(dto);
                    }
                    store.sort("name", Style.SortDir.ASC);

                    applyInternalValue();
                    for (LocationDTO location : store.getModels()) {
                        if (ids.contains(location.getId())) {
                            listView.setChecked(location, true);
                        }
                    }

                    baseFilter = filter;
                }

            });
        }
    }
}
