/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.IllegalCalendarFieldValueException;

/**
 * Defines the valid eras for the Thai Buddhist calendar system.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of a ThaiBuddhistEra
 * instance. Use getValue() instead.</b>
 * <p>
 * ThaiBuddhistEra is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 */
public enum ThaiBuddhistEra implements CalendricalProvider {

    /**
     * The singleton instance for the era before the current one - Before Buddhist -
     * which has the value 0.
     */
    BEFORE_BUDDHIST,
    /**
     * The singleton instance for the current era - Buddhist - which has the value 1.
     */
    BUDDHIST;

    /**
     * Obtains an instance of <code>ThaiBuddhistEra</code> from a value.
     * <p>
     * The current era (from ISO year -543 onwards) has the value 1
     * The previous era has the value 0.
     *
     * @param thaiBuddhistEra  the era to represent, from 0 to 1
     * @return the ThaiBuddhistEra singleton, never null
     * @throws IllegalCalendarFieldValueException if the era is invalid
     */
    public static ThaiBuddhistEra thaiBuddhistEra(int thaiBuddhistEra) {
        switch (thaiBuddhistEra) {
            case 0:
                return BEFORE_BUDDHIST;
            case 1:
                return BUDDHIST;
            default:
                throw new IllegalCalendarFieldValueException(ThaiBuddhistChronology.INSTANCE.era(), thaiBuddhistEra, 0, 1);
        }
    }

    /**
     * Gets the era value.
     * <p>
     * The current era (from ISO year -543 onwards) has the value 1
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
        return new Calendrical(ThaiBuddhistChronology.INSTANCE.era(), getValue());
    }

}
