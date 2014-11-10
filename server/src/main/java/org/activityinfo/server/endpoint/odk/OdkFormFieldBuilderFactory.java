package org.activityinfo.server.endpoint.odk;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.server.command.ResourceLocatorSync;
import org.activityinfo.server.endpoint.odk.xform.BindingType;
import org.activityinfo.server.endpoint.odk.xform.Item;
import org.activityinfo.service.lookup.ReferenceChoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class OdkFormFieldBuilderFactory {

    private static final Logger LOGGER = Logger.getLogger(OdkFormFieldBuilderFactory.class.getName());

    final private ResourceLocatorSync locator;

    @Inject
    public OdkFormFieldBuilderFactory(ResourceLocatorSync table) {
        this.locator = table;
    }

    public OdkFormFieldBuilder get(FieldType fieldType) {
        if (fieldType instanceof ParametrizedFieldType) {
            ParametrizedFieldType parametrizedFieldType = (ParametrizedFieldType) fieldType;
            if (!parametrizedFieldType.isValid()) return null;
        }

        if (fieldType instanceof BarcodeType) {
            return new SimpleInputBuilder(BindingType.BARCODE);
        }
        if (fieldType instanceof BooleanType) {
            return new SelectBuilder(BindingType.BOOLEAN, booleanOptions());
        }
        if (fieldType instanceof EnumType) {
            SelectOptions options = enumOptions((EnumType) fieldType);
            if(options.isEmpty()) {
                return null;
            } else {
                return new SelectBuilder(BindingType.STRING, options);
            }
        }
        if (fieldType instanceof GeoPointType) {
            return new SimpleInputBuilder(BindingType.GEOPOINT);
        }
        if (fieldType instanceof ImageType) {
            return new UploadBuilder("image/*");
        }
        if (fieldType instanceof LocalDateType) {
            return new SimpleInputBuilder(BindingType.DATE);
        }
        if (fieldType instanceof NarrativeType) {
            return new SimpleInputBuilder(BindingType.STRING);
        }
        if (fieldType instanceof QuantityType) {
            return new QuantityFieldBuilder((QuantityType) fieldType);
        }
        if (fieldType instanceof ReferenceType) {
            SelectOptions options = referenceOptions((ReferenceType) fieldType);
            if(options.isEmpty()) {
                return null;
            }
            return new SelectBuilder(BindingType.STRING, options);
        }
        if (fieldType instanceof TextType) {
            return new SimpleInputBuilder(BindingType.STRING);
        }

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        LOGGER.warning("Unknown FieldType: " + fieldType.getClass().getName());
        return null;
    }

    private SelectOptions enumOptions(EnumType enumType) {
        Cardinality cardinality = enumType.getCardinality();
        List<Item> items = Lists.newArrayListWithCapacity(enumType.getValues().size());
        for (EnumValue enumValue : enumType.getValues()) {
            Item item = new Item();
            item.setLabel(enumValue.getLabel());
            item.setValue(enumValue.getId().asString());
            items.add(item);
        }
        return new SelectOptions(cardinality, items);
    }

    private SelectOptions booleanOptions() {
        Item no = new Item();
        no.setLabel("no");
        no.setValue("FALSE");

        Item yes = new Item();
        yes.setLabel("yes");
        yes.setValue("TRUE");

        return new SelectOptions(Cardinality.SINGLE, Arrays.asList(yes, no));
    }

    private SelectOptions referenceOptions(ReferenceType referenceType) {
        ArrayList<Item> items = Lists.newArrayList();

        for (ReferenceChoice choice : locator.getReferenceChoices(referenceType.getRange())) {
            Item item = new Item();
            item.setLabel(choice.getLabel());
            item.setValue(choice.getId().asString());
            items.add(item);
        }

        return new SelectOptions(referenceType.getCardinality(), items);
    }
}
