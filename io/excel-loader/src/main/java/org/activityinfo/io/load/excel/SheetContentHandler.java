package org.activityinfo.io.load.excel;


import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;


/**
 * Derived from http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
 * <p/>
 * Also see Standard ECMA-376, 1st edition, part 4, pages 1928ff, at
 * http://www.ecma-international.org/publications/standards/Ecma-376.htm
 * <p/>
 * A web-friendly version is http://openiso.org/Ecma/376/Part4
 */
public class SheetContentHandler extends DefaultHandler {



    /**
     * The type of the data value is indicated by an attribute on the cell.
     * The value is usually in a "v" element within the cell.
     */
    enum xssfDataType {
        BOOL,
        ERROR,
        FORMULA,
        INLINESTR,
        SSTINDEX,
        NUMBER,
    }


    public List<ColumnBuffer> getColumnsBuffers() {
        return columnsBuffers;
    }

    /**
     * Table with styles
     */
    private StylesTable stylesTable;

    /**
     * Table with unique strings
     */
    private ReadOnlySharedStringsTable sharedStringsTable;

    /**
     * Destination for data
     */
    private final List<ColumnBuffer> columnsBuffers = Lists.newArrayList();

    /**
     * Number of columns to read starting with leftmost
     */
    private int numColumns;

    // Set when V start element is seen
    private boolean vIsOpen;

    // Set when cell start element is seen;
    // used when cell close element is seen.
    private xssfDataType nextDataType;

    // Used to format numeric cell values.
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter;

    private int thisColumn = -1;
    // The last column printed to the output stream
    private int lastColumnNumber = -1;

    // Gathers characters as they are seen.
    private StringBuffer value;

    /**
     * Accepts objects needed while parsing.
     *
     * @param styles  Table of styles
     * @param strings Table of shared strings
     */
    public SheetContentHandler(
        StylesTable styles,
        ReadOnlySharedStringsTable strings) {
        this.stylesTable = styles;
        this.sharedStringsTable = strings;
        this.value = new StringBuffer();
        this.nextDataType = xssfDataType.NUMBER;
        this.formatter = new DataFormatter();
    }



    /*
       * (non-Javadoc)
       * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
       */
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        if ("inlineStr".equals(name) || "v".equals(name)) {
            vIsOpen = true;
            // Clear contents cache
            value.setLength(0);
        }
        // c => cell
        else if ("c".equals(name)) {
            // Get the cell reference
            String r = attributes.getValue("r");
            int firstDigit = -1;
            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            thisColumn = nameToColumn(r.substring(0, firstDigit));


            // Set up defaults.
            this.nextDataType = xssfDataType.NUMBER;
            this.formatIndex = -1;
            this.formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType))
                nextDataType = xssfDataType.BOOL;
            else if ("e".equals(cellType))
                nextDataType = xssfDataType.ERROR;
            else if ("inlineStr".equals(cellType))
                nextDataType = xssfDataType.INLINESTR;
            else if ("s".equals(cellType))
                nextDataType = xssfDataType.SSTINDEX;
            else if ("str".equals(cellType))
                nextDataType = xssfDataType.FORMULA;
            else if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                //  with a special style or format
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                this.formatIndex = style.getDataFormat();
                this.formatString = style.getDataFormatString();
                if (this.formatString == null)
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            }
        }

    }


    /*
       * (non-Javadoc)
       * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
       */
    public void endElement(String uri, String localName, String name)
        throws SAXException {

        // Output after we've seen the string contents
        // Emit commas for any fields that were missing on this row
        if (lastColumnNumber == -1) {
            lastColumnNumber = 0;
        }


        // v => contents of a cell
        if ("v".equals(name)) {



            while(columnsBuffers.size() <= thisColumn) {
                int index = columnsBuffers.size();
                columnsBuffers.add(new ColumnBuffer(index));
            }

            for (int i = lastColumnNumber+1; i < thisColumn; ++i) {
                columnsBuffers.get(i).pushEmpty();
            }

            if(numColumns <= lastColumnNumber) {
                numColumns = lastColumnNumber;
            }

            ColumnBuffer thisBuffer = columnsBuffers.get(thisColumn);

            // Process the value contents as required.
            // Do now, as characters() may be called more than once
            switch (nextDataType) {

                case BOOL:
                    char first = value.charAt(0);
                    thisBuffer.push(first == '0');
                    break;

                case ERROR:
                    //thisStr = "\"ERROR:" + value.toString() + '"';
                    thisBuffer.pushEmpty();
                    break;

                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                   // thisStr = '"' + value.toString() + '"';
                    break;

                case INLINESTR:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisBuffer.pushString(rtsi.toString());
                    break;

                case SSTINDEX:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        thisBuffer.pushString(rtss.toString());
                    }
                    catch (NumberFormatException ex) {
                        System.out.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
                    }
                    break;

                case NUMBER:
                    String n = value.toString();
                    thisBuffer.pushNumber(Double.parseDouble(n));
                    break;

                default:
                    System.err.println("(TODO: Unexpected type: " + nextDataType + ")");
                    break;
            }

            // Update column
            if (thisColumn > -1)
                lastColumnNumber = thisColumn;

        } else if ("row".equals(name)) {

            // Print out any missing commas if needed

            // Columns are 0 based
            if (lastColumnNumber == -1) {
                lastColumnNumber = 0;
            }
            for (int i = lastColumnNumber; i < numColumns; i++) {
                this.columnsBuffers.get(i).pushEmpty();
            }

            // We're onto a new row
            lastColumnNumber = -1;
        }

    }

    /**
     * Captures characters only if a suitable element is open.
     * Originally was just "v"; extended for inlineStr also.
     */
    public void characters(char[] ch, int start, int length)
        throws SAXException {
        if (vIsOpen)
            value.append(ch, start, length);
    }

    /**
     * Converts an Excel column name like "C" to a zero-based index.
     *
     * @param name
     * @return Index corresponding to the specified name
     */
    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }

}

