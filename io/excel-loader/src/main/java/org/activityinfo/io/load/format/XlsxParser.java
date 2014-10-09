package org.activityinfo.io.load.format;

import org.activityinfo.io.load.FileFormatException;
import org.activityinfo.io.load.FileSource;
import org.activityinfo.io.load.LoadContext;
import org.activityinfo.io.load.table.FuzzyTableLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class XlsxParser implements AutoCloseable {

    private final FileSource fileSource;
    private final LoadContext context;

    private final ReadOnlySharedStringsTable stringTable;
    private final StylesTable styleTable;
    private final XSSFReader xssfReader;
    private final OPCPackage xlsxPackage;

    public XlsxParser(LoadContext context, FileSource fileSource) throws IOException {
        this.fileSource = fileSource;
        this.context = context;
        try {
            xlsxPackage = OPCPackage.open(fileSource.getContent().openStream());
            stringTable = new ReadOnlySharedStringsTable(xlsxPackage);
            xssfReader = new XSSFReader(xlsxPackage);
            styleTable = xssfReader.getStylesTable();

        } catch (SAXException | OpenXML4JException e) {
            throw new FileFormatException(fileSource, e);
        }
    }

    public void readSheets() throws IOException {
        try {
            XSSFReader.SheetIterator it = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            while (it.hasNext()) {

                try(InputStream stream = it.next()) {
                    String formLabel = formLabel(it.getSheetName());
                    FuzzyTableLoader tableLoader = new FuzzyTableLoader(context, formLabel);

                    XMLReader sheetReader = newXmlReader();
                    sheetReader.setContentHandler(sheetHandler(tableLoader));
                    sheetReader.parse(new InputSource(stream));

                    tableLoader.done();
                }
            }
        } catch (InvalidFormatException | SAXException e) {
            throw new FileFormatException(fileSource, e);
        }
    }

    private String formLabel(String sheetName) {
        return fileSource.getFilename() + " - [" + sheetName + "]";
    }

    private XMLReader newXmlReader() {
        try {
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxFactory.newSAXParser();
            return saxParser.getXMLReader();
        } catch(Throwable e) {
            throw new RuntimeException("Could not create XMLReader", e);
        }
    }

    private XlsxSheetHandler sheetHandler(FuzzyTableLoader importer) {
        return new XlsxSheetHandler(styleTable, stringTable, importer);
    }


    @Override
    public void close() throws IOException {
        xlsxPackage.close();
    }
}
