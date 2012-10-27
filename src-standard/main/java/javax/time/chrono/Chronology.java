/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.chrono;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.Clock;
import javax.time.DateTimeException;
import javax.time.LocalDate;
import javax.time.ZoneId;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateTimeField;

/**
 * A standard year-month-day calendar system.
 * <p>
 * The main date and time API is built on the ISO calendar system.
 * This class operates behind the scenes to represent the general concept of a calendar system.
 * For example, the Gregorian, Japanese, Minguo, Thai Buddhist and others.
 * It is built on the generic concepts of year, month and day - subclasses define the
 * meaning of those concepts in the calendar system that they represent.
 * <p>
 * In practical terms, the {@code Chronology} instance also acts as a factory.
 * The {@link #of(String)} method allows an instance to be looked up by identifier.
 * Note that the result will be an instance configured using the default values for that calendar.
 * <p>
 * The {@code Chronology} class provides a set of methods to create {@code ChronoDate} instances.
 * The date classes are used to manipulate specific dates.
 * <ul>
 * <li> {@link #now() now()}
 * <li> {@link #now(Clock) now(clock)}
 * <li> {@link #date(int, int, int) date(year, month, day)}
 * <li> {@link #date(javax.time.chrono.Era, int, int, int) date(era, year, month, day)}
 * <li> {@link #date(javax.time.calendrical.DateTimeAccessor) date(Calendrical)}
 * <li> {@link #dateFromEpochDay(long) dateFromEpochDay(epochDay)}
 * </ul>
 *
 * <h4 id="addcalendars">Adding New Calendars</h4>
 * <p>
 * A new calendar system may be defined and registered with this factory.
 * Implementors must provide a subclass of this class and the matching {@code ChronoDate}.
 * The {@link java.util.ServiceLoader} mechanism is then used to register the calendar.
 * To ensure immutable of dates the subclass of ChronoDate must be
 * final and the instances returned from the factory methods must be of final types.
 * The {@link java.util.ServiceLoader} mechanism is used to register the Chronology subclass.
 * 
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 */
public abstract class Chronology<C extends Chronology<C>> {

