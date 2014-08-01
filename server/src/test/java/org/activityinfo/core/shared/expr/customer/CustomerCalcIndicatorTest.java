package org.activityinfo.core.shared.expr.customer;
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

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.client.KeyGenerator;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.command.LocationDTOs;
import org.activityinfo.server.database.OnDataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.GregorianCalendar;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.activityFormClass;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.field;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author yuriyz on 8/1/14.
 */
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/schema1.db.xml")
public class CustomerCalcIndicatorTest extends CommandTestCase2 {

    FormClass formClass;

    @Test
    public void calculations() {
        formClass = createFormClass();
        int activityId = CuidAdapter.getLegacyIdFromCuid(formClass.getId());

        SiteDTO newSite = newSite(activityId);

        newSite.setIndicatorValue(fieldId("EXP"), 3);
        newSite.setIndicatorValue(fieldId("WATER_ALLOC"), 400);

        CreateResult createSiteResult = execute(new CreateSite(newSite));

        // let the client know the command has succeeded
        newSite.setId(createSiteResult.getNewId());

        PagingLoadResult<SiteDTO> loadResult = execute(GetSites.byId(newSite
                .getId()));
        Assert.assertEquals(1, loadResult.getData().size());

        SiteDTO siteDTO = loadResult.getData().get(0);

        Assert.assertEquals(indicatorValue(siteDTO, "WATER_EXP"), 12, 0); // WATER_EXP = EXP * (WATER_ALLOC / 100)

    }

    private Double indicatorValue(SiteDTO siteDTO, String nameInExpression) {
        int indicatorId = CuidAdapter.getLegacyIdFromCuid(field(nameInExpression).getId());
        return siteDTO.getIndicatorValue(indicatorId);
    }

    private FormField field(String nameInExpression) {
        for (FormField field : formClass.getFields()) {
            if (nameInExpression.equals(field.getNameInExpression())) {
                return field;
            }
        }
        throw new RuntimeException("Enable to find field with nameInExpression: " + nameInExpression);
    }

    private int fieldId(String nameInExpression) {
        return CuidAdapter.getLegacyIdFromCuid(field(nameInExpression).getId());
    }

    private SiteDTO newSite(int activityId) {
        LocationDTO location = LocationDTOs.newLocation();
        execute(new CreateLocation(location));

        SiteDTO newSite = new SiteDTO();
        newSite.setId(new KeyGenerator().generateInt());
        newSite.setActivityId(activityId);
        newSite.setLocationId(location.getId());
        newSite.setPartner(new PartnerDTO(1, "Foobar"));
        newSite.setReportingPeriodId(1);
        newSite.setDate1((new GregorianCalendar(2008, 12, 1)).getTime());
        newSite.setDate2((new GregorianCalendar(2009, 1, 3)).getTime());
        return newSite;
    }

