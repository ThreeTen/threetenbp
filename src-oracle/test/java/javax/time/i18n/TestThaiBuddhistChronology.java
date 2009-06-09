package javax.time.i18n;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.i18n.ThaiBuddhistChronology;
import javax.time.period.PeriodUnits;

import org.testng.annotations.Test;

public class TestThaiBuddhistChronology {

    @Test
    public void testGetName() {
        assertEquals(ThaiBuddhistChronology.INSTANCE.getName(), "ThaiBuddhist");
    }
    
    @Test
    public void testConstructor() throws Exception {
        for (Constructor constructor : ThaiBuddhistChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ThaiBuddhistChronology chronology = ThaiBuddhistChronology.INSTANCE;
        assertTrue(chronology instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(chronology);
        oos.close();
        
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), chronology);
    }

    @Test
    public void testImmutable() throws Exception {
        Class<ThaiBuddhistChronology> cls = ThaiBuddhistChronology.class;
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
    public void testEra() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.era();
        assertEquals(rule.getID(), "ThaiBuddhist.Era");
        assertEquals(rule.getName(), "Era");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DECADES);
        assertEquals(rule.getPeriodRange(), null);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);
    }

    @Test
    public void testYear() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.year();
        assertEquals(rule.getID(), "ThaiBuddhist.Year");
        assertEquals(rule.getName(), "Year");
        assertEquals(rule.getMinimumValue(), -ThaiBuddhistDate.MAX_YEAR_OF_ERA + 1);
        assertEquals(rule.getLargestMinimumValue(), -ThaiBuddhistDate.MAX_YEAR_OF_ERA + 1);
        assertEquals(rule.getMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.getSmallestMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.YEARS);
        assertEquals(rule.getPeriodRange(), null);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.yearOfEra();
        assertEquals(rule.getID(), "ThaiBuddhist.YearOfEra");
        assertEquals(rule.getName(), "YearOfEra");
        assertEquals(rule.getMinimumValue(), ThaiBuddhistDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getLargestMinimumValue(), ThaiBuddhistDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.getSmallestMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.YEARS);
        assertEquals(rule.getPeriodRange(), null);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.monthOfYear();
        assertEquals(rule.getID(), "ThaiBuddhist.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MONTHS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        assertEquals(rule.getValueQuiet(null, null), null);
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.dayOfMonth();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 31);
        assertEquals(rule.getSmallestMaximumValue(), 28);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.MONTHS);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.dayOfYear();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 366);
        assertEquals(rule.getSmallestMaximumValue(), 365);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeFieldRule rule = ThaiBuddhistChronology.INSTANCE.dayOfWeek();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.WEEKS);
        assertEquals(rule.getValueQuiet(null, null), null);
        serialize(rule);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_hourOfDayRule() throws Exception {
        ThaiBuddhistChronology.INSTANCE.hourOfDay();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_minuteOfHourRule() throws Exception {
        ThaiBuddhistChronology.INSTANCE.minuteOfHour();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_secondOfMinuteRule() throws Exception {
        ThaiBuddhistChronology.INSTANCE.secondOfMinute();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_nanoOfSecondRule() throws Exception {
        ThaiBuddhistChronology.INSTANCE.nanoOfSecond();
    }
    
    public void test_toString() throws Exception {
        assertEquals(ThaiBuddhistChronology.INSTANCE.toString(), "ThaiBuddhist");
    }

    private void serialize(DateTimeFieldRule rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

}
