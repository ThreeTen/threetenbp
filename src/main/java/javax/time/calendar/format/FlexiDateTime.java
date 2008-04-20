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

import java.util.Map;
import java.util.TreeMap;

import javax.time.calendar.Calendrical;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetDate;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.OffsetTime;
import javax.time.calendar.TimeFieldRule;
import javax.time.calendar.TimeZone;
import javax.time.calendar.UnsupportedCalendarFieldException;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.ZonedDateTime;

/**
 * Flexible date time implementation that can hold a date, time, date-time or
 * parts of a date-time, with optional offset and time zone.
 * <p>
 * This class is useful when you don't know the kind of date-time object that
 * you will receive, just that it will be some form of date-time. The various
 * fields of a flexible date-time can be setup to be invalid, thus instances
 * must be treated with care.
 *
 * @author Stephen Colebourne
 */
public final class FlexiDateTime implements Calendrical {

    /**
     * The date time map.
     */
    private Map<TimeFieldRule, Integer> fieldValueMap = new TreeMap<TimeFieldRule, Integer>();
    /**
     * The date.
     */
    private LocalDate date;
    /**
     * The time.
     */
    private LocalTime time;
    /**
     * The offset.
     */
    private ZoneOffset offset;
    /**
     * The zone.
     */
    private TimeZone zone;

    /**
     * Constructor creating from the four main objects.
     *
     * @param date  the optional local date, such as '2007-12-03', may be null
     * @param time  the optional local time, such as '10:15:30', may be null
     * @param offset  the optional time zone offset, such as '+02:00', may be null
     * @param zone  the optional time zone rules, such as 'Europe/Paris', may be null
     */
    public FlexiDateTime(LocalDate date, LocalTime time, ZoneOffset offset, TimeZone zone) {
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
    }

    /**
     * Constructor creating from a rule-value pair.
     *
     * @param rule  the rule, not null
     * @param value  the field value
     */
    public FlexiDateTime(TimeFieldRule rule, int value) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        fieldValueMap.put(rule, value);
    }

    /**
     * Constructor creating from two rule-value pairs.
     *
     * @param rule1  the first rule, not null
     * @param value1  the first field value
     * @param rule2  the second rule, not null
     * @param value2  the second field value
     */
    public FlexiDateTime(TimeFieldRule rule1, int value1, TimeFieldRule rule2, int value2) {
        if (rule1 == null || rule2 == null) {
            throw new NullPointerException("The rule2 must not be null");
        }
        fieldValueMap.put(rule1, value1);
        fieldValueMap.put(rule2, value2);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value returned might contradict the date or time, or be out of
     * range for the rule.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getValue(TimeFieldRule rule) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        return rule.getValue(this);
    }

    //-----------------------------------------------------------------------
    /**
     * The optional set of specific fields and values.
     * <p>
     * The map will never be null, however it may well be empty.
     * The values contained in the map might contradict the date or time, or
     * be out of range for the rule.
     *
     * @return the field-value map, never null
     */
    public Map<TimeFieldRule, Integer> getFieldValueMap() {
        return fieldValueMap;
    }

    /**
     * Gets the value for the specified field throwing an exception if the
     * field is not in the field-value map.
     * <p>
     * The value returned might contradict the date or time, or be out of
     * range for the rule.
     *
     * @param rule  the rule to query from the map, not null
     * @return the value mapped to the specified field
     * @throws UnsupportedCalendarFieldException if the field is not in the map
     */
    public int getFieldValueMapValue(TimeFieldRule rule) {
        if (rule == null) {
            throw new NullPointerException("The rule must not be null");
        }
        Integer value = fieldValueMap.get(rule);
        if (value != null) {
            return value;
        }
        throw new UnsupportedCalendarFieldException(rule, "FlexiDateTime");
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

    //-----------------------------------------------------------------------
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
     * Converts this object to a LocalDate.
     * This method will fail if the date is null.
     *
     * @return the LocalDate, never null
     * @throws UnsupportedOperationException if the date is null
     */
    public LocalDate toLocalDate() {
        if (date == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to LocalDate because the date is null");
        }
        return date;
    }

    /**
     * Converts this object to a LocalTime.
     * This method will fail if the time is null.
     *
     * @return the LocalTime, never null
     * @throws UnsupportedOperationException if the time is null
     */
    public LocalTime toLocalTime() {
        if (time == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to LocalTime because the time is null");
        }
        return time;
    }

    /**
     * Converts this object to a LocalDateTime.
     * This method will fail if the date or time is null.
     *
     * @return the LocalDateTime, never null
     * @throws UnsupportedOperationException if the date or time is null
     */
    public LocalDateTime toLocalDateTime() {
        if (date == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to LocalDateTime because the date is null");
        }
        if (time == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to LocalDateTime because the time is null");
        }
        return LocalDateTime.dateTime(date, time);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to an OffsetDate.
     * This method will fail if the date or offset is null.
     *
     * @return the OffsetDate, never null
     * @throws UnsupportedOperationException if the date or offset is null
     */
    public OffsetDate toOffsetDate() {
        LocalDate date = toLocalDate();
        if (offset == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to OffsetDate because the offset is null");
        }
        return OffsetDate.date(date, offset);
    }

    /**
     * Converts this object to an OffsetTime.
     * This method will fail if the time or offset is null.
     *
     * @return the OffsetTime, never null
     * @throws UnsupportedOperationException if the time or offset is null
     */
    public OffsetTime toOffsetTime() {
        LocalTime time = toLocalTime();
        if (offset == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to OffsetTime because the offset is null");
        }
        return OffsetTime.time(time, offset);
    }

    /**
     * Converts this object to an OffsetDateTime.
     * This method will fail if the time or offset is null.
     *
     * @return the OffsetDateTime, never null
     * @throws UnsupportedOperationException if the time or offset is null
     */
    public OffsetDateTime toOffsetDateTime() {
        LocalDateTime dateTime = toLocalDateTime();
        if (offset == null) {
            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to OffsetDateTime because the offset is null");
        }
        return OffsetDateTime.dateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a ZonedDateTime.
     * This method will fail if the date or offset is null.
     *
     * @return the ZonedDateTime, never null
     * @throws UnsupportedOperationException if the date, offset or zone is null
     */
    public ZonedDateTime toZonedDateTime() {
//        OffsetDateTime dateTime = toOffsetDateTime();
//        if (zone == null) {
//            throw new UnsupportedOperationException("Cannot convert FlexiDateTime to ZonedDateTime because the zone is null");
//        }
//        return ZonedDateTime.dateTime(dateTime, offset);
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this object to a FlexiDateTime, trivially returning <code>this</code>.
     *
     * @return this
     */
    public FlexiDateTime toFlexiDateTime() {
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the flexi date-time as a <code>String</code>.
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
     * OffsetDateTime or ZonedDateTime is converted to a FlexiDateTime then the
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
