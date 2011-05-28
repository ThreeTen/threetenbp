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

public class TestJapaneseChronology {

    @Test
    public void testGetName() {
        assertEquals(JapaneseChronology.INSTANCE.getName(), "Japanese");
    }
    
    @Test
    public void testConstructor() throws Exception {
        for (Constructor<?> constructor : JapaneseChronology.class.getDeclaredConstructors()) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }
    
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Object chronology = JapaneseChronology.INSTANCE;
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
        Class<JapaneseChronology> cls = JapaneseChronology.class;
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
        DateTimeRule rule = JapaneseChronology.eraRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseEra");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(-3, 2));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodEras());
        assertEquals(rule.getPeriodRange(), null);
        serialize(rule);
    }

    @Test
    public void testYearOfEra() throws Exception {
        DateTimeRule rule = JapaneseChronology.yearOfEraRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseYearOfEra");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(JapaneseDate.MIN_YEAR_OF_ERA, JapaneseDate.MAX_YEAR_OF_ERA));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodYears());
        assertEquals(rule.getPeriodRange(), JapaneseChronology.periodEras());
        serialize(rule);
    }

    @Test
    public void testMonthOfYear() throws Exception {
        DateTimeRule rule = JapaneseChronology.monthOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseMonthOfYear");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 12));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodMonths());
        assertEquals(rule.getPeriodRange(), JapaneseChronology.periodYears());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        DateTimeRule rule = JapaneseChronology.dayOfMonthRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseDayOfMonth");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 28, 31));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodDays());
        assertEquals(rule.getPeriodRange(), JapaneseChronology.periodMonths());
        serialize(rule);
    }

    @Test
    public void testDayOfYear() throws Exception {
        DateTimeRule rule = JapaneseChronology.dayOfYearRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseDayOfYear");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 365, 366));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodDays());
        assertEquals(rule.getPeriodRange(), JapaneseChronology.periodYears());
        serialize(rule);

    }

    @Test
    public void testDayOfWeek() throws Exception {
        DateTimeRule rule = JapaneseChronology.dayOfWeekRule();
        assertEquals(rule.getReifiedType(), DateTimeField.class);
        assertEquals(rule.getName(), "JapaneseDayOfWeek");
        assertEquals(rule.getRange(), DateTimeRuleRange.of(1, 7));
        assertEquals(rule.getPeriodUnit(), JapaneseChronology.periodDays());
        assertEquals(rule.getPeriodRange(), JapaneseChronology.periodWeeks());
        serialize(rule);
    }

    public void test_toString() throws Exception {
        assertEquals(JapaneseChronology.INSTANCE.toString(), "Japanese");
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
