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
package javax.time.calendrical;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.OffsetDate;
import javax.time.OffsetDateTime;
import javax.time.OffsetTime;
import javax.time.ZoneId;
import javax.time.ZoneOffset;

/**
 * Builder that can combine date and time fields into date and time objects.
 * <p>
 * The builder is used to make sense of different elements of date and time.
 * It is designed as two separate maps:
 * <ul>
 * <li>from {@link DateTimeField} to {@code long} value, where the value may be
 * outside the valid range for the field
 * <li>from {@code Class} to {@link CalendricalObject}, holding larger scale objects
 * like {@code LocalDateTime}.
 * </ul>
 * <p>
 * All implementations of {@code CalendricalObject} will return a builder if
 * {@code DateTimeBuilder.class} is passed to {@link CalendricalObject#extract(Class) extract(Class)}.
 * <p>
 * This class is mutable and not thread-safe.
 * It should only be used from a single thread.
 */
public final class DateTimeBuilder implements CalendricalObject {

    /**
     * The map of other fields.
     */
    private Map<DateTimeField, Long> otherFields;
    /**
     * The map of date fields.
     */
    private final EnumMap<LocalDateField, Long> dateFields = new EnumMap<LocalDateField, Long>(LocalDateField.class);
    /**
     * The map of time fields.
     */
    private final EnumMap<LocalTimeField, Long> timeFields = new EnumMap<LocalTimeField, Long>(LocalTimeField.class);
    /**
     * The map of calendrical objects by type.
     * A concurrent map is used to ensure no nulls are added.
     */
    private final Map<Class<?>, CalendricalObject> objects = new ConcurrentHashMap<Class<?>, CalendricalObject>(8, 0.75f, 1);

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code DateTimeBuilder} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code DateTimeBuilder}.
     * All implementations of {@link CalendricalObject} must return {@code DateTimeBuilder}
     * so this method should never fail.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the local date, not null
     * @throws CalendricalException if unable to convert to a {@code DateTimeBuilder}
     */
    public static DateTimeBuilder from(CalendricalObject calendrical) {
        DateTimeBuilder obj = calendrical.extract(DateTimeBuilder.class);
        if (obj == null) {
            throw new CalendricalException("Unable to convert calendrical to DateTimeBuilder: " + calendrical.getClass());
        }
        return obj;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an empty instance of the builder.
     */
    public DateTimeBuilder() {
    }

    /**
     * Creates a new instance of the builder with a single field-value.
     * <p>
     * This is equivalent to using {@link #addFieldValue(DateTimeField, long)} on an empty builder.
     */
    public DateTimeBuilder(DateTimeField field, long value) {
        addFieldValue(field, value);
    }

    /**
     * Creates a new instance of the builder with a single calendrical.
     * <p>
     * This is equivalent to using {@link #addCalendrical(CalendricalObject)} on an empty builder.
     */
    public DateTimeBuilder(CalendricalObject calendrical) {
        addCalendrical(calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of field-value pairs in the builder.
     * 
     * @return a modifiable copy of the field-value map, not null
     */
    public Map<DateTimeField, Long> getFieldValueMap() {
        Map<DateTimeField, Long> map = new HashMap<DateTimeField, Long>(dateFields);
        map.putAll(timeFields);
        if (otherFields != null) {
            map.putAll(otherFields);
        }
        return map;
    }

    /**
     * Checks whether the specified field is present in the builder.
     * 
     * @param field  the field to find in the field-value map, not null
     * @return true if the field is present
     */
    public boolean containsFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        return dateFields.containsKey(field) || timeFields.containsKey(field) || (otherFields != null && otherFields.containsKey(field));
    }

    /**
     * Gets the value of the specified field from the builder.
     * 
     * @param field  the field to query in the field-value map, not null
     * @return the value of the field, may be out of range
     * @throws CalendricalException if the field is not present
     */
    public long getFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long value = getFieldValue0(field);
        if (value == null) {
            throw new CalendricalException("Field not found: " + field);
        }
        return value;
    }

    private Long getFieldValue0(DateTimeField field) {
        if (field instanceof LocalDateField) {
            return dateFields.get(field);
        } else if (field instanceof LocalTimeField) {
            return timeFields.get(field);
        } else if (otherFields != null) {
            return otherFields.get(field);
        }
        return null;
    }

    /**
     * Gets the value of the specified field from the builder ensuring it is valid.
     * 
     * @param field  the field to query in the field-value map, not null
     * @return the value of the field, may be out of range
     * @throws CalendricalException if the field is not present
     */
    public long getValidFieldValue(DateTimeField field) {
        long value = getFieldValue(field);
        return field.getValueRange().checkValidValue(value, field);
    }

    /**
     * Adds a field-value pair to the builder.
     * <p>
     * This adds a field to the builder.
     * If the field is not already present, then the field-value pair is added to the map.
     * If the field is already present and it has the same value as that specified, no action occurs.
     * If the field is already present and it has a different value to that specified, then
     * an exception is thrown.
     * 
     * @param field  the field to add, not null
     * @param value  the value to add, not null
     * @return {@code this}, for method chaining
     * @throws CalendricalException if the field is already present with a different value
     */
    public DateTimeBuilder addFieldValue(DateTimeField field, long value) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long old = getFieldValue0(field);  // check first for better error message
        if (old != null && old.longValue() != value) {
            throw new CalendricalException("Conflict found: " + field + " " + old + " differs from " + field + " " + value + ": " + this);
        }
        if (field instanceof LocalDateField) {
            dateFields.put((LocalDateField) field, value);
        } else if (field instanceof LocalTimeField) {
            timeFields.put((LocalTimeField) field, value);
        } else {
            if (otherFields == null) {
                otherFields = new LinkedHashMap<DateTimeField, Long>();
            }
            otherFields.put(field, value);
        }
        return this;
    }

    /**
     * Removes a field-value pair from the builder.
     * <p>
     * This removes a field, which must exist, from the builder.
     * See {@link #removeFieldValues(DateTimeField...)} for a version which does not throw an exception
     * 
     * @param field  the field to remove, not null
     * @return the previous value of the field
     * @throws CalendricalException if the field is not found
     */
    public long removeFieldValue(DateTimeField field) {
        DateTimes.checkNotNull(field, "Field cannot be null");
        Long value = null;
        if (field instanceof LocalDateField) {
            value = dateFields.remove(field);
        } else if (field instanceof LocalTimeField) {
            value = timeFields.remove(field);
        } else if (otherFields != null) {
            value = otherFields.remove(field);
        }
        if (value == null) {
            throw new CalendricalException("Field not found: " + field);
        }
        return value;
    }

    //-----------------------------------------------------------------------
    /**
     * Removes a list of fields from the builder.
     * <p>
     * This removes the specified fields from the builder.
     * No exception is thrown if the fields are not present.
     * 
     * @param fields  the fields to remove, not null
     */
    public void removeFieldValues(DateTimeField... fields) {
        for (DateTimeField field : fields) {
            if (field instanceof LocalDateField) {
                dateFields.remove(field);
            } else if (field instanceof LocalTimeField) {
                timeFields.remove(field);
            } else if (otherFields != null) {
                otherFields.remove(field);
            }
        }
    }

    /**
     * Queries a list of fields from the builder.
     * <p>
     * This gets the value of the specified fields from the builder into
     * an array where the positions match the order of the fields.
     * If a field is not present, the array will contain null in that position.
     * 
     * @param fields  the fields to query, not null
     * @return the array of field values, not null
     */
    public Long[] queryFieldValues(DateTimeField... fields) {
        Long[] values = new Long[fields.length];
        int i = 0;
        for (DateTimeField field : fields) {
            values[i++] = getFieldValue0(field);
        }
        return values;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of calendricals in the builder.
     * <p>
     * This map contains date and time objects represented by {@code CalendricalObject}.
     * This includes all the major classes, such as {@link LocalDate}, {@link LocalTime},
     * {@link ZoneOffset} and {@link ZoneId}.
     * The returned map is live and may be edited.
     * 
     * @return the editable map of calendrical by type, not null
     */
    public Map<Class<?>, CalendricalObject> getCalendricalMap() {
        return objects;
    }

    /**
     * Adds a calendrical to the builder.
     * <p>
     * This adds a calendrical to the builder.
     * If the calendrical is not already present, then the calendrical is added to the map.
     * If the calendrical is already present and it is equal to that specified, no action occurs.
     * If the calendrical is already present and it is not equal to that specified, then an exception is thrown.
     * 
     * @param calendrical  the calendrical to add, not null
     * @return {@code this}, for method chaining
     * @throws CalendricalException if the field is already present with a different value
     */
    public DateTimeBuilder addCalendrical(CalendricalObject calendrical) {
        // preserve state of builder until validated
        Class<?> cls = calendrical.getClass();
        Object obj = objects.get(cls);
        if (obj != null) {
            if (obj.equals(calendrical) == false) {
                throw new CalendricalException("Conflict found: " + calendrical.getClass().getSimpleName() + " " + obj + " differs from " + calendrical + ": " + this);
            }
        } else {
            objects.put(cls, calendrical);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves the builder, evaluating the date and time.
     * <p>
     * This examines the contents of the builder and resolves it to produce the best
     * available date and time, throwing an exception if a problem occurs.
     * Calling this method changes the state of the builder.
     * 
     * @return {@code this}, for method chaining
     */
    public DateTimeBuilder resolve() {
        splitObjects();
        // handle unusual fields
        if (otherFields != null) {
            outer:
            while (true) {
                for (Entry<DateTimeField, Long> entry : otherFields.entrySet()) {
                    if (entry.getKey().getDateTimeRules().resolve(this, entry.getValue())) {
                        continue outer;
                    }
                }
                break;
            }
        }
        // handle standard fields
        mergeDate();
        mergeTime();
        mergeObjects();
        // TODO: cross validate remaining fields?
        return this;
    }

    private void mergeDate() {
        if (dateFields.containsKey(LocalDateField.EPOCH_DAY)) {
            checkDate(LocalDate.ofEpochDay(dateFields.remove(LocalDateField.EPOCH_DAY)));
            return;
        }
        
        // normalize fields
        if (dateFields.containsKey(LocalDateField.EPOCH_MONTH)) {
            long em = dateFields.remove(LocalDateField.EPOCH_MONTH);
            addFieldValue(LocalDateField.MONTH_OF_YEAR, (em % 12) + 1);
            addFieldValue(LocalDateField.YEAR, (em / 12) + 1970);
        }
        
        // build date
        if (dateFields.containsKey(LocalDateField.YEAR)) {
            if (dateFields.containsKey(LocalDateField.MONTH_OF_YEAR)) {
                if (dateFields.containsKey(LocalDateField.DAY_OF_MONTH)) {
                    int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                    int moy = DateTimes.safeToInt(dateFields.remove(LocalDateField.MONTH_OF_YEAR));
                    int dom = DateTimes.safeToInt(dateFields.remove(LocalDateField.DAY_OF_MONTH));
                    checkDate(LocalDate.of(y, moy, dom));
                    return;
                }
                if (dateFields.containsKey(LocalDateField.ALIGNED_WEEK_OF_MONTH)) {
                    if (dateFields.containsKey(LocalDateField.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                        int moy = DateTimes.safeToInt(dateFields.remove(LocalDateField.MONTH_OF_YEAR));
                        int aw = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_WEEK_OF_MONTH));
                        int ad = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                        return;
                    }
                    if (dateFields.containsKey(LocalDateField.DAY_OF_WEEK)) {
                        int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                        int moy = DateTimes.safeToInt(dateFields.remove(LocalDateField.MONTH_OF_YEAR));
                        int aw = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_WEEK_OF_MONTH));
                        int dow = DateTimes.safeToInt(dateFields.remove(LocalDateField.DAY_OF_WEEK));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7).with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow))));
                        return;
                    }
                }
            }
            if (dateFields.containsKey(LocalDateField.DAY_OF_YEAR)) {
                int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                int doy = DateTimes.safeToInt(dateFields.remove(LocalDateField.DAY_OF_YEAR));
                checkDate(LocalDate.ofYearDay(y, doy));
                return;
            }
            if (dateFields.containsKey(LocalDateField.ALIGNED_WEEK_OF_YEAR)) {
                if (dateFields.containsKey(LocalDateField.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                    int aw = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_WEEK_OF_YEAR));
                    int ad = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                    return;
                }
                if (dateFields.containsKey(LocalDateField.DAY_OF_WEEK)) {
                    int y = DateTimes.safeToInt(dateFields.remove(LocalDateField.YEAR));
                    int aw = DateTimes.safeToInt(dateFields.remove(LocalDateField.ALIGNED_WEEK_OF_YEAR));
                    int dow = DateTimes.safeToInt(dateFields.remove(LocalDateField.DAY_OF_WEEK));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7).with(DateAdjusters.nextOrCurrent(DayOfWeek.of(dow))));
                    return;
                }
            }
        }
    }

    private void checkDate(LocalDate date) {
        // TODO: this doesn't handle aligned weeks over into next month which would otherwise be valid
        
        addCalendrical(date);
        for (LocalDateField field : dateFields.keySet()) {
            long val1 = field.getDateRules().get(date);
            Long val2 = dateFields.get(field);
            if (val1 != val2) {
                throw new CalendricalException("Conflict found: Field " + field + " " + val1 + " differs from " + field + " " + val2 + " derived from " + date);
            }
        }
    }

    private void mergeTime() {
        if (timeFields.containsKey(LocalTimeField.CLOCK_HOUR_OF_DAY)) {
            long ch = timeFields.remove(LocalTimeField.CLOCK_HOUR_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, ch == 24 ? 0 : ch);
        }
        if (timeFields.containsKey(LocalTimeField.CLOCK_HOUR_OF_AMPM)) {
            long ch = timeFields.remove(LocalTimeField.CLOCK_HOUR_OF_AMPM);
            addFieldValue(LocalTimeField.HOUR_OF_AMPM, ch == 12 ? 0 : ch);
        }
        if (timeFields.containsKey(LocalTimeField.AMPM_OF_DAY) && timeFields.containsKey(LocalTimeField.HOUR_OF_AMPM)) {
            long ap = timeFields.remove(LocalTimeField.AMPM_OF_DAY);
            long hap = timeFields.remove(LocalTimeField.HOUR_OF_AMPM);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, ap * 2 + hap);
        }
