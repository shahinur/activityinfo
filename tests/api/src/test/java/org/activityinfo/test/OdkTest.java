package org.activityinfo.test;

import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.client.xform.XFormInstanceBuilder;
import org.activityinfo.client.xform.XFormItem;
import org.activityinfo.model.resource.ResourceId;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class OdkTest {

    @Test
    public void testEmptyQuantities() {
        ActivityInfoClient client = new ActivityInfoClient(TestConfig.getRootURI(),
                "odk.test@mailinator.com",
                "odk.test");

        List<XFormItem> forms = client.getXForms();

        XFormItem form = formNamed(forms, "ODK Test / HintTest");
        System.out.println(form.getUrl());

        Document formDoc = client.getXForm(form);

        Element data = (Element) formDoc.getElementsByTagName("data").item(0);
        String token = data.getAttribute("id");

        System.out.println(token);

        XFormInstanceBuilder builder = new XFormInstanceBuilder(token);
        builder.addFieldValue(ResourceId.valueOf("chz4mup7d1"), "2014-01-01");
        builder.addFieldValue(ResourceId.valueOf("chz4mv6ei2"), "");
        builder.addFieldValue(ResourceId.valueOf("chz4mvovm3"), "");



    }

    private XFormItem formNamed(List<XFormItem> forms, String label) {
        for(XFormItem item : forms) {
            if(item.getLabel().equals(label)) {
                return item;
            }
        }
        throw new IllegalArgumentException();
    }
}
