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

public class TestThaiBuddhistDate {
    
    private ThaiBuddhistEra testEra = ThaiBuddhistEra.BUDDHIST;
    private int testYear = 2552;
    private int testGregorianYear = 2009;
    private MonthOfYear testMonthOfYear = MonthOfYear.MARCH;
    private int testDayOfMonth = 3;
    private int testDayOfYear = 62;
    private boolean testLeapYear = false;
    private ThaiBuddhistDate testDate;

    @BeforeTest
    public void setUp() throws Exception {
        testDate = ThaiBuddhistDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth);
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
        Class<ThaiBuddhistDate> cls = ThaiBuddhistDate.class;
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
    public void test_factory_of_3() throws Exception{
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertThaiBuddhistDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void test_factory_of_4() throws Exception{
        assertEquals(ThaiBuddhistDate.of(testEra, testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertThaiBuddhistDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void test_factory_of_3_invalidYear() throws Exception{
        try {
            ThaiBuddhistDate.of(10000, testMonthOfYear, testDayOfMonth);// Invalid year.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }
    
    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_3_invalidMonth() throws Exception{
        ThaiBuddhistDate.of(testYear, null, testDayOfMonth);
    }
    
    @Test
    public void test_factory_of_3_invalidDay() throws Exception{
        try {
            ThaiBuddhistDate.of(testYear, testMonthOfYear, 40);// Invalid day of month.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.dayOfMonthRule());
        }
    }

    //-----------------------------------------------------------------------
    public void test_factory_of_Calendrical() throws Exception {
        assertEquals(ThaiBuddhistDate.of(testDate), testDate);
        assertThaiBuddhistDate(ThaiBuddhistDate.of(testDate), testEra, testYear, testMonthOfYear, testDayOfMonth);
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void test_factory_of_Calendrical_noData() throws Exception {
        ThaiBuddhistDate.of(new MockSimpleCalendrical());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_factory_of_Calendrical_null() throws Exception {
        ThaiBuddhistDate.of((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    @Test
    public void testGetChronology() {
        assertEquals(testDate.getChronology(), ThaiBuddhistChronology.INSTANCE);
    }
    
    @Test
    public void testGet() throws Exception {
        assertEquals(testDate.get(ThaiBuddhistChronology.eraRule()), testDate.getEra());
        assertEquals(testDate.get(ThaiBuddhistChronology.yearOfEraRule()), (Integer) testDate.getYearOfEra());
        assertEquals(testDate.get(ThaiBuddhistChronology.monthOfYearRule()), testDate.getMonthOfYear());
        assertEquals(testDate.get(ThaiBuddhistChronology.dayOfMonthRule()), (Integer) testDate.getDayOfMonth());
        assertEquals(testDate.get(ThaiBuddhistChronology.dayOfYearRule()), (Integer) testDate.getDayOfYear());
        assertEquals(testDate.get(ThaiBuddhistChronology.dayOfWeekRule()), testDate.getDayOfWeek());
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
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 2).getDayOfWeek(), DayOfWeek.MONDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 3).getDayOfWeek(), DayOfWeek.TUESDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 4).getDayOfWeek(), DayOfWeek.WEDNESDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 5).getDayOfWeek(), DayOfWeek.THURSDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 6).getDayOfWeek(), DayOfWeek.FRIDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 7).getDayOfWeek(), DayOfWeek.SATURDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 8).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, 9).getDayOfWeek(), DayOfWeek.MONDAY);
    }
    
    @Test
    public void testGetDayOfWeekCrossCheck() throws Exception {
        ThaiBuddhistDate date = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(date.getDayOfWeek(), date.toLocalDate().getDayOfWeek());
    }

    @Test
    public void testIsLeapYear() {
        assertEquals(testDate.isLeapYear(), testLeapYear);
    }

    //-----------------------------------------------------------------------
    // withYearOfEra
    //-----------------------------------------------------------------------
    @Test
    public void testWithYearOfEra() {
        ThaiBuddhistDate date = testDate.withYearOfEra(2010);
        assertEquals(date, ThaiBuddhistDate.of(2010, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearOfEraInvalidTooSmall() throws Exception {
        try {
            testDate.withYearOfEra(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearOfEraInvalidTooBig() throws Exception {
        try {
            testDate.withYearOfEra(10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withYearOfEra()
    //-----------------------------------------------------------------------
    @Test
    public void testWithYear() {
        ThaiBuddhistDate date = testDate.withYear(ThaiBuddhistEra.BEFORE_BUDDHIST, 2010);
        assertEquals(date, ThaiBuddhistDate.of(ThaiBuddhistEra.BEFORE_BUDDHIST, 2010, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearInvalidTooSmall() throws Exception {
        try {
            testDate.withYear(testEra, -1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearInvalidTooBig() throws Exception {
        try {
            testDate.withYear(testEra, 10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithMonthOfYear() {
        ThaiBuddhistDate date = testDate.withMonthOfYear(MonthOfYear.APRIL);
        assertEquals(date, ThaiBuddhistDate.of(testYear, MonthOfYear.APRIL, testDayOfMonth));
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
        ThaiBuddhistDate date = testDate.withDayOfMonth(4);
        assertEquals(date, ThaiBuddhistDate.of(testYear, testMonthOfYear, 4));
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfMonth(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.dayOfMonthRule());
        }
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfMonth(32);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.dayOfMonthRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfYear() {
        ThaiBuddhistDate date = testDate.withDayOfYear(15);
        assertEquals(date, ThaiBuddhistDate.of(testYear, MonthOfYear.JANUARY, 15));
    }
    
    @Test
    public void testWithDayOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.dayOfYearRule());
        }
    }
    
    @Test
    public void testWithDayOfYearInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfYear(367);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.dayOfYearRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusYears() {
        assertEquals(testDate.plusYears(10), ThaiBuddhistDate.of(testYear+10, testMonthOfYear, testDayOfMonth));
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
        assertEquals(testDate.plusMonths(5), ThaiBuddhistDate.of(testYear, testMonthOfYear.roll(5), testDayOfMonth));
    }
    
    @Test
    public void testPlusMonthsOverflow() throws Exception {
        try {
            testDate.plusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusDays() {
        assertEquals(testDate.plusDays(2), ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth+2));
    }
    
    @Test
    public void testPlusDaysOverflow() throws Exception {
        try {
            testDate.plusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusWeeks() {
        assertEquals(testDate.plusWeeks(2), ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth+(2*7)));
    }
    
    @Test
    public void testPlusWeeksOverflow() throws Exception {
        try {
            testDate.plusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusYears() {
        assertEquals(testDate.minusYears(10), ThaiBuddhistDate.of(testYear-10, testMonthOfYear, testDayOfMonth));
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
        assertEquals(testDate.minusMonths(1), ThaiBuddhistDate.of(testYear, testMonthOfYear.previous(), testDayOfMonth));
    }
    
    @Test
    public void testMinusMonthsOverflow() throws Exception {
        try {
            testDate.minusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusDays() {
        assertEquals(testDate.minusDays(2), ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth-2));
    }
    
    @Test
    public void testMinusDaysOverflow() throws Exception {
        try {
            testDate.minusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusWeeks() {
        assertEquals(testDate.minusWeeks(2), ThaiBuddhistDate.of(testYear, testMonthOfYear.previous(), 28+testDayOfMonth-(2*7)));
    }
    
    @Test
    public void testMinusWeeksOverflow() throws Exception {
        try {
            testDate.minusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), ThaiBuddhistChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test
    public void testToLocalDate() {
        assertEquals(ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth).toLocalDate(),
                LocalDate.of(testGregorianYear, testMonthOfYear, testDayOfMonth));
    }

    //-----------------------------------------------------------------------
    // compareTo(), isAfter(), isBefore(), and equals()
    //-----------------------------------------------------------------------   
    @Test
    public void testCompareTo() throws Exception {
        doTestComparisons(
            ThaiBuddhistDate.of(1, MonthOfYear.JANUARY, 1),
            ThaiBuddhistDate.of(1, MonthOfYear.JANUARY, 2),
            ThaiBuddhistDate.of(1, MonthOfYear.JANUARY, 31),
            ThaiBuddhistDate.of(1, MonthOfYear.FEBRUARY, 1),
            ThaiBuddhistDate.of(1, MonthOfYear.FEBRUARY, 28),
            ThaiBuddhistDate.of(1, MonthOfYear.DECEMBER, 31),
            ThaiBuddhistDate.of(2, MonthOfYear.JANUARY, 1),
            ThaiBuddhistDate.of(2, MonthOfYear.DECEMBER, 31),
            ThaiBuddhistDate.of(3, MonthOfYear.JANUARY, 1),
            ThaiBuddhistDate.of(3, MonthOfYear.DECEMBER, 31),
            ThaiBuddhistDate.of(9999, MonthOfYear.JANUARY, 1),
            ThaiBuddhistDate.of(9999, MonthOfYear.DECEMBER, 31)
        );
    }

    void doTestComparisons(ThaiBuddhistDate... dates) {
        for (int i = 0; i < dates.length; i++) {
            ThaiBuddhistDate a = dates[i];
            for (int j = 0; j < dates.length; j++) {
                ThaiBuddhistDate b = dates[j];
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
    public void testCompareToNoDate() throws Exception {
       Comparable c = testDate;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test
    public void testEqualsEaual() throws Exception {
        ThaiBuddhistDate a = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        ThaiBuddhistDate b = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }
    
    @Test
    public void testEqualsNotEqualDay() throws Exception {
        ThaiBuddhistDate a = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        ThaiBuddhistDate b = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth+1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualMonth() throws Exception {
        ThaiBuddhistDate a = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        ThaiBuddhistDate b = ThaiBuddhistDate.of(testYear, testMonthOfYear.next(), testDayOfMonth);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualYear() throws Exception {
        ThaiBuddhistDate a = ThaiBuddhistDate.of(testYear, testMonthOfYear, testDayOfMonth);
        ThaiBuddhistDate b = ThaiBuddhistDate.of(testYear+1, testMonthOfYear, testDayOfMonth);
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
        ThaiBuddhistDate a = ThaiBuddhistDate.of(1, MonthOfYear.JANUARY, 1);
        ThaiBuddhistDate b = ThaiBuddhistDate.of(1, MonthOfYear.JANUARY, 1);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), b.hashCode());
    }
    
    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void testToString() {
        String expected = "2552-03-03 (ThaiBuddhist)";
        String actual = testDate.toString();
        assertEquals(expected, actual);
    }
    
    private void assertThaiBuddhistDate(ThaiBuddhistDate test, ThaiBuddhistEra era, int year, MonthOfYear month, int day) throws Exception {
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
    
    private int[] getGregorianEraYearFromLocalEraYear(int localEra,
            int localYear) {
        int gregorianEraYear[] = new int[2];
        int gYear = 1 == localEra ? localYear
                - 543 : (1 - localYear) - 543;
        int gEra = 1; // AD
        if (gYear < 0) {
            gYear = 1 - gYear;
            gEra = 0; // BC
        }
        gregorianEraYear[0] = gEra;
        gregorianEraYear[1] = gYear;
        return gregorianEraYear;
    }
}
