package org.activityinfo.ui.client.page.config;

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

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.inject.Inject;
import org.activityinfo.i18n.shared.UiConstants;
import org.activityinfo.legacy.shared.model.PartnerDTO;
import org.activityinfo.legacy.shared.model.UserDatabaseDTO;
import org.activityinfo.ui.client.page.common.dialog.FormDialogCallback;
import org.activityinfo.ui.client.page.common.dialog.FormDialogImpl;
import org.activityinfo.ui.client.page.common.dialog.FormDialogTether;
import org.activityinfo.ui.client.page.common.grid.AbstractGridView;
import org.activityinfo.ui.client.page.common.toolbar.UIActions;
import org.activityinfo.ui.client.page.config.form.PartnerForm;
import org.activityinfo.ui.client.page.entry.column.StringWithTooltipRenderer;
import org.activityinfo.ui.client.style.legacy.icon.IconImageBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Bertram
 */
public class DbPartnerGrid extends AbstractGridView<PartnerDTO, DbPartnerEditor> implements DbPartnerEditor.View {

    private final UiConstants messages;
    private final IconImageBundle icons;

    private Grid<PartnerDTO> grid;

    @Inject
    public DbPartnerGrid(UiConstants messages, IconImageBundle icons) {
        this.messages = messages;
        this.icons = icons;
    }

    @Override
    public void init(DbPartnerEditor editor, UserDatabaseDTO db, ListStore<PartnerDTO> store) {
        super.init(editor, store);
        this.setHeadingText(db.getName() + " - " + messages.partners());

    }

    @Override
    protected Grid<PartnerDTO> createGridAndAddToContainer(Store store) {
        grid = new Grid<PartnerDTO>((ListStore) store, createColumnModel());
        grid.setAutoExpandColumn("fullName");
        grid.setLoadMask(true);

        this.setLayout(new FitLayout());
        this.add(grid);

        return grid;
    }

    protected ColumnModel createColumnModel() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig name = new ColumnConfig("name", messages.name(), 150);
        name.setToolTip(messages.name());
        name.setRenderer(new StringWithTooltipRenderer());

        ColumnConfig fullName = new ColumnConfig("fullName", messages.fullName(), 300);
        fullName.setToolTip(messages.fullName());
        fullName.setRenderer(new StringWithTooltipRenderer());

        columns.add(name);
        columns.add(fullName);

        return new ColumnModel(columns);
    }

    @Override
    protected void initToolBar() {
        toolBar.addButton(UIActions.ADD, messages.addPartner(), icons.add());
        toolBar.addButton(UIActions.DELETE, messages.delete(), icons.delete());
    }

    @Override
    public FormDialogTether showAddDialog(PartnerDTO partner, FormDialogCallback callback) {

        PartnerForm form = new PartnerForm();
        form.getBinding().bind(partner);

        FormDialogImpl dlg = new FormDialogImpl(form);
        dlg.setWidth(450);
        dlg.setHeight(300);
        dlg.setHeadingText(messages.newPartner());

        dlg.show(callback);

        return dlg;
    }
}
