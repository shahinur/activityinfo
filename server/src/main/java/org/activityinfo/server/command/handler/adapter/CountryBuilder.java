package org.activityinfo.server.command.handler.adapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;
import org.activityinfo.legacy.shared.model.CountryDTO;
import org.activityinfo.legacy.shared.model.LocationTypeDTO;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.service.store.StoreReader;

import java.util.Map;

public class CountryBuilder {
    private StoreReader reader;
    private ResourceId countryId;
    private final CountryDTO country;
    private final Resource countryResource;

    private Map<ResourceId, LocationTypeDTO> locationTypes = Maps.newHashMap();

    public CountryBuilder(StoreReader reader, ResourceId countryId) {
        this.reader = reader;
        this.countryId = countryId;
        this.country = new CountryDTO();
        this.countryResource = reader.getResource(countryId).getResource();
        Preconditions.checkState(countryResource.getClassId().equals(FolderClass.CLASS_ID));
    }

    public CountryDTO build() {
        country.setId(CuidAdapter.getLegacyId(countryId));
        country.setName(countryResource.getValue().getString(FolderClass.LABEL_FIELD_NAME));

        for (ResourceNode item : reader.getFolderItems(countryId)) {
            if(item.getId().getDomain() == CuidAdapter.ADMIN_LEVEL_DOMAIN) {
                addLevel(item.getId());
            }
        }

        for (ResourceNode item : reader.getFolderItems(countryId)) {
            if(item.getId().getDomain() == CuidAdapter.LOCATION_TYPE_DOMAIN) {
                addLocationType(item.getId());
            }
        }

        return country;
    }

    private void addLevel(ResourceId id) {
        FormClass formClass = FormClass.fromResource(reader.getResource(id).getResource());
        AdminLevelDTO level = new AdminLevelDTO();
        level.setId(CuidAdapter.getLegacyId(id));
        level.setName(formClass.getLabel());
        level.setCountryId(country.getId());
        level.setParentLevelId(findParentLevelId(formClass));
        country.getAdminLevels().add(level);
    }

    private Integer findParentLevelId(FormClass formClass) {
        for(FormField field : formClass.getFields()) {
            if(field.isSubPropertyOf(ApplicationProperties.PARENT_PROPERTY)) {
                ReferenceType type = (ReferenceType) field.getType();
                ResourceId formClassId = Iterables.getOnlyElement(type.getRange());
                Preconditions.checkState(formClassId.getDomain() == CuidAdapter.ADMIN_LEVEL_DOMAIN);

                return CuidAdapter.getLegacyId(formClassId);
            }
        }
        return null;
    }

    private void addLocationType(ResourceId id) {
        FormClass formClass = FormClass.fromResource(reader.getResource(id).getResource());
        LocationTypeDTO locationType = new LocationTypeDTO();
        locationType.setId(CuidAdapter.getLegacyId(id));
        locationType.setName(formClass.getLabel());
        locationType.setAdminLevels(country.getAdminLevels());
        country.getLocationTypes().add(locationType);
        locationTypes.put(id, locationType);
    }

    public Map<ResourceId, LocationTypeDTO> getLocationTypes() {
        return locationTypes;
    }

}
