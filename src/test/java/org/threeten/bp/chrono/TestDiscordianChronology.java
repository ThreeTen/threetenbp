package org.threeten.bp.chrono;

import org.testng.*;
import org.testng.annotations.*;
import org.threeten.bp.*;
import org.threeten.bp.temporal.*;

import static org.testng.Assert.*;
import static org.threeten.bp.chrono.DiscordianChronology.DAYS_PER_SEASON;
import static org.threeten.bp.chrono.DiscordianChronology.DAYS_PER_WEEK;
import static org.threeten.bp.chrono.DiscordianChronology.SEASONS_PER_YEAR;
import static org.threeten.bp.chrono.DiscordianDate.ST_TIBS_DAY;

@Test
public class TestDiscordianChronology {

    //-----------------------------------------------------------------------
    // Chrono.ofName("Discordian")  Lookup by name
    //-----------------------------------------------------------------------
    @Test
    public void test_chrono_byName() {
        Chronology c = DiscordianChronology.INSTANCE;
        Chronology test = Chronology.of("Discordian");
        Assert.assertNotNull(test, "The Discordian calendar could not be found byName");
        Assert.assertEquals(test.getId(), "Discordian", "ID mismatch");
        Assert.assertEquals(test.getCalendarType(), null, "Type mismatch");
        Assert.assertEquals(test, c);
    }

