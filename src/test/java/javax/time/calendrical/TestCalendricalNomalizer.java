/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import static javax.time.DayOfWeek.THURSDAY;
import static javax.time.MonthOfYear.FEBRUARY;
import static javax.time.MonthOfYear.JUNE;
import static javax.time.MonthOfYear.OCTOBER;
import static javax.time.QuarterOfYear.Q2;
import static javax.time.calendrical.ISODateTimeRule.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.AM_PM_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendrical.ISODateTimeRule.CLOCK_HOUR_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_WEEK;
import static javax.time.calendrical.ISODateTimeRule.DAY_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_AMPM;
import static javax.time.calendrical.ISODateTimeRule.HOUR_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.MINUTE_OF_DAY;
import static javax.time.calendrical.ISODateTimeRule.MINUTE_OF_HOUR;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendrical.ISODateTimeRule.MONTH_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.QUARTER_OF_YEAR;
import static javax.time.calendrical.ISODateTimeRule.SECOND_OF_MINUTE;
import static javax.time.calendrical.ISODateTimeRule.YEAR;
import static javax.time.calendrical.ISODateTimeRule.ZERO_EPOCH_MONTH;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.time.AmPmOfDay;
import javax.time.CalendricalException;
import javax.time.Chronology;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MonthDay;
import javax.time.MonthOfYear;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.QuarterOfYear;
import javax.time.Year;
import javax.time.YearMonth;
import javax.time.ZoneId;
import javax.time.ZoneOffset;
import javax.time.ZonedDateTime;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.ISOChronology;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test merging of ISO fields.
 */
@Test
public class TestCalendricalNomalizer {

    private static final ZoneOffset OFFSET = ZoneOffset.of("+03:00");
    private static final OffsetDateTime OFFSET_DATE_TIME_2011_06_30_11_30 = OffsetDateTime.of(2011, 6, 30, 11, 30, OFFSET);
    private static final OffsetDate OFFSET_DATE_2011_06_30 = OffsetDate.of(2011, 6, 30, OFFSET);
    private static final OffsetTime OFFSET_TIME_11_30 = OffsetTime.of(11, 30, OFFSET);
    private static final LocalDateTime DATE_TIME_2011_06_30_11_30 = LocalDateTime.of(2011, 6, 30, 11, 30);
    private static final LocalDate DATE_2011_06_30 = LocalDate.of(2011, 6, 30);
    private static final Year YEAR_2011 = Year.of(2011);
    private static final YearMonth YEAR_MONTH_2011_06 = YearMonth.of(2011, 6);
    private static final MonthDay MONTH_DAY_06_30 = MonthDay.of(6, 30);
    private static final LocalTime TIME_11_30 = LocalTime.of(11, 30);
    private static final DateTimeField FIELD_YEAR_2011 = YEAR.field(2011);
    private static final DateTimeField FIELD_MOY_06 = MONTH_OF_YEAR.field(6);
    private static final DateTimeField FIELD_DOM_30 = DAY_OF_MONTH.field(30);
    private static final DateTimeField FIELD_QOY_2 = QUARTER_OF_YEAR.field(2);
    private static final DateTimeField FIELD_MOQ_3 = MONTH_OF_QUARTER.field(3);
    private static final DateTimeField FIELD_DOW_4 = DAY_OF_WEEK.field(4);
    private static final DateTimeField FIELD_DOY_181 = DAY_OF_YEAR.field(181);
    private static final DateTimeField FIELD_ZEM_2011_06 = ZERO_EPOCH_MONTH.field(2011 * 12 + 6 - 1);
    private static final DateTimeField FIELD_AWOM_5 = ALIGNED_WEEK_OF_MONTH.field(5);
    private static final DateTimeField FIELD_AP_0 = AM_PM_OF_DAY.field(0);
    private static final DateTimeField FIELD_HAP_11 = HOUR_OF_AMPM.field(11);
    private static final DateTimeField FIELD_HOD_11 = HOUR_OF_DAY.field(11);
    private static final DateTimeField FIELD_MOD_690 = MINUTE_OF_DAY.field(690);
    private static final DateTimeField FIELD_MOH_30 = MINUTE_OF_HOUR.field(30);
    private static final DateTimeField FIELD_SOM_0 = SECOND_OF_MINUTE.field(0);