    /**
     * Map of available calendars by ID.
     */
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID;
    /**
     * Map of available calendars by calendar type.
     */
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE;
    static {
        // TODO: defer initialization?
        ConcurrentHashMap<String, Chronology> ids = new ConcurrentHashMap<String, Chronology>();
        ConcurrentHashMap<String, Chronology> types = new ConcurrentHashMap<String, Chronology>();
        ServiceLoader<Chronology> loader =  ServiceLoader.load(Chronology.class);
        for (Chronology chronology : loader) {
            ids.putIfAbsent(chronology.getId(), chronology);
            String type = chronology.getCalendarType();
            if (type != null) {
                types.putIfAbsent(type, chronology);
            }
        }
        CHRONOS_BY_ID = ids;
        CHRONOS_BY_TYPE = types;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chronology} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code Chronology}.
     * If the specified date-time object does not have a chronology, {@link ISOChronology} is returned.
     * 
     * @param dateTime  the date-time to convert, not null
     * @return the chronology, not null
     * @throws DateTimeException if unable to convert to an {@code Chronology}
     */
    public static Chronology<?> from(DateTimeAccessor dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        Chronology obj = dateTime.extract(Chronology.class);
        return (obj != null ? obj : ISOChronology.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chronology} from a locale.
     * <p>
     * The locale can be used to identify a calendar.
     * This uses {@link Locale#getUnicodeLocaleType(String)} to obtain the "ca" key
     * to identify the calendar system.
     * <p>
     * If the locale does not contain calendar system information, the standard
     * ISO calendar system is used.
     * 
     * @param locale  the locale to use to obtain the calendar system, not null
     * @return the calendar system associated with the locale, not null
     * @throws DateTimeException if the locale-specified calendar cannot be found
     */
    public static Chronology<?> ofLocale(Locale locale) {
        Objects.requireNonNull(locale, "Locale");
        String type = locale.getUnicodeLocaleType("ca");
        if (type == null) {
            return ISOChronology.INSTANCE;
        } else if ("iso".equals(type) || "iso8601".equals(type)) {
            return ISOChronology.INSTANCE;
        } else {
            Chronology chrono = CHRONOS_BY_TYPE.get(type);
            if (chrono == null) {
                throw new DateTimeException("Unknown calendar system: " + type);
            }
            return chrono;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chronology} from a chronology ID or
     * calendar system type.
     * <p>
     * This returns a chronology based on either the ID or the type.
     * The {@link #getId() chronology ID} uniquely identifies the chronology.
     * The {@link #getCalendarType() calendar system type} is defined by the LDML specification.
     * <p>
     * Since some calendars can be customized, the ID or type typically refers
     * to the default customization. For example, the Gregorian calendar can have multiple
     * cutover dates from the Julian, but the lookup only provides the default cutover date.
     * 
     * @param id  the chronology ID or calendar system type, not null
     * @return the chronology with the identifier requested, not null
     * @throws DateTimeException if the chronology cannot be found
     */
    public static Chronology of(String id) {
        Objects.requireNonNull(id, "id");
        Chronology chrono = CHRONOS_BY_ID.get(id);
        if (chrono != null) {
            return chrono;
        }
        chrono = CHRONOS_BY_TYPE.get(id);
        if (chrono != null) {
            return chrono;
        }
        throw new DateTimeException("Unknown chronology: " + id);
    }

    /**
     * Returns the IDs of the available chronologies.
     * <p>
     * These IDs can be used with {@link #of(String)}.
     * 
     * @return the independent, modifiable set of the available chronology IDs, not null
     */
    public static Set<String> getAvailableIds() {
        return new HashSet<String>(CHRONOS_BY_ID.keySet());
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     */
    protected Chronology() {
        // register the subclass
        CHRONOS_BY_ID.putIfAbsent(this.getId(), this);
        String type = this.getCalendarType();
        if (type != null) {
            CHRONOS_BY_TYPE.putIfAbsent(type, this);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology.
     * <p>
     * The ID uniquely identifies the {@code Chronology}.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * 
     * @return the chronology ID, not null
     * @see #getCalendarType()
     */
    public abstract String getId();

    /**
     * Gets the calendar type of the underlying calendar system.
     * <p>
     * The calendar type is an identifier defined by the
     * <em>Unicode Locale Data Markup Language (LDML)</em> specification.
     * It can be used to lookup the {@code Chronology} using {@link #of(String)}.
     * It can also be used as part of a locale, accessible via
     * {@link Locale#getUnicodeLocaleType(String)} with the key 'ca'.
     * 
     * @return the calendar system type, null if the calendar is not defined by LDML
     * @see #getId()
     */
    public abstract String getCalendarType();

    //-----------------------------------------------------------------------
    /**
     * Creates a date in this chronology from the era, year-of-era, month-of-year and day-of-month fields.
     * 
     * @param era  the era of the correct type for the chronology, not null
     * @param yearOfEra  the chronology year-of-era
     * @param month  the chronology month-of-year
     * @param dayOfMonth  the chronology day-of-month
     * @return the date in this chronology, not null
     */
    public ChronoDate<C> date(Era<C> era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Creates a date in this chronology from the proleptic-year, month-of-year and day-of-month fields.
     * 
     * @param prolepticYear  the chronology proleptic-year
     * @param month  the chronology month-of-year
     * @param dayOfMonth  the chronology day-of-month
     * @return the date in this chronology, not null
     */
    public abstract ChronoDate<C> date(int prolepticYear, int month, int dayOfMonth);

    /**
     * Creates a date in this chronology from the era, year-of-era and day-of-year fields.
     * 
     * @param era  the era of the correct type for the chronology, not null
     * @param yearOfEra  the chronology year-of-era
     * @param dayOfYear  the chronology day-of-year
     * @return the date in this chronology, not null
     */
    public ChronoDate<C> dateFromYearDay(Era<C> era, int yearOfEra, int dayOfYear) {
        return dateFromYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Creates a date in this chronology from the proleptic-year and day-of-year fields.
     * 
     * @param prolepticYear  the chronology proleptic-year
     * @param dayOfYear  the chronology day-of-year
     * @return the date in this chronology, not null
     */
    public abstract ChronoDate<C> dateFromYearDay(int prolepticYear, int dayOfYear);

    /**
     * Creates a date in this chronology from another date-time object.
     * <p>
     * This creates a date in this chronology by extracting the
     * {@link LocalDateTimeField#EPOCH_DAY local epoch-day} field
     * This implementation uses {@link #dateFromEpochDay(long)}.
     * 
     * @param dateTime  the other calendrical, not null
     * @return the date in this chronology, not null
     */
    public ChronoDate date(DateTimeAccessor dateTime) {
        long epochDay = dateTime.getLong(LocalDateTimeField.EPOCH_DAY);
        return dateFromEpochDay(epochDay);
    }

    /**
     * Creates a date in this chronology from the local epoch-day.
     * <p>
     * This creates a date in this chronology based on the specified local epoch-day
     * based on 1970-01-01 (ISO). Since the local epoch-day definition does not change
     * between chronologies it can be used to convert the date.
     * 
     * @param epochDay  the epoch day measured from 1970-01-01 (ISO), not null
     * @return the date in this chronology, not null
     */
    public abstract ChronoDate<C> dateFromEpochDay(long epochDay);

    /**
     * Creates the current date in this chronology from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * <p>
     * This implementation uses {@link #now(Clock)}.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public ChronoDate<C> now() {
        return now(Clock.systemDefaultZone());
    }

    /**
     * Creates the current date in this chronology from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock, not null
     */
    public ChronoDate<C> now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    /**
     * Creates the current date in this chronology from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     * <p>
     * This implementation uses {@link #dateFromEpochDay(long)}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     */
    public ChronoDate<C> now(Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");
        return dateFromEpochDay(LocalDate.now(clock).getLong(LocalDateTimeField.EPOCH_DAY));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * A leap-year must imply a year-length longer than a non leap-year.
     *
     * @param prolepticYear  the proleptic-year to check, not validated for range
     * @return true if the year is a leap year
     */
    public abstract boolean isLeapYear(long prolepticYear);

    /**
     * Calculates the proleptic-year given the era and year-of-era.
     * <p>
     * This combines the era and year-of-era into the single proleptic-year field.
     *
     * @param era  the era of the correct type for the chronology, not null
     * @param yearOfEra  the chronology year-of-era
     * @return the proleptic-year
     * @throws DateTimeException if unable to convert
     */
    public abstract int prolepticYear(Era<C> era, int yearOfEra);

    /**
     * Creates the chronology era object from the numeric value.
     * <p>
     * The era is, conceptually, the largest division of the time-line.
     * Most calendar systems have a single epoch dividing the time-line into two eras.
     * However, some have multiple eras, such as one for the reign of each leader.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p>
     * The era in use at 1970-01-01 must have the value 1.
     * Later eras must have sequentially higher values.
     * Earlier eras must have sequentially lower values.
     * Each chronology must refer to an enum or similar singleton to provide the era values.
     * <p>
     * This method returns the singleton era of the correct type for the specified era value.
     *
     * @param eraValue  the era value
     * @return the calendar system era, not null
     * @throws IllegalArgumentException if the {@code eraValue} is not valid for this chronology.
     */
    public abstract Era<C> eraOf(int eraValue);

    /**
     * Gets the list of Eras for the chronology.
     * @return the list of Eras for the chronology
     */
    public abstract List<Era<C>> eras();


    //-----------------------------------------------------------------------
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * All fields can be expressed as a {@code long} integer.
     * This method returns an object that describes the valid range for that value.
     * <p>
     * Note that the result only describes the minimum and maximum valid values
     * and it is important not to read too much into them. For example, there
     * could be values within the range that are invalid for the field.
     * <p>
     * This method will return a result whether or not the chronology supports the field.
     * 
     * @param field  the field to get the range for, not null
     * @return the range of valid values for the field, not null
     */
    public abstract DateTimeValueRange range(LocalDateTimeField field);

    //-----------------------------------------------------------------------
    /**
     * Checks if this chronology is equal to another chronology.
     * <p>
     * The comparison is based on the entire state of the object.
     * <p>
     * The default implementation compares the ID and class.
     * Subclasses must compare any additional state that they store.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other chronology
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            Chronology other = (Chronology) obj;
            return getId().equals(other.getId());
        }
        return false;
    }

    /**
     * A hash code for this chronology.
     * <p>
     * The default implementation is based on the ID and class.
     * Subclasses should add any additional state that they store.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getClass().hashCode() ^ getId().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this chronology as a {@code String}, using the ID.
     *
     * @return a string representation of this chronology, not null
     */
    @Override
    public String toString() {
        return getId();
    }

}
