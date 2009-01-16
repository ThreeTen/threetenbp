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
package javax.time.calendar;

import static javax.time.calendar.LocalDateTime.*;
import static javax.time.calendar.LocalTime.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static javax.time.calendar.field.MonthOfYear.*;
import static javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition.*;
import static org.testng.Assert.*;

import javax.time.calendar.TimeZone.OffsetInfo;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.zone.ZoneRulesBuilder;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;
import javax.time.period.Period;

import org.testng.annotations.Test;

/**
 * Test ZoneRulesBuilder.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneRulesBuilder {

    private static final ZoneOffset OFFSET_1 = ZoneOffset.zoneOffset(1);
    private static final ZoneOffset OFFSET_2 = ZoneOffset.zoneOffset(2);
    private static final ZoneOffset OFFSET_1_15 = ZoneOffset.zoneOffset(1, 15);
    private static final ZoneOffset OFFSET_2_30 = ZoneOffset.zoneOffset(2, 30);
    private static final Period PERIOD_0 = Period.ZERO;
    private static final Period PERIOD_1HOUR = Period.hours(1);
    private static final Period PERIOD_1HOUR30MIN = Period.hoursMinutesSeconds(1, 30, 0);
    private static final LocalDateTime DATE_TIME_FIRST = dateTime(Year.MIN_YEAR, 1, 1, 0, 0);
    private static final LocalDateTime DATE_TIME_LAST = dateTime(Year.MAX_YEAR, 12, 31, 23, 59);
    private static final LocalDateTime DATE_TIME_2008_01_01 = dateTime(2008, 1, 1, 0, 0);
    private static final LocalDateTime DATE_TIME_2008_07_01 = dateTime(2008, 7, 1, 0, 0);

    //-----------------------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------------------
    public void test_constructor() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), STANDARD);
        b.addWindowForever(OFFSET_2);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getID(), "Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertGap(test, 1950, 1, 1, 1, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_2);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_constructor_nullOffset() {
        new ZoneRulesBuilder((ZoneOffset) null, dateTime(1950, 1, 1, 1, 0), WALL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_constructor_nullDateTime() {
        new ZoneRulesBuilder(OFFSET_1, (LocalDateTime) null, WALL);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_constructor_nullTimeDefinition() {
        new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), (TimeDefinition) null);
    }

    //-----------------------------------------------------------------------
    // toRules()
    //-----------------------------------------------------------------------
    public void test_toRules_noWindows() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1_15, dateTime(1950, 1, 1, 1, 0), WALL);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test, TimeZone.timeZone(OFFSET_1_15));
        // TODO: should be aliased?
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toRules_nullID() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_2_30);
        b.toRules(null);
    }

    //-----------------------------------------------------------------------
    // Combined
    //-----------------------------------------------------------------------
    public void test_combined_localFixedRules() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1_15, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindow(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, Year.MAX_YEAR, OCTOBER, -1, SUNDAY, time(1, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1_15);
        assertOverlap(test, 1920, 1, 1, 0, 55, OFFSET_1_15, OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(1800, 7, 1, 1, 0)).getOffset(), OFFSET_1_15);
        assertEquals(test.getOffsetInfo(dateTime(1920, 1, 1, 1, 0)).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(1960, 1, 1, 1, 0)).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 1, 1, 1, 0)).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_2_30);
        assertGap(test, 2008, 3, 30, 1, 20, OFFSET_1, OFFSET_2_30);
        assertOverlap(test, 2008, 10, 26, 0, 20, OFFSET_2_30, OFFSET_1);
    }

    public void test_combined_endsInSavings() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1_15, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_0);
        b.addRuleToWindow(2000, Year.MAX_YEAR, OCTOBER, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR);
        TimeZone test = b.toRules("Pacific/Auckland");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1_15);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_2);
        assertOverlap(test, 1920, 1, 1, 0, 55, OFFSET_1_15, OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 26, 0, 59)).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 26, 1, 00)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 10, 29, 1, 20, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2001, 3, 25, 0, 20, OFFSET_2, OFFSET_1);
        assertGap(test, 2001, 10, 28, 1, 20, OFFSET_1, OFFSET_2);
    }

    public void test_combined_closeTransitions() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 20, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2000, MARCH, 20, time(4, 2), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 1, 59)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 20, 2, 0, OFFSET_1, OFFSET_2);
        assertGap(test, 2000, 3, 20, 2, 59, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 3, 0)).getOffset(), OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 3, 1)).getOffset(), OFFSET_2);
        assertOverlap(test, 2000, 3, 20, 3, 2, OFFSET_2, OFFSET_1);
        assertOverlap(test, 2000, 3, 20, 4, 1, OFFSET_2, OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 4, 2)).getOffset(), OFFSET_1);
    }

    public void test_combined_closeTransitionsMeet() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 20, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2000, MARCH, 20, time(4, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 1, 59)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 20, 2, 0, OFFSET_1, OFFSET_2);
        assertGap(test, 2000, 3, 20, 2, 59, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2000, 3, 20, 3, 0, OFFSET_2, OFFSET_1);
        assertOverlap(test, 2000, 3, 20, 3, 59, OFFSET_2, OFFSET_1);
        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 4, 0)).getOffset(), OFFSET_1);
    }

// TODO: handle conflicting gap/overlap
//    public void test_combined_closeTransitionsConflictPartly() {
//        TransitionRulesBuilder b = new TransitionRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
//        b.addWindowForever(OFFSET_1);
//        b.addRuleToWindow(2000, MARCH, 20, time(2, 0), WALL, PERIOD_1HOUR);
//        b.addRuleToWindow(2000, MARCH, 20, time(3, 30), WALL, PERIOD_0);
//        TimeZone test = b.toRules("Europe/London");
//        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
//        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
//        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 1, 59)).getOffset(), OFFSET_1);
//        assertGap(test, 2000, 3, 20, 2, 0, OFFSET_1, OFFSET_2);
//        assertGap(test, 2000, 3, 20, 2, 29, OFFSET_1, OFFSET_2);
//        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 2, 30)).getOffset(), OFFSET_1);
//        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 2, 59)).getOffset(), OFFSET_1);
//        assertOverlap(test, 2000, 3, 20, 3, 0, OFFSET_2, OFFSET_1);
//        assertOverlap(test, 2000, 3, 20, 3, 29, OFFSET_2, OFFSET_1);
//        assertEquals(test.getOffsetInfo(dateTime(2000, 3, 20, 3, 30)).getOffset(), OFFSET_1);
//    }

    public void test_combined_weirdSavingsBeforeLast() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(1998, MARCH, 20, time(2, 0), WALL, PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, 20, null, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2000, Year.MAX_YEAR, OCTOBER, 20, null, time(2, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(1999, 1, 1, 0, 0)).getOffset(), OFFSET_2_30);
        assertOverlap(test, 2000, 3, 20, 1, 30, OFFSET_2_30, OFFSET_2);
        assertOverlap(test, 2000, 10, 20, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2001, 3, 20, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2001, 10, 20, 1, 30, OFFSET_2, OFFSET_1);
    }

    public void test_combined_differentLengthLastRules1() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(1998, MARCH, 20, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(1998, Year.MAX_YEAR, OCTOBER, 30, null, time(2, 0), WALL, PERIOD_0);
        b.addRuleToWindow(1999, MARCH, 21, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2000, MARCH, 22, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2001, MARCH, 23, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(2002, Year.MAX_YEAR, MARCH, 24, null, time(2, 0), WALL, PERIOD_1HOUR);
        TimeZone test = b.toRules("Europe/London");
        
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        
        assertGap(test, 1998, 3, 20, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 1998, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 1999, 3, 21, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 1999, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2000, 3, 22, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2000, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2001, 3, 23, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2001, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2002, 3, 24, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2002, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2003, 3, 24, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2003, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2004, 3, 24, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2004, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2005, 3, 24, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2005, 10, 30, 1, 30, OFFSET_2, OFFSET_1);
    }

    public void test_combined_differentLengthLastRules2() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(1998, Year.MAX_YEAR, MARCH, 30, null, time(2, 0), WALL, PERIOD_1HOUR);
        b.addRuleToWindow(1998, OCTOBER, 20, time(2, 0), WALL, PERIOD_0);
        b.addRuleToWindow(1999, OCTOBER, 21, time(2, 0), WALL, PERIOD_0);
        b.addRuleToWindow(2000, OCTOBER, 22, time(2, 0), WALL, PERIOD_0);
        b.addRuleToWindow(2001, OCTOBER, 23, time(2, 0), WALL, PERIOD_0);
        b.addRuleToWindow(2002, Year.MAX_YEAR, OCTOBER, 24, null, time(2, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        
        assertGap(test, 1998, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 1998, 10, 20, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 1999, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 1999, 10, 21, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2000, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2000, 10, 22, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2001, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2001, 10, 23, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2002, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2002, 10, 24, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2003, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2003, 10, 24, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2004, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2004, 10, 24, 1, 30, OFFSET_2, OFFSET_1);
        assertGap(test, 2005, 3, 30, 2, 30, OFFSET_1, OFFSET_2);
        assertOverlap(test, 2005, 10, 24, 1, 30, OFFSET_2, OFFSET_1);
    }

// functionality removed
//    public void test_combined_wallContinuesAcrossWindow() {
//        TransitionRulesBuilder b = new TransitionRulesBuilder(OFFSET_1, dateTime(1920, 1, 1, 1, 0), WALL);
//        b.addWindow(OFFSET_1, dateTime(1950, 7, 1, 1, 0), WALL);
//        b.setFixedSavingsToWindow(PERIOD_1HOUR);  // applies to 1920-1950, but also extends to 2000
//        b.addWindow(OFFSET_1, dateTime(2000, 7, 1, 1, 0), WALL);
//        b.addWindowForever(OFFSET_1);
//        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
//        TimeZone test = b.toRules("Europe/London");
//        
//        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
//        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_2_30);
//        
//        assertEquals(test.getOffsetInfo(dateTime(1915, 1, 1, 0, 0)).getOffset(), OFFSET_1);
//        assertEquals(test.getOffsetInfo(dateTime(1925, 1, 1, 0, 0)).getOffset(), OFFSET_2);
//        assertEquals(test.getOffsetInfo(dateTime(1955, 1, 1, 0, 0)).getOffset(), OFFSET_2);
//        assertEquals(test.getOffsetInfo(dateTime(2005, 1, 1, 0, 0)).getOffset(), OFFSET_2_30);
//    }

    //-----------------------------------------------------------------------
    // addWindow()
    //-----------------------------------------------------------------------
    public void test_addWindow_constrainedRules() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1800, 7, 1, 0, 0), WALL);
        b.addWindow(OFFSET_1, dateTime(2008, 6, 30, 0, 0), STANDARD);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, Year.MAX_YEAR, OCTOBER, -1, SUNDAY, time(1, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_2_30);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_2_30);
        assertGap(test, 2000, 3, 26, 1, 30, OFFSET_1, OFFSET_2_30);
        assertOverlap(test, 2000, 10, 29, 0, 30, OFFSET_2_30, OFFSET_1);
        assertGap(test, 2008, 3, 30, 1, 30, OFFSET_1, OFFSET_2_30);
        assertEquals(test.getOffsetInfo(dateTime(2008, 10, 26, 0, 30)).getOffset(), OFFSET_2_30);
    }

    public void test_addWindow_noRules() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1800, 7, 1, 0, 0), WALL);
        b.addWindow(OFFSET_1, dateTime(2008, 6, 30, 0, 0), STANDARD);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addWindow_nullOffset() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindow((ZoneOffset) null, dateTime(2008, 6, 30, 0, 0), STANDARD);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addWindow_nullTime() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindow(OFFSET_1, (LocalDateTime) null, STANDARD);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addWindow_nullTimeDefinition() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindow(OFFSET_1, dateTime(2008, 6, 30, 0, 0), (TimeDefinition) null);
    }

    //-----------------------------------------------------------------------
    // addWindowForever()
    //-----------------------------------------------------------------------
    public void test_addWindowForever_noRules() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1800, 7, 1, 0, 0), WALL);
        b.addWindowForever(OFFSET_1);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_1);
    }

    public void test_addWindowForever_rules() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1800, 7, 1, 0, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, Year.MAX_YEAR, OCTOBER, -1, SUNDAY, time(1, 0), WALL, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_2_30);
        assertGap(test, 2008, 3, 30, 1, 20, OFFSET_1, OFFSET_2_30);
        assertOverlap(test, 2008, 10, 26, 0, 20, OFFSET_2_30, OFFSET_1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addWindowForever_nullOffset() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever((ZoneOffset) null);
    }

    //-----------------------------------------------------------------------
    // setFixedSavings()
    //-----------------------------------------------------------------------
    public void test_setFixedSavingsToWindow() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1800, 7, 1, 0, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(DATE_TIME_FIRST).getOffset(), OFFSET_1);
        assertEquals(test.getOffsetInfo(DATE_TIME_LAST).getOffset(), OFFSET_2_30);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_01_01).getOffset(), OFFSET_2_30);
        assertEquals(test.getOffsetInfo(DATE_TIME_2008_07_01).getOffset(), OFFSET_2_30);
        assertGap(test, 1800, 7, 1, 0, 0, OFFSET_1, OFFSET_2_30);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_setFixedSavingsToWindow_noWindow() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_setFixedSavingsToWindow_cannotMixSavingsWithRule() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, 2020, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_setFixedSavingsToWindow_cannotMixSavingsWithLastRule() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_setFixedSavingsToWindow_nullPeriod() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.setFixedSavingsToWindow((Period) null);
    }

    //-----------------------------------------------------------------------
    // addRuleToWindow()
    //-----------------------------------------------------------------------
    public void test_addRuleToWindow_endOfMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, 2001, MARCH, -1, SUNDAY, time(1, 0), UTC, PERIOD_1HOUR);
        b.addRuleToWindow(2000, 2001, OCTOBER, -1, SUNDAY, time(1, 0), UTC, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(dateTime(1999, 7, 1, 0, 0)).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2000, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 26, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2000, 10, 29, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2001, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2001, 3, 25, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2001, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2001, 10, 28, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2002, 7, 1, 0, 0)).getOffset(), OFFSET_1);
    }

    public void test_addRuleToWindow_endOfMonthFeb() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2004, 2005, FEBRUARY, -1, SUNDAY, time(1, 0), UTC, PERIOD_1HOUR);
        b.addRuleToWindow(2004, 2005, OCTOBER, -1, SUNDAY, time(1, 0), UTC, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(dateTime(2003, 7, 1, 0, 0)).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2004, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2004, 2, 29, 2, 30, OFFSET_1, OFFSET_2);  // leap
        assertEquals(test.getOffsetInfo(dateTime(2004, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2004, 10, 31, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2005, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2005, 2, 27, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2005, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2005, 10, 30, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2006, 7, 1, 0, 0)).getOffset(), OFFSET_1);
    }

    public void test_addRuleToWindow_fromDayOfMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, 2001, MARCH, 10, SUNDAY, time(1, 0), UTC, PERIOD_1HOUR);
        b.addRuleToWindow(2000, 2001, OCTOBER, 10, SUNDAY, time(1, 0), UTC, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(dateTime(1999, 7, 1, 0, 0)).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2000, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 12, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2000, 10, 15, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2001, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2001, 3, 11, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2001, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2001, 10, 14, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2002, 7, 1, 0, 0)).getOffset(), OFFSET_1);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_addRuleToWindow_noWindow() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_addRuleToWindow_cannotMixRuleWithSavings() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_addRuleToWindow_illegalYear1() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(Year.MIN_YEAR - 1, 2008, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_addRuleToWindow_illegalYear2() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MIN_YEAR - 1, MARCH, -1, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_addRuleToWindow_illegalDayOfMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, 2008, MARCH, 0, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_nullMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, (MonthOfYear) null, 31, SUNDAY, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_nullTime() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, (LocalTime) null, WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_nullTimeDefinition() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), (TimeDefinition) null, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_nullPeriod() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), WALL, (Period) null);
    }

    //-----------------------------------------------------------------------
    // addRuleToWindow() - single year object
    //-----------------------------------------------------------------------
    public void test_addRuleToWindow_singleYearObject() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(dateTime(2000, MARCH, 26, 1, 0), UTC, PERIOD_1HOUR);
        b.addRuleToWindow(dateTime(2000, OCTOBER, 29, 1, 0), UTC, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(dateTime(1999, 7, 1, 0, 0)).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2000, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 26, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2000, 10, 29, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2001, 7, 1, 0, 0)).getOffset(), OFFSET_1);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYearObject_nullTime() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow((LocalDateTime) null, WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYearObject_nullTimeDefinition() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(dateTime(2000, MARCH, 31, 1, 0), (TimeDefinition) null, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYearObject_nullPeriod() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(dateTime(2000, MARCH, 31, 1, 0), WALL, (Period) null);
    }

    //-----------------------------------------------------------------------
    // addRuleToWindow() - single year
    //-----------------------------------------------------------------------
    public void test_addRuleToWindow_singleYear() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 26, time(1, 0), UTC, PERIOD_1HOUR);
        b.addRuleToWindow(2000, OCTOBER, 29, time(1, 0), UTC, PERIOD_0);
        TimeZone test = b.toRules("Europe/London");
        assertEquals(test.getOffsetInfo(dateTime(1999, 7, 1, 0, 0)).getOffset(), OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2000, 1, 1, 0, 0)).getOffset(), OFFSET_1);
        assertGap(test, 2000, 3, 26, 2, 30, OFFSET_1, OFFSET_2);
        assertEquals(test.getOffsetInfo(dateTime(2000, 7, 1, 0, 0)).getOffset(), OFFSET_2);
        assertOverlap(test, 2000, 10, 29, 2, 30, OFFSET_2, OFFSET_1);
        
        assertEquals(test.getOffsetInfo(dateTime(2001, 7, 1, 0, 0)).getOffset(), OFFSET_1);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_addRuleToWindow_singleYear_noWindow() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addRuleToWindow(2000, MARCH, 31, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalStateException.class)
    public void test_addRuleToWindow_singleYear_cannotMixRuleWithSavings() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.setFixedSavingsToWindow(PERIOD_1HOUR30MIN);
        b.addRuleToWindow(2000, MARCH, 31, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_addRuleToWindow_singleYear_illegalYear() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(Year.MIN_YEAR - 1, MARCH, 31, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_addRuleToWindow_singleYear_illegalDayOfMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 0, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYear_nullMonth() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, (MonthOfYear) null, 31, time(1, 0), WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYear_nullTime() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 31, (LocalTime) null, WALL, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYear_nullTimeDefinition() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 31, time(1, 0), (TimeDefinition) null, PERIOD_1HOUR30MIN);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_addRuleToWindow_singleYear_nullPeriod() {
        ZoneRulesBuilder b = new ZoneRulesBuilder(OFFSET_1, dateTime(1950, 1, 1, 1, 0), WALL);
        b.addWindowForever(OFFSET_1);
        b.addRuleToWindow(2000, MARCH, 31, time(1, 0), WALL, (Period) null);
    }

    //-----------------------------------------------------------------------
    private static void assertGap(TimeZone test, int y, int m, int d, int hr, int min, ZoneOffset before, ZoneOffset after) {
        LocalDateTime dt = dateTime(y, m, d, hr, min);
        OffsetInfo offsetInfo = test.getOffsetInfo(dt);
        assertEquals(offsetInfo.getLocalDateTime(), dt);
        assertEquals(offsetInfo.isDiscontinuity(), true);
        assertEquals(offsetInfo.getDiscontinuity().isGap(), true);
        assertEquals(offsetInfo.getDiscontinuity().getOffsetBefore(), before);
        assertEquals(offsetInfo.getDiscontinuity().getOffsetAfter(), after);
    }

    private static void assertOverlap(TimeZone test, int y, int m, int d, int hr, int min, ZoneOffset before, ZoneOffset after) {
        LocalDateTime dt = dateTime(y, m, d, hr, min);
        OffsetInfo offsetInfo = test.getOffsetInfo(dt);
        assertEquals(offsetInfo.getLocalDateTime(), dt);
        assertEquals(offsetInfo.isDiscontinuity(), true);
        assertEquals(offsetInfo.getDiscontinuity().isOverlap(), true);
        assertEquals(offsetInfo.getDiscontinuity().getOffsetBefore(), before);
        assertEquals(offsetInfo.getDiscontinuity().getOffsetAfter(), after);
    }

}