    //-----------------------------------------------------------------------
    @DataProvider(name = "merge")
    public Object[][] data_merge() {
        return new Object[][] {
            // from nothing
            {cals(), LocalDate.rule(), null},
            {cals(), LocalTime.rule(), null},
            {cals(), LocalDateTime.rule(), null},
            {cals(), OffsetDate.rule(), null},
            {cals(), OffsetTime.rule(), null},
            {cals(), OffsetDateTime.rule(), null},
            {cals(), ZonedDateTime.rule(), null},
            {cals(), ZoneOffset.rule(), null},
            {cals(), ZoneId.rule(), null},
            {cals(), Chronology.rule(), null},
            
            // from OffsetDateTime
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), LocalTime.rule(), TIME_11_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), LocalDateTime.rule(), DATE_TIME_2011_06_30_11_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), OffsetDate.rule(), OFFSET_DATE_2011_06_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), OffsetTime.rule(), OFFSET_TIME_11_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), OffsetDateTime.rule(), OFFSET_DATE_TIME_2011_06_30_11_30},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), ZonedDateTime.rule(), ZonedDateTime.of(OFFSET_DATE_TIME_2011_06_30_11_30, ZoneId.of(OFFSET))},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), ZoneOffset.rule(), OFFSET},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), ZoneId.rule(), null},
            {cals(OFFSET_DATE_TIME_2011_06_30_11_30), Chronology.rule(), ISOChronology.INSTANCE},
            
            // from LocalDateTime
            {cals(DATE_TIME_2011_06_30_11_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(DATE_TIME_2011_06_30_11_30), LocalTime.rule(), TIME_11_30},
            {cals(DATE_TIME_2011_06_30_11_30), LocalDateTime.rule(), DATE_TIME_2011_06_30_11_30},
            {cals(DATE_TIME_2011_06_30_11_30), OffsetDate.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), OffsetTime.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), OffsetDateTime.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), ZonedDateTime.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), ZoneOffset.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), ZoneId.rule(), null},
            {cals(DATE_TIME_2011_06_30_11_30), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(DATE_TIME_2011_06_30_11_30), Year.rule(), YEAR_2011},
            {cals(DATE_TIME_2011_06_30_11_30), YearMonth.rule(), YEAR_MONTH_2011_06},
            {cals(DATE_TIME_2011_06_30_11_30), MonthDay.rule(), MONTH_DAY_06_30},
            {cals(DATE_TIME_2011_06_30_11_30), QuarterOfYear.rule(), Q2},
            {cals(DATE_TIME_2011_06_30_11_30), MonthOfYear.rule(), JUNE},
            {cals(DATE_TIME_2011_06_30_11_30), DayOfWeek.rule(), THURSDAY},
            {cals(DATE_TIME_2011_06_30_11_30), YEAR, FIELD_YEAR_2011},
            {cals(DATE_TIME_2011_06_30_11_30), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(DATE_TIME_2011_06_30_11_30), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
            {cals(DATE_TIME_2011_06_30_11_30), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(DATE_TIME_2011_06_30_11_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_YEAR, FIELD_DOY_181},
            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_MONTH, FIELD_DOM_30},
            {cals(DATE_TIME_2011_06_30_11_30), DAY_OF_WEEK, FIELD_DOW_4},
            {cals(DATE_TIME_2011_06_30_11_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
            {cals(DATE_TIME_2011_06_30_11_30), AmPmOfDay.rule(), AmPmOfDay.AM},
            {cals(DATE_TIME_2011_06_30_11_30), AM_PM_OF_DAY, FIELD_AP_0},
            {cals(DATE_TIME_2011_06_30_11_30), HOUR_OF_AMPM, FIELD_HAP_11},
            {cals(DATE_TIME_2011_06_30_11_30), HOUR_OF_DAY, FIELD_HOD_11},
            {cals(DATE_TIME_2011_06_30_11_30), MINUTE_OF_DAY, FIELD_MOD_690},
            {cals(DATE_TIME_2011_06_30_11_30), MINUTE_OF_HOUR, FIELD_MOH_30},
            {cals(DATE_TIME_2011_06_30_11_30), SECOND_OF_MINUTE, FIELD_SOM_0},
            
            // from LocalDate
            {cals(DATE_2011_06_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(DATE_2011_06_30), LocalTime.rule(), null},
            {cals(DATE_2011_06_30), LocalDateTime.rule(), null},
            {cals(DATE_2011_06_30), OffsetDate.rule(), null},
            {cals(DATE_2011_06_30), OffsetTime.rule(), null},
            {cals(DATE_2011_06_30), OffsetDateTime.rule(), null},
            {cals(DATE_2011_06_30), ZonedDateTime.rule(), null},
            {cals(DATE_2011_06_30), ZoneOffset.rule(), null},
            {cals(DATE_2011_06_30), ZoneId.rule(), null},
            {cals(DATE_2011_06_30), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(DATE_2011_06_30), Year.rule(), YEAR_2011},
            {cals(DATE_2011_06_30), YearMonth.rule(), YEAR_MONTH_2011_06},
            {cals(DATE_2011_06_30), MonthDay.rule(), MONTH_DAY_06_30},
            {cals(DATE_2011_06_30), QuarterOfYear.rule(), Q2},
            {cals(DATE_2011_06_30), MonthOfYear.rule(), JUNE},
            {cals(DATE_2011_06_30), DayOfWeek.rule(), THURSDAY},
            {cals(DATE_2011_06_30), YEAR, FIELD_YEAR_2011},
            {cals(DATE_2011_06_30), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(DATE_2011_06_30), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
            {cals(DATE_2011_06_30), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(DATE_2011_06_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
            {cals(DATE_2011_06_30), DAY_OF_YEAR, FIELD_DOY_181},
            {cals(DATE_2011_06_30), DAY_OF_MONTH, FIELD_DOM_30},
            {cals(DATE_2011_06_30), DAY_OF_WEEK, FIELD_DOW_4},
            {cals(DATE_2011_06_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
            {cals(DATE_2011_06_30), AmPmOfDay.rule(), null},
            {cals(DATE_2011_06_30), AM_PM_OF_DAY, null},
            {cals(DATE_2011_06_30), HOUR_OF_AMPM, null},
            {cals(DATE_2011_06_30), HOUR_OF_DAY, null},
            {cals(DATE_2011_06_30), MINUTE_OF_DAY, null},
            {cals(DATE_2011_06_30), MINUTE_OF_HOUR, null},
            {cals(DATE_2011_06_30), SECOND_OF_MINUTE, null},
            
            // from Year
            {cals(YEAR_2011), LocalDate.rule(), null},
            {cals(YEAR_2011), LocalTime.rule(), null},
            {cals(YEAR_2011), LocalDateTime.rule(), null},
            {cals(YEAR_2011), OffsetDate.rule(), null},
            {cals(YEAR_2011), OffsetTime.rule(), null},
            {cals(YEAR_2011), OffsetDateTime.rule(), null},
            {cals(YEAR_2011), ZonedDateTime.rule(), null},
            {cals(YEAR_2011), ZoneOffset.rule(), null},
            {cals(YEAR_2011), ZoneId.rule(), null},
            {cals(YEAR_2011), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(YEAR_2011), Year.rule(), YEAR_2011},
            {cals(YEAR_2011), YearMonth.rule(), null},
            {cals(YEAR_2011), MonthDay.rule(), null},
            {cals(YEAR_2011), QuarterOfYear.rule(), null},
            {cals(YEAR_2011), MonthOfYear.rule(), null},
            {cals(YEAR_2011), DayOfWeek.rule(), null},
            {cals(YEAR_2011), YEAR, FIELD_YEAR_2011},
            {cals(YEAR_2011), QUARTER_OF_YEAR, null},
            {cals(YEAR_2011), ZERO_EPOCH_MONTH, null},
            {cals(YEAR_2011), MONTH_OF_YEAR, null},
            {cals(YEAR_2011), MONTH_OF_QUARTER, null},
            {cals(YEAR_2011), DAY_OF_YEAR, null},
            {cals(YEAR_2011), DAY_OF_MONTH, null},
            {cals(YEAR_2011), DAY_OF_WEEK, null},
            {cals(YEAR_2011), ALIGNED_WEEK_OF_MONTH, null},
            
            // from YearMonth
            {cals(YEAR_MONTH_2011_06), LocalDate.rule(), null},
            {cals(YEAR_MONTH_2011_06), LocalTime.rule(), null},
            {cals(YEAR_MONTH_2011_06), LocalDateTime.rule(), null},
            {cals(YEAR_MONTH_2011_06), OffsetDate.rule(), null},
            {cals(YEAR_MONTH_2011_06), OffsetTime.rule(), null},
            {cals(YEAR_MONTH_2011_06), OffsetDateTime.rule(), null},
            {cals(YEAR_MONTH_2011_06), ZonedDateTime.rule(), null},
            {cals(YEAR_MONTH_2011_06), ZoneOffset.rule(), null},
            {cals(YEAR_MONTH_2011_06), ZoneId.rule(), null},
            {cals(YEAR_MONTH_2011_06), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(YEAR_MONTH_2011_06), Year.rule(), YEAR_2011},
            {cals(YEAR_MONTH_2011_06), YearMonth.rule(), YEAR_MONTH_2011_06},
            {cals(YEAR_MONTH_2011_06), MonthDay.rule(), null},
            {cals(YEAR_MONTH_2011_06), QuarterOfYear.rule(), Q2},
            {cals(YEAR_MONTH_2011_06), MonthOfYear.rule(), JUNE},
            {cals(YEAR_MONTH_2011_06), DayOfWeek.rule(), null},
            {cals(YEAR_MONTH_2011_06), YEAR, FIELD_YEAR_2011},
            {cals(YEAR_MONTH_2011_06), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(YEAR_MONTH_2011_06), ZERO_EPOCH_MONTH, FIELD_ZEM_2011_06},
            {cals(YEAR_MONTH_2011_06), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(YEAR_MONTH_2011_06), MONTH_OF_QUARTER, FIELD_MOQ_3},
            {cals(YEAR_MONTH_2011_06), DAY_OF_YEAR, null},
            {cals(YEAR_MONTH_2011_06), DAY_OF_MONTH, null},
            {cals(YEAR_MONTH_2011_06), DAY_OF_WEEK, null},
            {cals(YEAR_MONTH_2011_06), ALIGNED_WEEK_OF_MONTH, null},
            
            // from MonthDay
            {cals(MONTH_DAY_06_30), LocalDate.rule(), null},
            {cals(MONTH_DAY_06_30), LocalTime.rule(), null},
            {cals(MONTH_DAY_06_30), LocalDateTime.rule(), null},
            {cals(MONTH_DAY_06_30), OffsetDate.rule(), null},
            {cals(MONTH_DAY_06_30), OffsetTime.rule(), null},
            {cals(MONTH_DAY_06_30), OffsetDateTime.rule(), null},
            {cals(MONTH_DAY_06_30), ZonedDateTime.rule(), null},
            {cals(MONTH_DAY_06_30), ZoneOffset.rule(), null},
            {cals(MONTH_DAY_06_30), ZoneId.rule(), null},
            {cals(MONTH_DAY_06_30), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(MONTH_DAY_06_30), Year.rule(), null},
            {cals(MONTH_DAY_06_30), YearMonth.rule(), null},
            {cals(MONTH_DAY_06_30), MonthDay.rule(), MONTH_DAY_06_30},
            {cals(MONTH_DAY_06_30), QuarterOfYear.rule(), Q2},
            {cals(MONTH_DAY_06_30), MonthOfYear.rule(), JUNE},
            {cals(MONTH_DAY_06_30), DayOfWeek.rule(), null},
            {cals(MONTH_DAY_06_30), YEAR, null},
            {cals(MONTH_DAY_06_30), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(MONTH_DAY_06_30), ZERO_EPOCH_MONTH, null},
            {cals(MONTH_DAY_06_30), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(MONTH_DAY_06_30), MONTH_OF_QUARTER, FIELD_MOQ_3},
            {cals(MONTH_DAY_06_30), DAY_OF_YEAR, null},
            {cals(MONTH_DAY_06_30), DAY_OF_MONTH, FIELD_DOM_30},
            {cals(MONTH_DAY_06_30), DAY_OF_WEEK, null},
            {cals(MONTH_DAY_06_30), ALIGNED_WEEK_OF_MONTH, FIELD_AWOM_5},
            
            // from QuarterOfYear
            {cals(Q2), LocalDate.rule(), null},
            {cals(Q2), LocalTime.rule(), null},
            {cals(Q2), LocalDateTime.rule(), null},
            {cals(Q2), OffsetDate.rule(), null},
            {cals(Q2), OffsetTime.rule(), null},
            {cals(Q2), OffsetDateTime.rule(), null},
            {cals(Q2), ZonedDateTime.rule(), null},
            {cals(Q2), ZoneOffset.rule(), null},
            {cals(Q2), ZoneId.rule(), null},
            {cals(Q2), Chronology.rule(), null},
            {cals(Q2), Year.rule(), null},
            {cals(Q2), YearMonth.rule(), null},
            {cals(Q2), MonthDay.rule(), null},
            {cals(Q2), QuarterOfYear.rule(), Q2},
            {cals(Q2), MonthOfYear.rule(), null},
            {cals(Q2), DayOfWeek.rule(), null},
            {cals(Q2), YEAR, null},
            {cals(Q2), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(Q2), ZERO_EPOCH_MONTH, null},
            {cals(Q2), MONTH_OF_YEAR, null},
            {cals(Q2), MONTH_OF_QUARTER, null},
            {cals(Q2), DAY_OF_YEAR, null},
            {cals(Q2), DAY_OF_MONTH, null},
            {cals(Q2), DAY_OF_WEEK, null},
            {cals(Q2), ALIGNED_WEEK_OF_MONTH, null},
            
            // from MonthOfYear
            {cals(JUNE), LocalDate.rule(), null},
            {cals(JUNE), LocalTime.rule(), null},
            {cals(JUNE), LocalDateTime.rule(), null},
            {cals(JUNE), OffsetDate.rule(), null},
            {cals(JUNE), OffsetTime.rule(), null},
            {cals(JUNE), OffsetDateTime.rule(), null},
            {cals(JUNE), ZonedDateTime.rule(), null},
            {cals(JUNE), ZoneOffset.rule(), null},
            {cals(JUNE), ZoneId.rule(), null},
            {cals(JUNE), Chronology.rule(), null},
            {cals(JUNE), Year.rule(), null},
            {cals(JUNE), YearMonth.rule(), null},
            {cals(JUNE), MonthDay.rule(), null},
            {cals(JUNE), QuarterOfYear.rule(), Q2},
            {cals(JUNE), MonthOfYear.rule(), JUNE},
            {cals(JUNE), DayOfWeek.rule(), null},
            {cals(JUNE), YEAR, null},
            {cals(JUNE), QUARTER_OF_YEAR, FIELD_QOY_2},
            {cals(JUNE), ZERO_EPOCH_MONTH, null},
            {cals(JUNE), MONTH_OF_YEAR, FIELD_MOY_06},
            {cals(JUNE), MONTH_OF_QUARTER, FIELD_MOQ_3},
            {cals(JUNE), DAY_OF_YEAR, null},
            {cals(JUNE), DAY_OF_MONTH, null},
            {cals(JUNE), DAY_OF_WEEK, null},
            {cals(JUNE), ALIGNED_WEEK_OF_MONTH, null},
            
            // from DayOfWeek
            {cals(THURSDAY), LocalDate.rule(), null},
            {cals(THURSDAY), LocalTime.rule(), null},
            {cals(THURSDAY), LocalDateTime.rule(), null},
            {cals(THURSDAY), OffsetDate.rule(), null},
            {cals(THURSDAY), OffsetTime.rule(), null},
            {cals(THURSDAY), OffsetDateTime.rule(), null},
            {cals(THURSDAY), ZonedDateTime.rule(), null},
            {cals(THURSDAY), ZoneOffset.rule(), null},
            {cals(THURSDAY), ZoneId.rule(), null},
            {cals(THURSDAY), Chronology.rule(), null},
            {cals(THURSDAY), Year.rule(), null},
            {cals(THURSDAY), YearMonth.rule(), null},
            {cals(THURSDAY), MonthDay.rule(), null},
            {cals(THURSDAY), QuarterOfYear.rule(), null},
            {cals(THURSDAY), MonthOfYear.rule(), null},
            {cals(THURSDAY), DayOfWeek.rule(), THURSDAY},
            {cals(THURSDAY), YEAR, null},
            {cals(THURSDAY), QUARTER_OF_YEAR, null},
            {cals(THURSDAY), ZERO_EPOCH_MONTH, null},
            {cals(THURSDAY), MONTH_OF_YEAR, null},
            {cals(THURSDAY), MONTH_OF_QUARTER, null},
            {cals(THURSDAY), DAY_OF_YEAR, null},
            {cals(THURSDAY), DAY_OF_MONTH, null},
            {cals(THURSDAY), DAY_OF_WEEK, DAY_OF_WEEK.field(4)},
            {cals(THURSDAY), ALIGNED_WEEK_OF_MONTH, null},
            
            // from LocalTime
            {cals(TIME_11_30), LocalDate.rule(), null},
            {cals(TIME_11_30), LocalTime.rule(), TIME_11_30},
            {cals(TIME_11_30), LocalDateTime.rule(), null},
            {cals(TIME_11_30), OffsetDate.rule(), null},
            {cals(TIME_11_30), OffsetTime.rule(), null},
            {cals(TIME_11_30), OffsetDateTime.rule(), null},
            {cals(TIME_11_30), ZonedDateTime.rule(), null},
            {cals(TIME_11_30), ZoneOffset.rule(), null},
            {cals(TIME_11_30), ZoneId.rule(), null},
            {cals(TIME_11_30), Chronology.rule(), ISOChronology.INSTANCE},
            {cals(TIME_11_30), Year.rule(), null},
            {cals(TIME_11_30), YearMonth.rule(), null},
            {cals(TIME_11_30), MonthDay.rule(), null},
            {cals(TIME_11_30), QuarterOfYear.rule(), null},
            {cals(TIME_11_30), MonthOfYear.rule(), null},
            {cals(TIME_11_30), DayOfWeek.rule(), null},
            {cals(TIME_11_30), YEAR, null},
            {cals(TIME_11_30), QUARTER_OF_YEAR, null},
            {cals(TIME_11_30), ZERO_EPOCH_MONTH, null},
            {cals(TIME_11_30), MONTH_OF_YEAR, null},
            {cals(TIME_11_30), MONTH_OF_QUARTER, null},
            {cals(TIME_11_30), DAY_OF_YEAR, null},
            {cals(TIME_11_30), DAY_OF_MONTH, null},
            {cals(TIME_11_30), DAY_OF_WEEK, null},
            {cals(TIME_11_30), ALIGNED_WEEK_OF_MONTH, null},
            {cals(TIME_11_30), AmPmOfDay.rule(), AmPmOfDay.AM},
            {cals(TIME_11_30), AM_PM_OF_DAY, FIELD_AP_0},
            {cals(TIME_11_30), HOUR_OF_AMPM, FIELD_HAP_11},
            {cals(TIME_11_30), HOUR_OF_DAY, FIELD_HOD_11},
            {cals(TIME_11_30), MINUTE_OF_DAY, FIELD_MOD_690},
            {cals(TIME_11_30), MINUTE_OF_HOUR, FIELD_MOH_30},
            {cals(TIME_11_30), SECOND_OF_MINUTE, FIELD_SOM_0},
            
            // create date
            {cals(TIME_11_30), LocalDate.rule(), null},
            {cals(DATE_2011_06_30, TIME_11_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_ZEM_2011_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06, FIELD_DOY_181), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, JUNE, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06, FIELD_QOY_2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, JUNE, FIELD_QOY_2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, JUNE, Q2, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, Q2, FIELD_MOQ_3, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(YEAR_MONTH_2011_06, FIELD_DOM_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(YEAR_2011, MONTH_DAY_06_30), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, JUNE, FIELD_AWOM_5, THURSDAY), LocalDate.rule(), DATE_2011_06_30},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06), LocalDate.rule(), null},
            {cals(FIELD_YEAR_2011, FIELD_MOY_06, QUARTER_OF_YEAR.field(1), FIELD_DOM_30), LocalDate.rule(), CalendricalException.class},
            {cals(DATE_2011_06_30, LocalDate.of(2011, 6, 22)), LocalDate.rule(), CalendricalException.class},
            {cals(DATE_2011_06_30, FEBRUARY), LocalDate.rule(), CalendricalException.class},
            
            // create Year
            {cals(FIELD_YEAR_2011), Year.rule(), YEAR_2011},
            {cals(OCTOBER), Year.rule(), null},
            
            // create YearMonth
            {cals(YEAR_2011, OCTOBER), YearMonth.rule(), YearMonth.of(2011, 10)},
            {cals(OCTOBER), YearMonth.rule(), null},
            
            // create MonthDay
            {cals(LocalDateTime.of(2011, 6, 30, 12, 30)), MonthDay.rule(), MONTH_DAY_06_30},
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthDay.rule(), MonthDay.of(10, 23)},
            {cals(OCTOBER), MonthDay.rule(), null},
            
            // create MonthOfYear
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MonthOfYear.rule(), OCTOBER},
            
            // create Field:MonthOfYear
            {cals(DAY_OF_MONTH.field(23), OCTOBER), MONTH_OF_YEAR, MONTH_OF_YEAR.field(10)},
            
            // create LocalTime
            {cals(DATE_TIME_2011_06_30_11_30), LocalTime.rule(), TIME_11_30},
            {cals(DATE_2011_06_30, TIME_11_30), LocalTime.rule(), TIME_11_30},
            
            // create LocalDateTime
            {cals(DATE_2011_06_30, TIME_11_30), LocalDateTime.rule(), DATE_TIME_2011_06_30_11_30},
        };
    }

    @Test(dataProvider = "merge")
    public void test_merge(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        Calendrical[] array = calendicals.toArray(new Calendrical[calendicals.size()]);
        if (expectedVal instanceof Class) {
            try {
                CalendricalEngine.merge(array);
                fail();
            } catch (CalendricalException ex) {
                System.out.println(ex);
            }
        } else {
            CalendricalEngine m = CalendricalEngine.merge(array);
            Object derived = m.derive(ruleToDerive);
//            if (expectedVal != null && derived == null) {
//                try {
//                    m.deriveChecked(ruleToDerive);
//                } catch (CalendricalException ex) {
//                    System.out.println(ex);
//                }
//            }
            assertEquals(derived, expectedVal);
        }
    }

    @Test(dataProvider = "merge")
    public void test_mergeError(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        if (expectedVal == null) {
            Calendrical[] array = calendicals.toArray(new Calendrical[calendicals.size()]);
            CalendricalEngine m = CalendricalEngine.merge(array);
            try {
                Object d = m.deriveChecked(ruleToDerive);
                fail("Failed to throw error: " + calendicals + " -> " + ruleToDerive + " = " + d);
            } catch (CalendricalException ex) {
                System.out.println(ex);
            }
        }
    }

    private List<Calendrical> cals(Calendrical... cals) {
        return Arrays.asList(cals);
    }

    //-----------------------------------------------------------------------
    @Test(dataProvider = "merge")
    public void test_derive(List<Calendrical> calendicals, CalendricalRule<?> ruleToDerive, Object expectedVal) {
        if (calendicals.size() == 1 && expectedVal instanceof Class == false) {
            Calendrical calendrical = calendicals.get(0);
            if (calendrical instanceof LocalDate) {
                Object test = CalendricalEngine.derive(ruleToDerive, null, (LocalDate) calendrical, null, null, null, ISOChronology.INSTANCE, null);
                assertEquals(test, expectedVal);
            } else if (calendrical instanceof LocalTime) {
                Object test = CalendricalEngine.derive(ruleToDerive, null, null, (LocalTime) calendrical, null, null, ISOChronology.INSTANCE, null);
                assertEquals(test, expectedVal);
            } else if (calendrical instanceof LocalDateTime) {
                LocalDateTime ldt = (LocalDateTime) calendrical;
                Object test = CalendricalEngine.derive(ruleToDerive, null, ldt.toLocalDate(), ldt.toLocalTime(), null, null, ISOChronology.INSTANCE, null);
                assertEquals(test, expectedVal);
            }
        }
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="normalized")
    Object[][] data_normalized() {
        return new Object[][] {
            {DateTimeField.of(YEAR, 2008), DateTimeField.of(YEAR, 2008)},
            {DateTimeField.of(MONTH_OF_YEAR, 6), DateTimeField.of(MONTH_OF_YEAR, 6)},
            {DateTimeField.of(HOUR_OF_DAY, 2), DateTimeField.of(HOUR_OF_DAY, 2)},
            {DateTimeField.of(HOUR_OF_AMPM, 6), DateTimeField.of(HOUR_OF_AMPM, 6)},
            {DateTimeField.of(DAY_OF_WEEK, 3), DateTimeField.of(DAY_OF_WEEK, 3)},
            
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, -1), DateTimeField.of(HOUR_OF_DAY, -1)},
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, 0), DateTimeField.of(HOUR_OF_DAY, 0)},
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, 1), DateTimeField.of(HOUR_OF_DAY, 1)},
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, 23), DateTimeField.of(HOUR_OF_DAY, 23)},
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, 24), DateTimeField.of(HOUR_OF_DAY, 0)},
            {DateTimeField.of(CLOCK_HOUR_OF_DAY, 25), DateTimeField.of(HOUR_OF_DAY, 25)},
            
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, -1), DateTimeField.of(HOUR_OF_AMPM, -1)},
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, 0), DateTimeField.of(HOUR_OF_AMPM, 0)},
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, 1), DateTimeField.of(HOUR_OF_AMPM, 1)},
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, 11), DateTimeField.of(HOUR_OF_AMPM, 11)},
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, 12), DateTimeField.of(HOUR_OF_AMPM, 0)},
            {DateTimeField.of(CLOCK_HOUR_OF_AMPM, 13), DateTimeField.of(HOUR_OF_AMPM, 13)},
            
            {DateTimeField.of(MockReversedHourOfDayFieldRule.INSTANCE, 0), DateTimeField.of(HOUR_OF_DAY, 24)},
            {DateTimeField.of(MockReversedHourOfDayFieldRule.INSTANCE, 1), DateTimeField.of(HOUR_OF_DAY, 23)},
            {DateTimeField.of(MockReversedHourOfDayFieldRule.INSTANCE, 24), DateTimeField.of(HOUR_OF_DAY, 0)},
            {DateTimeField.of(MockReversedHourOfDayFieldRule.INSTANCE, 25), DateTimeField.of(HOUR_OF_DAY, -1)},
            
            {DateTimeField.of(MockBigClockHourOfDayFieldRule.INSTANCE, 300), DateTimeField.of(HOUR_OF_DAY, 3)},
        };
    }

    @Test(dataProvider = "normalized")
    public void test_normalized(DateTimeField input, DateTimeField output) {
        DateTimeField test = CalendricalEngine.derive(output.getRule(), input.getRule(), null, input);
        assertEquals(test, output);
    }

}
