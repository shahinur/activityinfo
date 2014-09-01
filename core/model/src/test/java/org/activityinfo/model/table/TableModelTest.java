package org.activityinfo.model.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.resource.ResourceId;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TableModelTest {


    @Test
    public void serialization() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.get();

        ResourceId classId = ResourceId.valueOf("f023423");
        TableModel model = new TableModel(classId);
        model.addColumn("c1").select().fieldPath(ResourceId.generateId());

        String json = mapper.writeValueAsString(model);
        System.out.println(json);

        TableModel remodel = mapper.readValue(json, TableModel.class);
        assertThat(remodel.getRowSources(), contains(new RowSource(classId)));
        assertThat(remodel.getColumns(), hasSize(1));

    }




}