package org.activityinfo.legacy.shared.model;

public interface IsFormField {

    /**
     *
     * @return this form field's label
     */
    String getLabel();

    /**
     *
     * @return the defined sort order index of the form field.
     */
    int getSortOrder();


}
