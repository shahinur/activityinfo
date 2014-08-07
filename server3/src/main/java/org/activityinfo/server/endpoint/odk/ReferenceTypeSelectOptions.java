package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.server.endpoint.odk.xform.Item;
import org.activityinfo.ui.client.component.form.field.InstanceLabelTable;

import java.util.List;

class ReferenceTypeSelectOptions implements SelectOptions {
    final private Cardinality cardinality;
    final private List<Item> item;

    ReferenceTypeSelectOptions(ReferenceType referenceType, TableService tableService) {
        cardinality = referenceType.getCardinality();
        item = Lists.newArrayList();

        TableModel tableModel = new TableModel(Iterables.getOnlyElement(referenceType.getRange()));
        tableModel.addResourceId("id");
        tableModel.addColumn("label").select().fieldPath(ApplicationProperties.LABEL_PROPERTY);

        TableData table = tableService.buildTable(tableModel);
        InstanceLabelTable data = new InstanceLabelTable(table.getColumnView("id"), table.getColumnView("label"));

        for (int i = 0; i < data.getNumRows(); i++) {
            Item item = new Item();
            item.label = data.getLabel(i);
            item.value = data.getId(i).asString();
            this.item.add(item);
        }
    }

    @Override
    public Cardinality getCardinality() {
        return cardinality;
    }

    @Override
    public List<Item> getItem() {
        return item;
    }
}
