package org.activityinfo.server.command.handler.adapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.legacy.shared.model.CountryDTO;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.StoreReader;

import java.util.Map;

public class CountryProvider {

    private StoreReader reader;
    private Map<ResourceId, CountryDTO> countries = Maps.newHashMap();


    public CountryProvider(StoreReader reader) {
        this.reader = reader;
    }

    public CountryDTO getCountry(ResourceId id) {
        Preconditions.checkArgument(id.getDomain() == CuidAdapter.COUNTRY_DOMAIN);
        CountryDTO country = countries.get(id);
        if(country == null) {
            country = new CountryBuilder(reader, id).build();
            countries.put(id, country);
        }
        return country;
    }

    public java.util.Collection<CountryDTO> getCountries() {
        return countries.values();
    }
}
