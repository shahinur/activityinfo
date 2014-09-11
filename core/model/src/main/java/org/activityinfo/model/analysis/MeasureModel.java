package org.activityinfo.model.analysis;

public class MeasureModel {

    private String id;
    private String label;
    private SourceModel source;


    public MeasureModel(String id, String label, SourceModel source) {
        this.id = id;
        this.label = label;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SourceModel getSource() {
        return source;
    }

    public void setSource(SourceModel source) {
        this.source = source;
    }
}
