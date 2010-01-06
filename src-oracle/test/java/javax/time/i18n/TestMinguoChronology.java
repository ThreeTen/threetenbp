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

import org.testng.annotations.Test;

public class TestMinguoChronology {

    @Test
    public void testGetName() {
        assertEquals(MinguoChronology.INSTANCE.getName(), "Minguo");
    }
    
    @Test
    public void testConstructor() throws Exception {
        for (Constructor<?> constructor : MinguoChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Object chronology = MinguoChronology.INSTANCE;
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
        Class<MinguoChronology> cls = MinguoChronology.class;
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
        DateTimeFieldRule<MinguoEra> rule = MinguoChronology.eraRule();
        assertEquals(rule.getID(), "Minguo.Era");
        assertEquals(rule.getName(), "Era");
        assertEquals(rule.getMinimumValue(), 0);
        assertEquals(rule.getLargestMinimumValue(), 0);
        assertEquals(rule.getMaximumValue(), 1);
        assertEquals(rule.getSmallestMaximumValue(), 1);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodEras());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeFieldRule<Integer> rule = MinguoChronology.yearOfEraRule();
        assertEquals(rule.getID(), "Minguo.YearOfEra");
        assertEquals(rule.getName(), "YearOfEra");
        assertEquals(rule.getMinimumValue(), MinguoDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getLargestMinimumValue(), MinguoDate.MIN_YEAR_OF_ERA);
        assertEquals(rule.getMaximumValue(), MinguoDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.getSmallestMaximumValue(), MinguoDate.MAX_YEAR_OF_ERA);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodYears());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodEras());
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = MinguoChronology.monthOfYearRule();
        assertEquals(rule.getID(), "Minguo.MonthOfYear");
        assertEquals(rule.getName(), "MonthOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 12);
        assertEquals(rule.getSmallestMaximumValue(), 12);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodYears());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeFieldRule<Integer> rule = MinguoChronology.dayOfMonthRule();
        assertEquals(rule.getID(), "Minguo.DayOfMonth");
        assertEquals(rule.getName(), "DayOfMonth");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 31);
        assertEquals(rule.getSmallestMaximumValue(), 28);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodMonths());
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeFieldRule<Integer> rule = MinguoChronology.dayOfYearRule();
        assertEquals(rule.getID(), "Minguo.DayOfYear");
        assertEquals(rule.getName(), "DayOfYear");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 366);
        assertEquals(rule.getSmallestMaximumValue(), 365);
        assertEquals(rule.isFixedValueSet(), false);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodYears());
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeFieldRule<Integer> rule = MinguoChronology.dayOfWeekRule();
        assertEquals(rule.getID(), "Minguo.DayOfWeek");
        assertEquals(rule.getName(), "DayOfWeek");
        assertEquals(rule.getMinimumValue(), 1);
        assertEquals(rule.getLargestMinimumValue(), 1);
        assertEquals(rule.getMaximumValue(), 7);
        assertEquals(rule.getSmallestMaximumValue(), 7);
        assertEquals(rule.isFixedValueSet(), true);
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodWeeks());
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(MinguoChronology.INSTANCE.toString(), "Minguo");
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
