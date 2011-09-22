package org.sigmah.server.report.generator.map.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.server.report.generator.map.RadiiCalculator;
import org.sigmah.server.report.generator.map.TiledMap;
import org.sigmah.shared.domain.AdminEntity;
import org.sigmah.shared.report.content.LatLng;
import org.sigmah.shared.report.content.Point;
import org.sigmah.shared.report.model.PointValue;
import org.sigmah.shared.report.model.SiteData;
import org.sigmah.shared.report.model.clustering.AdministrativeLevelClustering;

import com.google.inject.internal.Lists;

/**
 * Clusters a set of points by the administrative level the points reside in, 
 * calculated per country
 */
public class AdminLevelClusterer implements Clusterer {
	private AdministrativeLevelClustering model;
	private RadiiCalculator radiiCalculator;
	
	public AdminLevelClusterer(
			AdministrativeLevelClustering adminLevelClustering,
			RadiiCalculator radiiCalculator) {
		super();
		this.model = adminLevelClustering;
		this.radiiCalculator = radiiCalculator;
	}

	@Override
	public List<Cluster> cluster(TiledMap map, List<PointValue> points) {
		 
		// admin entity id -> cluster
		Map<Integer, Cluster> adminClusters = new HashMap<Integer,Cluster>();
		
		for(PointValue pv : points) {
			AdminEntity entity = getAdminEntityId(pv);
			if(entity != null) {
				Cluster cluster = adminClusters.get(entity.getId());
				if(cluster == null) {
					cluster = new Cluster(pv);
					cluster.setPoint(adminCenter(map, entity));
					cluster.setTitle(entity.getName());
					adminClusters.put(entity.getId(), cluster);
				} else {
					cluster.addPointValue(pv);
				}
			}
		}
	
		ArrayList<Cluster> clusters = Lists.newArrayList();
		
		// update centers of clusters based on points, if any
		for(Cluster cluster : adminClusters.values()) {
			updateCenter(cluster);
			if(cluster.hasPoint()) {
				clusters.add(cluster);
			}
		}
		radiiCalculator.calculate(clusters);
		
		return clusters;
	}

	private void updateCenter(Cluster cluster) {
		double count = 0;
		double sumX = 0;
		double sumY = 0;
		
		for(PointValue pv : cluster.getPointValues()) {
			if(pv.hasPoint()) {
				count ++;
				sumX += pv.getPoint().getDoubleX();
				sumY += pv.getPoint().getDoubleY();
			}
		}
		
		if(count > 0) {
			cluster.setPoint(new Point(sumX / count, sumY / count));
		}
	}

	private Point adminCenter(TiledMap map, AdminEntity entity) {
		if(entity.getBounds() == null) {
			return null;
		} else {
			LatLng center = new LatLng( 
					(entity.getBounds().getY1() + entity.getBounds().getY2()) / 2d,
					(entity.getBounds().getX1() + entity.getBounds().getX2()) / 2d);
					
			return map.fromLatLngToPixel(center);
		}
	}

	private AdminEntity getAdminEntityId(PointValue pv) {
		return getAdminEntity(pv.site);
	}

	private AdminEntity getAdminEntity(SiteData site) {
		Map<Integer, AdminEntity> membership = site.adminEntities;
		for(Integer levelId : model.getAdminLevels()) {
			if(membership.containsKey(levelId)) {
				return membership.get(levelId);
			}
		}
		return null;
	}

	@Override
	public boolean isMapped(SiteData site) {
		return getAdminEntity(site) != null;
	}
}
