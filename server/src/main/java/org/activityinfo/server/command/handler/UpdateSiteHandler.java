package org.activityinfo.server.command.handler;

import com.extjs.gxt.ui.client.data.RpcMap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.activityinfo.legacy.shared.command.UpdateSite;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.VoidResult;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.NarrativeValue;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.barcode.BarcodeValue;
import org.activityinfo.model.type.enumerated.EnumFieldValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.StoreReader;

import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class UpdateSiteHandler implements CommandHandler<UpdateSite> {

    private ResourceStore store;

    @Inject
    public UpdateSiteHandler(ResourceStore store) {
        this.store = store;
    }

    @Override
    public CommandResult execute(UpdateSite cmd, User user) throws CommandException {
        Resource instance;
        FormClass formClass;

        try (StoreReader reader = store.openReader(user.asAuthenticatedUser())) {
            ResourceId instanceId = resourceId(CuidAdapter.SITE_DOMAIN, cmd.getSiteId());
            instance = reader.getResource(instanceId).getResource();
            formClass = FormClass.fromResource(reader.getResource(instance.getClassId()).getResource());
        }

        RecordBuilder updated = Records.buildCopyOf(instance.getValue());
        RpcMap changes = cmd.getChanges();

        for (FormField field : formClass.getFields()) {

            String fieldName = field.getId().asString();

            if(changes.containsKey(fieldName)) {
                updateIndicator(updated, field, fieldName, changes);
            } else if(field.getType() instanceof EnumType) {
                updateEnum(updated, field, changes);
            }

        }

        if(changes.containsKey("comments")) {
            String commentId = field(formClass.getId(), COMMENT_FIELD).asString();
            String newValue = (String) changes.get("comments");
            NarrativeValue newFieldValue = null;
            if(newValue != null) {
                newFieldValue = new NarrativeValue(newValue);
            }
            updated.set(commentId, newFieldValue);
        }

        Resource updatedResource = Resources.createResource();
        updatedResource.setId(instance.getId());
        updatedResource.setOwnerId(instance.getOwnerId());
        updatedResource.setValue(updated.build());

        store.put(user.asAuthenticatedUser(), updatedResource);

        return new VoidResult();
    }

    private void updateIndicator(RecordBuilder updated, FormField field, String fieldName, RpcMap changes) {
        Object updatedValue = changes.get(fieldName);
        if (updatedValue == null) {
            updated.remove(fieldName);
        } else {
            if (field.getType() instanceof QuantityType) {
                updated.set(fieldName, new Quantity(((Number) changes.get(fieldName)).doubleValue(),
                        ((QuantityType) field.getType()).getUnits()));

            } else if (field.getType() instanceof TextType) {
                updated.set(fieldName, (String) updatedValue);

            } else if (field.getType() instanceof BarcodeType) {
                updated.set(fieldName, BarcodeValue.valueOf((String) updatedValue));

            } else if (field.getType() instanceof NarrativeType) {
                updated.set(fieldName, new NarrativeValue((String) updatedValue));
            }
        }
    }

    private void updateEnum(RecordBuilder updated, FormField field, RpcMap changes) {
        String fieldName = field.getName();
        EnumType enumType = (EnumType) field.getType();
        Set<ResourceId> value = Sets.newHashSet();
        boolean isUpdated = false;

        for(EnumValue enumItem : enumType.getValues()) {
            int attributeId = CuidAdapter.getLegacyId(enumItem.getId());
            String attributePropertyName = AttributeDTO.getPropertyName(attributeId);
            if(changes.containsKey(attributePropertyName)) {
                isUpdated = true;
                if(changes.get(attributePropertyName) == Boolean.TRUE) {
                    value.add(enumItem.getId());
                }
            }
        }

        if(isUpdated) {
            updated.set(fieldName, new EnumFieldValue(value));
        }
    }
}
