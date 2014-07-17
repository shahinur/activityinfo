package org.activityinfo.ui.client.component.report.editor.map;


import com.google.common.collect.Maps;
import com.google.gwt.dom.client.MapElement;
import org.activityinfo.legacy.shared.reports.model.MapReportElement;
import org.activityinfo.legacy.shared.reports.model.layers.MapLayer;
import org.activityinfo.ui.client.component.report.editor.map.layer.LayerController;
import org.discotools.gwt.leaflet.client.layers.raster.TileLayer;
import org.discotools.gwt.leaflet.client.map.Map;

public class MapController {

    private java.util.Map<MapElement, LayerController> controllers = Maps.newIdentityHashMap();
    private Map map;
    private MapReportElement model;
    private TileLayer tileLayer;

    public MapController(Map map, MapReportElement model) {
        this.map = map;
        this.model = model;
    }

    public void sync() {


        for (MapLayer layer : model.getLayers()) {
            LayerController controller = controllers.get(layer);
            if (controller == null) {
                createController(layer);
            } else {
                controller.update();
            }
        }
    }


    private void createController(MapLayer layer) {
    }

}
