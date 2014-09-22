package org.activityinfo.io.load.excel;


import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.service.store.FormImportOptions;
import org.activityinfo.service.store.ImportWriter;
import org.activityinfo.service.store.InstanceWriter;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelStreamingImporter {


    private OPCPackage xlsxPackage;
    private FormImportOptions options;
    private ImportWriter writer;


    /**
     * Creates a new XLSX -> CSV converter
     *
     * @param pkg        The XLSX package to process
     * @param options
     * @param writer
     */
    public ExcelStreamingImporter(OPCPackage pkg, FormImportOptions options, ImportWriter writer) {
        this.xlsxPackage = pkg;
        this.options = options;
        this.writer = writer;
    }

    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *  @param sheetName
     * @param styles
     * @param strings
     * @param sheetInputStream
     */
    public void processSheet(
        String sheetName, StylesTable styles,
        ReadOnlySharedStringsTable strings,
        InputStream sheetInputStream)
        throws IOException, ParserConfigurationException, SAXException {

        System.out.println("SHEET NAME: " + sheetName);

        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        SheetContentHandler handler = new SheetContentHandler(styles, strings);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);

        List<FieldReader> readers = Lists.newArrayList();

        FormClass form = new FormClass(Resources.generateId());
        form.setLabel(sheetName);
        form.setOwnerId(Resources.ROOT_ID);
        int maxRows = 0;

        for(ColumnBuffer buffer : handler.getColumnsBuffers()) {
            buffer.dump();
            if(buffer.getHeader() != null) {
                FormField field = new FormField(Resources.generateId());
                field.setType(buffer.guessType());
                field.setLabel(buffer.getHeader());
                form.addElement(field);
                if(buffer.getRowCount() > maxRows) {
                    maxRows = buffer.getRowCount();
                }

                if(field.getType() instanceof QuantityType) {
                    readers.add(new QuantityFieldReader(field.getId(), buffer));
                } else {
                    readers.add(new TextFieldReader(field.getId(), buffer));
                }
            }
        }
        InstanceWriter instanceWriter = writer.createFormClass(form);

        for(int i=1;i!=maxRows;++i) {
            FormInstance instance = new FormInstance(Resources.generateId(), form.getId());
            for(FieldReader reader : readers) {
                reader.read(instance, i);
            }
            instanceWriter.write(instance);
        }
    }

    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws IOException
     * @throws OpenXML4JException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void process()
        throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator it = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

        while (it.hasNext()) {
            InputStream stream = it.next();
            String sheetName = it.getSheetName();
            processSheet(sheetName, styles, strings, stream);
            stream.close();
        }
    }

}
