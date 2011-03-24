/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.UnsupportedRuleException;

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
 * @author Stephen Colebourne
 */
public enum JapaneseEra implements Calendrical {

    /**
     * The singleton instance for the before Keio era ( - 1865-04-06)
     * which has the value -3.
     */
    UNKNOWN,  // TODO: Remove
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
    private static final LocalDate[] ERA_END_DATES = {
        LocalDate.of(1865, 4, 6), // End of UNKOWN era
        LocalDate.of(1868, 9, 7), // End of KEIO era
        LocalDate.of(1912, 7, 29), // End of MEIJI era
        LocalDate.of(1926, 12, 24), // End of TAISHO era
        LocalDate.of(1989, 1, 7) // End of SHOWA era
        };

    //-----------------------------------------------------------------------
    /**
     * Gets the rule that defines how the era field operates.
     * <p>
     * The rule provides access to the minimum and maximum values, and a
     * generic way to access values within a calendrical.
     *
     * @return the era rule, never null
     */
    public static DateTimeRule rule() {
        return JapaneseChronology.eraRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from a value.
     * <p>
     * The SHOWA era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later era is numbered 2 (HEISEI). Earlier eras are numbered 0 (TAISHO), -1 (MEIJI),
     * -2 (KEIO), -3 (UNKNOWN), only Keio and later eras are supported.
     *
     * @param japaneseEra  the era to represent, from -3 to 2
     * @return the JapaneseEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static JapaneseEra of(int japaneseEra) {
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
                throw new IllegalCalendarFieldValueException(JapaneseChronology.eraRule(), japaneseEra);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code JapaneseEra} from a date.
     *
     * @param date  the date, not null
     * @return the JapaneseEra singleton, never null
     */
    static JapaneseEra from(LocalDate date) {
        for (int i = ERA_END_DATES.length; i > 0; i--) {
            LocalDate eraEndingDate = ERA_END_DATES[i - 1];
            if (date.isAfter(eraEndingDate)) {
                return of(i - 3);
            }
        }
        return UNKNOWN;
    }

    /**
     * Obtains an instance of {@code JapaneseEra} from a calendrical.
     * <p>
     * This can be used extract the era directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the JapaneseEra enum instance, never null
     * @throws UnsupportedRuleException if the era cannot be obtained
     */
    public static JapaneseEra from(Calendrical calendrical) {
        return of(rule().getValueChecked(calendrical).getValidIntValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this instance then
     * {@code null} will be returned.
     *
     * @param rule  the rule to use, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> rule) {
        return rule().deriveValueFor(rule, rule().field(getValue()), this, JapaneseChronology.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era numeric value.
     * <p>
     * The SHOWA era that contains 1970-01-01 (ISO calendar system) has the value 1
     * Later eras are numbered 2 (HEISEI).
     * Earlier eras are numbered 0 (TAISHO), -1 (MEIJI), -2 (KEIO).
     *
     * @return the era value, from -3 (UNKNOWN) to 2 (HEISEI)
     */
    public int getValue() {
        return ordinal() - 3;
    }

    /**
     * Returns year offset in the era.
     *
     * @return year offset, never null
     */
    public int getYearOffset() {
        // TODO: Better javadoc and method name
        if (this == UNKNOWN) {
            return 0;
        }
        LocalDate date = ERA_END_DATES[getValue() + 2];
        return date.getYear() - 1;
    }

}
