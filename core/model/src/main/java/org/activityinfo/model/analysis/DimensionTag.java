package org.activityinfo.model.analysis;

public class DimensionTag {
    private String dimensionId;
    private String dimensionValue;

    public DimensionTag(String dimensionId, String dimensionValue) {
        this.dimensionId = dimensionId;
        this.dimensionValue = dimensionValue;
    }

    public String getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
    }
}
