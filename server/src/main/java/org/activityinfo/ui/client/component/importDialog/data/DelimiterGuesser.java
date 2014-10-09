package org.activityinfo.ui.client.component.importDialog.data;

import java.util.List;

/**
 * Guesses the delimiter used in an a text file
 */
public class DelimiterGuesser {

    private static final char[] POSSIBLE_DELIMITERS = new char[]{',', ';', '\t', '|'};
    private static final int ROWS_TO_SCAN = 10;
    private static final double MATCH_RATE_IN_PERCENT = 1.0;

    private final String text;
    private int firstNotMatchedRow = -1;
    private boolean isDataSetOfOneColumn = false;

    public DelimiterGuesser(String text) {
        this.text = text;
    }

    public char guess() {
        // first, look for a delimiter that divides the columns into
        // a consistent number of columns > 1
        for (char delimiter : POSSIBLE_DELIMITERS) {
            if (matchColumnCount(delimiter)) {
                return delimiter;
            }
        }

        // if not, then assume that this is a dataset of 1 column
        isDataSetOfOneColumn = true;
        return '\0';
    }

    private boolean matchColumnCount(char delimiter) {

        // we expect a delimiter to divide the input data set into
        // a more or less similar number of columns

        List<PastedRow> rows = new RowParser(text, delimiter)
                .parseRows(ROWS_TO_SCAN);

        int numColumns = -1;

        int matchedRowCount = 1; // start with 1 for first match
        for (PastedRow row : rows) {
            if (numColumns < 0) {
                numColumns = row.getColumnCount();
            } else if(numColumns == row.getColumnCount()) {
                matchedRowCount++;
            } else {
                if (firstNotMatchedRow < 0) {
                    firstNotMatchedRow = rows.indexOf(row);
                }
            }
        }

        if (numColumns == 1) {
            return false;
        }

        double actualMatchPercent = (double) matchedRowCount / (double) rows.size();
        return actualMatchPercent >= MATCH_RATE_IN_PERCENT ;
    }

    public int getFirstNotMatchedRow() {
        return firstNotMatchedRow;
    }

    public boolean isDataSetOfOneColumn() {
        return isDataSetOfOneColumn;
    }
}
