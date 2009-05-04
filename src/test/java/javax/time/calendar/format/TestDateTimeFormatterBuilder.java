/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar.format;

import static org.testng.Assert.*;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.format.DateTimeFormatterBuilder.SignStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test DateTimeFormatterBuilder.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestDateTimeFormatterBuilder {

    private static final DateTimeFieldRule DOM_RULE = ISOChronology.dayOfMonthRule();

    private DateTimeFormatterBuilder builder;

    @BeforeMethod
    public void setUp() {
        builder = new DateTimeFormatterBuilder();
    }

    //-----------------------------------------------------------------------
    public void test_appendValue_1arg() throws Exception {
        builder.appendValue(DOM_RULE);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "Value(ISO.DayOfMonth)");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_appendValue_1arg_null() throws Exception {
        builder.appendValue(null);
    }

    //-----------------------------------------------------------------------
    public void test_appendValue_2arg() throws Exception {
        builder.appendValue(DOM_RULE, 3);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "Value(ISO.DayOfMonth,3)");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_appendValue_2arg_null() throws Exception {
        builder.appendValue(null, 3);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_2arg_widthTooSmall() throws Exception {
        builder.appendValue(DOM_RULE, 0);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_2arg_widthTooBig() throws Exception {
        builder.appendValue(DOM_RULE, 11);
    }

    //-----------------------------------------------------------------------
    public void test_appendValue_3arg() throws Exception {
        builder.appendValue(DOM_RULE, 2, 3, SignStyle.NORMAL);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "Value(ISO.DayOfMonth,2,3,NORMAL)");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_appendValue_3arg_nullField() throws Exception {
        builder.appendValue(null, 2, 3, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_3arg_minWidthTooSmall() throws Exception {
        builder.appendValue(DOM_RULE, 0, 2, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_3arg_minWidthTooBig() throws Exception {
        builder.appendValue(DOM_RULE, 11, 2, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthTooSmall() throws Exception {
        builder.appendValue(DOM_RULE, 2, 0, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthTooBig() throws Exception {
        builder.appendValue(DOM_RULE, 2, 11, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthMinWidth() throws Exception {
        builder.appendValue(DOM_RULE, 4, 2, SignStyle.NORMAL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_appendValue_3arg_nullSignStyle() throws Exception {
        builder.appendValue(DOM_RULE, 2, 3, null);
    }

    //-----------------------------------------------------------------------
    public void test_appendPattern_charLiteral() throws Exception {
        builder.appendPattern("'a'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'a'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "a");
    }

    public void test_appendPattern_charLiteral_singleApos() throws Exception {
        builder.appendPattern("''");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "''");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "'");
    }

    public void test_appendPattern_unexpectedOther() throws Exception {
        builder.appendPattern("%");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'%'");
    }

    //-----------------------------------------------------------------------
    public void test_appendPattern_stringLiteral() throws Exception {
        builder.appendPattern("'hello_people,][)('");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'hello_people,][)('");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "hello_people,][)(");
    }

    public void test_appendPattern_stringLiteralLength2() throws Exception {
        builder.appendPattern("'hi'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'hi'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "hi");
    }

    public void test_appendPattern_stringLiteral_wrapMeaningful() throws Exception {
        builder.appendPattern("'yyyy'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'yyyy'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "yyyy");
    }

    public void test_appendPattern_stringLiteral_singleApos() throws Exception {
        builder.appendPattern("''''");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "''");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "'");
    }

    public void test_appendPattern_stringLiteral_mixedApos() throws Exception {
        builder.appendPattern("'o''clock'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), "'o''clock'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "o'clock");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_stringLiteral_incomplete() throws Exception {
        builder.appendPattern("'hello");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_stringLiteral_incomplete_midApos() throws Exception {
        builder.appendPattern("'hel''lo");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_stringLiteral_incomplete_endApos() throws Exception {
        builder.appendPattern("'hello''");
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="validPatterns")
    Object[][] dataValid() {
        return new Object[][] {
            {"y", "Value(ISO.Year)"},
            {"yy", "Value(ISO.Year,2,10,NORMAL)"},
            {"yyy", "Value(ISO.Year,3,10,NORMAL)"},
            {"yyyy", "Value(ISO.Year,4,10,EXCEEDS_PAD)"},
            {"yyyyy", "Value(ISO.Year,5,10,EXCEEDS_PAD)"},
            
            {"x", "Value(ISO.WeekBasedYear)"},
            {"xx", "Value(ISO.WeekBasedYear,2,10,NORMAL)"},
            {"xxx", "Value(ISO.WeekBasedYear,3,10,NORMAL)"},
            {"xxxx", "Value(ISO.WeekBasedYear,4,10,EXCEEDS_PAD)"},
            {"xxxxx", "Value(ISO.WeekBasedYear,5,10,EXCEEDS_PAD)"},
            
            {"Q", "Value(ISO.QuarterOfYear)"},
            {"QQ", "Value(ISO.QuarterOfYear,2)"},
            {"QQQ", "Value(ISO.QuarterOfYear,3)"},
            
            {"M", "Value(ISO.MonthOfYear)"},
            {"MM", "Value(ISO.MonthOfYear,2)"},
            {"MMM", "Text(ISO.MonthOfYear,SHORT)"},
            {"MMMM", "Text(ISO.MonthOfYear)"},
            {"MMMMM", "Text(ISO.MonthOfYear)"},
            
            {"q", "Value(ISO.MonthOfQuarter)"},
            {"qq", "Value(ISO.MonthOfQuarter,2)"},
            {"qqq", "Value(ISO.MonthOfQuarter,3)"},
            
            {"w", "Value(ISO.WeekOfWeekBasedYear)"},
            {"ww", "Value(ISO.WeekOfWeekBasedYear,2)"},
            {"www", "Value(ISO.WeekOfWeekBasedYear,3)"},
            
            {"D", "Value(ISO.DayOfYear)"},
            {"DD", "Value(ISO.DayOfYear,2)"},
            {"DDD", "Value(ISO.DayOfYear,3)"},
            
            {"d", "Value(ISO.DayOfMonth)"},
            {"dd", "Value(ISO.DayOfMonth,2)"},
            {"ddd", "Value(ISO.DayOfMonth,3)"},
            
            {"F", "Value(ISO.WeekOfMonth)"},
            {"FF", "Value(ISO.WeekOfMonth,2)"},
            {"FFF", "Value(ISO.WeekOfMonth,3)"},
            
            {"E", "Text(ISO.DayOfWeek,SHORT)"},
            {"EE", "Text(ISO.DayOfWeek,SHORT)"},
            {"EEE", "Text(ISO.DayOfWeek,SHORT)"},
            {"EEEE", "Text(ISO.DayOfWeek)"},
            {"EEEEE", "Text(ISO.DayOfWeek)"},
            
            {"e", "Value(ISO.DayOfWeek)"},
            {"ee", "Value(ISO.DayOfWeek,2)"},
            {"eee", "Value(ISO.DayOfWeek,3)"},
            
            {"a", "Text(ISO.AmPmOfDay,SHORT)"},
            {"aa", "Text(ISO.AmPmOfDay,SHORT)"},
            {"aaa", "Text(ISO.AmPmOfDay,SHORT)"},
            {"aaaa", "Text(ISO.AmPmOfDay)"},
            {"aaaaa", "Text(ISO.AmPmOfDay)"},
            
            {"H", "Value(ISO.HourOfDay)"},
            {"HH", "Value(ISO.HourOfDay,2)"},
            {"HHH", "Value(ISO.HourOfDay,3)"},
            
            {"K", "Value(ISO.HourOfAmPm)"},
            {"KK", "Value(ISO.HourOfAmPm,2)"},
            {"KKK", "Value(ISO.HourOfAmPm,3)"},
            
            {"m", "Value(ISO.MinuteOfHour)"},
            {"mm", "Value(ISO.MinuteOfHour,2)"},
            {"mmm", "Value(ISO.MinuteOfHour,3)"},
            
            {"s", "Value(ISO.SecondOfMinute)"},
            {"ss", "Value(ISO.SecondOfMinute,2)"},
            {"sss", "Value(ISO.SecondOfMinute,3)"},
            
            {"S", "Value(ISO.MilliOfSecond)"},
            {"SS", "Value(ISO.MilliOfSecond,2)"},
            {"SSS", "Value(ISO.MilliOfSecond,3)"},
            
            {"n", "Value(ISO.NanoOfSecond)"},
            {"nn", "Value(ISO.NanoOfSecond,2)"},
            {"nnn", "Value(ISO.NanoOfSecond,3)"},
            
            {"yyyy-MM-dd'T'HH:mm:ss.SSS", "Value(ISO.Year,4,10,EXCEEDS_PAD)'-'Value(ISO.MonthOfYear,2)'-'Value(ISO.DayOfMonth,2)" +
                "'T'Value(ISO.HourOfDay,2)':'Value(ISO.MinuteOfHour,2)':'Value(ISO.SecondOfMinute,2)'.'Value(ISO.MilliOfSecond,3)"},
        };
    }

    @Test(dataProvider="validPatterns")
    public void test_appendPattern_valid(String input, String expected) throws Exception {
        builder.appendPattern(input);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toString(), expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="invalidPatterns")
    Object[][] dataInvalid() {
        return new Object[][] {
            {"'"},
            {"'hello"},
            {"'hello''"},
        };
    }

    @Test(dataProvider="invalidPatterns", expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_invalid(String input) throws Exception {
        try {
            builder.appendPattern(input);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

//    public void test_sdf() throws Exception {
//        SimpleDateFormat dsf = new SimpleDateFormat("ss.SSS");
//        Date date = dsf.parse("1.1");
//        System.out.println(Instant.millisInstant(date.getTime()));
//    }

}
