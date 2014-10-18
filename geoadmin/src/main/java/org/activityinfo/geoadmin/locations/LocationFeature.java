package org.activityinfo.geoadmin.locations;

import com.google.common.collect.Maps;
import org.activityinfo.geoadmin.ImportFeature;
import org.activityinfo.geoadmin.model.AdminEntity;

import java.util.Map;

public class LocationFeature {
	private ImportFeature feature;
	private Map<Integer, AdminEntity> entities;
	
	public LocationFeature(ImportFeature feature) {
		this.feature = feature;
		this.entities = Maps.newHashMap();
	}

	public ImportFeature getFeature() {
		return feature;
	}

	public Map<Integer, AdminEntity> getEntities() {
		return entities;
	}	
}
