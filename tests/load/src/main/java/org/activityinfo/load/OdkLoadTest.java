package org.activityinfo.load;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.client.ActivityInfoClient;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.store.CommitStatus;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Random;

public class OdkLoadTest {

    private Random random = new Random();
    private RandomText randomText;

    public static void main(String[] args) {

        URI devServer = UriBuilder.fromPath("http://localhost:8080/").build();
        ActivityInfoClient client = new ActivityInfoClient(devServer, "test@test.org", "testing123");

        new OdkLoadTest(client).run();

    }

    public OdkLoadTest(ActivityInfoClient client) {

        randomText = new RandomText();

        // Create a new form to test against
        FormClass formClass = new FormClass(ResourceId.generateId());
        formClass.setOwnerId(ResourceId.valueOf("d518"));
        formClass.setLabel("Thousand monkeys");

        int numFields = 5+random.nextInt(25);

        for(int i=0;i!=numFields;++i) {
           formClass.addElement(generateRandomField());
        }

        System.out.println("Submitting form " + formClass.getId());

        Preconditions.checkState(client.putForm(formClass).getStatus() == CommitStatus.COMMITTED);
    }

    private FormField generateRandomField() {
        FormField field = new FormField(ResourceId.generateId());
        field.setLabel(randomText.getRandomLabel());
        field.setType(generateRandomType());
        return field;
    }

    private TextType generateRandomType() {
//        List<FieldTypeClass> typeClasses = getTypes();
//        FieldTypeClass typeClass = typeClasses.get(random.nextInt(typeClasses.size()));
//        if(typeClass instanceof ParametrizedFieldType) {
//            return generateRandomParameters(typeClass)
//        }
        return TextType.INSTANCE;
    }

    private List<FieldTypeClass> getTypes() {
        List<FieldTypeClass> range = Lists.newArrayList();
        range.add(TextType.TYPE_CLASS);
        //return Lists.newArrayList(TypeRegistry.get().getTypeClasses());

        return range;
    }


    private void run() {

    }

}
