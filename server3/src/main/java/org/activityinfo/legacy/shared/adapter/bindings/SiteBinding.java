package org.activityinfo.legacy.shared.adapter.bindings;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.getLegacyIdFromCuid;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.locationField;

/**
 * Defines a two-way binding between Sites and FormInstances
 */
public class SiteBinding extends ModelBinding<SiteDTO> {

    private final ActivityDTO activity;

    SiteBinding(ActivityDTO activity) {
        super(CuidAdapter.activityFormClass(activity.getId()), CuidAdapter.SITE_DOMAIN);
        this.activity = activity;
    }

    public ActivityDTO getActivity() {
        return activity;
    }

    public ResourceId getLocationField() {
        return locationField(activity.getId());
    }

    public int getAdminEntityId(FormInstance instance) {
        return getLegacyIdFromCuid(instance.getInstanceId(getLocationField()));
    }

    public LocationTypeDTO getLocationType() {
        return activity.getLocationType();
    }
}
