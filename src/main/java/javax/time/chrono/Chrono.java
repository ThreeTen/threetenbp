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
import javax.time.LocalTime;
import javax.time.ZoneId;
import javax.time.calendrical.ChronoField;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAccessor.Query;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.format.DateTimeFormatterBuilder;
import javax.time.format.TextStyle;
import javax.time.jdk8.DefaultInterfaceDateTimeAccessor;

/**
 * A calendar system, defining a set of human-scale date fields.
 * <p>
 * The main date and time API is built on the ISO calendar system.
 * This class operates behind the scenes to represent the general concept of a calendar system.
 * For example, the Gregorian, Japanese, Minguo, Thai Buddhist and others.
 * <p>
 * Most other calendar systems also operate on the shared concepts of year, month and day,
 * linked to the cycles of the Earth around the Sun, and the Moon around the Earth.
 * These shared concepts are defined by {@link ChronoField} and are availalbe
 * for use by any {@code Chrono} implementation:
 * <pre>
 *   LocalDate isoDate = ...
 *   ChronoLocalDate&lt;ThaiBuddhistChrono&gt; minguoDate = ...
 *   int isoYear = isoDate.get(ChronoField.YEAR);
 *   int thaiYear = thaiDate.get(ChronoField.YEAR);
 * </pre>
 * As shown, although the date objects are in different calendar systems, represented by different
 * {@code Chrono} instances, both can be queried using the same constant on {@code ChronoField}.
 * For a full discussion of the implications of this, see {@link ChronoLocalDate}.
 * In general, the advice is to use the known ISO-based {@code LocalDate}, rather than
 * {@code ChronoLocalDate}.
 * <p>
 * While a {@code Chrono} object typically uses {@code ChronoField} and is based on
 * an era, year-of-era, month-of-year, day-of-month model of a date, this is not required.
 * A {@code Chrono} instance may represent a totally different kind of calendar system,
 * such as the Mayan.
 * <p>
 * In practical terms, the {@code Chrono} instance also acts as a factory.
 * The {@link #of(String)} method allows an instance to be looked up by identifier,
 * while the {@link #ofLocale(Locale)} method allows lookup by locale.
 * <p>
 * The {@code Chrono} instance provides a set of methods to create {@code ChronoLocalDate} instances.
 * The date classes are used to manipulate specific dates.
 * <p><ul>
 * <li> {@link #dateNow() dateNow()}
 * <li> {@link #dateNow(Clock) dateNow(clock)}
 * <li> {@link #dateNow(ZoneId) dateNow(zone)}
 * <li> {@link #date(int, int, int) date(yearProleptic, month, day)}
 * <li> {@link #date(javax.time.chrono.Era, int, int, int) date(era, yearOfEra, month, day)}
 * <li> {@link #dateFromYearDay(int, int) date(yearProleptic, dayOfYear)}
 * <li> {@link #dateFromYearDay(Era, int, int) date(era, yearOfEra, dayOfYear)}
 * <li> {@link #date(DateTimeAccessor) date(DateTimeAccessor)}
 * </ul><p>
 *
 * <h4 id="addcalendars">Adding New Calendars</h4>
 * The set of available chronologies can be extended by applications.
 * Adding a new calendar system requires the writing of an implementation of
 * {@code Chrono}, {@code ChronoLocalDate} and {@code Era}.
 * The majority of the logic specific to the calendar system will be in
 * {@code ChronoLocalDate}. The {@code Chrono} subclass acts as a factory.
 * <p>
 * To permit the discovery of additional chronologies, the {@link java.util.ServiceLoader ServiceLoader}
 * is used. A file must be added to the {@code META-INF/services} directory with the
 * name 'javax.time.chrono.Chrono' listing the implementation classes.
 * See the service loader for more details on service loading.
 * <p>
 * Each chronology must define a chronology ID that is unique within the system.
 * If the chronology represents a calendar system defined by the
 * <em>Unicode Locale Data Markup Language (LDML)</em> specification then that
 * calendar type should also be specified.
 *
 * <h4>Implementation notes</h4>
 * This interface must be implemented with care to ensure other classes operate correctly.
 * All implementations that can be instantiated must be final, immutable and thread-safe.
 * Subclasses should be Serializable wherever possible.
 *
 * @param <C> the type of the implementing subclass
 */
