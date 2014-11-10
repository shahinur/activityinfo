package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.io.Resources;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.UpdateFormClass;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.model.form.*;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.endpoint.odk.xform.XForm;
import org.activityinfo.server.endpoint.odk.xform.XFormReader;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
public class XFormReaderTest extends CommandTestCase2 {

    @Test
    @OnDataSet("/dbunit/sites-simple1.db.xml")
    public void test() throws JAXBException {

        int databaseId = 1;

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setName("MHFS");
        activityDTO.set("databaseId", databaseId);
        activityDTO.set("locationTypeId", 1);

        CreateResult createResult = execute(new CreateEntity(activityDTO));
        int activityId = createResult.getNewId();

        JAXBContext jaxbContext = JAXBContext.newInstance(XForm.class);

        URL formURL = Resources.getResource(XForm.class, "survey.xml");
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XForm xform = (XForm) jaxbUnmarshaller.unmarshal(formURL);

        XFormReader reader = new XFormReader(xform);
        reader.setActivityId(activityId);
        reader.setDatabaseId(databaseId);
        FormClass formClass = reader.build();

        dump("", formClass);

        FormField veg408 = findField(formClass, "veg408");

        assertThat(veg408.getLabel(),
                equalTo("E.37 In the last seven days did anyone in your household consume any cabbage ?"));

        FormField consumption_veg401 = findField(formClass, "consumption_veg401");
        assertThat(consumption_veg401.isVisible(), equalTo(true));
        assertThat(consumption_veg401.getLabel(),
                equalTo("E.30.1.1 why was the previous question left blank?"));

        execute(new UpdateFormClass(formClass));
    }

    private void dump(String indent, FormElementContainer container) {
        for(FormElement element : container.getElements()) {
            if(element instanceof FormSection) {
                System.out.println(indent + element.getLabel());
                dump(indent + "   ", ((FormSection) element));
            } else {
                FormField field = ((FormField) element);
                System.out.println(String.format("%s[%s] %s : %s", indent, field.getCode(), field.getLabel(),
                        field.getType().getTypeClass().getId()));
            }
        }

    }

    private FormField findField(FormClass formClass, String code) {
        for(FormField field : formClass.getFields()) {
            if(code.equals(field.getCode())) {
                return field;
            }
        }
        throw new AssertionError("No field with code '" + code + "'");
    }
}