    private FormClass createFormClass() {
        SchemaDTO schema = execute(new GetSchema());
        UserDatabaseDTO db = schema.getDatabaseById(1);
        LocationTypeDTO locType = schema.getCountryById(1).getLocationTypes().get(0);

        ActivityDTO act = new ActivityDTO();
        act.setName("Calculated indicators");
        act.setLocationType(locType);
        act.setReportingFrequency(ActivityDTO.REPORT_ONCE);

        CreateResult createResult = execute(CreateEntity.Activity(db, act));
        ResourceId classId = activityFormClass(createResult.getNewId());

        ResourceLocatorAdaptor resourceLocator = new ResourceLocatorAdaptor(getDispatcher());
        FormClass formClass = assertResolves(resourceLocator.getFormClass(classId));

        FormField expField = new FormField(ResourceId.generateId());
        expField.setType(new QuantityType().setUnits("currency"));
        expField.setLabel("Expenditure");
        expField.setNameInExpression("EXP");

        FormField waterAllocField = new FormField(ResourceId.generateId());
        waterAllocField.setType(new QuantityType().setUnits("%"));
        waterAllocField.setLabel("Allocation watter programme");
        waterAllocField.setNameInExpression("WATER_ALLOC");

        FormField pctInitialField = new FormField(ResourceId.generateId());
        pctInitialField.setType(new QuantityType().setUnits("%"));
        pctInitialField.setLabel("Initial Cost - Not specified");
        pctInitialField.setNameInExpression("PCT_INITIAL");

        FormField pctInitialHardField = new FormField(ResourceId.generateId());
        pctInitialHardField.setType(new QuantityType().setUnits("%"));
        pctInitialHardField.setLabel("Initial Cost - Cap Hard");
        pctInitialHardField.setNameInExpression("PCT_INITIAL_HARD");

        FormField pctInitialSoftField = new FormField(ResourceId.generateId());
        pctInitialSoftField.setType(new QuantityType().setUnits("%"));
        pctInitialSoftField.setLabel("Initial Cost - Cap Soft");
        pctInitialSoftField.setNameInExpression("PCT_INITIAL_SOFT");

        FormField pctExtensionField = new FormField(ResourceId.generateId());
        pctExtensionField.setType(new QuantityType().setUnits("%"));
        pctExtensionField.setLabel("Extension Cost - Not specified");
        pctExtensionField.setNameInExpression("PCT_EXTENSION");

        FormField pctExtensionHardField = new FormField(ResourceId.generateId());
        pctExtensionHardField.setType(new QuantityType().setUnits("%"));
        pctExtensionHardField.setLabel("Extension Cost - Hard");
        pctExtensionHardField.setNameInExpression("PCT_EXTENSION_HARD");

        FormField pctExtensionSoftField = new FormField(ResourceId.generateId());
        pctExtensionSoftField.setType(new QuantityType().setUnits("%"));
        pctExtensionSoftField.setLabel("Extension Cost - Soft");
        pctExtensionSoftField.setNameInExpression("PCT_EXTENSION_SOFT");

        FormField pctOpField = new FormField(ResourceId.generateId());
        pctOpField.setType(new QuantityType().setUnits("%"));
        pctOpField.setLabel("Operational Cost");
        pctOpField.setNameInExpression("PCT_OP");

        FormField pctMaintenanceField = new FormField(ResourceId.generateId());
        pctMaintenanceField.setType(new QuantityType().setUnits("%"));
        pctMaintenanceField.setLabel("Maintenance Cost");
        pctMaintenanceField.setNameInExpression("PCT_MAINTENANCE");

        FormField pctOpMaintField = new FormField(ResourceId.generateId());
        pctOpMaintField.setType(new QuantityType().setUnits("%"));
        pctOpMaintField.setLabel("Operational & Maintenance Cost");
        pctOpMaintField.setNameInExpression("PCT_OP_MAINT");

        FormField waterExpField = new FormField(ResourceId.generateId());
        waterExpField.setType(new QuantityType().setUnits("%"));
        waterExpField.setLabel("Expenditure on water programme");
        waterExpField.setNameInExpression("WATER_EXP");

        FormField initialField = new FormField(ResourceId.generateId());
        initialField.setType(new QuantityType().setUnits("%"));
        initialField.setLabel("Value of Initial Cost - Not specified");
        initialField.setNameInExpression("INITIAL");

        FormField initialHardField = new FormField(ResourceId.generateId());
        initialHardField.setType(new QuantityType().setUnits("%"));
        initialHardField.setLabel("Value of Initial Cost - Cap Hard");
        initialHardField.setNameInExpression("INITIAL_HARD");

        FormField initialSoftField = new FormField(ResourceId.generateId());
        initialSoftField.setType(new QuantityType().setUnits("%"));
        initialSoftField.setLabel("Value of Initial Cost – Cap Soft");
        initialSoftField.setNameInExpression("INITIAL_SOFT");

        FormField initialTotalField = new FormField(ResourceId.generateId());
        initialTotalField.setType(new QuantityType().setUnits("%"));
        initialTotalField.setLabel("Total Value of Initial Cost");
        initialTotalField.setNameInExpression("INITIAL_TOTAL");

        formClass.addElement(expField);
        formClass.addElement(waterAllocField);
        formClass.addElement(pctInitialField);
        formClass.addElement(pctInitialHardField);
        formClass.addElement(pctInitialSoftField);
        formClass.addElement(pctExtensionField);
        formClass.addElement(pctExtensionHardField);
        formClass.addElement(pctExtensionSoftField);
        formClass.addElement(pctOpField);
        formClass.addElement(pctMaintenanceField);
        formClass.addElement(pctOpMaintField);
        formClass.addElement(waterExpField);

        formClass.addElement(initialField);
        formClass.addElement(initialHardField);
        formClass.addElement(initialSoftField);
        formClass.addElement(initialTotalField);

        assertResolves(resourceLocator.persist(formClass));

        FormClass reform = assertResolves(resourceLocator.getFormClass(formClass.getId()));
        assertHasFieldWithLabel(reform, "Expenditure");
        assertHasFieldWithLabel(reform, "Allocation watter programme");
        assertHasFieldWithLabel(reform, "Initial Cost - Not specified");
        assertHasFieldWithLabel(reform, "Initial Cost - Cap Hard");
        assertHasFieldWithLabel(reform, "Initial Cost - Cap Soft");
        assertHasFieldWithLabel(reform, "Extension Cost - Not specified");
        assertHasFieldWithLabel(reform, "Extension Cost - Hard");
        assertHasFieldWithLabel(reform, "Extension Cost - Soft");
        assertHasFieldWithLabel(reform, "Operational Cost");
        assertHasFieldWithLabel(reform, "Maintenance Cost");
        assertHasFieldWithLabel(reform, "Operational & Maintenance Cost");
        assertHasFieldWithLabel(reform, "Expenditure on water programme");
        assertHasFieldWithLabel(reform, "Value of Initial Cost - Not specified");
        assertHasFieldWithLabel(reform, "Value of Initial Cost - Cap Hard");
        assertHasFieldWithLabel(reform, "Value of Initial Cost – Cap Soft");
        assertHasFieldWithLabel(reform, "Total Value of Initial Cost");
        return reform;
    }

    private static FormField assertHasFieldWithLabel(FormClass formClass, String label) {
        for (FormField field : formClass.getFields()) {
            if (label.equals(field.getLabel())) {
                return field;
            }
        }
        throw new RuntimeException("No field with label: " + label);
    }
}
