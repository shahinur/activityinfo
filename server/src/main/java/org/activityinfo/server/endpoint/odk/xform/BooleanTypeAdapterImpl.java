package org.activityinfo.server.endpoint.odk.xform;

import com.google.common.collect.Lists;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

public class BooleanTypeAdapterImpl implements OdkTypeAdapter {
    final private List<Item> item;

    BooleanTypeAdapterImpl() {
        Item no = new Item();
        no.label = "no";
        no.value = "false";
        Item yes = new Item();
        yes.label = "yes";
        yes.value = "true";
        item = Lists.newArrayList(yes, no);
    }

    @Override
    public String getModelBindType() {
        return "boolean";
    }

    @Override
    public JAXBElement<PresentationElement> createPresentationElement(String ref, String label, String hint) {
        PresentationElement presentationElement = new PresentationElement();

        presentationElement.ref = ref;
        presentationElement.label = label;
        presentationElement.item = item;
        presentationElement.hint = hint;

        QName qName = new QName("http://www.w3.org/2002/xforms", "select1");
        return new JAXBElement<>(qName, PresentationElement.class, presentationElement);
    }
}
