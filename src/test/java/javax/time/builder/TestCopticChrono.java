package javax.time.builder;

import static javax.time.MonthOfYear.DECEMBER;
import static javax.time.MonthOfYear.MARCH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_MONTH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.MONTH_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.YEAR;
import static org.testng.Assert.assertEquals;

import javax.time.LocalDate;
import javax.time.calendrical.DateTimeRuleRange;

import org.testng.annotations.Test;

@Test
public class TestCopticChrono {

    CopticChrono chrono = CopticChrono.INSTANCE;
    LocalDate march15 = LocalDate.of(2012, MARCH, 15);
    LocalDate dec28 = LocalDate.of(2011, DECEMBER, 28);
    LocalDate dec28leap = LocalDate.of(2012, DECEMBER, 28);

    @Test(groups = "tck")
    public void monthRanges() {
        DateTimeRuleRange monthRange = chrono.getRange(MONTH_OF_YEAR);
        assertEquals(1, monthRange.getMinimum());
        assertEquals(1, monthRange.getLargestMinimum());
        assertEquals(13, monthRange.getMaximum());
        assertEquals(13, monthRange.getSmallestMaximum());
    }

    @Test(groups = "tck")
    public void dayRanges() {
        DateTimeRuleRange monthRange = chrono.getRange(DAY_OF_MONTH);
        assertEquals(1, monthRange.getMinimum());
        assertEquals(1, monthRange.getLargestMinimum());
        assertEquals(30, monthRange.getMaximum());
        assertEquals(5, monthRange.getSmallestMaximum());
    }

    @Test(groups = "tck")
    public void specificRanges() {
        assertEquals(chrono.getRange(DAY_OF_MONTH, march15, null).getMaximum(), 30);
        assertEquals(chrono.getRange(DAY_OF_YEAR, march15, null).getMaximum(), 366);

        assertEquals(chrono.getRange(DAY_OF_MONTH, dec28, null).getMaximum(), 5);

        assertEquals(chrono.getRange(DAY_OF_MONTH, dec28leap, null).getMaximum(), 6);
    }
    
    @Test(groups = "tck")
    public void getMonthsAndDays() {
        assertEquals(chrono.getValue(MONTH_OF_YEAR, march15, null), 3);
        assertEquals(chrono.getValue(MONTH_OF_YEAR, dec28, null), 13);
        assertEquals(chrono.getValue(MONTH_OF_YEAR, dec28leap, null), 13);
        
        assertEquals(chrono.getValue(DAY_OF_MONTH, march15, null), 15);
        assertEquals(chrono.getValue(DAY_OF_MONTH, dec28, null), 2);
        assertEquals(chrono.getValue(DAY_OF_MONTH, dec28leap, null), 3);
    }
    
    @Test(groups = "tck")
    public void setMonthsAndDays() {
        assertChronoEquals(2012, 3, 15, march15);
        assertChronoEquals(2011, 13, 2, dec28);
        assertChronoEquals(2012, 13, 3, dec28leap);
    }
    
    void assertChronoEquals(int year, int month, int day, LocalDate isoDate) {
        LocalDate value = chrono.setDate(YEAR, LocalDate.now(), year);
        value = chrono.setDate(DAY_OF_MONTH, value, day);
        value = chrono.setDate(MONTH_OF_YEAR, value, month);
        assertEquals(isoDate, value);
    }

}
