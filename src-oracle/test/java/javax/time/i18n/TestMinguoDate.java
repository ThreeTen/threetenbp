package javax.time.i18n;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
import javax.time.calendar.field.HourOfDay;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestMinguoDate {
    
    private MinguoEra testEra = MinguoEra.MINGUO;
    private int testYear = 98;
    private int testGregorianYear = 2009;
    private int testMonthOfYear = 3;
    private int testDayOfMonth = 3;
    private int testDayOfYear = 62;
    private boolean testLeapYear = false;
    private MinguoDate testDate;

    @BeforeTest
    public void setUp() throws Exception {
        testDate = MinguoDate.minguoDate(testEra, testYear, testMonthOfYear, testDayOfMonth);
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
        Class<MinguoDate> cls = MinguoDate.class;
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
    public void testMinguoDateDateProvider() throws Exception {
        assertEquals(MinguoDate.minguoDate(testDate), testDate);
        assertMinguoDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test(expectedExceptions=NullPointerException.class)
    public void testMinguoDateDateProviderNull() throws Exception {
        MinguoDate.minguoDate(null);
    }

    @Test
    public void testMinguoDateIntIntInt() throws Exception{
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertMinguoDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void testMinguoDateIntIntIntInt() throws Exception{
        assertEquals(MinguoDate.minguoDate(testEra, testYear, testMonthOfYear, testDayOfMonth), testDate);
        assertMinguoDate(testDate, testEra, testYear, testMonthOfYear, testDayOfMonth);
    }
    
    @Test
    public void testMinguoDateInvalidYear() throws Exception{
        try {
            MinguoDate.minguoDate(10000, testMonthOfYear, testDayOfMonth);// Invalid year.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testMinguoDateInvalidMonth() throws Exception{
        try {
            MinguoDate.minguoDate(testYear, 13, testDayOfMonth);// Invalid month of year
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.monthOfYearRule());
        }
    }
    
    @Test
    public void testMinguoDateInvalidDay() throws Exception{
        try {
            MinguoDate.minguoDate(testYear, testMonthOfYear, 40);// Invalid day of month.
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.dayOfMonthRule());
        }
    }

    @Test
    public void testGetChronology() {
        assertEquals(testDate.getChronology(), MinguoChronology.INSTANCE);
    }
    
    @Test
    public void testGet() throws Exception {
        assertEquals(testDate.get(MinguoChronology.eraRule()), testDate.getEra());
        assertEquals(testDate.get(MinguoChronology.yearOfEraRule()), (Integer) testDate.getYearOfEra());
        assertEquals(testDate.get(MinguoChronology.monthOfYearRule()), (Integer) testDate.getMonthOfYear());
        assertEquals(testDate.get(MinguoChronology.dayOfMonthRule()), (Integer) testDate.getDayOfMonth());
        assertEquals(testDate.get(MinguoChronology.dayOfYearRule()), (Integer) testDate.getDayOfYear());
        assertEquals(testDate.get(MinguoChronology.dayOfWeekRule()), (Integer) testDate.getDayOfWeek());
    }
    
    public void testGetUnsupported() throws Exception {
        assertEquals(testDate.get(HourOfDay.rule()), null);
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
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 2).getDayOfWeek(), 1);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 3).getDayOfWeek(), 2);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 4).getDayOfWeek(), 3);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 5).getDayOfWeek(), 4);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 6).getDayOfWeek(), 5);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 7).getDayOfWeek(), 6);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 8).getDayOfWeek(), 7);
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, 9).getDayOfWeek(), 1);
    }
    
    @Test
    public void testGetDayOfWeekCrossCheck() throws Exception {
        MinguoDate date = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(date.getDayOfWeek(), date.toLocalDate().getDayOfWeek().getValue());
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
        MinguoDate date = testDate.withYearOfEra(100);
        assertEquals(date, MinguoDate.minguoDate(100, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearOfEraInvalidTooSmall() throws Exception {
        try {
            testDate.withYearOfEra(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearOfEraInvalidTooBig() throws Exception {
        try {
            testDate.withYearOfEra(10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withYearOfEra()
    //-----------------------------------------------------------------------
    @Test
    public void testWithYear() {
        MinguoDate date = testDate.withYear(MinguoEra.BEFORE_MINGUO, 100);
        assertEquals(date, MinguoDate.minguoDate(MinguoEra.BEFORE_MINGUO, 100, testMonthOfYear, testDayOfMonth));
    }
    
    @Test
    public void testWithYearInvalidTooSmall() throws Exception {
        try {
            testDate.withYear(testEra, -1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }
    
    @Test
    public void testWithYearInvalidTooBig() throws Exception {
        try {
            testDate.withYear(testEra, 10000);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithMonthOfYear() {
        MinguoDate date = testDate.withMonthOfYear(4);
        assertEquals(date, MinguoDate.minguoDate(testYear, 4, testDayOfMonth));
    }
    
    @Test
    public void testWithMonthOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withMonthOfYear(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.monthOfYearRule());
        }
    }
    
    @Test
    public void testWithMonthOfYearInvalidTooBig() throws Exception {
        try {
            testDate.withMonthOfYear(13);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.monthOfYearRule());
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfMonth() {
        MinguoDate date = testDate.withDayOfMonth(4);
        assertEquals(date, MinguoDate.minguoDate(testYear, testMonthOfYear, 4));
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfMonth(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.dayOfMonthRule());
        }
    }
    
    @Test
    public void testWithDayOfMonthInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfMonth(32);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.dayOfMonthRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // withDayOfYear()
    //-----------------------------------------------------------------------
    @Test
    public void testWithDayOfYear() {
        MinguoDate date = testDate.withDayOfYear(15);
        assertEquals(date, MinguoDate.minguoDate(testYear, 1, 15));
    }
    
    @Test
    public void testWithDayOfYearInvalidTooSmall() throws Exception {
        try {
            testDate.withDayOfYear(0);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.dayOfYearRule());
        }
    }
    
    @Test
    public void testWithDayOfYearInvalidTooBig() throws Exception {
        try {
            testDate.withDayOfYear(367);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.dayOfYearRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusYears() {
        assertEquals(testDate.plusYears(10), MinguoDate.minguoDate(testYear+10, testMonthOfYear, testDayOfMonth));
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
        assertEquals(testDate.plusMonths(5), MinguoDate.minguoDate(testYear, testMonthOfYear+5, testDayOfMonth));
    }
    
    @Test
    public void testPlusMonthsOverflow() throws Exception {
        try {
            testDate.plusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusDays() {
        assertEquals(testDate.plusDays(2), MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth+2));
    }
    
    @Test
    public void testPlusDaysOverflow() throws Exception {
        try {
            testDate.plusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // plusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testPlusWeeks() {
        assertEquals(testDate.plusWeeks(2), MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth+(2*7)));
    }
    
    @Test
    public void testPlusWeeksOverflow() throws Exception {
        try {
            testDate.plusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusYears()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusYears() {
        assertEquals(testDate.minusYears(10), MinguoDate.minguoDate(testYear-10, testMonthOfYear, testDayOfMonth));
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
        assertEquals(testDate.minusMonths(1), MinguoDate.minguoDate(testYear, testMonthOfYear-1, testDayOfMonth));
    }
    
    @Test
    public void testMinusMonthsOverflow() throws Exception {
        try {
            testDate.minusMonths(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // minusDays()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusDays() {
        assertEquals(testDate.minusDays(2), MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth-2));
    }
    
    @Test
    public void testMinusDaysOverflow() throws Exception {
        try {
            testDate.minusDays(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }
    
    //-----------------------------------------------------------------------
    // minusWeeks()
    //-----------------------------------------------------------------------
    @Test
    public void testMinusWeeks() {
        assertEquals(testDate.minusWeeks(2), MinguoDate.minguoDate(testYear, testMonthOfYear-1, 28+testDayOfMonth-(2*7)));
    }
    
    @Test
    public void testMinusWeeksOverflow() throws Exception {
        try {
            testDate.minusWeeks(Integer.MAX_VALUE);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MinguoChronology.yearOfEraRule());
        }
    }

    //-----------------------------------------------------------------------
    // toLocalDate()
    //-----------------------------------------------------------------------
    @Test
    public void testToLocalDate() {
        assertEquals(MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth).toLocalDate(),
                LocalDate.date(testGregorianYear, testMonthOfYear, testDayOfMonth));
    }

    //-----------------------------------------------------------------------
    // compareTo(), isAfter(), isBefore(), and equals()
    //-----------------------------------------------------------------------   
    @Test
    public void testCompareTo() throws Exception {
        doTestComparisons(
            MinguoDate.minguoDate(1, 1, 1),
            MinguoDate.minguoDate(1, 1, 2),
            MinguoDate.minguoDate(1, 1, 31),
            MinguoDate.minguoDate(1, 2, 1),
            MinguoDate.minguoDate(1, 2, 28),
            MinguoDate.minguoDate(1, 12, 31),
            MinguoDate.minguoDate(2, 1, 1),
            MinguoDate.minguoDate(2, 12, 31),
            MinguoDate.minguoDate(3, 1, 1),
            MinguoDate.minguoDate(3, 12, 31),
            MinguoDate.minguoDate(9999, 1, 1),
            MinguoDate.minguoDate(9999, 12, 31)
        );
    }

    void doTestComparisons(MinguoDate... dates) {
        for (int i = 0; i < dates.length; i++) {
            MinguoDate a = dates[i];
            for (int j = 0; j < dates.length; j++) {
                MinguoDate b = dates[j];
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
        MinguoDate a = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        MinguoDate b = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }
    
    @Test
    public void testEqualsNotEqualDay() throws Exception {
        MinguoDate a = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        MinguoDate b = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth+1);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualMonth() throws Exception {
        MinguoDate a = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        MinguoDate b = MinguoDate.minguoDate(testYear, testMonthOfYear+1, testDayOfMonth);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(a.equals(a), true);
        assertEquals(b.equals(b), true);
    }

    @Test
    public void testEqualsNotEqualYear() throws Exception {
        MinguoDate a = MinguoDate.minguoDate(testYear, testMonthOfYear, testDayOfMonth);
        MinguoDate b = MinguoDate.minguoDate(testYear+1, testMonthOfYear, testDayOfMonth);
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
        MinguoDate a = MinguoDate.minguoDate(1, 1, 1);
        MinguoDate b = MinguoDate.minguoDate(1, 1, 1);
        assertEquals(a.hashCode(), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(b.hashCode(), b.hashCode());
    }
    
    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    public void testToString() {
        String expected = "98-03-03 (Minguo)";
        String actual = testDate.toString();
        assertEquals(expected, actual);
    }
    
    private void assertMinguoDate(MinguoDate test, MinguoEra era, int year, int month, int day) throws Exception {
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
        int gYear = 1 == localEra ? localYear + 1911
                : (1 - localYear) + 1911;
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
