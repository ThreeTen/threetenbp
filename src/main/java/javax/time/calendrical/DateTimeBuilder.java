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

import static javax.time.calendrical.DateTimeAdjusters.nextOrCurrent;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.ALIGNED_WEEK_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.AMPM_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.CLOCK_HOUR_OF_AMPM;
import static javax.time.calendrical.LocalDateTimeField.CLOCK_HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_WEEK;
import static javax.time.calendrical.LocalDateTimeField.DAY_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_MONTH;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_AMPM;
import static javax.time.calendrical.LocalDateTimeField.HOUR_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MICRO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MICRO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.MILLI_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MILLI_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.MINUTE_OF_HOUR;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.NANO_OF_SECOND;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_DAY;
import static javax.time.calendrical.LocalDateTimeField.SECOND_OF_MINUTE;
import static javax.time.calendrical.LocalDateTimeField.YEAR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.DayOfWeek;
import javax.time.LocalDate;
import javax.time.LocalTime;
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
 * 
 * <h4>Implementation notes</h4>
 * This class is mutable and not thread-safe.
 * It should only be used from a single thread.
 */
public final class DateTimeBuilder implements DateTimeCalendrical, Cloneable {

    /**
     * The map of other fields.
     */
    private Map<DateTimeField, Long> otherFields;
    /**
     * The map of date-time fields.
     */
    private final EnumMap<LocalDateTimeField, Long> standardFields = new EnumMap<LocalDateTimeField, Long>(LocalDateTimeField.class);
    /**
     * The map of calendrical objects by type.
     */
    private final List<Object> objects = new ArrayList<Object>();

//    //-----------------------------------------------------------------------
//    /**
//     * Obtains an instance of {@code DateTimeBuilder} from a calendrical.
//     * <p>
//     * A calendrical represents some form of date and time information.
//     * This factory converts the arbitrary calendrical to an instance of {@code DateTimeBuilder}.
//     * All implementations of {@link CalendricalObject} must return {@code DateTimeBuilder}
//     * so this method should never fail.
//     * 
//     * @param calendrical  the calendrical to convert, not null
//     * @return the local date, not null
//     * @throws CalendricalException if unable to convert to a {@code DateTimeBuilder}
//     */
//    public static DateTimeBuilder from(DateTimeCalendrical calendrical) {  // TODO
//        DateTimeBuilder obj = calendrical.extract(DateTimeBuilder.class);
//        if (obj == null) {
//            throw new CalendricalException("Unable to convert calendrical to DateTimeBuilder: " + calendrical.getClass());
//        }
//        return obj;
//    }

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
    public DateTimeBuilder(Object calendrical) {
        addCalendrical(calendrical);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map of field-value pairs in the builder.
     * 
     * @return a modifiable copy of the field-value map, not null
     */
    public Map<DateTimeField, Long> getFieldValueMap() {
        Map<DateTimeField, Long> map = new HashMap<DateTimeField, Long>(standardFields);
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
        return standardFields.containsKey(field) || (otherFields != null && otherFields.containsKey(field));
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
        if (field instanceof LocalDateTimeField) {
            return standardFields.get(field);
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
        return putFieldValue0(field, value);
    }

    private DateTimeBuilder putFieldValue0(DateTimeField field, long value) {
        if (field instanceof LocalDateTimeField) {
            standardFields.put((LocalDateTimeField) field, value);
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
        if (field instanceof LocalDateTimeField) {
            value = standardFields.remove(field);
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
            if (field instanceof LocalDateTimeField) {
                standardFields.remove(field);
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
     * Gets the list of calendricals in the builder.
     * <p>
     * This map contains date and time objects represented by {@code CalendricalObject}.
     * This includes all the major classes, such as {@link LocalDate}, {@link LocalTime},
     * {@link ZoneOffset} and {@link ZoneId}.
     * The returned map is live and may be edited.
     * 
     * @return the editable map of calendrical by type, not null
     */
    public List<Object> getCalendricals() {
        return objects;
    }

    /**
     * Adds a calendrical to the builder.
     * <p>
     * This adds a calendrical to the builder.
     * If the calendrical is a {@code DateTimeBuilder}, each field is added using {@link #addFieldValue}.
     * If the calendrical is not already present, then the calendrical is added to the map.
     * If the calendrical is already present and it is equal to that specified, no action occurs.
     * If the calendrical is already present and it is not equal to that specified, then an exception is thrown.
     * 
     * @param calendrical  the calendrical to add, not null
     * @return {@code this}, for method chaining
     * @throws CalendricalException if the field is already present with a different value
     */
    public DateTimeBuilder addCalendrical(Object calendrical) {
        // special case
        if (calendrical instanceof DateTimeBuilder) {
            DateTimeBuilder dtb = (DateTimeBuilder) calendrical;
            for (DateTimeField field : dtb.getFieldValueMap().keySet()) {
                addFieldValue(field, dtb.getFieldValue(field));
            }
            return this;
        }
        objects.add(calendrical);
//        TODO
//        // preserve state of builder until validated
//        Class<?> cls = calendrical.extract(Class.class);
//        if (cls == null) {
//            throw new CalendricalException("Invalid calendrical, unable to extract Class");
//        }
//        Object obj = objects.get(cls);
//        if (obj != null) {
//            if (obj.equals(calendrical) == false) {
//                throw new CalendricalException("Conflict found: " + calendrical.getClass().getSimpleName() + " " + obj + " differs from " + calendrical + ": " + this);
//            }
//        } else {
//            objects.put(cls, calendrical);
//        }
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
                    if (entry.getKey().resolve(this, entry.getValue())) {
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
        if (standardFields.containsKey(EPOCH_DAY)) {
            checkDate(LocalDate.ofEpochDay(standardFields.remove(EPOCH_DAY)));
            return;
        }
        
        // normalize fields
        if (standardFields.containsKey(EPOCH_MONTH)) {
            long em = standardFields.remove(EPOCH_MONTH);
            addFieldValue(MONTH_OF_YEAR, (em % 12) + 1);
            addFieldValue(YEAR, (em / 12) + 1970);
        }
        
        // build date
        if (standardFields.containsKey(YEAR)) {
            if (standardFields.containsKey(MONTH_OF_YEAR)) {
                if (standardFields.containsKey(DAY_OF_MONTH)) {
                    int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                    int moy = DateTimes.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                    int dom = DateTimes.safeToInt(standardFields.remove(DAY_OF_MONTH));
                    checkDate(LocalDate.of(y, moy, dom));
                    return;
                }
                if (standardFields.containsKey(ALIGNED_WEEK_OF_MONTH)) {
                    if (standardFields.containsKey(ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                        int moy = DateTimes.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                        int aw = DateTimes.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_MONTH));
                        int ad = DateTimes.safeToInt(standardFields.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                        return;
                    }
                    if (standardFields.containsKey(DAY_OF_WEEK)) {
                        int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                        int moy = DateTimes.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                        int aw = DateTimes.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_MONTH));
                        int dow = DateTimes.safeToInt(standardFields.remove(DAY_OF_WEEK));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7).with(nextOrCurrent(DayOfWeek.of(dow))));
                        return;
                    }
                }
            }
            if (standardFields.containsKey(DAY_OF_YEAR)) {
                int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                int doy = DateTimes.safeToInt(standardFields.remove(DAY_OF_YEAR));
                checkDate(LocalDate.ofYearDay(y, doy));
                return;
            }
            if (standardFields.containsKey(ALIGNED_WEEK_OF_YEAR)) {
                if (standardFields.containsKey(ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                    int aw = DateTimes.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_YEAR));
                    int ad = DateTimes.safeToInt(standardFields.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                    return;
                }
                if (standardFields.containsKey(DAY_OF_WEEK)) {
                    int y = DateTimes.safeToInt(standardFields.remove(YEAR));
                    int aw = DateTimes.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_YEAR));
                    int dow = DateTimes.safeToInt(standardFields.remove(DAY_OF_WEEK));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7).with(nextOrCurrent(DayOfWeek.of(dow))));
                    return;
                }
            }
        }
    }

    private void checkDate(LocalDate date) {
        // TODO: this doesn't handle aligned weeks over into next month which would otherwise be valid
        
        addCalendrical(date);
        for (LocalDateTimeField field : standardFields.keySet()) {
            long val1 = field.get(date);
            Long val2 = standardFields.get(field);
            if (val1 != val2) {
                throw new CalendricalException("Conflict found: Field " + field + " " + val1 + " differs from " + field + " " + val2 + " derived from " + date);
            }
        }
    }

    private void mergeTime() {
        if (standardFields.containsKey(CLOCK_HOUR_OF_DAY)) {
            long ch = standardFields.remove(CLOCK_HOUR_OF_DAY);
            addFieldValue(HOUR_OF_DAY, ch == 24 ? 0 : ch);
        }
        if (standardFields.containsKey(CLOCK_HOUR_OF_AMPM)) {
            long ch = standardFields.remove(CLOCK_HOUR_OF_AMPM);
            addFieldValue(HOUR_OF_AMPM, ch == 12 ? 0 : ch);
        }
        if (standardFields.containsKey(AMPM_OF_DAY) && standardFields.containsKey(HOUR_OF_AMPM)) {
            long ap = standardFields.remove(AMPM_OF_DAY);
            long hap = standardFields.remove(HOUR_OF_AMPM);
            addFieldValue(HOUR_OF_DAY, ap * 12 + hap);
        }
//        if (timeFields.containsKey(HOUR_OF_DAY) && timeFields.containsKey(MINUTE_OF_HOUR)) {
//            long hod = timeFields.remove(HOUR_OF_DAY);
//            long moh = timeFields.remove(MINUTE_OF_HOUR);
//            addFieldValue(MINUTE_OF_DAY, hod * 60 + moh);
//        }
//        if (timeFields.containsKey(MINUTE_OF_DAY) && timeFields.containsKey(SECOND_OF_MINUTE)) {
//            long mod = timeFields.remove(MINUTE_OF_DAY);
//            long som = timeFields.remove(SECOND_OF_MINUTE);
//            addFieldValue(SECOND_OF_DAY, mod * 60 + som);
//        }
        if (standardFields.containsKey(NANO_OF_DAY)) {
            long nod = standardFields.remove(NANO_OF_DAY);
            addFieldValue(SECOND_OF_DAY, nod / 1000000000L);
            addFieldValue(NANO_OF_SECOND, nod % 1000000000L);
        }
        if (standardFields.containsKey(MICRO_OF_DAY)) {
            long cod = standardFields.remove(MICRO_OF_DAY);
            addFieldValue(SECOND_OF_DAY, cod / 1000000);
            addFieldValue(MICRO_OF_SECOND, cod % 1000000);
        }
        if (standardFields.containsKey(MILLI_OF_DAY)) {
            long lod = standardFields.remove(MILLI_OF_DAY);
            addFieldValue(SECOND_OF_DAY, lod / 1000);
            addFieldValue(MILLI_OF_SECOND, lod % 1000);
        }
        if (standardFields.containsKey(SECOND_OF_DAY)) {
            long sod = standardFields.remove(SECOND_OF_DAY);
            addFieldValue(HOUR_OF_DAY, sod / 3600);
            addFieldValue(MINUTE_OF_HOUR, (sod / 60) % 60);
            addFieldValue(SECOND_OF_MINUTE, sod % 60);
        }
        if (standardFields.containsKey(MINUTE_OF_DAY)) {
            long mod = standardFields.remove(MINUTE_OF_DAY);
            addFieldValue(HOUR_OF_DAY, mod / 60);
            addFieldValue(MINUTE_OF_HOUR, mod % 60);
        }
        
//            long sod = nod / 1000000000L;
//            addFieldValue(HOUR_OF_DAY, sod / 3600);
//            addFieldValue(MINUTE_OF_HOUR, (sod / 60) % 60);
//            addFieldValue(SECOND_OF_MINUTE, sod % 60);
//            addFieldValue(NANO_OF_SECOND, nod % 1000000000L);
        if (standardFields.containsKey(MILLI_OF_SECOND) && standardFields.containsKey(MICRO_OF_SECOND)) {
            long los = standardFields.remove(MILLI_OF_SECOND);
            long cos = standardFields.get(MICRO_OF_SECOND);
            addFieldValue(MICRO_OF_SECOND, los * 1000 + (cos % 1000));
        }
        
        Long hod = standardFields.get(HOUR_OF_DAY);
        Long moh = standardFields.get(MINUTE_OF_HOUR);
        Long som = standardFields.get(SECOND_OF_MINUTE);
        Long nos = standardFields.get(NANO_OF_SECOND);
        if (hod != null) {
            int hodVal = DateTimes.safeToInt(hod);
            if (moh != null) {
                int mohVal = DateTimes.safeToInt(moh);
                if (som != null) {
                    int somVal = DateTimes.safeToInt(som);
                    if (nos != null) {
                        int nosVal = DateTimes.safeToInt(nos);
                        addCalendrical(LocalTime.of(hodVal, mohVal, somVal, nosVal));
                    } else {
                        addCalendrical(LocalTime.of(hodVal, mohVal, somVal));
                    }
                } else {
                    addCalendrical(LocalTime.of(hodVal, mohVal));
                }
            } else {
                addCalendrical(LocalTime.of(hodVal, 0));
            }
        }
    }

    private void splitObjects() {
        // TODO
//        OffsetDateTime odt = (OffsetDateTime) objects.get(OffsetDateTime.class);
//        if (odt != null) {
//            addCalendrical(odt.toLocalDateTime());
//            addCalendrical(odt.getOffset());
//        }
//        OffsetDate od = (OffsetDate) objects.get(OffsetDate.class);
//        if (od != null) {
//            addCalendrical(od.toLocalDate());
//            addCalendrical(od.getOffset());
//        }
//        OffsetTime ot = (OffsetTime) objects.get(OffsetTime.class);
//        if (ot != null) {
//            addCalendrical(ot.toLocalTime());
//            addCalendrical(ot.getOffset());
//        }
//        LocalDateTime ldt = (LocalDateTime) objects.get(LocalDateTime.class);
//        if (ldt != null) {
//            addCalendrical(ldt.toLocalDate());
//            addCalendrical(ldt.toLocalTime());
//        }
    }

    private void mergeObjects() {
        // TODO
//        LocalDate ld = (LocalDate) objects.get(LocalDate.class);
//        LocalTime lt = (LocalTime) objects.get(LocalTime.class);
//        ZoneOffset offset = (ZoneOffset) objects.get(ZoneOffset.class);
//        ZoneId id = (ZoneId) objects.get(ZoneId.class);
//        LocalDateTime ldt = null;
//        OffsetDateTime odt = null;
//        if (ld != null && lt != null) {
//            ldt = LocalDateTime.of(ld, lt);
//            addCalendrical(ldt);
//        }
//        if (ld != null && offset != null) {
//            addCalendrical(OffsetDate.of(ld, offset));
//        }
//        if (lt != null && offset != null) {
//            addCalendrical(OffsetDate.of(ld, offset));
//        }
//        if (ldt != null && offset != null) {
//            odt = OffsetDateTime.of(ldt, offset);
//            addCalendrical(odt);
//            addCalendrical(odt.toInstant());
//        }
//        if (odt != null && id != null) {
//            addCalendrical(ZonedDateTime.of(odt, id));
//        }
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == Class.class) {
            return (R) DateTimeBuilder.class;
        }
        return null;  // TODO
