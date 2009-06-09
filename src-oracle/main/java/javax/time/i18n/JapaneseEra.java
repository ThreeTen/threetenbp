/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateProvider;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;

/**
 * Defines the valid eras for the Japanese Imperial calendar system.
 * Only Keio (1865-04-07 - 1868-09-07) and later eras are supported.
 * Older eras are recognized as unknown era, and the year of era of
 * unknown era is Gregorian year.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a JapaneseEra
 * instance. Use getValue() instead.</b>
 * <p>
 * JapaneseEra is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 */
public enum JapaneseEra implements CalendricalProvider {

    /**
     * The singleton instance for the before Keio era ( - 1865-04-06)
     * which has the value -3.
     */
    UNKNOWN,
    /**
     * The singleton instance for the Keio era (1865-04-07 - 1868-09-07)
     * which has the value -2.
     */
    KEIO,
    /**
     * The singleton instance for the Meiji era (1868-09-08 - 1912-07-29)
     * which has the value -1.
     */
    MEIJI,
    /**
     * The singleton instance for the Taisho era (1912-07-30 - 1926-12-24)
     * which has the value 0.
     */
    TAISHO,
    /**
     * The singleton instance for the Showa era (1926-12-25 - 1989-01-07)
     * which has the value 1.
     */
    SHOWA,
    /**
     * The singleton instance for the Heisei era (1989-01-08 - current)
     * which has the value 2.
     */
    HEISEI;

    /**
     * Arrays containing the end date of era as LocalDate.
     */
    private static final LocalDate ERA_END_DATES[] = {
        LocalDate.date(1865, 4, 6), // End of UNKOWN era
        LocalDate.date(1868, 9, 7), // End of KEIO era
        LocalDate.date(1912, 7, 29), // End of MEIJI era
        LocalDate.date(1926, 12, 24), // End of TAISHO era
        LocalDate.date(1989, 1, 7) // End of SHOWA era
        };
    
    /**
     * Obtains an instance of <code>JapaneseEra</code> from a value.
     * <p>
     * The SHOWA era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later era is numbered 2 (HEISEI). Earlier eras are numbered 0 (TAISHO), -1 (MEIJI),
     * -2 (KEIO), -3 (UNKNOWN), only Keio and later eras are supported.
     *
     * @param japaneseEra  the era to represent, from -3 to 2
     * @return the JapaneseEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static JapaneseEra japaneseEra(int japaneseEra) {
        switch (japaneseEra) {
            case -3:
                return UNKNOWN;
            case -2:
                return KEIO;
            case -1:
                return MEIJI;
            case 0:
                return TAISHO;
            case 1:
                return SHOWA;
            case 2:
                return HEISEI;
            default:
                throw new IllegalCalendarFieldValueException(JapaneseChronology.INSTANCE.era(), japaneseEra, 0, 1);
        }
    }
    
    /**
     * Obtains an instance of <code>JapaneseEra</code> from a date provider.
     * <p>
     * This can be used extract an era directly from any implementation
     * of DateProvider, including those in other calendar systems.
     *
     * @param dateProvider  the date provider to use, not null
     * @return the JapaneseEra singleton, never null
     */
    public static JapaneseEra japaneseEra(DateProvider dateProvider) {
        return getEra(dateProvider.toLocalDate());
    }
    /**
     * Gets the era value.
     * <p>
     * The SHOWA era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later eras are numbered 2 (HEISEI). Earlier eras are numbered 0 (TAISHO), -1 (MEIJI), -2 (KEIO).
     *
     * @return the era value, from -2 to 2
     */
    public int getValue() {
        return ordinal() - 3;
    }

    /**
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return new Calendrical(JapaneseChronology.INSTANCE.era(), getValue());
    }

    /**
     * Returns year offset in the era.
     *
     * @return year offset, never null
     */
    public int getYearOffset() {
        if (getValue() == UNKNOWN.getValue()) {
            return 0;
        }
        LocalDate date = ERA_END_DATES[getValue() + 2];
        return date.getYear() - 1;
    }
    
    /**
     * Return JapaneseEra from LocalDate object.
     *
     * @param date  the date, validated in range, validated not null
     * @return the JapaneseEra singleton, never null
     */
    private static JapaneseEra getEra(LocalDate date) {
        for (int i = ERA_END_DATES.length; i > 0; i--) {
            LocalDate eraEndingDate = ERA_END_DATES[i - 1];
            if (date.isAfter(eraEndingDate)) {
                return japaneseEra(i - 3);
            }
        }
        return UNKNOWN;
    }
}
