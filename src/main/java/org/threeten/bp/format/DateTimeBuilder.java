/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.format;

import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.AMPM_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static org.threeten.bp.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.ERA;
import static org.threeten.bp.temporal.ChronoField.HOUR_OF_AMPM;
import static org.threeten.bp.temporal.ChronoField.HOUR_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MICRO_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MICRO_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.MILLI_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MILLI_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.MINUTE_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.MINUTE_OF_HOUR;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.NANO_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.NANO_OF_SECOND;
import static org.threeten.bp.temporal.ChronoField.PROLEPTIC_MONTH;
import static org.threeten.bp.temporal.ChronoField.SECOND_OF_DAY;
import static org.threeten.bp.temporal.ChronoField.SECOND_OF_MINUTE;
import static org.threeten.bp.temporal.ChronoField.YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR_OF_ERA;
import static org.threeten.bp.temporal.TemporalAdjusters.nextOrSame;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.Month;
import org.threeten.bp.Period;
import org.threeten.bp.Year;
import org.threeten.bp.ZoneId;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.jdk8.DefaultInterfaceTemporalAccessor;
import org.threeten.bp.jdk8.Jdk8Methods;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.TemporalQueries;
import org.threeten.bp.temporal.TemporalQuery;

/**
 * Builder that can holds date and time fields and related date and time objects.
 * <p>
 * The builder is used to hold onto different elements of date and time.
 * It is designed as two separate maps:
 * <p><ul>
 * <li>from {@link TemporalField} to {@code long} value, where the value may be
 * outside the valid range for the field
 * <li>from {@code Class} to {@link TemporalAccessor}, holding larger scale objects
 * like {@code LocalDateTime}.
 * </ul><p>
 *
 * <h3>Specification for implementors</h3>
 * This class is mutable and not thread-safe.
 * It should only be used from a single thread.
 */
