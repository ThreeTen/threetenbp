/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.NANO_OF_DAY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.MathUtils;

/**
 * Stateful class used to merge calendrical information.
 * <p>
 * This class is a tool for merging any set of calendrical information into the
 * most meaningful set of information. For example, separate year, month and day
 * fields will be merged into a date. And if both date and time are present, then
 * they will be merged into a date-time.
 * <p>
 * This class is mutable and not thread-safe.
 * It must only be used from a single thread and must not be passed between threads.
 *
 * @author Stephen Colebourne
 */
public final class CalendricalNormalizer {

    /**
     * The original input.
     */
    private List<Calendrical> input;
    /**
     * The rule of the calendrical supplying the normalized fields.
     */
    private final CalendricalRule<?> sourceRule;
    /**
     * The date.
     */
    private LocalDate date;
    /**
     * The offset.
     */
    private ZoneOffset offset;
    /**
     * The zone.
     */
    private ZoneId zone;
    /**
     * The chronloogy.
     */
    private Chronology chronology;
    /**
     * The rules (half of a map).
     */
    private DateTimeRule[] rules = new DateTimeRule[16];
    /**
     * The rule values (half of a map).
     */
    private long[] ruleValues = new long[16];
    /**
     * The count of rules.
     */
    private int ruleCount;
    /**
     * The errors that occur during normalization.
     */
    private final Set<String> errors = new LinkedHashSet<String>();
    /**
     * A cached {@code LocalTime} object.
     */
    private LocalTime cachedTime;

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code CalendricalNormalizer}.
     *
     * @return the rule for the merger, not null
     */
    public static CalendricalRule<CalendricalNormalizer> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a set of calendricals.
     * <p>
     * This method is used to combine any number of calendrical objects into
     * a more meaningful form. For example, a {@code Year}, {@code MonthDay}
     * and {@code LocalTime} could be combined into a {@code LocalDateTime}.
     * <p>
     * The returned merger will contain the combined information of the input
     * in a normalized form. Use {@link #derive(CalendricalRule)} or
     * {@link #deriveChecked(CalendricalRule)} to derive the desired values or values.
     * <p>
     * If the input cannot be combined into any sensible normalized output then
     * an exception is thrown. For example, passing in the month January and the
     * date 2011-06-30 will fail as the months are in conflict.
     * 
     * @param calendricals  the calendricals to merge, not null, no nulls
     * @return the normalized merger to query, not null
     * @throws CalendricalException if the calendricals cannot be successfully merged
     */
    public static CalendricalNormalizer merge(Calendrical... calendricals) {
        ISOChronology.checkNotNull(calendricals, "Calendricals must not be null");
        CalendricalNormalizer target;
        try {
            List<CalendricalNormalizer> semiNormalized = new ArrayList<CalendricalNormalizer>(calendricals.length);
            for (Calendrical calendrical : calendricals) {
                CalendricalNormalizer merger = rule().getValue(calendrical);
                if (merger != null) {  // ignore anything with no normalized form
                    semiNormalized.add(merger);
                }
            }
            semiNormalized = Collections.unmodifiableList(semiNormalized);  // make list safe for external use
            for (CalendricalNormalizer merger : semiNormalized) {
                if (merger.getRule() != null) {
                    merger.getRule().merge(merger, semiNormalized);
                }
            }
            target = new CalendricalNormalizer(calendricals, semiNormalized);
            target.validate();
            target.normalize();
        } catch (NullPointerException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            // provide more useful error message
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + ex.getMessage(), ex);
        }
        // normalization is quiet, so need to check it was successful
        if (target.getErrors().size() > 0) {
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + target.getErrors());
        }
        return target;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a the normalized set of objects.
     * <p>
     * This method is designed to be called from {@link Calendrical#get(CalendricalRule)}.
     * The class implementing the interface must call this method passing in
     * parameters to fully describe the state of the object to be derived from.
     * Avoid duplicating information between the date, time and fields if possible.
     * 
     * @param <R>  the type of the desired rule
     * @param ruleToDerive  the rule to derive, not null
     * @param ruleOfData  the rule of the data to derive from, may be null
     * @param date  the date to derive from, may be null
     * @param time  the time to derive from, may be null
     * @param offset  the zone offset to derive from, may be null
     * @param zoneId  the zone ID to derive from, may be null
     * @param chrono  the chronology to derive from, may be null
     * @param fields  the fields to derive from, may be null
     * @return the derived value for the rule, null if unable to derive
     */
    @SuppressWarnings("unchecked")
    public static <R> R derive(CalendricalRule<R> ruleToDerive, CalendricalRule<?> ruleOfData,
            LocalDate date, LocalTime time, ZoneOffset offset, ZoneId zoneId, Chronology chrono, DateTimeFields fields) {
        ISOChronology.checkNotNull(ruleToDerive, "CalendricalRule must not be null");
        if (fields == null) {
            // optimize simple cases
            if (ruleToDerive instanceof ISOCalendricalRule<?>) {
                switch (((ISOCalendricalRule<?>) ruleToDerive).ordinal) {
                    case ISOCalendricalRule.LOCAL_DATE_ORDINAL: return (R) date;
                    case ISOCalendricalRule.LOCAL_TIME_ORDINAL: return (R) time;
                    case ISOCalendricalRule.ZONE_OFFSET_ORDINAL: return (R) offset;
                    case ISOCalendricalRule.ZONE_ID_ORDINAL: return (R) zoneId;
                    case ISOCalendricalRule.CHRONOLOGY_ORDINAL: return (R) chrono;
                    // other cases are not so simple, so drop through
                }
//            } else if (ruleToDerive instanceof ISODateTimeRule) {
//                return (R) ((ISODateTimeRule) ruleToDerive).deriveFrom(date, time, offset);
            }
        }
        CalendricalNormalizer merger = new CalendricalNormalizer(ruleOfData, date, time, offset, zoneId, chrono, fields);
        merger.normalize();
        return merger.derive(ruleToDerive);
    }

    /**
     * Derives the specified rule from a the normalized set of objects.
     * <p>
     * This method is designed to be called from {@link Calendrical#get(CalendricalRule)}.
     * The class implementing the interface must call this method passing in
     * parameters to fully describe the state of the object to be derived from.
     * 
     * @param <R>  the type of the desired rule
     * @param ruleToDerive  the rule to derive, not null
     * @param ruleOfData  the rule of the data to derive from, may be null
     * @param chrono  the chronology to derive from, may be null
     * @param field  the field to derive from, not null
     * @return the derived value for the rule, null if unable to derive
     */
    public static <R> R derive(CalendricalRule<R> ruleToDerive, CalendricalRule<?> ruleOfData, Chronology chrono, DateTimeField field) {
        ISOChronology.checkNotNull(ruleToDerive, "CalendricalRule must not be null");
        ISOChronology.checkNotNull(field, "DateTimeField must not be null");
        CalendricalNormalizer merger = new CalendricalNormalizer(ruleOfData, null, null, null, null, chrono, Collections.singleton(field));
        merger.normalize();
        return merger.derive(ruleToDerive);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from a normalized list of mergers.
     * 
     * @param calendricals  the original calendricals prior to merging, not null
     * @param mergers  the merged form of the calendricals, not null
     */
    private CalendricalNormalizer(Calendrical[] calendricals, List<CalendricalNormalizer> mergers) {
        this.input = Collections.unmodifiableList(Arrays.asList(calendricals));
        this.sourceRule = null;
        for (CalendricalNormalizer merger : mergers) {
            if (merger.date != null) {
                setDate(merger.date, true);
            }
            if (merger.offset != null) {
                setOffset(merger.offset, true);
            }
            if (merger.zone != null) {
                setZone(merger.zone, true);
            }
            if (merger.chronology != null) {
                setChronology(merger.chronology, true);
            }
            for (int i = 0; i < merger.ruleCount; i++) {
                setField(merger.rules[i], merger.ruleValues[i], true);
            }
            if (merger.cachedTime != null) {
                cachedTime = merger.cachedTime;
            }
        }
    }

    /**
     * Creates an instance from a normalized set of objects.
     * 
     * @param ruleOfData  the rule of the calendrical that these objects represent, may be null
     * @param date  the date, may be null
     * @param time  the time, may be null
     * @param offset  the zone offset, may be null
     * @param zoneId  the zone ID, may be null
     * @param chrono  the chronology, may be null
     * @param fields  the fields, may be null
     */
    private CalendricalNormalizer(
            CalendricalRule<?> ruleOfData, LocalDate date, LocalTime time, ZoneOffset offset,
            ZoneId zone, Chronology chronology, Iterable<DateTimeField> fields) {
        this.sourceRule = ruleOfData;
        this.date = date;
        this.offset = offset;
        this.zone = zone;
        this.chronology = chronology;
        if (fields != null) {
            for (DateTimeField field : fields) {
                setField(field.getRule(), field.getValue(), false);  // can use false here as DateTimeFields ensure no rule clashes
            }
        }
        if (time != null) {
            setField(NANO_OF_DAY, time.toNanoOfDay(), true);
        }
        getInput();  // field map is modifiable, so lock input now
        this.cachedTime = time;
    }

    //-----------------------------------------------------------------------
    /**
     * The original input provided to the merger.
     * 
     * @return the unmodifiable original input, not null
     */
    public List<Calendrical> getInput() {
        if (input == null) {
            List<Calendrical> list = new ArrayList<Calendrical>();
            if (date != null) {
                list.add(date);
            }
            if (offset != null) {
                list.add(offset);
            }
            if (zone != null) {
                list.add(zone);
            }
            if (chronology != null) {
                list.add(chronology);
            }
            // TODO
//            if (fields != null) {
//                list.addAll(fields.values());
//            }
            input = Collections.unmodifiableList(list);
        }
        return input;
    }

    /**
     * The single rule that defines the data in this merger.
     * <p>
     * This will only be present if this merger is created from a single
     * conceptual object as opposed to being merged.
     * 
     * @return the rule of the data, may be null
     */
    public CalendricalRule<?> getRule() {
        return sourceRule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalDate getDate(boolean storeErrorIfNull) {
        if (storeErrorIfNull && date == null) {
            addError("Missing LocalDate");
        }
        return date;
    }

    /**
     * Gets the time.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalTime getTime(boolean storeErrorIfNull) {
        long result = -1;
        for (int i = 0; i < ruleCount; i++) {
            long fieldTime = rules[i].createTime(ruleValues[i], this);
            if (fieldTime >= 0) {
                if (result >= 0 && fieldTime != result) {
                    addError("Clash creating LocalTime: " + LocalTime.ofNanoOfDay(result) + " and " + LocalTime.ofNanoOfDay(fieldTime));
                    return null;
                }
                result = fieldTime;
            }
        }
        if (result >= 0) {
            return (cachedTime != null && cachedTime.toNanoOfDay() == result ? cachedTime : LocalTime.ofNanoOfDay(result));
        }
        if (storeErrorIfNull) {
            addError("Missing LocalTime");
        }
        return null;
    }

    /**
     * Gets the offset.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneOffset getOffset(boolean storeErrorIfNull) {
        if (storeErrorIfNull && offset == null) {
            addError("Missing ZoneOffset");
        }
        return offset;
    }

    /**
     * Gets the zone.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneId getZone(boolean storeErrorIfNull) {
        if (storeErrorIfNull && zone == null) {
            addError("Missing ZoneId");
        }
        return zone;
    }

    /**
     * Gets the chronology.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public Chronology getChronology(boolean storeErrorIfNull) {
        if (storeErrorIfNull && chronology == null) {
            addError("Missing Chronology");
        }
        return chronology;
    }

    /**
     * Gets a field by rule.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param rule  the rule to retrieve, null returns null
     * @return the field, may be null
     */
    public DateTimeField getField(DateTimeRule rule, boolean storeErrorIfNull) {
        for (int i = 0; i < ruleCount; i++) {
            if (rules[i].equals(rule)) {
                return DateTimeField.of(rules[i], ruleValues[i]);
            }
        }
        if (storeErrorIfNull) {
            addError("Missing field " + rule.getName());
        }
        return null;
    }

    /**
     * Gets a field by rule, deriving the value.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param ruleToDerive  the rule to retrieve, null returns null
     * @return the field, may be null
     */
    public DateTimeField getFieldDerived(DateTimeRule ruleToDerive, boolean storeErrorIfNull) {
        DateTimeField result = ruleToDerive.deriveFrom(this);
        if (result == null) {
            result = deriveField(ruleToDerive);
        }
        if (storeErrorIfNull && result == null) {
            addError("Missing field " + ruleToDerive.getName());
        }
        return result;
    }

    private DateTimeField deriveField(DateTimeRule ruleToDerive) {
        DateTimeRule baseRule = ruleToDerive.getBaseRule();
        for (int i = 0; i < ruleCount; i++) {
            DateTimeRule rule = rules[i];
            long ruleValue = ruleValues[i];
            if (rule.equals(ruleToDerive)) {
                return DateTimeField.of(ruleToDerive, ruleValue);
            }
            if (rule.getBaseRule().equals(baseRule) &&
                    rule.comparePeriodUnit(ruleToDerive) <= 0 &&
                    rule.comparePeriodRange(ruleToDerive) >= 0) {
                DateTimeField result = derive(rule, ruleValue, ruleToDerive);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static DateTimeField derive(DateTimeRule rule, long ruleValue, DateTimeRule ruleToDerive) {
        // TODO: doesn't handle DAYS well, as DAYS are not a multiple of NANOS
        long period = rule.convertToPeriod(ruleValue);
        PeriodField bottomConversion = ruleToDerive.getPeriodUnit().getEquivalentPeriod(rule.getPeriodUnit());
        period = MathUtils.floorDiv(period, bottomConversion.getAmount());
        PeriodUnit rangeToDerive = ruleToDerive.getPeriodRange();
        if (rangeToDerive != null && rule.comparePeriodRange(ruleToDerive) != 0) {
//                if (periodRange.equals(DAYS)) {  // TODO: hack
//                    periodRange = _24_HOURS;
//                }
            PeriodField topConversion = rangeToDerive.getEquivalentPeriod(ruleToDerive.getPeriodUnit());
            period = MathUtils.floorMod(period, topConversion.getAmount());
        }
        return ruleToDerive.field(ruleToDerive.convertFromPeriod(period));
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the date.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param date  the date to store, may be null
     * @param storeErrorIfClash  true to store an error if the date clashes with the stored value
     */
    public void setDate(LocalDate date, boolean storeErrorIfClash) {
        this.date = set(this.date, date, storeErrorIfClash);
    }

    /**
     * Sets the time.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param time  the time to store, may be null
     * @param storeErrorIfClash  true to store an error if the time clashes with the stored value
     */
    public void setTime(LocalTime time, boolean storeErrorIfClash) {
        setField(NANO_OF_DAY, time.toNanoOfDay(), storeErrorIfClash);
        this.cachedTime = time;
    }

    /**
     * Sets the offset.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param offset  the offset to store, may be null
     * @param storeErrorIfClash  true to store an error if the offset clashes with the stored value
     */
    public void setOffset(ZoneOffset offset, boolean storeErrorIfClash) {
        this.offset = set(this.offset, offset, storeErrorIfClash);
    }

    /**
     * Sets the zone.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param zone  the zone to store, may be null
     * @param storeErrorIfClash  true to store an error if the zone clashes with the stored value
     */
    public void setZone(ZoneId zone, boolean storeErrorIfClash) {
        this.zone = set(this.zone, zone, storeErrorIfClash);
    }

    /**
     * Sets the chronology.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param chronology  the chronology to store, may be null
     * @param storeErrorIfClash  true to store an error if the chronology clashes with the stored value
     */
    public void setChronology(Chronology chronology, boolean storeErrorIfClash) {
        this.chronology = set(this.chronology, chronology, storeErrorIfClash);
    }

    /**
     * Sets the specified field.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * The flag should be set to <code>true</code> to cause the method to check
     * if the value for the field clashes with an existing value.
     * A <code>false</code> value simply overwrites any previous value.
     * 
     * @param field  the field to store, null ignored
     * @param storeErrorIfClash  true to store an error if the field clashes with an existing field
     */
    public void setField(DateTimeRule rule, long ruleValue, boolean storeErrorIfClash) {
        for (int j = 0; j < ruleCount; j++) {
            if (rules[j].equals(rule)) {
                if (ruleValues[j] != ruleValue) {
                    addError("Clash: " + rule + " " + ruleValues[j] + " and " + ruleValue);
                }
                break;
            }
        }
        rules[ruleCount] = rule;
        ruleValues[ruleCount++] = ruleValue;
    }

    private <T> T set(T curObj, T newObj, boolean storeErrorIfClash) {
        if (storeErrorIfClash) {
            if (curObj != null && curObj.equals(newObj) == false) {
                addError("Clash: " + curObj + " and " + newObj);
                return curObj;
            }
        }
        return newObj;
    }

    // phase 1
    //-----------------------------------------------------------------------
    private void validate() {
        for (int i = 0; i < ruleCount; i++) {
            if (rules[i].getValueRange().isValidValue(ruleValues[i]) == false) {
                addError("Value out of range: " + rules[i] + " " + ruleValues[i]);
            }
        }
    }

    // phase 2
    //-----------------------------------------------------------------------
    /**
     * Normalize the fields, converting to LocalDate/LocalTime if possible.
     */
    private void normalize() {
        // do not call from the constructor
        normalizeAuto();
        if (errors.size() == 0) {
            normalizeManual();
            if (errors.size() == 0) {
                normalizeCrossCheck();
            }
        }
    }

    private void normalizeAuto() {
        // normalize each individual field in isolation
        for (int i = 0; i < ruleCount; i++) {
            DateTimeRule rule = rules[i];
            DateTimeRule normalizationRule = rule.getNormalizationRule();
            if (rule.equals(normalizationRule) == false) {
                long newValue = normalizationRule.convertFromPeriod(rule.convertToPeriod(ruleValues[i]));
                setField(normalizationRule, newValue, true);
                removeRule(i--);
            }
        }
        if (ruleCount < 2 || errors.size() > 0) {
            return;
        }
        
        // group according to base rule
        Map<DateTimeRule, List<DateTimeField>> grouped = new HashMap<DateTimeRule, List<DateTimeField>>();
        for (int i = 0; i < ruleCount; i++) {
            DateTimeRule baseRule = rules[i].getBaseRule();
            List<DateTimeField> groupedList = grouped.get(baseRule);
            if (groupedList == null) {
                groupedList = new ArrayList<DateTimeField>();
                grouped.put(baseRule, groupedList);
            }
            groupedList.add(DateTimeField.of(rules[i], ruleValues[i]));
        }
        
        // normalize groups
        // TODO: loop again (group again) if register on group is public
        ruleCount = 0;
        for (Map.Entry<DateTimeRule, List<DateTimeField>> entry : grouped.entrySet()) {
            List<DateTimeField> group = entry.getValue();
            if (group.size() >= 2) {
                mergeGroup(entry.getKey(), group);
            }
            for (DateTimeField field : group) {
                rules[ruleCount] = field.getRule();
                ruleValues[ruleCount++] = field.getValue();  // should be no clashes here
            }
        }
    }

    private void mergeGroup(DateTimeRule baseRule, List<DateTimeField> group) {
        sort(group);
        DateTimeRuleGroup ruleGroup = DateTimeRuleGroup.of(baseRule);
        for (int i = 0; i < group.size() - 1; i++) {
            final DateTimeField fieldLge = group.get(i);
            for (int j = i + 1; j < group.size(); j++) {
                final DateTimeField fieldSml = group.get(j);
                final DateTimeRule ruleLge = fieldLge.getRule();
                final DateTimeRule ruleSml = fieldSml.getRule();
                // remove if derived
                // the fields share a base rule, so this must succeed if there fieldLge surrounds fieldSml
                final DateTimeField derivedSml = fieldLge.derive(ruleSml);
                if (derivedSml != null) {
                    if (derivedSml.equals(fieldSml)) {
                        group.remove(j--);
                        continue;
                    } else {
                        addError("Clash: " + fieldSml + " and " + derivedSml);
                        return;
                    }
                }
                // merge overlap or adjacent
                if (DateTimeRule.comparePeriodUnits(ruleSml.getPeriodRange(), ruleLge.getPeriodUnit()) >= 0 &&
                        DateTimeRule.comparePeriodUnits(ruleSml.getPeriodUnit(), ruleLge.getPeriodUnit()) < 0) {
                    final long periodLge = ruleLge.convertToPeriod(fieldLge.getValue());
                    final long periodSml = ruleSml.convertToPeriod(fieldSml.getValue());
                    final PeriodField conversion1 = ruleSml.getPeriodRange().getEquivalentPeriod(ruleLge.getPeriodUnit());
                    // if was an overlap, then check it is valid
                    // this must be done before the combined rule check to ensure that the final derivation is OK
                    if (DateTimeRule.comparePeriodUnits(ruleSml.getPeriodRange(), ruleLge.getPeriodUnit()) > 0) {
                        long periodMidLge = MathUtils.floorMod(periodLge, conversion1.getAmount());
                        final PeriodField conversion2 = ruleSml.getPeriodRange().getEquivalentPeriod(ruleSml.getPeriodUnit());
                        final PeriodField conversion3 = ruleLge.getPeriodUnit().getEquivalentPeriod(ruleSml.getPeriodUnit());
                        long periodMidSml = MathUtils.floorMod(periodSml, conversion2.getAmount());
                        periodMidSml = MathUtils.floorDiv(periodMidSml, conversion3.getAmount());
                        if (periodMidLge != periodMidSml) {
                            addError("Clash: " + fieldLge + " and " + fieldSml);
                            return;
                        }
                    }
                    // merge if possible
                    DateTimeRule ruleCombined = ruleGroup.getRelatedRule(ruleSml.getPeriodUnit(), ruleLge.getPeriodRange());
                    if (ruleCombined != null) {
                        long period = MathUtils.floorDiv(periodLge, conversion1.getAmount());
                        final PeriodField conversion2 = ruleSml.getPeriodRange().getEquivalentPeriod(ruleSml.getPeriodUnit());
                        period = MathUtils.safeMultiply(period, conversion2.getAmount());
                        period = MathUtils.safeAdd(period, periodSml);
                        DateTimeField fieldCombined = ruleCombined.field(ruleCombined.convertFromPeriod(period));
                        group.set(i, fieldCombined);
                        group.remove(j);
                        i = -1;
                        break;
                    }
                }
            }
        }
    }

    private void sort(List<DateTimeField> group) {
        Collections.sort(group, new Comparator<DateTimeField>() {
            public int compare(DateTimeField dtf1, DateTimeField dtf2) {
                int cmp = -dtf1.getRule().comparePeriodRange(dtf2.getRule());
                if (cmp == 0) {
                    cmp = dtf1.getRule().comparePeriodUnit(dtf2.getRule());
                }
                return cmp;
            }
        });
    }

    private void normalizeManual() {
        for (int i = 0; i < ruleCount; i++) {
            rules[i].normalize(this);
        }
    }

    private void normalizeCrossCheck() {
        for (int i = 0; i < ruleCount; i++) {
            DateTimeField derived = rules[i].deriveFrom(this);
            if (derived != null) {
                if (derived.getValue() != ruleValues[i]) {
                    addError("Cross-check clash: " + derived + " and " + ruleValues[i]);
                } else {
                    removeRule(i--);
                }
            }
        }
    }

    private void removeRule(int i) {
        ruleCount--;
        if (i < ruleCount) {
            rules[i] = rules[ruleCount];
            ruleValues[i] = ruleValues[ruleCount];
        }
    }

    // phase 3
    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public <R> R derive(CalendricalRule<R> ruleToDerive) {
        if (errors.size() > 0) {
            return null;  // quiet
        }
        if (ruleToDerive == rule()) {
            return (R) this;
        }
        try {
            R result = ruleToDerive.deriveFrom(this);
            if (result == null && ruleToDerive instanceof DateTimeRule) {
                result = (R) deriveField((DateTimeRule) ruleToDerive);
            }
            if (errors.size() > 0) {
                return null;
            }
            return result;
        } catch (RuntimeException ex) {
            addError(ex.getMessage());
            return null;
        }
    }

    public <R> R deriveChecked(CalendricalRule<R> ruleToDerive) {
        R result = derive(ruleToDerive);
        if (result == null) {
            if (errors.size() > 0) {
                throw new CalendricalException("Unable to derive " + ruleToDerive + " from " + this + ": " + errors);
            }
            throw new CalendricalException("Unable to derive " + ruleToDerive + " from " + this);
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the modifiable list of errors that have occurred.
     * <p>
     * Use {@link #addError} to add to this list.
     * 
     * @return the list of errors, not null, no nulls
     */
    public Set<String> getErrors() {
        return errors;
    }

    /**
     * Adds an error to those tracked by the normalizer.
     * 
     * @param error  the error text, not null
     */
    public void addError(String error) {
        if (error != null) {
            errors.add(error);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getInput().toString() + (errors.size() > 0 ? " " + errors : "");
    }

    //-----------------------------------------------------------------------
    /**
     * Rule class.
     */
    static final class Rule extends CalendricalRule<CalendricalNormalizer> implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Serialization version. */
        static final Rule INSTANCE = new Rule();

        private Rule() {
            super(CalendricalNormalizer.class, "CalendricalNormalizer");  // TODO
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

}