//        if (timeFields.containsKey(LocalTimeField.HOUR_OF_DAY) && timeFields.containsKey(LocalTimeField.MINUTE_OF_HOUR)) {
//            long hod = timeFields.remove(LocalTimeField.HOUR_OF_DAY);
//            long moh = timeFields.remove(LocalTimeField.MINUTE_OF_HOUR);
//            addFieldValue(LocalTimeField.MINUTE_OF_DAY, hod * 60 + moh);
//        }
//        if (timeFields.containsKey(LocalTimeField.MINUTE_OF_DAY) && timeFields.containsKey(LocalTimeField.SECOND_OF_MINUTE)) {
//            long mod = timeFields.remove(LocalTimeField.MINUTE_OF_DAY);
//            long som = timeFields.remove(LocalTimeField.SECOND_OF_MINUTE);
//            addFieldValue(LocalTimeField.SECOND_OF_DAY, mod * 60 + som);
//        }
        if (timeFields.containsKey(LocalTimeField.NANO_OF_DAY)) {
            long nod = timeFields.remove(LocalTimeField.NANO_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, nod / 1000000000L);
            addFieldValue(LocalTimeField.NANO_OF_SECOND, nod % 1000000000L);
        }
        if (timeFields.containsKey(LocalTimeField.MICRO_OF_DAY)) {
            long cod = timeFields.remove(LocalTimeField.MICRO_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, cod / 1000000);
            addFieldValue(LocalTimeField.MICRO_OF_SECOND, cod % 1000000);
        }
        if (timeFields.containsKey(LocalTimeField.MILLI_OF_DAY)) {
            long lod = timeFields.remove(LocalTimeField.MILLI_OF_DAY);
            addFieldValue(LocalTimeField.SECOND_OF_DAY, lod / 1000);
            addFieldValue(LocalTimeField.MILLI_OF_SECOND, lod % 1000);
        }
        if (timeFields.containsKey(LocalTimeField.SECOND_OF_DAY)) {
            long sod = timeFields.remove(LocalTimeField.SECOND_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, sod / 3600);
            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, (sod / 60) % 60);
            addFieldValue(LocalTimeField.SECOND_OF_MINUTE, sod % 60);
        }
        if (timeFields.containsKey(LocalTimeField.MINUTE_OF_DAY)) {
            long mod = timeFields.remove(LocalTimeField.MINUTE_OF_DAY);
            addFieldValue(LocalTimeField.HOUR_OF_DAY, mod / 60);
            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, mod % 60);
        }
        
