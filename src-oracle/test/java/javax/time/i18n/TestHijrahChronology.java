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
import javax.time.calendar.DayOfWeek;

import org.testng.annotations.Test;

public class TestHijrahChronology {

    @Test
    public void testGetName() {
        assertEquals(HijrahChronology.INSTANCE.getName(), "Hijrah");
    }
    
    @Test
    public void testConstructor() throws Exception {
        for (Constructor<?> constructor : HijrahChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Object chronology = HijrahChronology.INSTANCE;
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
        Class<HijrahChronology> cls = HijrahChronology.class;
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
        DateTimeFieldRule<HijrahEra> rule = HijrahChronology.eraRule();
        assertEquals(rule.getID(), "Hijrah.Era");
        assertEquals(rule.getName(), "Era");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodEras());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeFieldRule<Integer> rule = HijrahChronology.yearOfEraRule();
        assertEquals(rule.getID(), "Hijrah.YearOfEra");
        assertEquals(rule.getName(), "YearOfEra");
        assertEquals(rule.getMinimumValue(), HijrahDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getLargestMinimumValue(), HijrahDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getMaximumValue(), HijrahDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.getSmallestMaximumValue(), HijrahDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodYears());
        assertEquals(rule.getPeriodRange(), HijrahChronology.periodEras());
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = HijrahChronology.monthOfYearRule();
        assertEquals(rule.getID(), "Hijrah.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), HijrahChronology.periodYears());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeFieldRule<Integer> rule = HijrahChronology.dayOfMonthRule();
        assertEquals(rule.getID(), "Hijrah.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 30);
        assertEquals(rule.getSmallestMaximumValue(), 29);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodDays());
        assertEquals(rule.getPeriodRange(), HijrahChronology.periodMonths());
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = HijrahChronology.dayOfYearRule();
        assertEquals(rule.getID(), "Hijrah.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 355);
        assertEquals(rule.getSmallestMaximumValue(), 354);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodDays());
        assertEquals(rule.getPeriodRange(), HijrahChronology.periodYears());
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeFieldRule<DayOfWeek> rule = HijrahChronology.dayOfWeekRule();
        assertEquals(rule.getReifiedType(), DayOfWeek.class);
        assertEquals(rule.getID(), "Hijrah.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), HijrahChronology.periodDays());
        assertEquals(rule.getPeriodRange(), HijrahChronology.periodWeeks());
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(HijrahChronology.INSTANCE.toString(), "Hijrah");
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