public abstract class Chrono<C extends Chrono<C>> implements Comparable<Chrono<?>> {

    /**
     * Map of available calendars by ID.
     */
    private static final ConcurrentHashMap<String, Chrono<?>> CHRONOS_BY_ID;
    /**
     * Map of available calendars by calendar type.
     */
    private static final ConcurrentHashMap<String, Chrono<?>> CHRONOS_BY_TYPE;
    static {
        // TODO: defer initialization?
        ConcurrentHashMap<String, Chrono<?>> ids = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Chrono<?>> types = new ConcurrentHashMap<>();
        @SuppressWarnings("rawtypes")
        ServiceLoader<Chrono> loader =  ServiceLoader.load(Chrono.class);
        for (Chrono<?> chrono : loader) {
            ids.putIfAbsent(chrono.getId(), chrono);
            String type = chrono.getCalendarType();
            if (type != null) {
                types.putIfAbsent(type, chrono);
            }
        }
        CHRONOS_BY_ID = ids;
        CHRONOS_BY_TYPE = types;
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chrono} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code Chrono}.
     * If the specified date-time object does not have a chronology, {@link ISOChrono} is returned.
     *
     * @param dateTime  the date-time to convert, not null
     * @return the chronology, not null
     * @throws DateTimeException if unable to convert to an {@code Chrono}
     */
    public static Chrono<?> from(DateTimeAccessor dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        Chrono<?> obj = dateTime.query(Query.CHRONO);
        return (obj != null ? obj : ISOChrono.INSTANCE);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chrono} from a locale.
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
    public static Chrono<?> ofLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        String type = locale.getUnicodeLocaleType("ca");
        if (type == null) {
            return ISOChrono.INSTANCE;
        } else if ("iso".equals(type) || "iso8601".equals(type)) {
            return ISOChrono.INSTANCE;
        } else {
            Chrono<?> chrono = CHRONOS_BY_TYPE.get(type);
            if (chrono == null) {
                throw new DateTimeException("Unknown calendar system: " + type);
            }
            return chrono;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code Chrono} from a chronology ID or
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
    public static Chrono<?> of(String id) {
        Chrono<?> chrono = CHRONOS_BY_ID.get(id);
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
     * Returns the available chronologies.
     * <p>
     * Each returned {@code Chrono} is available for use in the system.
     *
     * @return the independent, modifiable set of the available chronology IDs, not null
     */
    public static Set<Chrono<?>> getAvailableChronologies() {
        return new HashSet<>(CHRONOS_BY_ID.values());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new {@code ChronoLocalDateTime} with the {@code date} and {@code time}.
     * <p>
     * This method is intended for chronology implementations. It uses a standard
     * local date-time implementation that can be shared for all chronologies.
     *
     * @param <R>  the chronology of the date
     * @param date  the date, not null
     * @param time  the time, not null
     * @return a new {@code ChronoLocalDateTime} with the {@code date} and {@code time}, not null
     */
    public static <R extends Chrono<R>> ChronoLocalDateTime<R> dateTime(ChronoLocalDate<R> date, LocalTime time) {
        return ChronoDateTimeImpl.of(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance.
     */
    protected Chrono() {
        // register the subclass
        CHRONOS_BY_ID.putIfAbsent(this.getId(), this);
        String type = this.getCalendarType();
        if (type != null) {
            CHRONOS_BY_TYPE.putIfAbsent(type, this);
        }
    }

    /**
     * Casts the {@code DateTime} to {@code ChronoLocalDate} with the same chronology.
     *
     * @param dateTime  a date-time to cast, not null
     * @return the date-time checked and cast to {@code ChronoLocalDate}, not null
     * @throws ClassCastException if the date-time cannot be cast to ChronoLocalDate
     *  or the chronology is not equal this Chrono
     */
    public /* protected */ ChronoLocalDate<C> ensureChronoLocalDate(DateTime dateTime) {
        ChronoLocalDate<C> other = (ChronoLocalDate<C>) dateTime;
        if (this.equals(other.getChrono()) == false) {
            throw new ClassCastException("Chrono mismatch, expected: " + getId() + ", actual: " + other.getChrono().getId());
        }
        return other;
    }

    /**
     * Casts the {@code DateTime} to {@code ChronoLocalDateTime} with the same chronology.
     *
     * @param dateTime   a date-time to cast, not null
     * @return the date-time checked and cast to {@code ChronoLocalDateTime}, not null
     * @throws ClassCastException if the date-time cannot be cast to ChronoDateTimeImpl
     *  or the chronology is not equal this Chrono
     */
    public /* package-scoped */ ChronoDateTimeImpl<C> ensureChronoLocalDateTime(DateTime dateTime) {
        ChronoDateTimeImpl<C> other = (ChronoDateTimeImpl<C>) dateTime;
        if (this.equals(other.getDate().getChrono()) == false) {
            throw new ClassCastException("Chrono mismatch, required: " + getId()
                    + ", supplied: " + other.getDate().getChrono().getId());
        }
        return other;
    }

    /**
     * Casts the {@code DateTime} to {@code ChronoOffsetDateTime} with the same chronology.
     *
     * @param dateTime  a date-time to cast, not null
     * @return the date-time checked and cast to {@code ChronoOffsetDateTime}, not null
     * @throws ClassCastException if the date-time cannot be cast to ChronoOffsetDateTimeImpl
     *  or the chronology is not equal this Chrono
     */
    public /* package-scoped */ ChronoOffsetDateTimeImpl<C> ensureChronoOffsetDateTime(DateTime dateTime) {
        ChronoOffsetDateTimeImpl<C> other = (ChronoOffsetDateTimeImpl<C>) dateTime;
        if (this.equals(other.getDate().getChrono()) == false) {
            throw new ClassCastException("Chrono mismatch, required: " + getId()
                    + ", supplied: " + other.getDate().getChrono().getId());
        }
        return other;
    }

    /**
     * Casts the {@code DateTime} to {@code ChronoZonedDateTimeImpl} with the same chronology.
     *
     * @param dateTime  a date-time to cast, not null
     * @return the date-time checked and cast to {@code ChronoZonedDateTimeImpl}, not null
     * @throws ClassCastException if the date-time cannot be cast to ChronoZonedDateTimeImpl
     *  or the chronology is not equal this Chrono
     */
    public /* package-scoped */ ChronoZonedDateTimeImpl<C> ensureChronoZonedDateTime(DateTime dateTime) {
        ChronoZonedDateTimeImpl<C> other = (ChronoZonedDateTimeImpl<C>) dateTime;
        if (this.equals(other.getDate().getChrono()) == false) {
            throw new ClassCastException("Chrono mismatch, required: " + getId()
                    + ", supplied: " + other.getDate().getChrono().getId());
        }
        return other;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ID of the chronology.
     * <p>
     * The ID uniquely identifies the {@code Chrono}.
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
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
     * It can be used to lookup the {@code Chrono} using {@link #of(String)}.
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
     * @throws DateTimeException if unable to create the date
     */
    public ChronoLocalDate<C> date(Era<C> era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    /**
     * Creates a date in this chronology from the proleptic-year, month-of-year and day-of-month fields.
     *
     * @param prolepticYear  the chronology proleptic-year
     * @param month  the chronology month-of-year
     * @param dayOfMonth  the chronology day-of-month
     * @return the date in this chronology, not null
     * @throws DateTimeException if unable to create the date
     */
    public abstract ChronoLocalDate<C> date(int prolepticYear, int month, int dayOfMonth);

    /**
     * Creates a date in this chronology from the era, year-of-era and day-of-year fields.
     *
     * @param era  the era of the correct type for the chronology, not null
     * @param yearOfEra  the chronology year-of-era
     * @param dayOfYear  the chronology day-of-year
     * @return the date in this chronology, not null
     * @throws DateTimeException if unable to create the date
     */
    public ChronoLocalDate<C> dateFromYearDay(Era<C> era, int yearOfEra, int dayOfYear) {
        return dateFromYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    /**
     * Creates a date in this chronology from the proleptic-year and day-of-year fields.
     *
     * @param prolepticYear  the chronology proleptic-year
     * @param dayOfYear  the chronology day-of-year
     * @return the date in this chronology, not null
     * @throws DateTimeException if unable to create the date
     */
    public abstract ChronoLocalDate<C> dateFromYearDay(int prolepticYear, int dayOfYear);

    /**
     * Creates a date in this chronology from another date-time object.
     * <p>
     * This creates a date in this chronology by extracting the
     * {@link ChronoField#EPOCH_DAY local epoch-day} field.
     *
     * @param dateTime  the date-time object to convert, not null
     * @return the date in this chronology, not null
     * @throws DateTimeException if unable to create the date
     */
    public abstract ChronoLocalDate<C> date(DateTimeAccessor dateTime);

    /**
     * Creates the current date in this chronology from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     * <p>
     * This implementation uses {@link #dateNow(Clock)}.
     *
     * @return the current date using the system clock and default time-zone, not null
     * @throws DateTimeException if unable to create the date
     */
    public ChronoLocalDate<C> dateNow() {
        return dateNow(Clock.systemDefaultZone());
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
     * @throws DateTimeException if unable to create the date
     */
    public ChronoLocalDate<C> dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    /**
     * Creates the current date in this chronology from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if unable to create the date
     */
    public ChronoLocalDate<C> dateNow(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return date(LocalDate.now(clock));
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified year is a leap year.
     * <p>
     * A leap-year is a year of a longer length than normal.
     * The exact meaning is determined by the chronology according to the following constraints.
     * <p><ul>
     * <li>a leap-year must imply a year-length longer than a non leap-year.
     * <li>a chronology that does not support the concept of a year must return false.
     * </ul><p>
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
     * @throws DateTimeException if unable to create the era
     */
    public abstract Era<C> eraOf(int eraValue);

    /**
     * Gets the list of eras for the chronology.
     * <p>
     * Most calendar systems have an era, within which the year has meaning.
     * If the calendar system does not support the concept of eras, an empty
     * list must be returned.
     *
     * @return the list of eras for the chronology, may be immutable, not null
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
     * @throws DateTimeException if the range for the field cannot be obtained
     */
    public abstract DateTimeValueRange range(ChronoField field);

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of this chronology.
     * <p>
     * This returns the textual name used to identify the chronology.
     * The parameters control the length of the returned text and the locale.
     *
     * @param style  the length of the text required, not null
     * @param locale  the locale to use, not null
     * @return the text value of the chronology, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendChronoText(style).toFormatter(locale).print(new DefaultInterfaceDateTimeAccessor() {
            @Override
            public boolean isSupported(DateTimeField field) {
                return false;
            }
            @Override
            public long getLong(DateTimeField field) {
                throw new DateTimeException("Unsupported field: " + field);
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R> R query(Query<R> query) {
                if (query == Query.CHRONO) {
                    return (R) this;
                }
                return super.query(query);
            }
        });
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this chronology to another chronology.
     * <p>
     * The comparison order first by the chronology ID string, then by any
     * additional information specific to the subclass.
     * It is "consistent with equals", as defined by {@link Comparable}.
     * <p>
     * The default implementation compares the chronology ID.
     * Subclasses must compare any additional state that they store.
     *
     * @param other  the other chronology to compare to, not null
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(Chrono<?> other) {
        return getId().compareTo(other.getId());
    }

    /**
     * Checks if this chronology is equal to another chronology.
     * <p>
     * The comparison is based on the entire state of the object.
     * <p>
     * The default implementation checks the type and calls {@link #compareTo(Chrono)}.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other chronology
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof Chrono) {
            return compareTo((Chrono<?>) obj) == 0;
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
