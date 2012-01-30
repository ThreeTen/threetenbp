/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.CalendricalException;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.IllegalCalendarFieldValueException;

/**
 * Defines the valid eras for the Thai Buddhist calendar system.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a ThaiBuddhistEra
 * instance. Use getValue() instead.</b>
 * <p>
 * ThaiBuddhistEra is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public enum ThaiBuddhistEra implements Calendrical {

    /**
     * The singleton instance for the era before the current one - Before Buddhist -
     * which has the value 0.
     */
    BEFORE_BUDDHIST,
    /**
     * The singleton instance for the current era - Buddhist - which has the value 1.
     */
    BUDDHIST;

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
        return ThaiBuddhistChronology.eraRule();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ThaiBuddhistEra} from a value.
     * <p>
     * The current era (from ISO year -543 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param thaiBuddhistEra  the era to represent, from 0 to 1
     * @return the ThaiBuddhistEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static ThaiBuddhistEra of(int thaiBuddhistEra) {
        switch (thaiBuddhistEra) {
            case 0:
                return BEFORE_BUDDHIST;
            case 1:
                return BUDDHIST;
            default:
                throw new IllegalCalendarFieldValueException(ThaiBuddhistChronology.eraRule(), thaiBuddhistEra);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ThaiBuddhistEra} from a calendrical.
     * <p>
     * This can be used extract the era directly from any implementation
     * of Calendrical, including those in other calendar systems.
     *
     * @param calendrical  the calendrical to extract from, not null
     * @return the ThaiBuddhistEra enum instance, never null
     * @throws CalendricalException if the era cannot be obtained
     */
    public static ThaiBuddhistEra of(Calendrical calendrical) {
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
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        if (ruleToDerive == rule()) {
            return (T) this;
        }
        return CalendricalEngine.derive(ruleToDerive, rule(), ThaiBuddhistChronology.INSTANCE, rule().field(getValue()));
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the era numeric value.
     * <p>
     * The current era (from ISO year -543 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @return the era value, from 0 (BEFORE_BUDDHIST) to 1 (BUDDHIST)
     */
    public int getValue() {
        return ordinal();
    }

}
