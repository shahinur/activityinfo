package org.activityinfo.io.importing.data;

import java.util.List;

/**
 * Guesses the delimiter used in an a text file
 */
public class DelimiterGuesser {

    private static final char[] POSSIBLE_DELIMITERS = new char[] { ',', ';', '\t', '|'};
    private static final int ROWS_TO_SCAN = 10;

    private final String text;

    public DelimiterGuesser(String text) {
        this.text = text;
    }

    public char guess() {
        // first, look for a delimiter that divides the columns into
        // a consistent number of columns > 1
        for(char delimiter : POSSIBLE_DELIMITERS) {
            if(numColumns(delimiter) > 1) {
                return delimiter;
            }
        }

        // if not, then assume that this is a dataset of 1 column
        return '\0';
    }

    private int numColumns(char delimiter) {

        // we expect a delimiter to divide the input data set into
        // a more or less similar number of columns

        List<PastedRow> rows = new RowParser(text, delimiter)
                .parseRows(ROWS_TO_SCAN);

        int numColumns = -1;

        for(PastedRow row : rows) {
            if(numColumns < 0) {
                numColumns = row.getColumnCount();
            } else if(numColumns != row.getColumnCount()) {
                return -1;
            }
        }
        return numColumns;
    }

}
