package javax.time.i18n;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

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
import javax.time.period.PeriodUnits;

import org.testng.annotations.Test;

public class TestThaiBuddhistChronology {

    @Test
    public void testGetName() {
        assertEquals(ThaiBuddhistChronology.INSTANCE.getName(), "ThaiBuddhist");
    }
    
    @Test
    public void testConstructor() throws Exception {
        for (Constructor<?> constructor : ThaiBuddhistChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Object chronology = ThaiBuddhistChronology.INSTANCE;
        assertTrue(chronology instanceof Serializable);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(chronology);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
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
        DateTimeFieldRule<ThaiBuddhistEra> rule = ThaiBuddhistChronology.eraRule();
        assertEquals(rule.getID(), "ThaiBuddhist.Era");
        assertEquals(rule.getName(), "Era");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DECADES);
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeFieldRule<Integer> rule = ThaiBuddhistChronology.yearOfEraRule();
        assertEquals(rule.getID(), "ThaiBuddhist.YearOfEra");
        assertEquals(rule.getName(), "YearOfEra");
        assertEquals(rule.getMinimumValue(), ThaiBuddhistDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getLargestMinimumValue(), ThaiBuddhistDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.getSmallestMaximumValue(), ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.YEARS);
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = ThaiBuddhistChronology.monthOfYearRule();
        assertEquals(rule.getID(), "ThaiBuddhist.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.MONTHS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeFieldRule<Integer> rule = ThaiBuddhistChronology.dayOfMonthRule();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 31);
        assertEquals(rule.getSmallestMaximumValue(), 28);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.MONTHS);
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = ThaiBuddhistChronology.dayOfYearRule();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 366);
        assertEquals(rule.getSmallestMaximumValue(), 365);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.YEARS);
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeFieldRule<Integer> rule = ThaiBuddhistChronology.dayOfWeekRule();
        assertEquals(rule.getID(), "ThaiBuddhist.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), PeriodUnits.DAYS);
        assertEquals(rule.getPeriodRange(), PeriodUnits.WEEKS);
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(ThaiBuddhistChronology.INSTANCE.toString(), "ThaiBuddhist");
    }

    private void serialize(DateTimeFieldRule<?> rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

}
