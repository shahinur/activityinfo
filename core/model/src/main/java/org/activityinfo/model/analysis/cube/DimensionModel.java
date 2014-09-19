package org.activityinfo.model.analysis.cube;

import com.google.common.collect.Lists;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.List;

/**
 * <em>Dimensions</em> represent our main business concepts and are a generalization of
 * concrete entities (Geography, Time, or Products).
 *
 * <p>Dimensions are described by one or more <em>attributes</em>.</p>
 */
public class DimensionModel {

    private ResourceId id;
    private String label;
    private boolean temporal;
    private final List<AttributeModel> attributes = Lists.newArrayList();


    /**
     * Creates a new dimension with a newly generated cuid
     * @param label the attribute's label
     * @return a new {@code AttributeModel}
     */
    public static DimensionModel newDimension(String label) {
        DimensionModel dimensionModel = new DimensionModel();
        dimensionModel.setId(Resources.generateId());
        dimensionModel.setLabel(label);
        return dimensionModel;
    }

    public DimensionModel() {
    }


    public ResourceId getId() {
        return id;
    }

    public boolean isTemporal() {
        return temporal;
    }

    public void setTemporal(boolean temporal) {
        this.temporal = temporal;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public DimensionModel setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<AttributeModel> getAttributes() {
        return attributes;
    }

    public AttributeModel addAttribute(AttributeModel attributeModel) {
        attributes.add(attributeModel);
        return attributeModel;
    }
}
