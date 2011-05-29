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

import javax.time.calendar.DateTimeField;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.DateTimeRuleRange;

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
        DateTimeRule rule = MinguoChronology.eraRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoEra");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(0, 1));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodEras());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeRule rule = MinguoChronology.yearOfEraRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoYearOfEra");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(MinguoDate.MIN_YEAR_OF_ERA, MinguoDate.MAX_YEAR_OF_ERA));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodYears());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodEras());
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeRule rule = MinguoChronology.monthOfYearRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoMonthOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 12));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodYears());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeRule rule = MinguoChronology.dayOfMonthRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoDayOfMonth");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 28, 31));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodMonths());
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeRule rule = MinguoChronology.dayOfYearRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoDayOfYear");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 365, 366));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodYears());
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeRule rule = MinguoChronology.dayOfWeekRule();
        assertEquals(rule.getType(), DateTimeField.class);
        assertEquals(rule.getName(), "MinguoDayOfWeek");
        assertEquals(rule.getValueRange(), DateTimeRuleRange.of(1, 7));
        assertEquals(rule.getPeriodUnit(), MinguoChronology.periodDays());
        assertEquals(rule.getPeriodRange(), MinguoChronology.periodWeeks());
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(MinguoChronology.INSTANCE.toString(), "Minguo");
    }

    private void serialize(DateTimeRule rule) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rule);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertSame(ois.readObject(), rule);
    }

}