final class DateTimeBuilder
        extends DefaultInterfaceTemporalAccessor
        implements TemporalAccessor, Cloneable {

    /**
     * The map of other fields.
     */
    private Map<TemporalField, Long> otherFields;
    /**
     * The map of date-time fields.
     */
    private final EnumMap<ChronoField, Long> standardFields = new EnumMap<ChronoField, Long>(ChronoField.class);
    /**
     * The chronology.
     */
    private Chronology chrono;
    /**
     * The zone.
     */
    private ZoneId zone;
    /**
     * The date.
     */
    private LocalDate date;
    /**
     * The time.
     */
    private LocalTime time;
    /**
     * The leap second flag.
     */
    boolean leapSecond;
    /**
     * The excess days.
     */
    Period excessDays;

    //-----------------------------------------------------------------------
    /**
     * Creates an empty instance of the builder.
     */
    public DateTimeBuilder() {
    }

    /**
     * Creates a new instance of the builder with a single field-value.
     * <p>
     * This is equivalent to using {@link #addFieldValue(TemporalField, long)} on an empty builder.
     *
     * @param field  the field to add, not null
     * @param value  the value to add, not null
     */
    public DateTimeBuilder(TemporalField field, long value) {
        addFieldValue(field, value);
    }

    //-----------------------------------------------------------------------
    private Long getFieldValue0(TemporalField field) {
        if (field instanceof ChronoField) {
            return standardFields.get(field);
        } else if (otherFields != null) {
            return otherFields.get(field);
        }
        return null;
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
     * @throws DateTimeException if the field is already present with a different value
     */
    DateTimeBuilder addFieldValue(TemporalField field, long value) {
        Objects.requireNonNull(field, "field");
        Long old = getFieldValue0(field);  // check first for better error message
        if (old != null && old.longValue() != value) {
            throw new DateTimeException("Conflict found: " + field + " " + old + " differs from " + field + " " + value + ": " + this);
        }
        return putFieldValue0(field, value);
    }

    private DateTimeBuilder putFieldValue0(TemporalField field, long value) {
        if (field instanceof ChronoField) {
            standardFields.put((ChronoField) field, value);
        } else {
            if (otherFields == null) {
                otherFields = new LinkedHashMap<TemporalField, Long>();
            }
            otherFields.put(field, value);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    void addObject(Chronology chrono) {
        this.chrono = chrono;
    }

    void addObject(ZoneId zone) {
        this.zone = zone;
    }

    void addObject(LocalDate date) {
        this.date = date;
    }

    void addObject(LocalTime time) {
        this.time = time;
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves the builder, evaluating the date and time.
     * <p>
     * This examines the contents of the builder and resolves it to produce the best
     * available date and time, throwing an exception if a problem occurs.
     * Calling this method changes the state of the builder.
     *
     * @param resolverStyle how to resolve
     * @return {@code this}, for method chaining
     */
    public DateTimeBuilder resolve(ResolverStyle resolverStyle, Set<TemporalField> resolverFields) {
        if (resolverFields != null) {
            standardFields.keySet().retainAll(resolverFields);
            if (otherFields != null) {
                otherFields.keySet().retainAll(resolverFields);
            }
        }
        // handle standard fields
        mergeDate(resolverStyle);
        mergeTime(resolverStyle);
        if (excessDays != null && date != null && time != null) {
            date = date.plus(excessDays);
            excessDays = Period.ZERO;
        }
        // TODO: cross validate remaining fields?
        return this;
    }

    private void mergeDate(ResolverStyle resolverStyle) {
        if (standardFields.containsKey(EPOCH_DAY)) {
            checkDate(LocalDate.ofEpochDay(standardFields.remove(EPOCH_DAY)));
            return;
        }

        // normalize fields
        if (standardFields.containsKey(PROLEPTIC_MONTH)) {
            long em = standardFields.remove(PROLEPTIC_MONTH);
            addFieldValue(MONTH_OF_YEAR, (em % 12) + 1);
            addFieldValue(YEAR, (em / 12));
        }

        // eras
        Long yoeLong = standardFields.remove(YEAR_OF_ERA);
        if (yoeLong != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                YEAR_OF_ERA.checkValidValue(yoeLong);
            }
            Long era = standardFields.remove(ERA);
            if (era == null) {
                Long year = standardFields.get(YEAR);
                if (resolverStyle == ResolverStyle.STRICT) {
                    // do not invent era if strict, but do cross-check with year
                    if (year != null) {
                        addFieldValue(YEAR, (year > 0 ? yoeLong: Jdk8Methods.safeSubtract(1, yoeLong)));
                    } else {
                        // reinstate the field removed earlier, no cross-check issues
                        standardFields.put(YEAR_OF_ERA, yoeLong);
                    }
                } else {
                    // invent era
                    addFieldValue(YEAR, (year == null || year > 0 ? yoeLong: Jdk8Methods.safeSubtract(1, yoeLong)));
                }
            } else if (era.longValue() == 1L) {
                addFieldValue(YEAR, yoeLong);
            } else if (era.longValue() == 0L) {
                addFieldValue(YEAR, Jdk8Methods.safeSubtract(1, yoeLong));
            } else {
                throw new DateTimeException("Invalid value for era: " + era);
            }
        } else if (standardFields.containsKey(ERA)) {
            ERA.checkValidValue(standardFields.get(ERA));  // always validated
        }

        // build date
        if (standardFields.containsKey(YEAR)) {
            if (standardFields.containsKey(MONTH_OF_YEAR)) {
                if (standardFields.containsKey(DAY_OF_MONTH)) {
                    int y = YEAR.checkValidIntValue(standardFields.remove(YEAR));
                    int moy = Jdk8Methods.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                    int dom = Jdk8Methods.safeToInt(standardFields.remove(DAY_OF_MONTH));
                    if (resolverStyle == ResolverStyle.LENIENT) {
                        long months = Jdk8Methods.safeSubtract(moy, 1);
                        long days = Jdk8Methods.safeSubtract(dom, 1);
                        checkDate(LocalDate.of(y, 1, 1).plusMonths(months).plusDays(days));
                    } else if (resolverStyle == ResolverStyle.SMART){
                        if (moy == 4 || moy == 6 || moy == 9 || moy == 11) {
                            dom = Math.min(dom, 30);
                        } else if (moy == 2) {
                            dom = Math.min(dom, Month.FEBRUARY.length(Year.isLeap(y)));
                        }
                        checkDate(LocalDate.of(y, moy, dom));
                    } else {
                        checkDate(LocalDate.of(y, moy, dom));
                    }
                    return;
                }
                if (standardFields.containsKey(ALIGNED_WEEK_OF_MONTH)) {
                    if (standardFields.containsKey(ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        int y = Jdk8Methods.safeToInt(standardFields.remove(YEAR));
                        int moy = Jdk8Methods.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                        int aw = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_MONTH));
                        int ad = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                        return;
                    }
                    if (standardFields.containsKey(DAY_OF_WEEK)) {
                        int y = Jdk8Methods.safeToInt(standardFields.remove(YEAR));
                        int moy = Jdk8Methods.safeToInt(standardFields.remove(MONTH_OF_YEAR));
                        int aw = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_MONTH));
                        int dow = Jdk8Methods.safeToInt(standardFields.remove(DAY_OF_WEEK));
                        checkDate(LocalDate.of(y, moy, 1).plusDays((aw - 1) * 7).with(nextOrSame(DayOfWeek.of(dow))));
                        return;
                    }
                }
            }
            if (standardFields.containsKey(DAY_OF_YEAR)) {
                int y = Jdk8Methods.safeToInt(standardFields.remove(YEAR));
                int doy = Jdk8Methods.safeToInt(standardFields.remove(DAY_OF_YEAR));
                checkDate(LocalDate.ofYearDay(y, doy));
                return;
            }
            if (standardFields.containsKey(ALIGNED_WEEK_OF_YEAR)) {
                if (standardFields.containsKey(ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    int y = Jdk8Methods.safeToInt(standardFields.remove(YEAR));
                    int aw = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_YEAR));
                    int ad = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7 + (ad - 1)));
                    return;
                }
                if (standardFields.containsKey(DAY_OF_WEEK)) {
                    int y = Jdk8Methods.safeToInt(standardFields.remove(YEAR));
                    int aw = Jdk8Methods.safeToInt(standardFields.remove(ALIGNED_WEEK_OF_YEAR));
                    int dow = Jdk8Methods.safeToInt(standardFields.remove(DAY_OF_WEEK));
                    checkDate(LocalDate.of(y, 1, 1).plusDays((aw - 1) * 7).with(nextOrSame(DayOfWeek.of(dow))));
                    return;
                }
            }
        }
    }

    private void checkDate(LocalDate date) {
        // TODO: this doesn't handle aligned weeks over into next month which would otherwise be valid

        addObject(date);
        for (ChronoField field : standardFields.keySet()) {
            long val1;
            try {
                val1 = date.getLong(field);
            } catch (DateTimeException ex) {
                continue;
            }
            Long val2 = standardFields.get(field);
            if (val1 != val2) {
                throw new DateTimeException("Conflict found: Field " + field + " " + val1 + " differs from " + field + " " + val2 + " derived from " + date);
            }
        }
    }

    private void mergeTime(ResolverStyle resolverStyle) {
        if (standardFields.containsKey(CLOCK_HOUR_OF_DAY)) {
            long ch = standardFields.remove(CLOCK_HOUR_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                if (resolverStyle == ResolverStyle.SMART && ch == 0) {
                    // ok
                } else {
                    CLOCK_HOUR_OF_DAY.checkValidValue(ch);
                }
            }
            addFieldValue(HOUR_OF_DAY, ch == 24 ? 0 : ch);
        }
        if (standardFields.containsKey(CLOCK_HOUR_OF_AMPM)) {
            long ch = standardFields.remove(CLOCK_HOUR_OF_AMPM);
            if (resolverStyle != ResolverStyle.LENIENT) {
                if (resolverStyle == ResolverStyle.SMART && ch == 0) {
                    // ok
                } else {
                    CLOCK_HOUR_OF_AMPM.checkValidValue(ch);
                }
            }
            addFieldValue(HOUR_OF_AMPM, ch == 12 ? 0 : ch);
        }
        if (resolverStyle != ResolverStyle.LENIENT) {
            if (standardFields.containsKey(AMPM_OF_DAY)) {
                AMPM_OF_DAY.checkValidValue(standardFields.get(AMPM_OF_DAY));
            }
            if (standardFields.containsKey(HOUR_OF_AMPM)) {
                HOUR_OF_AMPM.checkValidValue(standardFields.get(HOUR_OF_AMPM));
            }
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
            if (resolverStyle != ResolverStyle.LENIENT) {
                NANO_OF_DAY.checkValidValue(nod);
            }
            addFieldValue(SECOND_OF_DAY, nod / 1000_000_000L);
            addFieldValue(NANO_OF_SECOND, nod % 1000_000_000L);
        }
        if (standardFields.containsKey(MICRO_OF_DAY)) {
            long cod = standardFields.remove(MICRO_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MICRO_OF_DAY.checkValidValue(cod);
            }
            addFieldValue(SECOND_OF_DAY, cod / 1000_000L);
            addFieldValue(MICRO_OF_SECOND, cod % 1000_000L);
        }
        if (standardFields.containsKey(MILLI_OF_DAY)) {
            long lod = standardFields.remove(MILLI_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MILLI_OF_DAY.checkValidValue(lod);
            }
            addFieldValue(SECOND_OF_DAY, lod / 1000);
            addFieldValue(MILLI_OF_SECOND, lod % 1000);
        }
        if (standardFields.containsKey(SECOND_OF_DAY)) {
            long sod = standardFields.remove(SECOND_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                SECOND_OF_DAY.checkValidValue(sod);
            }
            addFieldValue(HOUR_OF_DAY, sod / 3600);
            addFieldValue(MINUTE_OF_HOUR, (sod / 60) % 60);
            addFieldValue(SECOND_OF_MINUTE, sod % 60);
        }
        if (standardFields.containsKey(MINUTE_OF_DAY)) {
            long mod = standardFields.remove(MINUTE_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MINUTE_OF_DAY.checkValidValue(mod);
            }
            addFieldValue(HOUR_OF_DAY, mod / 60);
            addFieldValue(MINUTE_OF_HOUR, mod % 60);
        }

//            long sod = nod / 1000_000_000L;
//            addFieldValue(HOUR_OF_DAY, sod / 3600);
//            addFieldValue(MINUTE_OF_HOUR, (sod / 60) % 60);
//            addFieldValue(SECOND_OF_MINUTE, sod % 60);
//            addFieldValue(NANO_OF_SECOND, nod % 1000_000_000L);
        if (resolverStyle != ResolverStyle.LENIENT) {
            if (standardFields.containsKey(MILLI_OF_SECOND)) {
                MILLI_OF_SECOND.checkValidValue(standardFields.get(MILLI_OF_SECOND));
            }
            if (standardFields.containsKey(MICRO_OF_SECOND)) {
                MICRO_OF_SECOND.checkValidValue(standardFields.get(MICRO_OF_SECOND));
            }
        }
        if (standardFields.containsKey(MILLI_OF_SECOND) && standardFields.containsKey(MICRO_OF_SECOND)) {
            long los = standardFields.remove(MILLI_OF_SECOND);
            long cos = standardFields.get(MICRO_OF_SECOND);
            addFieldValue(MICRO_OF_SECOND, los * 1000 + (cos % 1000));
        }
        if (standardFields.containsKey(MICRO_OF_SECOND) && standardFields.containsKey(NANO_OF_SECOND)) {
            long nos = standardFields.get(NANO_OF_SECOND);
            addFieldValue(MICRO_OF_SECOND, nos / 1000);
            standardFields.remove(MICRO_OF_SECOND);
        }
        if (standardFields.containsKey(MILLI_OF_SECOND) && standardFields.containsKey(NANO_OF_SECOND)) {
            long nos = standardFields.get(NANO_OF_SECOND);
            addFieldValue(MILLI_OF_SECOND, nos / 1000000);
            standardFields.remove(MILLI_OF_SECOND);
        }
        if (standardFields.containsKey(MICRO_OF_SECOND)) {
            long cos = standardFields.remove(MICRO_OF_SECOND);
            addFieldValue(NANO_OF_SECOND, cos * 1000);
        } else if (standardFields.containsKey(MILLI_OF_SECOND)) {
            long los = standardFields.remove(MILLI_OF_SECOND);
            addFieldValue(NANO_OF_SECOND, los * 1000000);
        }

        Long hod = standardFields.get(HOUR_OF_DAY);
        Long moh = standardFields.get(MINUTE_OF_HOUR);
        Long som = standardFields.get(SECOND_OF_MINUTE);
        Long nos = standardFields.get(NANO_OF_SECOND);
        if (resolverStyle != ResolverStyle.LENIENT) {
            if (hod != null) {
                if (resolverStyle == ResolverStyle.SMART &&
                                hod.longValue() == 24 && 
                                (moh == null || moh.longValue() == 0) && 
                                (som == null || som.longValue() == 0) && 
                                (nos == null || nos.longValue() == 0)) {
                    hod = 0L;
                    excessDays = Period.ofDays(1);
                }
                int hodVal = HOUR_OF_DAY.checkValidIntValue(hod);
                if (moh != null) {
                    int mohVal = MINUTE_OF_HOUR.checkValidIntValue(moh);
                    if (som != null) {
                        int somVal = SECOND_OF_MINUTE.checkValidIntValue(som);
                        if (nos != null) {
                            int nosVal = NANO_OF_SECOND.checkValidIntValue(nos);
                            addObject(LocalTime.of(hodVal, mohVal, somVal, nosVal));
                        } else {
                            addObject(LocalTime.of(hodVal, mohVal, somVal));
                        }
                    } else {
                        if (nos == null) {
                            addObject(LocalTime.of(hodVal, mohVal));
                        }
                    }
                } else {
                    if (som == null && nos == null) {
                        addObject(LocalTime.of(hodVal, 0));
                    }
                }
            }
        } else {
            if (hod != null) {
                long hodVal = hod;
                if (moh != null) {
                    if (som != null) {
                        if (nos == null) {
                            nos = 0L;
                        }
                        long totalNanos = Jdk8Methods.safeMultiply(hodVal, 3600_000_000_000L);
                        totalNanos = Jdk8Methods.safeAdd(totalNanos, Jdk8Methods.safeMultiply(moh, 60_000_000_000L));
                        totalNanos = Jdk8Methods.safeAdd(totalNanos, Jdk8Methods.safeMultiply(som, 1_000_000_000L));
                        totalNanos = Jdk8Methods.safeAdd(totalNanos, nos);
                        int excessDays = (int) Jdk8Methods.floorDiv(totalNanos, 86400_000_000_000L);  // safe int cast
                        long nod = Jdk8Methods.floorMod(totalNanos, 86400_000_000_000L);
                        addObject(LocalTime.ofNanoOfDay(nod));
                        this.excessDays = Period.ofDays(excessDays);
                    } else {
                        long totalSecs = Jdk8Methods.safeMultiply(hodVal, 3600L);
                        totalSecs = Jdk8Methods.safeAdd(totalSecs, Jdk8Methods.safeMultiply(moh, 60L));
                        int excessDays = (int) Jdk8Methods.floorDiv(totalSecs, 86400L);  // safe int cast
                        long sod = Jdk8Methods.floorMod(totalSecs, 86400L);
                        addObject(LocalTime.ofSecondOfDay(sod));
                        this.excessDays = Period.ofDays(excessDays);
                    }
                } else {
                    int excessDays = Jdk8Methods.safeToInt(Jdk8Methods.floorDiv(hodVal, 24L));
                    hodVal = Jdk8Methods.floorMod(hodVal, 24);
                    addObject(LocalTime.of((int) hodVal, 0));
                    this.excessDays = Period.ofDays(excessDays);
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Builds the specified type from the values in this builder.
     * <p>
     * This attempts to build the specified type from this builder.
     * If the builder cannot return the type, an exception is thrown.
     *
     * @param <R>  the type to return
     * @param type  the type to invoke {@code from} on, not null
     * @return the extracted value, not null
     * @throws DateTimeException if an error occurs
     */
    public <R> R build(TemporalQuery<R> type) {
        return type.queryFrom(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean isSupported(TemporalField field) {
        if (field == null) {
            return false;
        }
        return standardFields.containsKey(field) ||
                (otherFields != null && otherFields.containsKey(field)) ||
                (date != null && date.isSupported(field)) ||
                (time != null && time.isSupported(field));
    }

    @Override
    public long getLong(TemporalField field) {
        Objects.requireNonNull(field, "field");
        Long value = getFieldValue0(field);
        if (value == null) {
            if (date != null && date.isSupported(field)) {
                return date.getLong(field);
            }
            if (time != null && time.isSupported(field)) {
                return time.getLong(field);
            }
            throw new DateTimeException("Field not found: " + field);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId()) {
            return (R) zone;
        } else if (query == TemporalQueries.chronology()) {
            return (R) chrono;
        } else if (query == TemporalQueries.localDate()) {
            return (R) date;
        } else if (query == TemporalQueries.localTime()) {
            return (R) time;
        } else if (query == TemporalQueries.zone() || query == TemporalQueries.offset()) {
            return query.queryFrom(this);
        } else if (query == TemporalQueries.precision()) {
            return null;  // not a complete date/time
        }
        // inline TemporalAccessor.super.query(query) as an optimization
        // non-JDK classes are not permitted to make this optimization
        return query.queryFrom(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append("DateTimeBuilder[");
        Map<TemporalField, Long> fields = new HashMap<>();
        fields.putAll(standardFields);
        if (otherFields != null) {
            fields.putAll(otherFields);
        }
        if (fields.size() > 0) {
            buf.append("fields=").append(fields);
        }
        buf.append(", ").append(chrono);
        buf.append(", ").append(zone);
        buf.append(", ").append(date);
        buf.append(", ").append(time);
        buf.append(']');
        return buf.toString();
    }

}
