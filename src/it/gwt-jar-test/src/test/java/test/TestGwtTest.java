package test;

import com.google.gwt.junit.client.GWTTestCase;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import walkingkooka.j2cl.locale.LocaleAware;

@LocaleAware
public class TestGwtTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
                1,
                1
        );
    }

    public void testLocaleDate() {
        final LocalDate localDate = LocalDate.of(2023,1,23);

        assertEquals(
                23,
                localDate.getDayOfMonth()
        );
    }

    public void testDateTimeFormatterFormat() {
        final LocalDate localDate = LocalDate.of(2023,1,23);

        assertEquals(
                "2023-01-23",
                localDate.format(DateTimeFormatter.ISO_DATE)
        );
    }
}
