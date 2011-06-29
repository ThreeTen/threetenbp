/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.CalendricalException;
import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalNormalizer;
import javax.time.calendar.CalendricalRule;
import javax.time.calendar.DateTimeRule;
import javax.time.calendar.IllegalCalendarFieldValueException;

/**
 * Defines the valid eras for the Hijrah calendar system.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a HijrahEra
 * instance. Use getValue() instead.</b>
 * <p>
 * HijrahEra is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public enum HijrahEra implements Calendrical {

    /**
     * The singleton instance for the era before the current one - Before Hijrah -
     * which has the value 0.
     */
    BEFORE_HIJRAH,
    /**
     * The singleton instance for the current era - Hijrah - which has the value 1.
     */
    HIJRAH;

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
        return HijrahChronology.eraRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HijrahEra} from a value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param hijrahEra  the era to represent, from 0 to 1
     * @return the HijrahEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static HijrahEra of(int hijrahEra) {
        switch (hijrahEra) {
            case 0:
                return BEFORE_HIJRAH;
            case 1:
                return HIJRAH;
            default:
                throw new IllegalCalendarFieldValueException(HijrahChronology.eraRule(), hijrahEra);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code HijrahEra} from a calendrical.
     * <p>
     * This can be used extract the era directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the HijrahEra enum instance, never null
     * @throws CalendricalException if the era cannot be obtained
     */
    public static HijrahEra from(Calendrical calendrical) {
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
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> rule) {
        if (rule == rule()) {
            return (T) this;
        }
        return CalendricalNormalizer.derive(rule, rule(), HijrahChronology.INSTANCE, rule().field(getValue()));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era numeric value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1.
     * The previous era has the value 0.
     *
     * @return the era value, from 0 (BEFORE_HIJRAH) to 1 (HIJRAH)
     */
    public int getValue() {
        return ordinal();
    }

}
