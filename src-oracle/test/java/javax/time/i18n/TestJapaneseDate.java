package javax.time.i18n;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.DateProvider;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.UnsupportedRuleException;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestJapaneseDate {
    
    private JapaneseEra testEra = JapaneseEra.HEISEI;
    private int testYear = 21;
    private int testGregorianYear = 2009;
    private MonthOfYear testMonthOfYear = MonthOfYear.MARCH;
    private int testDayOfMonth = 3;
    private int testDayOfYear = 62;
    private boolean testLeapYear = false;
    private JapaneseDate testDate;

    @BeforeTest
    public void setUp() throws Exception {
        testDate = JapaneseDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void test_interfaces() {
        Object obj = testDate;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof DateProvider);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testDate);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), testDate);
    }
    
    @Test
    public void test_immutable() {
        Class<JapaneseDate> cls = JapaneseDate.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                assertTrue(Modifier.isPrivate(field.getModifiers()));
                assertTrue(Modifier.isFinal(field.getModifiers()));
            }
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_factoy_of_3() throws Exception{
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertJapaneseDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void test_factoy_of_4() throws Exception{
        assertEquals(JapaneseDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertJapaneseDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void test_factoy_of_3_invalidYear() throws Exception{
        try {
            JapaneseDate.of(10000, testMonthOfYear, testDayOfMonth);// Invalid year.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factoy_of_3_invalidMonth() throws Exception{
        JapaneseDate.of(testYear, null, testDayOfMonth);
    }

    @Test
    public void test_factoy_of_3_invalidDay() throws Exception{
        try {
            JapaneseDate.of(testYear, testMonthOfYear, 40);// Invalid day of month.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.dayOfMonthRule());
        }
    }

    //-----------------------------------------------------------------------
    public void factory_of_Calendrical() throws Exception {
        assertEquals(JapaneseDate.of(testDate), testDate);
        assertJapaneseDate(JapaneseDate.of(testDate), JapaneseEra.HEISEI, 21, MonthOfYear.MARCH, 3);
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void factory_of_Calendrical_noData() throws Exception {
        JapaneseDate.of(new MockSimpleCalendrical());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_of_Calendrical_null() throws Exception {
        JapaneseDate.of((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void testGetChronology() {
        assertEquals(testDate.getChronology(), JapaneseChronology.INSTANCE);
    }
    
    @Test
    public void testGet() throws Exception {
        assertEquals(testDate.get(JapaneseChronology.eraRule()), testDate.getEra());
        assertEquals(testDate.get(JapaneseChronology.yearOfEraRule()), (Integer) testDate.getYearOfEra());
        assertEquals(testDate.get(JapaneseChronology.monthOfYearRule()), testDate.getMonthOfYear());
        assertEquals(testDate.get(JapaneseChronology.dayOfMonthRule()), (Integer) testDate.getDayOfMonth());
        assertEquals(testDate.get(JapaneseChronology.dayOfYearRule()), (Integer) testDate.getDayOfYear());
        assertEquals(testDate.get(JapaneseChronology.dayOfWeekRule()), testDate.getDayOfWeek());
    }
    
    public void testGetUnsupported() throws Exception {
        assertEquals(testDate.get(LocalTime.rule()), null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testGetNull() throws Exception {
        testDate.get(null);
    }

    @Test
    public void testGetEra() {
        assertEquals(testDate.getEra(), testEra);
    }

    @Test
    public void testGetYearOfEra() {
        assertEquals(testDate.getYearOfEra(), testYear);
    }

    @Test
    public void testGetMonthOfYear() {
        assertEquals(testDate.getMonthOfYear(), testMonthOfYear);
    }

    @Test
    public void testGetDayOfMonth() {
        assertEquals(testDate.getDayOfMonth(), testDayOfMonth);
    }

    @Test
    public void testGetDayOfYear() {
        assertEquals(testDate.getDayOfYear(), testDayOfYear);
    }

    //-----------------------------------------------------------------------
    // getDayOfWeek()
    //-----------------------------------------------------------------------
    @Test
    public void testGetDayOfWeek() {
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 2).getDayOfWeek(), DayOfWeek.MONDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 3).getDayOfWeek(), DayOfWeek.TUESDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 4).getDayOfWeek(), DayOfWeek.WEDNESDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 5).getDayOfWeek(), DayOfWeek.THURSDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 6).getDayOfWeek(), DayOfWeek.FRIDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 7).getDayOfWeek(), DayOfWeek.SATURDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 8).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, 9).getDayOfWeek(), DayOfWeek.MONDAY);
    }

    @Test
    public void testGetDayOfWeekCrossCheck() throws Exception {
        JapaneseDate date = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(date.getDayOfWeek(), date.toLocalDate().getDayOfWeek());
    }

    @Test
    public void testIsLeapYear() {
        assertEquals(testDate.isLeapYear(), testLeapYear);
    }

    //-----------------------------------------------------------------------
    // withYearOfEra()
    //-----------------------------------------------------------------------
    @Test
    public void testWithYearOfEra() {
        JapaneseDate date = testDate.withYearOfEra(2010);
        assertEquals(date, JapaneseDate.of(2010, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearOfEraInvalidTooSmall() throws Exception {
        try {
            testDate.withYearOfEra(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearOfEraInvalidTooBig() throws Exception {
        try {
            testDate.withYearOfEra(10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------

    @Test
    public void testWithYear() {
        JapaneseDate date = testDate.withYear(JapaneseEra.SHOWA, 48);
        assertEquals(date, JapaneseDate.of(JapaneseEra.SHOWA, 48, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearInvalidTooSmall() throws Exception {
        try {
            testDate.withYear(testEra, -1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearInvalidTooBig() throws Exception {
        try {
            testDate.withYear(testEra, 10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithMonthOfYear() {
        JapaneseDate date = testDate.withMonthOfYear(MonthOfYear.APRIL);
        assertEquals(date, JapaneseDate.of(testYear, MonthOfYear.APRIL, testDayOfMonth));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testWithMonthOfYearInvalidNull() throws Exception {
        testDate.withMonthOfYear(null);
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfMonth() {
        JapaneseDate date = testDate.withDayOfMonth(4);
        assertEquals(date, JapaneseDate.of(testYear, testMonthOfYear, 4));
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfMonth(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.dayOfMonthRule());
        }
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfMonth(32);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.dayOfMonthRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfYear() {
        JapaneseDate date = testDate.withDayOfYear(15);
        assertEquals(date, JapaneseDate.of(testYear, MonthOfYear.JANUARY, 15));
    }
    
    @Test
    public void testWithDayOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.dayOfYearRule());
        }
    }
    
    @Test
    public void testWithDayOfYearInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfYear(367);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.dayOfYearRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusYears() {
        assertEquals(testDate.plusYears(10), JapaneseDate.of(testYear+10, testMonthOfYear, testDayOfMonth));
    }
    
    @Test (expectedExceptions=CalendricalException.class)
    public void testPlusYearsInvalidTooBig() {
        testDate.plusYears(9999);
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusMonths() {
        assertEquals(testDate.plusMonths(5), JapaneseDate.of(testYear, testMonthOfYear.roll(5), testDayOfMonth));
    }
    
    @Test
    public void testPlusMonthsOverflow() throws Exception {
        try {
            testDate.plusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusDays() {
        assertEquals(testDate.plusDays(2), JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth+2));
    }
    
    @Test
    public void testPlusDaysOverflow() throws Exception {
        try {
            testDate.plusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusWeeks() {
        assertEquals(testDate.plusWeeks(2), JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth+(2*7)));
    }
    
    @Test
    public void testPlusWeeksOverflow() throws Exception {
        try {
            testDate.plusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusYears() {
        assertEquals(testDate.minusYears(10), JapaneseDate.of(testYear-10, testMonthOfYear, testDayOfMonth));
    }
    
    @Test (expectedExceptions=CalendricalException.class)
    public void testMinusYearsInvalidTooSmall() {
        testDate.minusYears(20000);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusMonths() {
        assertEquals(testDate.minusMonths(1), JapaneseDate.of(testYear, testMonthOfYear.previous(), testDayOfMonth));
    }
    
    @Test
    public void testMinusMonthsOverflow() throws Exception {
        try {
            testDate.minusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusDays() {
        assertEquals(testDate.minusDays(2), JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth-2));
    }
    
    @Test
    public void testMinusDaysOverflow() throws Exception {
        try {
            testDate.minusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusWeeks() {
        assertEquals(testDate.minusWeeks(2), JapaneseDate.of(testYear, testMonthOfYear.previous(), 28+testDayOfMonth-(2*7)));
    }
    
    @Test
    public void testMinusWeeksOverflow() throws Exception {
        try {
            testDate.minusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), JapaneseChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test
    public void testToLocalDate() {
        assertEquals(JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth).toLocalDate(),
                LocalDate.of(testGregorianYear, testMonthOfYear, testDayOfMonth));
    }

    //-----------------------------------------------------------------------
    // compareTo(), isAfter(), isBefore(), and equals()
    //-----------------------------------------------------------------------   
    @Test
    public void testCompareTo() throws Exception {
        doTestComparisons(
            JapaneseDate.of(1, MonthOfYear.JANUARY, 1),
            JapaneseDate.of(1, MonthOfYear.JANUARY, 2),
            JapaneseDate.of(1, MonthOfYear.JANUARY, 31),
            JapaneseDate.of(1, MonthOfYear.FEBRUARY, 1),
            JapaneseDate.of(1, MonthOfYear.FEBRUARY, 28),
            JapaneseDate.of(1, MonthOfYear.DECEMBER, 31),
            JapaneseDate.of(2, MonthOfYear.JANUARY, 1),
            JapaneseDate.of(2, MonthOfYear.DECEMBER, 31),
            JapaneseDate.of(3, MonthOfYear.JANUARY, 1),
            JapaneseDate.of(3, MonthOfYear.DECEMBER, 31),
            JapaneseDate.of(9999, MonthOfYear.JANUARY, 1),
            JapaneseDate.of(9999, MonthOfYear.DECEMBER, 31)
        );
    }

    void doTestComparisons(JapaneseDate... dates) {
        for (int i = 0; i < dates.length; i++) {
            JapaneseDate a = dates[i];
            for (int j = 0; j < dates.length; j++) {
                JapaneseDate b = dates[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }
    
    @Test(expectedExceptions=NullPointerException.class)
    public void testCompareToObjectNull() throws Exception {
        testDate.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testIsBeforeObjectNull() throws Exception {
        testDate.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void testIsAfterObjectNull() throws Exception {
        testDate.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testCompareToNonDate() throws Exception {
       Comparable c = testDate;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void testEqualsEaual() throws Exception {
        JapaneseDate a = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        JapaneseDate b = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }
    
    @Test
    public void testEqualsNotEqualDay() throws Exception {
        JapaneseDate a = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        JapaneseDate b = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth+1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualMonth() throws Exception {
        JapaneseDate a = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        JapaneseDate b = JapaneseDate.of(testYear, testMonthOfYear.next(), testDayOfMonth);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualYear() throws Exception {
        JapaneseDate a = JapaneseDate.of(testYear, testMonthOfYear, testDayOfMonth);
        JapaneseDate b = JapaneseDate.of(testYear+1, testMonthOfYear, testDayOfMonth);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsItselfTrue() throws Exception {
        assertEquals(testDate.equals(testDate), true);
    }

    @Test
    public void testEqualsStringFalse() throws Exception {
        assertEquals(testDate.equals("22-07-15"), false);
    }
    
    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------

    @Test
    public void testHashCode() throws Exception {
        JapaneseDate a = JapaneseDate.of(1, MonthOfYear.JANUARY, 1);
        JapaneseDate b = JapaneseDate.of(1, MonthOfYear.JANUARY, 1);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), b.hashCode());
    }
    
    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void testToString() {
        String expected = "HEISEI 21-03-03 (Japanese)";
        String actual = testDate.toString();
        assertEquals(expected, actual);
    }
    
    private void assertJapaneseDate(JapaneseDate test, JapaneseEra era, int year, MonthOfYear month, int day) throws Exception {
        assertEquals(test.getEra(), era);
        assertEquals(test.getYearOfEra(), year);
        assertEquals(test.getMonthOfYear(), month);
        assertEquals(test.getDayOfMonth(), day);
        assertEquals(test.isLeapYear(), isLeapYear(era.getValue(), year));
    }
    
    private boolean isLeapYear(int era, int year) {
        int[] gEraYear = getGregorianEraYearFromLocalEraYear(era, year);
        era = gEraYear[0];
        year = gEraYear[1];
        
        if (era == 0) {
            year = 1 - year;
        } 
        return ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)));
    }
    
    private int[] getGregorianEraYearFromLocalEraYear(int era, int year) {

        int eraInfo[] = new int[2];

        if (era == 0) {
            eraInfo[0] = 0;
            eraInfo[1] = year;
            return eraInfo;
        }

        int offSet = 0;
        int eraCnt = 0;

        for (int i = 0; i < ERA_DATA.length; i = i + ERA_ARRAY_SIZE) {
            int targetEra = TOTAL_ERA_SIZE - eraCnt - 1;
            if (targetEra == era) {
                offSet = ERA_DATA[i + PISITION_YEAR] - 1;
                year += offSet;
                eraInfo[0] = 1;
                eraInfo[1] = year;
                return eraInfo;
            }
            eraCnt++;
        }
        eraInfo[0] = 1;
        eraInfo[1] = year;
        return eraInfo;
    }
    
    private static final int[] ERA_DATA = {
        1989, 1, 8, 6, // HEISEI
        1926, 12, 25, 5, // SHOWA
        1912, 7, 30, 4, // TAISHO
        1868, 9, 8, 3, // MEIJI
        1865, 4, 7, 2, // KEIO
        // ignore other eras.
        1, 1, 1, 1, // AD
        0, 1, 1, 0, // BC
        };

    private static final int ERA_ARRAY_SIZE = 4;
    private static final int PISITION_YEAR = 0;
    private static final int TOTAL_ERA_SIZE = ERA_DATA.length / ERA_ARRAY_SIZE;

}
