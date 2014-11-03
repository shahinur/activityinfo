package org.activityinfo.odk.driver;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class FormListTest {

    @Test
    public void parse() throws IOException {
        PageSource page = new PageSource(Resources.toString(Resources.getResource("formList.xml"), Charsets.UTF_8));

        List<String> forms = FormList.parseFormList(page);

        assertThat(forms,
                contains("Birds",
                        "Cascading Select Form",
                        "Cascading Triple Select Form",
                        "Forest Plot Survey",
                        "Geo Tagger v2",
                        "Hypertension Screening"));
    }
}
