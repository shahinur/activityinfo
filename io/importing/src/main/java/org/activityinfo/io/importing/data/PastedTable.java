package org.activityinfo.io.importing.data;

import com.google.common.collect.Lists;
import org.activityinfo.io.importing.match.ColumnTypeGuesser;
import org.activityinfo.io.importing.source.SourceColumn;
import org.activityinfo.io.importing.source.SourceRow;
import org.activityinfo.io.importing.source.SourceTable;
import org.activityinfo.model.type.converter.ConverterFactory;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * An import source pasted in to a text field by the user.
 */
public class PastedTable implements SourceTable {

    private static final Logger LOGGER = Logger.getLogger(PastedTable.class.getName());

    public static final int HEADER_ROW_COUNT = 1;
    public static final int DEFAULT_ROW_PARSER_COUNT = 10;

    private final RowParser rowParser;

    private List<SourceColumn> columns;
    private final List<PastedRow> rows = Lists.newArrayList();

    public PastedTable(String text) {
        final char delimiter = new DelimiterGuesser(text).guess();
        this.rowParser = new RowParser(text, delimiter);
    }

    @Override
    public List<SourceColumn> getColumns() {
        ensureColumnsParsed();
        return columns;
    }

    private void ensureColumnsParsed() {
        if (columns == null) {
            // ensure header row is parsed
            if (rows.isEmpty()) {
                parseNextRows(HEADER_ROW_COUNT);
            }
            if (!rows.isEmpty()) {
                columns = new ColumnParser(rows.get(0)).parseColumns();
                rows.remove(0); // remove header row
            } else {
                columns = Lists.newArrayList();
            }
        }
    }

    private void ensureRowsParsed() {
        if (rowParser.eof() || rows.size() > DEFAULT_ROW_PARSER_COUNT) {
            ensureColumnsParsed();
            return;
        }

        parseNextRows(DEFAULT_ROW_PARSER_COUNT);
        ensureColumnsParsed();
    }

    /**
     * Parses all rows if not parsed yet. Otherwise if parsed do nothing.
     *
     * @return returns newly parsed rows (if nothing was parsed before returns all rows)
     */
    public List<PastedRow> parseAllRows() {
        return parseNextRows(Integer.MAX_VALUE);
    }

    @Override
    public boolean parsedAllRows() {
        return rowParser.eof();
    }

    public List<PastedRow> parseNextRows(int numberOfRowsToParse) {
        long startTime = new Date().getTime();
        List<PastedRow> parsedRows = rowParser.parseRows(numberOfRowsToParse);
        rows.addAll(parsedRows);
        LOGGER.fine("Parsed " + parsedRows.size() + " row(s), takes: " + (new Date().getTime() - startTime));
        return parsedRows;
    }

    public void guessColumnsType(ConverterFactory converterFactory) {
        ensureRowsParsed();
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setGuessedType(new ColumnTypeGuesser(columnRowValues(i), converterFactory).guessType());
        }
    }

    private List<String> columnRowValues(int columnIndex) {
        List<String> rowValues = Lists.newArrayList();

        for (PastedRow row : rows) {
            rowValues.add(row.getColumnValue(columnIndex));
        }
        return rowValues;
    }

    @Override
    public List<? extends SourceRow> getRows() {
        ensureRowsParsed();
        return rows;
    }

    public String get(int row, int column) {
        ensureRowsParsed();
        int rowSize = rows.size();
        if (row > rowSize && rowParser.hasNextRow()) {
            parseNextRows(row - rowSize + 1);
        }
        return rows.get(row).getColumnValue(column);
    }

    @Override
    public String getColumnHeader(Integer columnIndex) {
        ensureColumnsParsed();
        return columns.get(columnIndex).getHeader();
    }
}