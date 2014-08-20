package org.activityinfo.ui.widget.grid;

public class ColumnWidths {
    public static final int DEFAULT_COLUMN_WIDTH_IN_EM = 10;

    /**
     * Sometimes "hardcode" is not as good solution if column width is big, here we are trying to increase
     * column width if column header is bigger then default value. (Indeed we have room for improvements for this dummy algorithm ;))
     *
     * @param columnHeader column header string
     * @return width of column in em
     */
    public static int columnWidthInEm(String columnHeader) {
        int width = columnHeader.length() / 2;
        return width < DEFAULT_COLUMN_WIDTH_IN_EM ? DEFAULT_COLUMN_WIDTH_IN_EM : width;
    }
}
