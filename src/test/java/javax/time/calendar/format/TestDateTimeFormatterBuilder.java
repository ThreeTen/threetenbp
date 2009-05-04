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
        assertEquals(f.toPattern(), "Value(ISO.DayOfMonth)");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_appendValue_1arg_null() throws Exception {
        builder.appendValue(null);
    }

    //-----------------------------------------------------------------------
    public void test_appendValue_2arg() throws Exception {
        builder.appendValue(DOM_RULE, 3);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "Value(ISO.DayOfMonth,3)");
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
        assertEquals(f.toPattern(), "Value(ISO.DayOfMonth,2,3,NORMAL)");
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
        assertEquals(f.toPattern(), "'a'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "a");
    }

    public void test_appendPattern_charLiteral_singleApos() throws Exception {
        builder.appendPattern("''");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "''");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "'");
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_unexpectedEndRoundBracket() throws Exception {
        builder.appendPattern(")");
    }

    
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_appendPattern_unexpectedEndSquareBracket() throws Exception {
        builder.appendPattern("]");
    }

    public void test_appendPattern_unexpectedComma() throws Exception {
        builder.appendPattern(",");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "','");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), ",");
    }

    public void test_appendPattern_unexpectedOther() throws Exception {
        builder.appendPattern("%");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'%'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "%");
    }

    //-----------------------------------------------------------------------
    public void test_appendPattern_stringLiteral() throws Exception {
        builder.appendPattern("'hello_people,][)('");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'hello_people,][)('");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "hello_people,][)(");
    }

    public void test_appendPattern_stringLiteralLength2() throws Exception {
        builder.appendPattern("'hi'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'hi'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "hi");
    }

    public void test_appendPattern_stringLiteral_wrapMeaningful() throws Exception {
        builder.appendPattern("'ZoneId()'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'ZoneId()'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "ZoneId()");
    }

    public void test_appendPattern_stringLiteral_singleApos() throws Exception {
        builder.appendPattern("''''");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "''");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "'");
    }

    public void test_appendPattern_stringLiteral_mixedApos() throws Exception {
        builder.appendPattern("'o''clock'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'o''clock'");
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
    public void test_appendPattern_compositeMandatory() throws Exception {
        builder.appendPattern("'aaa'('bbb')'ccc'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'aaa'('bbb')'ccc'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "aaabbbccc");
    }

    public void test_appendPattern_compositeMandatory_midApos() throws Exception {
        builder.appendPattern("'aaa'('b''bb')'ccc'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'aaa'('b''bb')'ccc'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "aaab'bbccc");
    }

    public void test_appendPattern_compositeOptional() throws Exception {
        builder.appendPattern("'aaa'['bbb']'ccc'");
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), "'aaa'['bbb']'ccc'");
        assertEquals(f.print(LocalDate.date(2008, 6, 30)), "aaabbbccc");
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="validPatterns")
    Object[][] dataValid() {
        return new Object[][] {
            {"Value(ISO.DayOfMonth)", "Value(ISO.DayOfMonth)"},
            
            {"Value(ISO.DayOfMonth,2)", "Value(ISO.DayOfMonth,2)"},
            
            {"Value(ISO.DayOfMonth,1,2,NORMAL)", "Value(ISO.DayOfMonth,1,2,NORMAL)"},
            {"Value(ISO.DayOfMonth,1,10,NORMAL)", "Value(ISO.DayOfMonth)"},
            
            {"Fraction(ISO.NanoOfSecond,3,9)", "Fraction(ISO.NanoOfSecond,3,9)"},
            
            {"Text(ISO.DayOfWeek)", "Text(ISO.DayOfWeek)"},
            
            {"Text(ISO.DayOfWeek,FULL)", "Text(ISO.DayOfWeek)"},
            {"Text(ISO.DayOfWeek,SHORT)", "Text(ISO.DayOfWeek,SHORT)"},
            
            {"OffsetId()", "OffsetId()"},
            
            {"Offset('Z',true,false)", "Offset('Z',true,false)"},
            {"Offset('Z',true,true)", "OffsetId()"},
            {"Offset('+00:00',true,false)", "Offset('+00:00',true,false)"},
            {"Offset('',true,false)", "Offset('',true,false)"},
            {"Offset('''',true,false)", "Offset('''',true,false)"},
            {"Offset('a,b',true,false)", "Offset('a,b',true,false)"},
            {"Offset('Z',T,F)", "Offset('Z',true,false)"},
            {"Offset('Z',T,T)", "OffsetId()"},
            
            {"ZoneId()", "ZoneId()"},
            
            {"ZoneText(FULL)", "ZoneText(FULL)"},
            {"ZoneText(SHORT)", "ZoneText(SHORT)"},
            
            {"Value(ISO.Year,4)-Value(ISO.MonthOfYear,2)-Value(ISO.DayOfMonth,2)", "Value(ISO.Year,4)'-'Value(ISO.MonthOfYear,2)'-'Value(ISO.DayOfMonth,2)"},
        };
    }

    @Test(dataProvider="validPatterns")
    public void test_appendPattern_valid(String input, String expected) throws Exception {
        builder.appendPattern(input);
        DateTimeFormatter f = builder.toFormatter();
        assertEquals(f.toPattern(), expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="invalidPatterns")
    Object[][] dataInvalid() {
        return new Object[][] {
            {"Value"},
            {"Value(ISO.DayOfMonth,2,3)"},
            {"Value(ISO.DayOfMonth,2,3,NORMAL,2)"},
            
            {"Value()"},
            {"Value(X)"},
            
            {"Value(X,2)"},
            {"Value(ISO.DayOfMonth,X)"},
            {"Value(ISO.DayOfMonth,0)"},
            {"Value(ISO.DayOfMonth,11)"},
            
            {"Value(X,2,3,NORMAL)"},
            {"Value(ISO.DayOfMonth,X,3,NORMAL)"},
            {"Value(ISO.DayOfMonth,2,X,NORMAL)"},
            {"Value(ISO.DayOfMonth,2,3,X)"},
            {"Value(ISO.DayOfMonth,0,3,NORMAL)"},
            {"Value(ISO.DayOfMonth,2,11,NORMAL)"},
            
            {"Fraction"},
            {"Fraction()"},
            {"Fraction(ISO.NanoOfSecond)"},
            {"Fraction(ISO.NanoOfSecond,3)"},
            {"Fraction(ISO.NanoOfSecond,3,9,2)"},
            {"Fraction(X,3,9)"},
            {"Fraction(ISO.NanoOfSecond,X,9)"},
            {"Fraction(ISO.NanoOfSecond,3,X)"},
            {"Fraction(ISO.NanoOfSecond,-1,9)"},
            {"Fraction(ISO.NanoOfSecond,3,10)"},
            {"Fraction(ISO.DayOfMonth,3,9)"},
            
            {"Text"},
            {"Text()"},
            {"Text(ISO.DayOfWeek,FULL,2)"},
            
            {"Text(X)"},
            {"Text(X,FULL)"},
            {"Text(ISO.DayOfWeek,X)"},
            
            {"OffsetId"},
            {"OffsetId(2)"},
            
            {"Offset"},
            {"Offset()"},
            {"Offset('Z')"},
            {"Offset('Z',true)"},
            {"Offset('Z,true,true,true)"},
            
            {"Offset(X,true,false)"},
            {"Offset('Z',X,false)"},
            {"Offset('Z',true,X)"},
            {"Offset('Z,true,true)"},
            {"Offset('Z'X,true,false)"},
            
            {"ZoneId"},
            {"ZoneId(2)"},
            
            {"ZoneText"},
            {"ZoneText()"},
            {"ZoneText(FULL,2)"},
            
            {"ZoneText(X)"},
            
            {"yyyy-MM-dd"},
            
            {"ZoneId("},
            {"ZoneId()(')"},
            {"ZoneId()(()"},
            {"ZoneId()([)"},
            {"ZoneId()(]"},
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

}
