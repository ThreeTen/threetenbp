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
        assertEquals(chrono.getDateValue(march15, MONTH_OF_YEAR), 3);
        assertEquals(chrono.getDateValue(dec28, MONTH_OF_YEAR), 13);
        assertEquals(chrono.getDateValue(dec28leap, MONTH_OF_YEAR), 13);
        
        assertEquals(chrono.getDateValue(march15, DAY_OF_MONTH), 15);
        assertEquals(chrono.getDateValue(dec28, DAY_OF_MONTH), 2);
        assertEquals(chrono.getDateValue(dec28leap, DAY_OF_MONTH), 3);
    }

    @Test(groups = "tck")
    public void setMonthsAndDays() {
        assertChronoEquals(2012, 3, 15, march15);
        assertChronoEquals(2011, 13, 2, dec28);
        assertChronoEquals(2012, 13, 3, dec28leap);
    }

    void assertChronoEquals(int year, int month, int day, LocalDate isoDate) {
        LocalDate value = chrono.setDate(LocalDate.now(), YEAR, year);
        value = chrono.setDate(value, DAY_OF_MONTH, day);
        value = chrono.setDate(value, MONTH_OF_YEAR, month);
        assertEquals(isoDate, value);
    }

}