//            long sod = nod / 1000000000L;
//            addFieldValue(LocalTimeField.HOUR_OF_DAY, sod / 3600);
//            addFieldValue(LocalTimeField.MINUTE_OF_HOUR, (sod / 60) % 60);
//            addFieldValue(LocalTimeField.SECOND_OF_MINUTE, sod % 60);
//            addFieldValue(LocalTimeField.NANO_OF_SECOND, nod % 1000000000L);
        if (timeFields.containsKey(LocalTimeField.MILLI_OF_SECOND) && timeFields.containsKey(LocalTimeField.MICRO_OF_SECOND)) {
            long los = timeFields.remove(LocalTimeField.MILLI_OF_SECOND);
            long cos = timeFields.get(LocalTimeField.MICRO_OF_SECOND);
            addFieldValue(LocalTimeField.MICRO_OF_SECOND, los * 1000 + (cos % 1000));
        }
        
        Long hod = timeFields.get(LocalTimeField.HOUR_OF_DAY);
        Long moh = timeFields.get(LocalTimeField.MINUTE_OF_HOUR);
        Long som = timeFields.get(LocalTimeField.SECOND_OF_MINUTE);
        if (hod != null) {
            int hodVal = DateTimes.safeToInt(hod);
            if (moh != null) {
                int mohVal = DateTimes.safeToInt(hod);
                if (som != null) {
                    int somVal = DateTimes.safeToInt(hod);
                    addCalendrical(LocalTime.of(hodVal, mohVal, somVal));
                } else {
                    addCalendrical(LocalTime.of(hodVal, mohVal));
                }
            } else {
                addCalendrical(LocalTime.of(hodVal, 0));
            }
        }
    }

    private void splitObjects() {
        OffsetDateTime odt = (OffsetDateTime) objects.get(OffsetDateTime.class);
        if (odt != null) {
            addCalendrical(odt.toLocalDateTime());
            addCalendrical(odt.getOffset());
        }
        OffsetDate od = (OffsetDate) objects.get(OffsetDate.class);
        if (od != null) {
            addCalendrical(od.toLocalDate());
            addCalendrical(od.getOffset());
        }
        OffsetTime ot = (OffsetTime) objects.get(OffsetTime.class);
        if (ot != null) {
            addCalendrical(ot.toLocalTime());
            addCalendrical(ot.getOffset());
        }
        LocalDateTime ldt = (LocalDateTime) objects.get(LocalDateTime.class);
        if (ldt != null) {
            addCalendrical(ldt.toLocalDate());
            addCalendrical(ldt.toLocalTime());
        }
    }

    private void mergeObjects() {
        LocalDate ld = (LocalDate) objects.get(LocalDate.class);
        LocalTime lt = (LocalTime) objects.get(LocalTime.class);
        ZoneOffset offset = (ZoneOffset) objects.get(ZoneOffset.class);
        LocalDateTime ldt = null;
        if (ld != null && lt != null) {
            ldt = LocalDateTime.of(ld, lt);
            addCalendrical(ldt);
        }
        if (ld != null && offset != null) {
            addCalendrical(OffsetDate.of(ld, offset));
        }
        if (lt != null && offset != null) {
            addCalendrical(OffsetDate.of(ld, offset));
        }
        if (ldt != null && offset != null) {
            addCalendrical(OffsetDateTime.of(ldt, offset));
        }
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        Object result = null;
        Object obj = objects.get(type);
        if (obj != null) {
            result = obj;
        }
        for (CalendricalObject cal : objects.values()) {
            R extracted = cal.extract(type);
            if (extracted != null) {
                if (result != null && result.equals(extracted) == false) {
                    throw new CalendricalException("Conflict found: " + type.getSimpleName() + " differs " + result + " vs " + cal + ": " + this);
                }
                result = extracted;
            }
        }
        return (R)  result;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append("DateTimeBuilder[");
        Map<DateTimeField, Long> fields = getFieldValueMap();
        if (fields.size() > 0) {
            buf.append("fields=").append(fields);
        }
        if (objects.size() > 0) {
            if (fields.size() > 0) {
                buf.append(", ");
            }
            buf.append("objects={");
            for (Class<?> type : objects.keySet()) {
                buf.append(type.getSimpleName()).append('=').append(objects.get(type)).append(", ");
            }
            buf.setLength(buf.length() - 2);
            buf.append('}');
        }
        buf.append(']');
        return buf.toString();
    }

}
