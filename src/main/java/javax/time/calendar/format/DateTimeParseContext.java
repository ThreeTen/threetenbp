/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.Calendrical;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.ZoneOffset;

/**
 * Context object used during date and time parsing.
 * <p>
 * This class is mutable and thus not thread-safe.
 * Usage of the class is thread-safe within the Java Time Framework as the
 * framework creates a new instance of the class for each parse.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public final class DateTimeParseContext implements CalendricalProvider {

    /**
     * The date time format symbols, not null.
     */
    private DateTimeFormatSymbols symbols;

    /**
     * The date time map, never null, may be empty.
     */
    private final TreeMap<DateTimeFieldRule, Integer> fieldValueMap = new TreeMap<DateTimeFieldRule, Integer>();
    /**
     * The date, may be null.
     */
    private LocalDate date;
    /**
     * The time, may be null.
     */
    private LocalTime time;
    /**
     * The offset, may be null.
     */
    private ZoneOffset offset;
    /**
     * The zone, may be null.
     */
    private TimeZone zone;

    /**
     * Constructor.
     */
    public DateTimeParseContext() {
        super();
    }

    /**
     * Constructor.
     *
     * @param symbols  the symbols to use during parsing, not null
     */
    public DateTimeParseContext(DateTimeFormatSymbols symbols) {
        super();
        DateTimeFormatterBuilder.checkNotNull(symbols, "symbols");
        this.symbols = symbols;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use for printing and parsing text.
     *
     * @return the locale, never null
     */
    public Locale getLocale() {
        return symbols.getLocale();
    }

    /**
     * Gets the formatting symbols.
     *
     * @return the formatting symbols, never null
     */
    public DateTimeFormatSymbols getSymbols() {
        return symbols;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of fields and their values.
     * <p>
     * The map will never be null, however it may be empty.
     * The values contained in the map might contradict the date or time, or
     * be out of range for the rule.
     * <p>
     * For example, the day of month might be set to 50, or the hour to 1000.
     * The purpose of this class is simply to store the values, not to provide
     * any guarantees as to their validity.
     *
     * @return a modifiable copy of the field-value map, never null
     */
    @SuppressWarnings("unchecked")
    public Map<DateTimeFieldRule, Integer> getFieldValueMap() {
        return (Map<DateTimeFieldRule, Integer>) fieldValueMap.clone();
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value returned might contradict the date or time, or be out of
     * range for the rule.
     * <p>
     * For example, the day of month might be set to 50, or the hour to 1000.
     * The purpose of this class is simply to store the values, not to provide
     * any guarantees as to their validity.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getFieldValueMapValue(DateTimeFieldRule rule) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        Integer value = fieldValueMap.get(rule);
        if (value != null) {
            return value;
        }
        throw new UnsupportedCalendarFieldException(rule, "Calendrical");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the optional local date, such as '2007-12-03'.
     * This method will return null if the date is null.
     *
     * @return the date, may be null
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the optional local time, such as '10:15:30'.
     * This method will return null if the time is null.
     *
     * @return the time, may be null
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Gets the optional time zone offset, such as '+02:00'.
     * This method will return null if the offset is null.
     *
     * @return the offset, may be null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Gets the optional time zone rules, such as 'Europe/Paris'.
     * This method will return null if the zone is null.
     *
     * @return the zone, may be null
     */
    public TimeZone getZone() {
        return zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the value associated with the specified field rule.
     *
     * @param fieldRule  the field to set in the field-value map, not null
     * @param value  the value to set in the field-value map
     */
    public void setFieldValue(DateTimeFieldRule fieldRule, int value) {
        if (fieldRule == null) {
            throw new NullPointerException("The field rule must not be null");
        }
        fieldValueMap.put(fieldRule, value);
    }

    /**
     * Sets the parsed date.
     *
     * @param date  the date to store, may be null
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Sets the parsed time.
     *
     * @param time  the time to store, may be null
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Sets the parsed offset.
     *
     * @param offset  the zone offset to store, may be null
     */
    public void setOffset(ZoneOffset offset) {
        this.offset = offset;
    }

    /**
     * Sets the parsed zone.
     *
     * @param zone  the zone to store, may be null
     */
    public void setZone(TimeZone zone) {
        this.zone = zone;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a Calendrical with the same fields.
     *
     * @return a new Calendrical with the parsed fields, never null
     */
    public Calendrical toCalendrical() {
        return new Calendrical(fieldValueMap, null, null, offset, zone);
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the calendrical as a <code>String</code>.
     * <p>
     * The output will use the following format:
     * <ul>
     * <li>Field-Value map, followed by space if non-empty</li>
     * <li>Date</li>
     * <li>Time, prefixed by 'T' if non-null</li>
     * <li>Offset</li>
     * <li>Zone, prefixed by a space if non-null</li>
     * </ul>
     * If an instance of LocalDate, LocalTime, LocalDateTime, OffsetDate, OffsetTime,
     * OffsetDateTime or ZonedDateTime is converted to a Calendrical then the
     * toString output will remain the same.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (getFieldValueMap().size() > 0) {
            buf.append(getFieldValueMap());
            if (date != null || time != null || offset != null) {
                buf.append(' ');
            }
        }
        if (date != null) {
            buf.append(date);
        }
        if (time != null) {
            buf.append('T').append(time);
        }
        if (offset != null) {
            buf.append(offset);
        }
        if (zone != null) {
            if (date != null || time != null || offset != null) {
                buf.append(' ');
            }
            buf.append(zone);
        }
        return buf.toString();
    }

}
