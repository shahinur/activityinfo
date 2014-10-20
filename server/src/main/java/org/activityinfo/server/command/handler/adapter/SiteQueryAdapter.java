package org.activityinfo.server.command.handler.adapter;

import com.google.common.collect.Sets;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.StoreReader;

import java.util.Set;

import static org.activityinfo.model.legacy.CuidAdapter.*;

/**
 * Deconstructs a legacy Filter to determine which forms are necessary
 * to include.
 */
public final class SiteQueryAdapter {
    private StoreReader reader;
    private Set<ResourceId> forms = Sets.newHashSet();

    public static Set<ResourceId> getFormClasses(StoreReader reader, Filter filter) {
        return new SiteQueryAdapter(reader, filter).forms;
    }

    private SiteQueryAdapter(StoreReader reader, Filter filter) {
        this.reader = reader;
        findActivities(filter);
    }

    private void findActivities(Filter filter) {
        if(filter.isRestricted(DimensionType.Site)) {
            for(Integer siteId : filter.getRestrictions(DimensionType.Site)) {
                UserResource resource = reader.getResource(CuidAdapter.resourceId(SITE_DOMAIN, siteId));
                forms.add(resource.getResource().getClassId());
            }
        } else if(filter.isRestricted(DimensionType.Activity)) {
            for(Integer activityId : filter.getRestrictions(DimensionType.Activity)) {
                forms.add(activityFormClass(activityId));
            }
        } else if(filter.isRestricted(DimensionType.Database)) {
            for(Integer databaseId : filter.getRestrictions(DimensionType.Database)) {
                findActivityForms(databaseId(databaseId));
            }
        } else {
            throw new UnsupportedOperationException("GetSites must be filtered " +
                    "by one or more databases, forms, or sites");
        }
    }

    private void findActivityForms(ResourceId parent) {

        for (ResourceNode child : reader.getFolderItems(parent)) {
            ResourceId childClassId = child.getClassId();
            if(childClassId.equals(FormClass.CLASS_ID)) {
                forms.add(child.getId());

            } else if(childClassId.equals(FolderClass.CLASS_ID) &&
                      childClassId.getDomain() == ACTIVITY_DOMAIN) {

                forms.add(child.getId());
            }
        }
    }

}
