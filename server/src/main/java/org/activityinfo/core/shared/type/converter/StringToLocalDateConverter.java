package org.activityinfo.core.shared.type.converter;


import org.activityinfo.model.type.time.LocalDate;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Parses strings to local dates
 */
public class StringToLocalDateConverter implements StringConverter<LocalDate> {

    private static final String SEPARATORS = "-/\\, \t.";
    public static final int NUM_COMPONENTS = 3;

    private static final int JAN = 1;
    private static final int FEB = 2;
    private static final int MARCH = 3;
    private static final int APRIL = 4;
    private static final int MAY = 5;
    private static final int JUNE = 6;
    private static final int JULY = 7;
    private static final int AUG = 8;
    private static final int SEPT = 9;
    private static final int OCT = 10;
    private static final int NOV = 11;
    private static final int DEC = 12;

    private static final int PIVOT_YEAR = 50;

    private static final int MIN_MONTH_NAME_LENGTH = 2;

    public static final int NOT_FOUND = -1;

    @Nonnull
    @Override
    public LocalDate convert(@Nonnull String string) {
        // basically we expect three components, in SOME format
        // that are separated by something normal

        int components[] = new int[NUM_COMPONENTS];

        int charIndex = 0;
        int component = 0;

        // if we stumble across the month name,
        // make sure we note this.
        int monthIndex = NOT_FOUND;

        while(component < NUM_COMPONENTS) {

            if(charIndex >= string.length()) {
                throw new IllegalArgumentException("Not enough components in '" + string + "', found: " +
                        Arrays.toString(components));
            }

            int start = charIndex;
            char c = string.charAt(start);

            if(Character.isDigit(c)) {

                // read all the digits in
                do {
                    charIndex++;
                }
                while(isDigit(string, charIndex));

                // parse as number
                components[component] = Integer.parseInt(string.substring(start, charIndex));

                // move on the next component
                component++;

            } else {

                // read until we hit a separator or digit
                do {
                    charIndex++;
                } while(isPartOfWord(string, charIndex));

                int monthNameLength = charIndex-start;
                if(monthNameLength > MIN_MONTH_NAME_LENGTH) {
                    int month = tryParseLatinMonthName(string.substring(start, charIndex));
                    if(month != NOT_FOUND) {
                        components[component] = month;
                        monthIndex = component;
                        component++;
                    }
                }
            }

            // advance through any separator chars
            while(isSeparator(string, charIndex)) {
                charIndex++;
            }
        }

        if(monthIndex != -1) {
            return parseUsingKnownMonthPosition(components, monthIndex);

        } else {
            // try to find the obvious year
            int yearIndex = findYearIndex(components, -1);
            if(yearIndex == -1) {
                // if we can't find a 4-digit year, we can only assume that it comes at
                // the end in some completely ambiguous form like 3/4/12
                yearIndex = 2;
            }

            return parseUsingKnownYearPosition(string, components, yearIndex);
        }
    }

    private LocalDate parseUsingKnownYearPosition(String string, int[] components, int yearIndex) {
        if(yearIndex == 0) {
            // usually YYYY-MM-dd
            if(monthAndDayMatch(components, 1, 2)) {
                return toDate(components, yearIndex, 1, 2);

            } else {
                return toDate(components, yearIndex, 2, 1);
            }

        } else if(yearIndex == 1) {
            // date in the middle?? 31-2000-12 ??  i don't think so...
            throw new IllegalArgumentException(string);

        } else {
            // the classic ambiguous 5/3/2007
            if(monthAndDayMatch(components, 1, 0)) {
                return toDate(components, yearIndex, 1, 0);
            } else {
                return toDate(components, yearIndex, 0, 1);
            }
        }
    }

    private boolean monthAndDayMatch(int[] components, int monthIndex, int dayIndex) {
        int month = components[monthIndex];
        if(month > 12) {
            return false;
        }
        if(components[dayIndex] > getMaxDaysInMonth(month)) {
            return false;
        }
        return true;
    }

    private LocalDate parseUsingKnownMonthPosition(int components[], int monthIndex) {
        int yearIndex = findYearIndex(components, monthIndex);
        if(yearIndex != -1) {
            int dayIndex = remainingIndex(monthIndex, yearIndex);
            return new LocalDate(components[yearIndex], components[monthIndex], components[dayIndex]);
        } else {
            // best guess
            if(monthIndex == 1) {
                // usually 31st May 12
                return toDate(components, 2, monthIndex, 1);
            } else {
                // who knows...
                return toDate(components, 1, monthIndex, 2);
            }
        }
    }

