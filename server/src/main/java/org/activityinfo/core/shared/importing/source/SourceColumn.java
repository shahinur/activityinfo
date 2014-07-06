package org.activityinfo.core.shared.importing.source;

import org.activityinfo.model.form.FormFieldType;

/**
 * Describes a column in the imported table
 */
public class SourceColumn {

    private String header;
    private int index;
    private FormFieldType guessedType = FormFieldType.FREE_TEXT;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FormFieldType getGuessedType() {
        return guessedType;
    }

    public void setGuessedType(FormFieldType guessedType) {
        this.guessedType = guessedType;
    }

    @Override
    public String toString() {
        return "[" + header + "]";
    }
}
