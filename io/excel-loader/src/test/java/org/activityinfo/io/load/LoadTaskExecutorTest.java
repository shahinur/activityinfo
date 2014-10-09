package org.activityinfo.io.load;

import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.tasks.LoadTaskModel;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoadTaskExecutorTest {


    private LoadingTestContext context;

    @Before
    public void setUp() throws Exception {
        context = new LoadingTestContext();
    }

    @Test
    public void excelXlsx() throws Exception {

        loadResource("import_small.xlsx");

        assertThat(context.getLoadedForms(), hasSize(1));

        LoadingTestContext.LoadedForm form = context.getLoadedForms().get(0);
        assertThat(form.formClass().getLabel(), equalTo("import_small.xlsx - [UNHCR LCCA Ethiopia 2014 - Bamb]"));
        assertThat(form.instanceCount(), equalTo(23));

        assertThat(form.field("D2. Water Point Number").getType(), instanceOf(TextType.class));
        assertThat(form.field("Row").getType(), instanceOf(QuantityType.class));

        assertThat(form.field("I5. Provided code ID:").getType(), instanceOf(QuantityType.class));

        assertThat(form.fieldStringValues("Water Point"), contains(
            "C13", "A11","A11","A1","A1","B9","A5","C4","B3","A6","C1","B11","C1",
            "B9","A11","A11","A11","A5","B11","B3","C13","C13","A10"));

    }

    @Test
    public void csv() throws Exception {
        loadResource("nfi.csv");

        assertThat(context.getLoadedForms(), hasSize(1));

        LoadingTestContext.LoadedForm form = context.getLoadedForms().get(0);
        assertThat(form.formClass().getLabel(), equalTo("nfi.csv"));

        assertThat(form.field("Code Province").getType(), instanceOf(TextType.class));

    }


    public void loadResource(String blobId) throws Exception {
        LoadTaskModel loadTask = new LoadTaskModel();
        loadTask.setBlobId(blobId);
        loadTask.setFolderId(Resources.ROOT_ID);

        LoadTaskExecutor executor = new LoadTaskExecutor();
        executor.execute(context, loadTask);
    }
}