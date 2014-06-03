package org.activityinfo.core.shared.table;
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

import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.table.provider.MainColumnViewProvider;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author yuriyz on 5/30/14.
 */
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-calculated-indicators.db.xml")
public class MainColumnViewProviderTest extends CommandTestCase2 {

    private ResourceLocatorAdaptor resourceLocator;
    private MainColumnViewProvider columnViewProvider;

    @Before
    public final void setup() {
        resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        columnViewProvider = new MainColumnViewProvider(resourceLocator);
    }

    @Test
    public void columnViewParserAndCaching() {

        TableModel tableModel = new TableModel();
        Cuid formClassId = CuidAdapter.activityFormClass(1);
        tableModel.setFormClassId(formClassId);
        FieldPath path1 = new FieldPath(CuidAdapter.indicatorField(1));
        FieldPath path2 = new FieldPath(CuidAdapter.partnerField(1),
                CuidAdapter.field(CuidAdapter.partnerFormClass(1), CuidAdapter.NAME_FIELD));

        FieldColumn beneficiaries = new FieldColumn(path1, "Beneficiaries");
        FieldColumn partner = new FieldColumn(path2, "Partner");

        tableModel.setColumns(Arrays.asList(beneficiaries, partner));

        FormClass formClass = assertResolves(resourceLocator.getFormClass(formClassId));

        ColumnView beneficiariesColumnView = assertResolves(columnViewProvider.view(beneficiaries, formClass));
        ColumnView partnerColumnView = assertResolves(columnViewProvider.view(partner, formClass));

        assertThat(beneficiariesColumnView.numRows(), Matchers.equalTo(3));
        assertThat(partnerColumnView.numRows(), Matchers.equalTo(3));

        ColumnView beneficiariesFromCache = assertResolves(columnViewProvider.getCache().view(beneficiaries, formClass));
        ColumnView partnerFromCache = assertResolves(columnViewProvider.getCache().view(partner, formClass));

        // check whether column views are cached
        assertEquals(beneficiariesColumnView, beneficiariesFromCache);
        assertEquals(partnerColumnView, partnerFromCache);
    }

    @Test
    public void calculatedColumnView() {
        Cuid formClassId = CuidAdapter.activityFormClass(1);

        FieldPath path1 = new FieldPath(CuidAdapter.indicatorField(1));
        FieldPath path2 = new FieldPath(CuidAdapter.indicatorField(2));
        FieldPath path3 = new FieldPath(CuidAdapter.indicatorField(12451));

        AsyncFormTreeBuilder formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);

        FormTree formTree = assertResolves(formTreeBuilder.apply(formClassId));

        FieldColumn column1 = new FieldColumn(formTree.getNodeByPath(path1));
        FieldColumn column2 = new FieldColumn(formTree.getNodeByPath(path2));
        FieldColumn calculatedColumn = new FieldColumn(formTree.getNodeByPath(path3));


        FormClass formClass = assertResolves(resourceLocator.getFormClass(formClassId));

        // first call callculated column -> make sure that all dependent columns are build independently
        ColumnView calculatedColumnView = assertResolves(columnViewProvider.view(calculatedColumn, formClass));

        // query dependent column after calculated one is evaluated
        ColumnView columnView1 = assertResolves(columnViewProvider.view(column1, formClass));
        ColumnView columnView2 = assertResolves(columnViewProvider.view(column2, formClass));

        ColumnView calculatedColumnFromCache = assertResolves(columnViewProvider.getCache().view(calculatedColumn, formClass));

        // check whether calculated column view is in cache
        assertEquals(calculatedColumnView, calculatedColumnFromCache);

        // row count check
        assertThat(columnView1.numRows(), Matchers.equalTo(3));
        assertThat(columnView2.numRows(), Matchers.equalTo(3));
        assertThat(calculatedColumnView.numRows(), Matchers.equalTo(3));

        // calculation check
        for (int row = 0; row != columnView1.numRows(); row++) {
            double sum = columnView1.getDouble(row) + columnView2.getDouble(row);
            assertThat(calculatedColumnView.getDouble(row), Matchers.equalTo(sum));
        }
    }
}