//        Object result = null;
//        Object obj = objects.get(type);
//        if (obj != null) {
//            result = obj;
//        }
//        for (CalendricalObject cal : objects.values()) {
//            R extracted = cal.extract(type);
//            if (extracted != null) {
//                if (result != null && result.equals(extracted) == false) {
//                    throw new CalendricalException("Conflict found: " + type.getSimpleName() + " differs " + result + " vs " + cal + ": " + this);
//                }
//                result = extracted;
//            }
//        }
//        return (R)  result;
    }

    // TODO
//    @Override
//    public CalendricalObject with(CalendricalAdjuster adjuster) {
//        if (adjuster instanceof DateTimeBuilder) {
//            DateTimeBuilder dtb = (DateTimeBuilder) adjuster;
//            DateTimeBuilder result = clone();
//            result.objects.putAll(dtb.objects);
//            result.standardFields.putAll(dtb.standardFields);
//            result.standardFields.putAll(dtb.standardFields);
//            if (this.otherFields != null) {
//                result.otherFields.putAll(dtb.otherFields);
//            }
//            return result;
//        } else if (adjuster instanceof CalendricalObject) {
//            CalendricalObject calendrical = (CalendricalObject) adjuster;
//            Class<?> cls = calendrical.extract(Class.class);
//            if (cls == null) {
//                throw new CalendricalException("Invalid calendrical, unable to extract Class");
//            }
//            DateTimeBuilder result = clone();
//            result.objects.put(cls, calendrical);
//        }
//        DateTimes.checkNotNull(adjuster, "Adjuster must not be null");
//        throw new CalendricalException("Unable to adjust DateTimeBuilder with " + adjuster.getClass().getSimpleName());
//    }

    //-----------------------------------------------------------------------
    /**
     * Clones this builder, creating a new independent copy referring to the
     * same map of fields and calendricals.
     * 
     * @return the cloned builder, not null
     */
    @Override
    public DateTimeBuilder clone() {
        DateTimeBuilder dtb = new DateTimeBuilder();
        dtb.objects.addAll(this.objects);
        dtb.standardFields.putAll(this.standardFields);
        dtb.standardFields.putAll(this.standardFields);
        if (this.otherFields != null) {
            dtb.otherFields.putAll(this.otherFields);
        }
        return dtb;
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
            for (Object obj : objects) {
                buf.append(obj.getClass().getSimpleName()).append('=').append(obj).append(", ");
            }
            buf.setLength(buf.length() - 2);
            buf.append('}');
        }
        buf.append(']');
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    @Override
    public long get(DateTimeField field) {
        return getFieldValue(field);
    }

    @Override
    public DateTimeCalendrical with(DateTimeField field, long newValue) {
        putFieldValue0(field, newValue);
        return this;
    }

    @Override
    public List<DateTimeField> fieldList() {
        return Collections.unmodifiableList(new ArrayList<DateTimeField>(getFieldValueMap().keySet()));
    }

}
