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
        DateTimeRule rule = ThaiBuddhistChronology.eraRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistEra");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(0, 1));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodEras());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeRule rule = ThaiBuddhistChronology.yearOfEraRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistYearOfEra");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(ThaiBuddhistDate.MIN_YEAR_OF_ERA, ThaiBuddhistDate.MAX_YEAR_OF_ERA));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodYears());
        assertEquals(rule.getPeriodRange(), ThaiBuddhistChronology.periodEras());
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeRule rule = ThaiBuddhistChronology.monthOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistMonthOfYear");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 12));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), ThaiBuddhistChronology.periodYears());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeRule rule = ThaiBuddhistChronology.dayOfMonthRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistDayOfMonth");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 28, 31));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodDays());
        assertEquals(rule.getPeriodRange(), ThaiBuddhistChronology.periodMonths());
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeRule rule = ThaiBuddhistChronology.dayOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistDayOfYear");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 365, 366));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodDays());
        assertEquals(rule.getPeriodRange(), ThaiBuddhistChronology.periodYears());
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeRule rule = ThaiBuddhistChronology.dayOfWeekRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "ThaiBuddhistDayOfWeek");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 7));
        assertEquals(rule.getPeriodUnit(), ThaiBuddhistChronology.periodDays());
        assertEquals(rule.getPeriodRange(), ThaiBuddhistChronology.periodWeeks());
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(ThaiBuddhistChronology.INSTANCE.toString(), "ThaiBuddhist");
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