    //-----------------------------------------------------------------------
    // creation, toLocalDate()
    //-----------------------------------------------------------------------
    @DataProvider(name = "samples")
    Object[][] data_samples() {
        return new Object[][]{
                // YOLD era starts in 1166 BC
                {DiscordianChronology.INSTANCE.date(0, 1, 1), LocalDate.of(-1166, 1, 1)},
                // sanity check current date
                {DiscordianChronology.INSTANCE.date(3179, 2, 19), LocalDate.of(2013, 4, 2)},
                // leap day occurs on day 60 of the year but is *not* considered part of the 1st season
                {DiscordianChronology.INSTANCE.date(3179, 1, 59), LocalDate.of(2013, 2, 28)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), LocalDate.of(2013, 3, 1)}, // non leap year
                {DiscordianChronology.INSTANCE.date(3178, 1, 60), LocalDate.of(2012, 3, 1)}, // leap year
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY), LocalDate.of(2012, 2, 29)},
                {DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1), LocalDate.of(2012, 3, 1)},
                // the season boundaries occur on the same Gregorian day every year...
                {DiscordianChronology.INSTANCE.date(3179, 1, 1), LocalDate.of(2013, 1, 1)},
                {DiscordianChronology.INSTANCE.date(3179, 1, 73), LocalDate.of(2013, 3, 14)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 1), LocalDate.of(2013, 3, 15)},
                {DiscordianChronology.INSTANCE.date(3179, 2, 73), LocalDate.of(2013, 5, 26)},
                {DiscordianChronology.INSTANCE.date(3179, 3, 1), LocalDate.of(2013, 5, 27)},
                {DiscordianChronology.INSTANCE.date(3179, 3, 73), LocalDate.of(2013, 8, 7)},
                {DiscordianChronology.INSTANCE.date(3179, 4, 1), LocalDate.of(2013, 8, 8)},
                {DiscordianChronology.INSTANCE.date(3179, 4, 73), LocalDate.of(2013, 10, 19)},
                {DiscordianChronology.INSTANCE.date(3179, 5, 1), LocalDate.of(2013, 10, 20)},
                {DiscordianChronology.INSTANCE.date(3179, 5, 73), LocalDate.of(2013, 12, 31)},
                // ... even if it is a leap year
                {DiscordianChronology.INSTANCE.date(3178, 1, 1), LocalDate.of(2012, 1, 1)},
                {DiscordianChronology.INSTANCE.date(3178, 1, 73), LocalDate.of(2012, 3, 14)},
                {DiscordianChronology.INSTANCE.date(3178, 2, 1), LocalDate.of(2012, 3, 15)},
                {DiscordianChronology.INSTANCE.date(3178, 2, 73), LocalDate.of(2012, 5, 26)},
                {DiscordianChronology.INSTANCE.date(3178, 3, 1), LocalDate.of(2012, 5, 27)},
                {DiscordianChronology.INSTANCE.date(3178, 3, 73), LocalDate.of(2012, 8, 7)},
                {DiscordianChronology.INSTANCE.date(3178, 4, 1), LocalDate.of(2012, 8, 8)},
                {DiscordianChronology.INSTANCE.date(3178, 4, 73), LocalDate.of(2012, 10, 19)},
                {DiscordianChronology.INSTANCE.date(3178, 5, 1), LocalDate.of(2012, 10, 20)},
                {DiscordianChronology.INSTANCE.date(3178, 5, 73), LocalDate.of(2012, 12, 31)},
        };
    }

    @Test(dataProvider = "samples")
    public void test_toLocalDate(ChronoLocalDate<?> ddate, LocalDate iso) {
        assertEquals(LocalDate.from(ddate), iso);
    }

    @Test(dataProvider = "samples")
    public void test_fromCalendrical(ChronoLocalDate<?> ddate, LocalDate iso) {
        assertEquals(DiscordianChronology.INSTANCE.date(iso), ddate);
    }

    @DataProvider(name = "badDates")
    Object[][] data_badDates() {
        return new Object[][]{
                // out of range season
                {3179, 0, 0},
                {3179, -1, 1},
                {3179, 6, 1},

                // out of range day
                {3179, 1, 0},
                {3179, 1, 74},
        };
    }

    @Test(dataProvider = "badDates", expectedExceptions = DateTimeException.class)
    public void test_badDates(int year, int season, int dayOfSeason) {
        DiscordianChronology.INSTANCE.date(year, season, dayOfSeason);
    }

    //-----------------------------------------------------------------------
    // with(WithAdjuster)
    //-----------------------------------------------------------------------
    @Test
    public void test_withLastDayOfMonth() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3179, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3179, 1, 73));
    }

    @Test
    public void test_withLastDayOfMonthInLeapYear() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3178, 1, ST_TIBS_DAY - 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 73));
    }

    @Test
    public void test_withLastDayOfMonthFromLeapDay() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastDayOfMonth());
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 73));
    }

    @Test
    void test_withDayOfWeek() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3179, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(1)));
        assertEquals(test, DiscordianChronology.INSTANCE.date(3179, 1, 71));
    }

    @Test
    void test_withDayOfWeekInLeapYear() {
        ChronoLocalDate<?> base = DiscordianChronology.INSTANCE.date(3178, 1, 1);
        ChronoLocalDate<?> test = base.with(TemporalAdjusters.lastInMonth(DayOfWeek.of(1)));
        assertEquals(test, DiscordianChronology.INSTANCE.date(3178, 1, 71));
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name = "toString")
    Object[][] data_toString() {
        return new Object[][]{
                {DiscordianChronology.INSTANCE.date(0, 1, 1), "Sweetmorn, Chaos 1, 0 YOLD"},
                {DiscordianChronology.INSTANCE.date(3179, 2, 19), "Boomtime, Discord 19, 3179 YOLD"},
                // 60th day of the year is St. Tib's Day if it is a leap year
                {DiscordianChronology.INSTANCE.dateYearDay(3178, 60), "St. Tib's Day! 3178 YOLD"},
                {DiscordianChronology.INSTANCE.dateYearDay(3179, 60), "Setting Orange, Chaos 60, 3179 YOLD"},
                // St. Tib's Day is not part of the week, so day names after it should be the same in a leap year
                {DiscordianChronology.INSTANCE.date(3179, 1, 60), "Setting Orange, Chaos 60, 3179 YOLD"},
                {DiscordianChronology.INSTANCE.date(3178, 1, 60), "Setting Orange, Chaos 60, 3178 YOLD"},
        };
    }

    @Test(dataProvider = "toString")
    public void test_toString(ChronoLocalDate<?> jdate, String expected) {
        assertEquals(jdate.toString(), expected);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_true() {
        assertTrue(DiscordianChronology.INSTANCE.equals(DiscordianChronology.INSTANCE));
    }

    @Test
    public void test_equals_false() {
        assertFalse(DiscordianChronology.INSTANCE.equals(IsoChronology.INSTANCE));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------

    @Test
    public void test_compareTo() {
        final ChronoLocalDate<?> ddate1 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY);
        final ChronoLocalDate<?> ddate2 = DiscordianChronology.INSTANCE.dateYearDay(3178, ST_TIBS_DAY + 1);
        assertTrue(ddate1.compareTo(ddate2) < 0);
        assertTrue(ddate2.compareTo(ddate1) > 0);
    }

    //-----------------------------------------------------------------------
    // isLeapYear(), field ranges in leap and non-leap years
    //-----------------------------------------------------------------------

    @DataProvider(name = "leapYears")
    Object[][] data_leapYears() {
        return new Object[][]{
                {DiscordianChronology.INSTANCE.date(3179, 1, 1), false},
                {DiscordianChronology.INSTANCE.date(3178, 1, 1), true},
                {DiscordianChronology.INSTANCE.date(3066, 1, 1), false},
                {DiscordianChronology.INSTANCE.date(3166, 1, 1), true},
        };
    }

    @Test(dataProvider = "leapYears")
    public void test_isLeapYear(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.isLeapYear(), isLeapYear);
        assertEquals(DiscordianChronology.INSTANCE.isLeapYear(ddate.getYear()), isLeapYear);
    }

    @Test(dataProvider = "leapYears")
    public void test_dayOfWeekRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.DAY_OF_WEEK).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.DAY_OF_WEEK).getMaximum(), DAYS_PER_WEEK);
    }

    @Test(dataProvider = "leapYears")
    public void test_dayOfMonthRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.DAY_OF_MONTH).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.DAY_OF_MONTH).getMaximum(), DAYS_PER_SEASON);
    }

    @Test(dataProvider = "leapYears")
    public void test_monthOfYearRange(DiscordianDate ddate, boolean isLeapYear) {
        assertEquals(ddate.range(ChronoField.MONTH_OF_YEAR).getMinimum(), isLeapYear ? 0 : 1);
        assertEquals(ddate.range(ChronoField.MONTH_OF_YEAR).getMaximum(), SEASONS_PER_YEAR);
    }

    @Test
    public void test_dayOfWeekRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.DAY_OF_WEEK);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), DAYS_PER_WEEK);
        assertEquals(range.getMaximum(), DAYS_PER_WEEK);
    }

    @Test
    public void test_dayOfMonthRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.DAY_OF_MONTH);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), DAYS_PER_SEASON);
        assertEquals(range.getMaximum(), DAYS_PER_SEASON);
    }

    @Test
    public void test_monthOfYearRangeOnChronology() {
        ValueRange range = DiscordianChronology.INSTANCE.range(ChronoField.MONTH_OF_YEAR);
        assertEquals(range.getMinimum(), 0);
        assertEquals(range.getLargestMinimum(), 1);
        assertEquals(range.getSmallestMaximum(), 5);
        assertEquals(range.getMaximum(), 5);
    }

}
