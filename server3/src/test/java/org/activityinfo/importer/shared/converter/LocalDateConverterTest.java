package org.activityinfo.importer.shared.converter;

import org.activityinfo.core.shared.type.converter.StringToLocalDateConverter;
import org.activityinfo.model.type.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class LocalDateConverterTest {


    @Test
    public void simpleTests() {

        StringToLocalDateConverter converter = new StringToLocalDateConverter();

        assertThat(converter.convert("2011-01-15"), equalTo(ymd(2011,1,15)));
        assertThat(converter.convert("15 May 2049"), equalTo(ymd(2049,5,15)));
        assertThat(converter.convert("4/30/07"), equalTo(ymd(2007,4,30)));
    }

    @Test
    public void monthNameDayYear() {
        assertThat(convertString("Oct 31st, 1940"), equalTo(ymd(1940,10,31)));
    }

    @Test
    public void longDateTest() {
        assertThat(convertString("Wed, 14th of May, 1999"), equalTo(ymd(1999,5,14)));
    }


    private LocalDate convertString(String string) {
        return new StringToLocalDateConverter().convert(string);
    }

    private LocalDate ymd(int year, int month, int day) {
        return new LocalDate(year, month, day);
    }

    @Ignore
    @Test
    public void exhaustiveTest() {

        int styles[] = new int[] { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG };

        String languages[] = new String[] { "en", "fr", "es", "it", "pt", "nl" };

        for(int style : styles) {
            for(String language : languages) {

                testFormat(language, DateFormat.getDateInstance(style, Locale.forLanguageTag(language)));
            }
        }
    }


    private void testFormat(String language, DateFormat format) {
        StringToLocalDateConverter converter = new StringToLocalDateConverter();

        for(int year=1950;year<2050;++year) {
            for(int month=1;month<=12;++month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for(int day=1;day<=daysInMonth;++day) {

                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    Date date = calendar.getTime();
                    LocalDate localDate = new LocalDate(date);

                    String string = format.format(date);
                    try {
                        LocalDate converted = converter.convert(string);

                        if(!converted.equals(localDate)) {
                            System.out.println("[" + language +"] " + string + " => " + converted +
                            " [expected: " + localDate + "]");
                        }
                    } catch(Exception e) {
                        System.out.println("[" + language +"] " + string + " => " + "ERROR: " + e.getMessage());
                    }
                }
            }
        }
    }
}
