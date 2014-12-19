package org.activityinfo.server.endpoint.odk;

import com.google.api.client.util.Maps;
import com.google.common.base.Preconditions;
import org.activityinfo.legacy.shared.command.CreateLocation;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.geo.GeoPoint;
import org.activityinfo.model.type.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.activityinfo.model.legacy.CuidAdapter.END_DATE_FIELD;
import static org.activityinfo.model.legacy.CuidAdapter.LOCATION_FIELD;
import static org.activityinfo.model.legacy.CuidAdapter.START_DATE_FIELD;
import static org.activityinfo.model.legacy.CuidAdapter.field;
import static org.activityinfo.model.legacy.CuidAdapter.locationInstanceId;
import static org.activityinfo.server.endpoint.odk.FieldValueParserFactory.fromFieldType;

public class XFormInstanceReader {
    static final private String UUID = "_uuid";
    static final private String START = "start";
    static final private String END = "end";

    final private LinkedHashMap<String, Object> array[];
    final private ResourceId formClassId;
    final private ArrayList<FormField> formFields;
    final private MetadataPersister metadataPersister;
    final private int locationTypeId;

    public XFormInstanceReader(LinkedHashMap<String, Object> array[], FormClass formClass,
                               MetadataPersister metadataPersister, int locationTypeId) {
        Preconditions.checkNotNull(array, formClass);
        this.array = array;
        this.formClassId = formClass.getId();
        this.formFields = new ArrayList<>(formClass.getFields());
        this.metadataPersister = metadataPersister;
        this.locationTypeId = locationTypeId;
    }

    public FormInstance[] build() {
        final int length = array.length;
        final FormInstance formInstances[] = new FormInstance[length];

        for (int i = 0; i < length; i++) {
            final Object instanceId = array[i].get(UUID);
            final Object start = array[i].get(START);
            final Object end = array[i].get(END);
            final ResourceId formInstanceId = CuidAdapter.newLegacyFormInstanceId(formClassId);

            formInstances[i] = new FormInstance(formInstanceId, formClassId);

            if (instanceId instanceof String) {
                metadataPersister.persist(formInstanceId, new MetadataPersister.Metadata((String) instanceId, null));
            } else {
                throw new IllegalStateException("Invalid uuid");
            }

            if (start instanceof String) {
                String date[] = ((String) start).split("T");
                formInstances[i].set(field(formClassId, START_DATE_FIELD), LocalDate.parse(date[0]));
            } else {
                formInstances[i].set(field(formClassId, START_DATE_FIELD), new LocalDate());
            }

            if (end instanceof String) {
                String date[] = ((String) end).split("T");
                formInstances[i].set(field(formClassId, END_DATE_FIELD), LocalDate.parse(date[0]));
            } else {
                formInstances[i].set(field(formClassId, END_DATE_FIELD), new LocalDate());
            }
        }

        for (FormField formField : formFields) {
            final FieldValueParser fieldValueParser = fromFieldType(formField.getType(), false, false);
            final String code = formField.getCode();

            for (int i = 0; i < length; i++) {
                final LinkedHashMap<String, Object> map = array[i];
                final FormInstance formInstance = formInstances[i];

                if (map != null) {
                    final Object value = map.get(code);

                    if (value instanceof String) {
                        final FieldValue fieldValue = fieldValueParser.parse((String) value);

                        if (fieldValue instanceof GeoPoint) {
                            int newLocationId = new KeyGenerator().generateInt();
                            ReferenceValue referenceValue = new ReferenceValue(locationInstanceId(newLocationId));
                            ResourceId locationFieldId = field(formClassId, LOCATION_FIELD);
                            CreateLocation createLocation = createLocation(newLocationId, (GeoPoint) fieldValue);
                            Object instanceId = map.get(UUID);

                            if (instanceId instanceof String) {
                                formInstance.set(locationFieldId, referenceValue);
                                metadataPersister.persist(formInstance.getId(),
                                        new MetadataPersister.Metadata((String) instanceId, createLocation));
                            } else {
                                throw new IllegalStateException("The \"Invalid uuid exception\" should've been thrown");
                            }
                        } else {
                            formInstance.set(formField.getId(), fieldValue);
                        }
                    }
                }
            }
        }

        return formInstances;
    }

    private CreateLocation createLocation(int id, GeoPoint geoPoint) {
        Map<String, Object> properties = Maps.newHashMap();

        properties.put("id", id);
        properties.put("locationTypeId", locationTypeId);
        properties.put("name", "Custom location");
        properties.put("latitude", geoPoint.getLatitude());
        properties.put("longitude", geoPoint.getLongitude());

        return new CreateLocation(properties);
    }

    static public interface MetadataPersister {
        void persist(ResourceId formInstanceId, Metadata metadata);

        static final public class Metadata {
            final public String instanceId;
            final public CreateLocation createLocation;

            public Metadata(String instanceId, CreateLocation createLocation) {
                this.instanceId = instanceId;
                this.createLocation = createLocation;
            }
        }
    }
}
