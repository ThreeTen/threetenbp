package javax.time.builder;

import static javax.time.MonthOfYear.APRIL;
import static javax.time.MonthOfYear.DECEMBER;
import static javax.time.MonthOfYear.JANUARY;
import static javax.time.MonthOfYear.MARCH;
import static javax.time.builder.QuarterYearDateTimeField.DAY_OF_QUARTER;
import static javax.time.builder.StandardDateTimeField.DAY_OF_MONTH;
import static javax.time.builder.StandardDateTimeField.DAY_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.EPOCH_DAY;
import static javax.time.builder.StandardDateTimeField.MONTH_OF_YEAR;
import static javax.time.builder.StandardDateTimeField.YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.time.CalendricalException;
import javax.time.LocalDate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestDateTimeBuilder {
    
    ISOChrono iso = ISOChrono.INSTANCE;
    CopticChrono coptic = CopticChrono.INSTANCE;
    DateTimeBuilder forNulls = DateTimeBuilder.of();

    //-----------------------------------------------------------------------
    // builders
    //-----------------------------------------------------------------------
    @DataProvider(name="isodates")
    Object[][] dateEquivalencesProvider() {
        return new Object[][]{
            { 2012, 3, 15,  LocalDate.of(2012, MARCH, 15),},
            { 2011, 12, 28, LocalDate.of(2011, DECEMBER, 28),},
            { 1, 1, 1,      LocalDate.of(1, JANUARY, 1),},
        };
    }
    
    @Test(dataProvider="isodates", groups = "tck")
    public void buildsTo(int year, int month, int day, LocalDate result) {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(YEAR, year);
        builder.add(MONTH_OF_YEAR, month);
        builder.add(DAY_OF_MONTH, day);
        assertEquals(builder.buildLocalDate(), result);
    }

    @Test(dataProvider="isodates", groups = "tck")
    public void reverseBuildsTo(int year, int month, int day, LocalDate result) {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(DAY_OF_MONTH, day);
        builder.add(MONTH_OF_YEAR, month);
        builder.add(YEAR, year);
        assertEquals(builder.buildLocalDate(), result);
    }
    
    @Test(dataProvider="isodates", groups = "tck")
    public void buildsFromEpochDay(int year, int month, int day, LocalDate result) {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(EPOCH_DAY, result.toEpochDay());
        assertEquals(builder.buildLocalDate(), result);
    }
    
    @Test(dataProvider="isodates", groups = "tck")
    public void buildsFromDayOfYear(int year, int month, int day, LocalDate result) {
        DateTimeBuilder builder = DateTimeBuilder.of();
        long dayOfYear = iso.getDateValue(result, DAY_OF_YEAR);
        builder.add(YEAR, year);
        builder.add(DAY_OF_YEAR, dayOfYear);
        assertEquals(builder.buildLocalDate(), result);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void buildLocalDate_null() {
        forNulls.buildLocalDate(null);
    }

    //-----------------------------------------------------------------------
    // Custom chronology builders
    //-----------------------------------------------------------------------
    
    @DataProvider(name="chronoDates")
    Object[][] chronologyDateProvider() {
        return new Object[][]{
            { 1728, 8, 6, LocalDate.of(2012, APRIL, 14),     coptic },
            { 1729, 4, 19, LocalDate.of(2012, DECEMBER, 28), coptic },
        };
    }
    
    @Test(dataProvider="chronoDates", groups = "tck")
    public void buildsFromChrono(int year, int month, int day, LocalDate result, Chrono chrono) {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(DAY_OF_MONTH, day);
        builder.add(MONTH_OF_YEAR, month);
        builder.add(YEAR, year);
        assertEquals(builder.buildLocalDate(chrono), result);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void buildChronoDateView_null() {
        forNulls.buildChronoDateView(null);
    }
    
    //-----------------------------------------------------------------------
    // Contains Value
    //-----------------------------------------------------------------------
    
    @Test(groups = "tck")
    public void containsValue_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(YEAR, 1920);
        assertTrue(builder.containsValue(YEAR));
    }
    
    @Test(groups = "tck")
    public void containsValue_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(DAY_OF_QUARTER, 2);
        assertTrue(builder.containsValue(DAY_OF_QUARTER));
    }
    
    @Test(groups = "tck")
    public void containsValue_false_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        assertFalse(builder.containsValue(YEAR));
    }
    
    @Test(groups = "tck")
    public void containsValue_false_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        assertFalse(builder.containsValue(DAY_OF_QUARTER));
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void containsValue_null() {
        forNulls.containsValue(null);
    }

    //-----------------------------------------------------------------------
    // Get Value
    //-----------------------------------------------------------------------
    
    @Test(groups = "tck")
    public void getValue_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(YEAR, 1920);
        assertEquals(builder.getValue(YEAR), 1920);
    }
    
    @Test(groups = "tck")
    public void getValue_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(DAY_OF_QUARTER, 2);
        assertEquals(builder.getValue(DAY_OF_QUARTER), 2);
    }
    
    @Test(expectedExceptions=CalendricalException.class, groups = "tck")
    public void getValue_empty_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.getValue(YEAR);
    }
    
    @Test(expectedExceptions=CalendricalException.class, groups = "tck")
    public void getValue_empty_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.getValue(DAY_OF_QUARTER);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void getValue_null() {
        forNulls.getValue(null);
    }

    //-----------------------------------------------------------------------
    // Add Value
    //-----------------------------------------------------------------------
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void add_null() {
        forNulls.add(null, 1);
    }
    
    //-----------------------------------------------------------------------
    // Remove Value
    //-----------------------------------------------------------------------
    
    @Test(groups = "tck")
    public void remove_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(YEAR, 1920);
        assertEquals(builder.remove(YEAR), builder);
        assertFalse(builder.containsValue(YEAR));
    }
    
    @Test(groups = "tck")
    public void remove_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        builder.add(DAY_OF_QUARTER, 2);
        assertEquals(builder.remove(DAY_OF_QUARTER), builder);
        assertFalse(builder.containsValue(DAY_OF_QUARTER));
    }
    
    @Test(groups = "tck")
    public void remove_empty_standard() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        assertEquals(builder.remove(YEAR), builder);
    }
    
    @Test(groups = "tck")
    public void remove_empty_custom() {
        DateTimeBuilder builder = DateTimeBuilder.of();
        assertEquals(builder.remove(DAY_OF_QUARTER), builder);
    }
    
    @Test(expectedExceptions=NullPointerException.class, groups = "tck")
    public void remove_null() {
        forNulls.remove(null);
    }

}
