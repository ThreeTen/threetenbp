/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
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
 */
public enum HijrahEra implements CalendricalProvider {

    /**
     * The singleton instance for the era before the current one - Before Hijrah -
     * which has the value 0.
     */
    BEFORE_HIJRAH,
    /**
     * The singleton instance for the current era - Hijrah - which has the value 1.
     */
    HIJRAH;

    /**
     * Obtains an instance of <code>HijrahEra</code> from a value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param hijrahEra  the era to represent, from 0 to 1
     * @return the HijrahEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static HijrahEra hijrahEra(int hijrahEra) {
        switch (hijrahEra) {
            case 0:
                return BEFORE_HIJRAH;
            case 1:
                return HIJRAH;
            default:
                throw new IllegalCalendarFieldValueException(HijrahChronology.INSTANCE.era(), hijrahEra, 0, 1);
        }
    }

    /**
     * Gets the era value.
     * <p>
     * The current era (from ISO date 622-06-19 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @return the era value, from 0 to 1
     */
    public int getValue() {
        return ordinal();
    }

    /**
     * Converts this field to a <code>Calendrical</code>.
     *
     * @return the calendrical representation for this instance, never null
     */
    public Calendrical toCalendrical() {
        return new Calendrical(HijrahChronology.INSTANCE.era(), getValue());
    }

}
