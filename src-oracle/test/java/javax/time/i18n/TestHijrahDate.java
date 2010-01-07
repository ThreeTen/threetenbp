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
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.field.DayOfWeek;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestHijrahDate {
    
    private HijrahEra testEra = HijrahEra.HIJRAH;
    private int testYear = 1430;
    private int testGregorianYear = 2009;
    private int testGregorianMonthOfYear = 3;
    private int testGregorianDayOfMonth = 2;
    private int testMonthOfYear = 3;
    private int testDayOfMonth = 5;
    private int testDayOfYear = 64;
    private boolean testLeapYear = false;
    private HijrahDate testDate;

    @BeforeTest
    public void setUp() throws Exception {
        testDate = HijrahDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void testInterfaces() {
        Object obj = testDate;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof DateProvider);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testDate);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), testDate);
    }
    
    @Test
    public void testImmutable() {
        Class<HijrahDate> cls = HijrahDate.class;
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

    @Test
    public void testHijrahDateDateProvider() throws Exception {
        assertEquals(HijrahDate.from(testDate), testDate);
        assertHijrahDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test(expectedExceptions=NullPointerException.class)
    public void testHijrahDateDateProviderNull() throws Exception {
        HijrahDate.from(null);
    }

    @Test
    public void testHijrahDateIntIntInt() throws Exception{
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertHijrahDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void testHijrahDateIntIntIntInt() throws Exception{
        assertEquals(HijrahDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertHijrahDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void testHijrahDateInvalidYear() throws Exception{
        try {
            HijrahDate.of(10000, testMonthOfYear, testDayOfMonth);// Invalid year.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testHijrahDateInvalidMonth() throws Exception{
        try {
            HijrahDate.of(testYear, 13, testDayOfMonth);// Invalid month of year
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.monthOfYearRule());
        }
    }
    
    @Test
    public void testHijrahDateInvalidDay() throws Exception{
        try {
            HijrahDate.of(testYear, testMonthOfYear, 40);// Invalid day of month.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.dayOfMonthRule());
        }
    }

    @Test
    public void testGetChronology() {
        assertEquals(testDate.getChronology(), HijrahChronology.INSTANCE);
    }
    
    @Test
    public void testGet() throws Exception {
        assertEquals(testDate.get(HijrahChronology.eraRule()), testDate.getEra());
        assertEquals(testDate.get(HijrahChronology.yearOfEraRule()), (Integer) testDate.getYearOfEra());
        assertEquals(testDate.get(HijrahChronology.monthOfYearRule()), (Integer) testDate.getMonthOfYear());
        assertEquals(testDate.get(HijrahChronology.dayOfMonthRule()), (Integer) testDate.getDayOfMonth());
        assertEquals(testDate.get(HijrahChronology.dayOfYearRule()), (Integer) testDate.getDayOfYear());
        assertEquals(testDate.get(HijrahChronology.dayOfWeekRule()), testDate.getDayOfWeek());
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
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 5).getDayOfWeek(), DayOfWeek.MONDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 6).getDayOfWeek(), DayOfWeek.TUESDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 7).getDayOfWeek(), DayOfWeek.WEDNESDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 8).getDayOfWeek(), DayOfWeek.THURSDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 9).getDayOfWeek(), DayOfWeek.FRIDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 10).getDayOfWeek(), DayOfWeek.SATURDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 11).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, 12).getDayOfWeek(), DayOfWeek.MONDAY);
    }
    
    @Test
    public void testGetDayOfWeekCrossCheck() throws Exception {
        HijrahDate date = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(date.getDayOfWeek(), date.toLocalDate().getDayOfWeek());
    }

    @Test
    public void testIsLeapYear() {
        assertEquals(testDate.isLeapYear(), testLeapYear);
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithYearOfEra() {
        HijrahDate date = testDate.withYearOfEra(1545);
        assertEquals(date, HijrahDate.of(1545, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearOfEraInvalidTooSmall() throws Exception {
        try {
            testDate.withYearOfEra(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearOfEraInvalidTooBig() throws Exception {
        try {
            testDate.withYearOfEra(10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithYear() {
        HijrahDate date = testDate.withYear(HijrahEra.BEFORE_HIJRAH, 1540);
        assertEquals(date, HijrahDate.of(HijrahEra.BEFORE_HIJRAH, 1540, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearInvalidTooSmall() throws Exception {
        try {
            testDate.withYear(testEra, -1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearInvalidTooBig() throws Exception {
        try {
            testDate.withYear(testEra, 10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithMonthOfYear() {
        HijrahDate date = testDate.withMonthOfYear(4);
        assertEquals(date, HijrahDate.of(testYear, 4, testDayOfMonth));
    }
    
    @Test
    public void testWithMonthOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withMonthOfYear(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.monthOfYearRule());
        }
    }
    
    @Test
    public void testWithMonthOfYearInvalidTooBig() throws Exception {
        try {
            testDate.withMonthOfYear(13);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.monthOfYearRule());
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfMonth() {
        HijrahDate date = testDate.withDayOfMonth(4);
        assertEquals(date, HijrahDate.of(testYear, testMonthOfYear, 4));
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfMonth(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.dayOfMonthRule());
        }
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfMonth(32);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.dayOfMonthRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfYear() {
        HijrahDate date = testDate.withDayOfYear(15);
        assertEquals(date, HijrahDate.of(testYear, 1, 15));
    }
    
    @Test
    public void testWithDayOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.dayOfYearRule());
        }
    }
    
    @Test
    public void testWithDayOfYearInvalidTooBig() throws Exception {
        HijrahDate date = HijrahDate.of(2008, 2, 1);
        try {
            date.withDayOfYear(367);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.dayOfYearRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusYears() {
        assertEquals(testDate.plusYears(10), HijrahDate.of(testYear+10, testMonthOfYear, testDayOfMonth));
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
        assertEquals(testDate.plusMonths(5), HijrahDate.of(testYear, testMonthOfYear+5, testDayOfMonth));
    }
    
    @Test
    public void testPlusMonthsOverflow() throws Exception {
        try {
            testDate.plusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusDays() {
        assertEquals(testDate.plusDays(2), HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth+2));
    }
    
    @Test
    public void testPlusDaysOverflow() throws Exception {
        try {
            testDate.plusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusWeeks() {
        assertEquals(testDate.plusWeeks(2), HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth+(2*7)));
    }
    
    @Test
    public void testPlusWeeksOverflow() throws Exception {
        try {
            testDate.plusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusYears() {
        assertEquals(testDate.minusYears(10), HijrahDate.of(testYear-10, testMonthOfYear, testDayOfMonth));
    }
    
    @Test (expectedExceptions=CalendricalException.class)
    public void testMinusYearsInvalidTooSmall() {
        testDate.minusYears(9999);
    }

    //-----------------------------------------------------------------------
    // minusMonths()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusMonths() {
        assertEquals(testDate.minusMonths(1), HijrahDate.of(testYear, testMonthOfYear-1, testDayOfMonth));
    }
    
    @Test
    public void testMinusMonthsOverflow() throws Exception {
        try {
            testDate.minusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusDays() {
        assertEquals(testDate.minusDays(2), HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth-2));
    }
    
    @Test
    public void testMinusDaysOverflow() throws Exception {
        try {
            testDate.minusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusWeeks() {
        assertEquals(testDate.minusWeeks(2), HijrahDate.of(testYear, testMonthOfYear-1, 29+testDayOfMonth-(2*7)));
    }
    
    @Test
    public void testMinusWeeksOverflow() throws Exception {
        try {
            testDate.minusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), HijrahChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test
    public void testToLocalDate() {
        assertEquals(HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth).toLocalDate(),
                LocalDate.of(testGregorianYear, testGregorianMonthOfYear, testGregorianDayOfMonth));
    }

    //-----------------------------------------------------------------------
    // compareTo(), isAfter(), isBefore(), and equals()
    //-----------------------------------------------------------------------   
    @Test
    public void testCompareTo() throws Exception {
        doTestComparisons(
            HijrahDate.of(1, 1, 1),
            HijrahDate.of(1, 1, 2),
            HijrahDate.of(1, 1, 30),
            HijrahDate.of(1, 2, 1),
            HijrahDate.of(1, 2, 29),
            HijrahDate.of(1, 12, 29),
            HijrahDate.of(2, 1, 1),
            HijrahDate.of(2, 12, 29),
            HijrahDate.of(3, 1, 1),
            HijrahDate.of(3, 12, 29),
            HijrahDate.of(4500, 1, 1),
            HijrahDate.of(4500, 12, 29)
        );
    }

    void doTestComparisons(HijrahDate... dates) {
        for (int i = 0; i < dates.length; i++) {
            HijrahDate a = dates[i];
            for (int j = 0; j < dates.length; j++) {
                HijrahDate b = dates[j];
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

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions=ClassCastException.class)
    public void testCompareToNonDate() throws Exception {
       Comparable c = testDate;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void testEqualsEaual() throws Exception {
        HijrahDate a = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        HijrahDate b = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }
    
    @Test
    public void testEqualsNotEqualDay() throws Exception {
        HijrahDate a = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        HijrahDate b = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth+1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualMonth() throws Exception {
        HijrahDate a = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        HijrahDate b = HijrahDate.of(testYear, testMonthOfYear+1, testDayOfMonth);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualYear() throws Exception {
        HijrahDate a = HijrahDate.of(testYear, testMonthOfYear, testDayOfMonth);
        HijrahDate b = HijrahDate.of(testYear+1, testMonthOfYear, testDayOfMonth);
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
        assertEquals(testDate.equals("2009-07-15"), false);
    }
    
    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------

    @Test
    public void testHashCode() throws Exception {
        HijrahDate a = HijrahDate.of(1, 1, 1);
        HijrahDate b = HijrahDate.of(1, 1, 1);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), b.hashCode());
    }
    
    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void testToString() {
        String expected = "1430-03-05 (Hijrah)";
        String actual = testDate.toString();
        assertEquals(expected, actual);
    }
    
    private void assertHijrahDate(HijrahDate test, HijrahEra era, int year, int month, int day) throws Exception {
        assertEquals(test.getEra(), era);
        assertEquals(test.getYearOfEra(), year);
        assertEquals(test.getMonthOfYear(), month);
        assertEquals(test.getDayOfMonth(), day);
        assertEquals(test.isLeapYear(), isLeapYear(year));
    }
    
    private  boolean isLeapYear(int year) {
        return (14 + 11 * (year > 0 ? year : -year)) % 30 < 11;
    }
}
