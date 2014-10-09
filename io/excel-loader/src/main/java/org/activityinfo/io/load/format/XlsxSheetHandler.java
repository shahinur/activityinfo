package org.activityinfo.io.load.format;

import org.activityinfo.io.load.table.FuzzyTableLoader;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Derived from http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
 * <p/>
 * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at
 * http://www.ecma-international.org/publications/standards/Ecma-376.htm
 * <p/>
 * A web-friendly version is http://openiso.org/Ecma/376/Part4
 */
class XlsxSheetHandler extends DefaultHandler {


    public static final String ROW_TAG = "row";
    public static final String CELL_TAG = "c";
    public static final String VALUE_TAG = "v";
    public static final String INLINE_STRING_TAG = "inlineStr";

    public static final String CELL_TYPE_ATTRIBUTE = "t";
    public static final String SHARED_STRING_CELL_TYPE = "s";
    public static final String CELL_STYLE_ATTRIBUTE = "s";
    public static final String CELL_REFERENCE_ATTRIBUTE = "r";

    public static final String BOOLEAN_CELL_TYPE = "b";
    public static final String ERROR_CELL_TYPE = "e";
    public static final String INLINE_STRING_TYPE = "inlineStr";
    public static final String FORMULA_TYPE = "str";


    private final StylesTable styleTable;
    private final ReadOnlySharedStringsTable stringTable;

    private final FuzzyTableLoader table;

    // Set when VALUE_TAG start element is seen
    private boolean valueIsOpen;

    private String cellType;
    private String cellStyle;
    private String cellReference;

    // Gathers characters as they are seen.
    private final StringBuffer value;

    public XlsxSheetHandler(StylesTable styleTable, ReadOnlySharedStringsTable stringTable, FuzzyTableLoader table) {
        this.styleTable = styleTable;
        this.stringTable = stringTable;
        this.table = table;
        this.value = new StringBuffer();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        switch (name) {
            case CELL_TAG:
                cellReference = attributes.getValue(CELL_REFERENCE_ATTRIBUTE);
                cellType = attributes.getValue(CELL_TYPE_ATTRIBUTE);
                cellStyle = attributes.getValue(CELL_STYLE_ATTRIBUTE);
                break;

            case INLINE_STRING_TAG:
            case VALUE_TAG:
                valueIsOpen = true;
                value.setLength(0);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (valueIsOpen) {
            value.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        switch(name) {
            case ROW_TAG:
                table.nextRow();
                break;
            case VALUE_TAG:
                pushColumnValue();
                break;
        }
        valueIsOpen = false;
    }

    private void pushColumnValue() {

        int columnIndex = columnIndexOf(cellReference);

        switch(cellType) {
            case BOOLEAN_CELL_TYPE:
                table.pushBoolean(columnIndex, booleanValue());
                break;

            case ERROR_CELL_TYPE:
                // NOOP
                break;

            case SHARED_STRING_CELL_TYPE:
                table.pushString(columnIndex, sharedStringValue());
                break;

            case INLINE_STRING_TYPE:
                table.pushString(columnIndex, inlineStringValue());
                break;

            case FORMULA_TYPE:
                table.pushString(columnIndex, value.toString());
                break;

            default:
                table.pushDouble(columnIndex, Double.parseDouble(value.toString()));
                // It's a number, but almost certainly one
                //  with a special style or format
//                    int styleIndex = Integer.parseInt(cellStyleStr);
//                    XSSFCellStyle style = styleTable.getStyleAt(styleIndex);
//                    this.formatIndex = style.getDataFormat();
//                    this.formatString = style.getDataFormatString();
//                    if (this.formatString == null)
//                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
        }
    }

    private int columnIndexOf(String reference) {
        return columnNameToIndex(reference.substring(0, indexOfFirstDigit(reference)));
    }

    private int indexOfFirstDigit(String reference) {
        for (int c = 0; c < reference.length(); ++c) {
            if (Character.isDigit(reference.charAt(c))) {
                return c;
            }
        }
        throw new IllegalArgumentException(reference);
    }

    private String sharedStringValue() {
        int sharedStringIndex;
        try {
            sharedStringIndex = Integer.parseInt(value.toString());
        } catch(NumberFormatException e) {
            //  System.out.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
            return null;
        }

        String sharedString = stringTable.getEntryAt(sharedStringIndex);
        XSSFRichTextString richTextString = new XSSFRichTextString(sharedString);
        return richTextString.toString();
    }

    private String inlineStringValue() {
        // TODO: have seen an example of this, so it's untested.
        XSSFRichTextString richTextString = new XSSFRichTextString(value.toString());
        return richTextString.toString();
    }

    private boolean booleanValue() {
        return value.charAt(0) != '0';
    }

    /**
     * Converts an Excel column name like "C" to a zero-based index.
     */
    private int columnNameToIndex(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }
}