    private LocalDate toDate(int[] components, int yearIndex, int monthIndex, int dayIndex) {
        int year = components[yearIndex];
        if(isTwoDigits(year)) {
            year += inferCentury(year);
        }
        return new LocalDate(year, components[monthIndex], components[dayIndex]);
    }

    private int inferCentury(int year) {
        if(year < PIVOT_YEAR) {
            return 2000;
        } else {
            return 1900;
        }
    }

    private boolean isTwoDigits(int year) {
        return year < 1000;
    }

    private int remainingIndex(int monthIndex, int yearIndex) {
        for(int i=0;i!=NUM_COMPONENTS;++i) {
            if(i != monthIndex && i != yearIndex) {
                return i;
            }
        }
        throw new IllegalStateException();
    }

    private int findYearIndex(int components[], int monthIndex) {
        int maxDaysInThisMonth = monthIndex == -1 ? 31 : getMaxDaysInMonth(components[monthIndex]);
        for(int i=0;i!=NUM_COMPONENTS;++i) {
            if(i != monthIndex) {
                if(components[i] > maxDaysInThisMonth) {
                    return i;
                }
            }
        }
        return NOT_FOUND;
    }

    private int getMaxDaysInMonth(int month) {
        if(month == 2) {
            return 29;
        } else if(month == APRIL || month == JUNE || month == SEPT || month == NOV) {
            return 30;
        } else {
            return 31;
        }
    }

    /**
     * Tries to parse a string as a month name in any language using
     * a series of hand-tuned heuristics.
     *
     * @param string the string, with a length of at least {@code MIN_MONTH_NAME_LENGTH}
     * @return a month index, 1-12, or {@code NOT_FOUND} if there is no match
     */
    private int tryParseLatinMonthName(String string) {
        String lowered = string.toLowerCase();
        switch(lowered.charAt(0)) {
            case 'e':
                return JAN; // enero
            case 'f':
                return FEB; // februrary, febrero
            case 'm':
                if(hasAny(lowered, 'y', 'i', 'g')) {
                    return MAY; // may, maio, mei, mayo, mag (it)
                } else {
                    return MARCH; // march, marzo, marco, marz
                }
            case 'a':
                if(hasAny(lowered, 'g', 'o')) {
                    return AUG;
                } else if(hasAny(lowered, 'b', 'p', 'v')) {
                    return APRIL;
                }
                break;
            case 'i':
            case 'j':
                if(hasChar(lowered, 'a')) {
                    return JAN;  // january, januar, januari,

                } else if(hasChar(lowered, 'n')) {
                    return JUNE; // june, juni, junio

                } else if(hasAny(lowered, 'i', 'y', 'l')) {
                    return JULY; // july, julio, juli
                }
                break;

            case 'l':
                if(lowered.charAt(1) == 'u') {
                    return JULY; // luglio (it)
                }

            case 's':
                return SEPT; // september, septiembre, etc

            case 'o':
                return OCT; // oktober, october, octubre

            case 'n':
                if(lowered.indexOf('v') != -1) {
                    return NOV; // november, noviembre,
                }
                break;

            case 'd':
                return DEC;



            case 'g':
                 // italian
                if(lowered.indexOf('e') != -1) {
                    return JAN; // genn
                } else if(lowered.indexOf('u') != -1) {
                    return JUNE; // giugno
                }
        }

        return NOT_FOUND;
    }

    private boolean hasAny(String lowered, char i, char b, char c) {
        return lowered.indexOf(i) != -1 || lowered.indexOf(b) != -1 || lowered.indexOf(c) != -1;
    }

    private boolean hasChar(String lowered, char a) {
        return lowered.indexOf(a) != -1;
    }

    private boolean hasAny(String lowered, char a, char b) {
        return lowered.indexOf(a) != -1 || lowered.indexOf(b) != -1;
    }

    private boolean isSeparator(String string, int charIndex) {
        if(charIndex < string.length()) {
            return SEPARATORS.indexOf(string.charAt(charIndex)) != -1;
        } else {
            return false;
        }
    }

    private boolean isDigit(String string, int charIndex) {
        if(charIndex < string.length()) {
            return Character.isDigit(string.charAt(charIndex));
        } else {
            return false;
        }
    }

    private boolean isPartOfWord(String string, int charIndex) {
        if(charIndex < string.length()) {
            return Character.isLetter(string.charAt(charIndex));
        } else {
            return false;
        }
    }
}
