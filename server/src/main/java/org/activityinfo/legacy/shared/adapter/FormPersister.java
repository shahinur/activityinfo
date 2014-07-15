package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.*;
import org.activityinfo.legacy.shared.command.result.BatchResult;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.legacy.shared.command.result.CreateResult;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.TextType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.database.hibernate.entity.AttributeGroup;
import org.activityinfo.server.database.hibernate.entity.Indicator;
import org.apache.poi.ss.formula.functions.T;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Oh god, why, god, why....
 *
 * Re-translate the update FormClass BACK into the legacy model. We can't yet
 * save it as an actual FormClass because the rest of the app still needs to catch up.
 *
 */
public class FormPersister {

    /**
     * Maintain a mapping, during this browser session, of ids that have been assigned
     * to new Cuids.
     */
    private static Map<ResourceId, Integer> newIds = Maps.newHashMap();

    private Dispatcher dispatcher;
    private FormClass form;
    private ActivityDTO activity;
    private Map<Integer, IndicatorDTO> indicators = Maps.newHashMap();
    private Map<Integer, AttributeGroupDTO> attributeGroups = Maps.newHashMap();
    private List<Command> commands = Lists.newArrayList();
    private Map<CreateEntity, ResourceId> oldIds = Maps.newHashMap();

    public FormPersister(Dispatcher dispatcher, FormClass form) {
        this.dispatcher = dispatcher;
        this.form = form;
    }

    public Promise<Void> persist() {
        int activityId = CuidAdapter.getLegacyIdFromCuid(form.getId());
        return dispatcher.execute(new GetFormViewModel(activityId)).join(new Function<ActivityDTO, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable ActivityDTO input) {
                activity = input;
                return syncActivity();
            }
        });
    }


    private void indexFields() {
        for(IndicatorDTO indicator : activity.getIndicators()) {
            indicators.put(indicator.getId(), indicator);
        }
        for(AttributeGroupDTO group : activity.getAttributeGroups()) {
            attributeGroups.put(group.getId(), group);
        }
    }

    private Promise<Void> syncActivity() {
        // make a list of indicators / attributes
        indexFields();

        // for each field, find the existing field or create a new one
        int sortOrder = 1;
        for(FormField field : form.getFields()) {

            if(isIndicator(field)) {
                updateOrCreateIndicator(field, sortOrder);
            }

            sortOrder++;
        }
        if(commands.isEmpty()) {
            return Promise.done();
        } else {
            return dispatcher.execute(new BatchCommand(commands)).then(new Function<BatchResult, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable BatchResult input) {
                    mapNewIds(input);
                    return null;
                }

            });
        }
    }

    private void mapNewIds(BatchResult results) {
        for(int i=0;i<commands.size();++i) {
            if(commands.get(i) instanceof CreateEntity) {
                CreateEntity creation = (CreateEntity) commands.get(i);
                CreateResult result = results.getResult(i);
                ResourceId oldId = oldIds.get(creation);
                newIds.put(oldId, result.getNewId());
            }
        }
    }

    private void updateOrCreateIndicator(FormField field, int sortOrder) {
        IndicatorDTO indicator = null;
        boolean created = true;
        if(field.getId().getDomain() == CuidAdapter.INDICATOR_DOMAIN) {
            indicator = indicators.get(CuidAdapter.getLegacyIdFromCuid(field.getId()));
            created = false;
        } else if(newIds.containsKey(field.getId())) {
            indicator = indicators.get(newIds.get(field.getId()));
            created = false;
        }
        if(indicator == null) {
            indicator = new IndicatorDTO();
            indicator.setActivityId(activity.getId());
        }

        indicator.setName(field.getLabel());
        indicator.setMandatory(field.isRequired());
        indicator.setDescription(field.getDescription());
        indicator.set("sortOrder", sortOrder);

        if(field.getType() instanceof QuantityType) {
            indicator.setType(FieldTypeClass.QUANTITY);
            indicator.setUnits(((QuantityType) field.getType()).getUnits());
        }

        if(created) {
            CreateEntity create = new CreateEntity(indicator);
            oldIds.put(create, field.getId());
            commands.add(create);
        } else {
            commands.add(new UpdateEntity(indicator, indicator.getProperties()));
        }
    }

    private boolean isIndicator(FormField field) {

        if(field.getId().equals(CuidAdapter.commentsField(activity.getId()))) {
            return false;
        }

        return field.getType() instanceof QuantityType ||
               field.getType() instanceof NarrativeType ||
               field.getType() instanceof TextType;
    }

}
