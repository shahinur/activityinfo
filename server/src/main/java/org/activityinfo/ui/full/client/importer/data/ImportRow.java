package org.activityinfo.ui.full.client.importer.data;

/**
 * Generic interface to a row of an imported table
 */
public interface ImportRow {

    String getColumnValue(int index);

}